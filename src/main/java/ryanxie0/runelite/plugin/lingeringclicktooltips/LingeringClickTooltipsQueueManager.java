/*
 * Copyright (c) 2021, Ryan Xie <ryanlxie@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package ryanxie0.runelite.plugin.lingeringclicktooltips;

import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.events.MenuOptionClicked;
import ryanxie0.runelite.plugin.lingeringclicktooltips.wrapper.LingeringClickTooltipsWrapper;
import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static ryanxie0.runelite.plugin.lingeringclicktooltips.LingeringClickTooltipsConfig.*;
import static ryanxie0.runelite.plugin.lingeringclicktooltips.color.LingeringClickTooltipsColor.*;
import static ryanxie0.runelite.plugin.lingeringclicktooltips.color.LingeringClickTooltipsColorConstants.*;
import static ryanxie0.runelite.plugin.lingeringclicktooltips.fade.LingeringClickTooltipsFade.*;
import static ryanxie0.runelite.plugin.lingeringclicktooltips.filtering.LingeringClickTooltipsFilterMode.*;
import static ryanxie0.runelite.plugin.lingeringclicktooltips.filtering.LingeringClickTooltipsFilteringConstants.*;
import static ryanxie0.runelite.plugin.lingeringclicktooltips.filtering.LingeringClickTooltipsFilteringUtil.*;
import static ryanxie0.runelite.plugin.lingeringclicktooltips.filtering.LingeringClickTooltipsFiltering.*;
import static ryanxie0.runelite.plugin.lingeringclicktooltips.wrapper.LingeringClickTooltipsWrapperUtil.*;

public class LingeringClickTooltipsQueueManager {

    @Inject
    private LingeringClickTooltipsConfig config;

    @Getter(AccessLevel.PACKAGE)
    private Queue<LingeringClickTooltipsWrapper> tooltips;

    @Getter(AccessLevel.PACKAGE)
    private LingeringClickTooltipsWrapper fixedLocationTooltip;

    @Getter(AccessLevel.PACKAGE)
    private LingeringClickTooltipsWrapper infoTooltip;

    private LingeringClickTooltipsWrapper tickSyncTooltip;

    private List<LingeringClickTooltipsWrapper> tooltipsToFlush;

    private LingeringClickTooltipsInputListener inputListener;

    @Getter(AccessLevel.PACKAGE)
    private String lastUnfilteredTooltipText; // used for peeking while no filter mode is enabled

    private String lastTooltipText; // used for managing filter lists

    private String lastInfoTooltipText; // used for optimizing info tooltips

    /**
     * Creates a new tooltip from a click. Builds the raw tooltip text, applies color tags, checks for
     * blocked/bypass clicks, then sets the currently rendered tooltip.
     * @param event the event fired when the user left clicks
     */
    public void createNewTooltip(MenuOptionClicked event)
    {
        String rawTooltipText = getRawTooltipText(event.getMenuOption(), event.getMenuTarget());
        String tooltipText = applyCustomTextColor(rawTooltipText, config.overrideMenuColors());
        lastTooltipText = tooltipText;
        tooltipText = getBlockedClickText(tooltipText, event) + tooltipText;
        setRenderedTooltip(tooltipText);
    }

    /**
     * Gets the text for a blocked click and consumes the MenuOptionClicked event, if applicable.
     * @param tooltipText the text which may be blocked by a filter list
     * @param event the event fired when the user left clicks
     * @return text indicating a block/bypass, proper formatting and color tags applied, empty string if N/A
     */
    private String getBlockedClickText(String tooltipText, MenuOptionClicked event)
    {
        String blockedClickText = "";
        if (config.filterMode() == NONE)
        {
            return blockedClickText;
        }
        else if (config.shiftBlocks() && inputListener.isShiftPressed())
        {
            event.consume();
            blockedClickText = BLOCKED_BY + SHIFT;
        }
        else if (config.blockFilteredClicks() && isFilteredByList(removeTags(tooltipText), config))
        {
            if (config.ctrlBypassesBlock() && inputListener.isCtrlPressed())
            {
                blockedClickText = BYPASS + config.filterMode();
            }
            else
            {
                event.consume();
                blockedClickText = BLOCKED_BY + config.filterMode();
            }
        }
        return getBlockedClickTextWithColor(blockedClickText);
    }

    /**
     * Builds a tooltip using tooltipText, then assigns the tooltip to the currently rendered variable.
     * @param tooltipText the tooltip text that will be rendered
     */
    private void setRenderedTooltip(String tooltipText)
    {
        if (shouldRenderTooltip(tooltipText, inputListener.isHide(), inputListener.isCtrlPressed(), config))
        {
            LingeringClickTooltipsWrapper tooltip = buildTooltipWrapper(
                tooltipText,
                getOffsetLocation(inputListener.getLastClickPoint(), config),
                getTooltipBackgroundColor(tooltipText),
                false,
                config
            );
            if (config.tickSyncMode())
            {
                tickSyncTooltip = tooltip;
            }
            else if (config.tooltipLocation() == LingeringClickTooltipsLocation.FIXED)
            {
                fixedLocationTooltip = tooltip;
            }
            else
            {
                addTooltip(tooltip);
            }
            lastUnfilteredTooltipText = tooltipText;
        }
    }

    /**
     * Adds a tooltip wrapper to the queue. If doing so were to increase the queue size beyond maximumTooltipsShown,
     * adds the head of the queue to flush.
     * @param tooltip the tooltip wrapper to add to the queue
     */
    private void addTooltip(LingeringClickTooltipsWrapper tooltip)
    {
        tooltips.add(tooltip);
        if (tooltips.size() > config.maximumTooltipsShown())
        {
            tooltipsToFlush.add(tooltips.peek());
        }
    }

    /**
     * Assigns tickSyncTooltip to the appropriate variable, then consumes it. Called on each game tick.
     */
    public void processTick()
    {
        if (config.tickSyncMode() && tickSyncTooltip != null)
        {
            refreshTooltipTimeOfCreation(tickSyncTooltip);
            if (config.tooltipLocation() == LingeringClickTooltipsLocation.FIXED)
            {
                fixedLocationTooltip = tickSyncTooltip;
            }
            else
            {
                addTooltip(tickSyncTooltip);
            }
            tickSyncTooltip = null;
        }
    }

    /**
     * Creates a new info tooltip, which will begin rendering at the next render cycle. If the text did not change,
     * infoTooltip is refreshed instead.
     * @param infoTooltipText the text of the info tooltip, contains color tags
     */
    private void createNewInfoTooltip(String infoTooltipText)
    {
        if (lastInfoTooltipText != null && !lastInfoTooltipText.isEmpty() && removeTags(infoTooltipText).equals(removeTags(lastInfoTooltipText)))
        {
            refreshInfoTooltip(infoTooltip, config);
        }
        else
        {
            infoTooltip = buildTooltipWrapper(
                infoTooltipText,
                null,
                getTooltipBackgroundColor(infoTooltipText),
                true,
                config
            );
            lastInfoTooltipText = infoTooltipText;
        }
    }

    /**
     * Creates an info tooltip regarding the current hide mode state. First generates text containing keywords
     * structured as (TOOLTIPS + HIDDEN), then applies the appropriate formatting and color tags.
     * @param isHide whether hide mode is enabled
     */
    public void createHideModeInfoTooltip(boolean isHide)
    {
        String hideModeTooltipText = isHide? TOOLTIPS + HIDDEN : TOOLTIPS + SHOWN;
        String toggledHideModeTooltipTextWithColor = applyInfoTooltipTextColor(hideModeTooltipText, false);
        createNewInfoTooltip(toggledHideModeTooltipTextWithColor);
    }

    /**
     * Creates an info tooltip regarding a filter list update. If no filter list action is detected, indicates to
     * the user that no filter mode is enabled.
     */
    public void createFilterListUpdateInfoTooltip()
    {
        String filterListUpdateTooltipText = updateFilterLists(config, lastTooltipText, false);
        if (!filterListUpdateTooltipText.isEmpty())
        {
            String filterListUpdateTooltipTextWithColor = applyInfoTooltipTextColor(filterListUpdateTooltipText, false);
            createNewInfoTooltip(filterListUpdateTooltipTextWithColor);
        }
    }

    /**
     * Creates an info tooltip peeking a relevant action. If no filter list action is detected, creates a peek info
     * tooltip containing the last unfiltered action.
     */
    public void createPeekInfoTooltip()
    {
        String filterListUpdateTooltipText = updateFilterLists(config, lastTooltipText, true);
        if (!filterListUpdateTooltipText.isEmpty() && !filterListUpdateTooltipText.contains(NO_FILTER_MODE_ENABLED))
        {
            String filterListUpdateTooltipTextWithColor = applyInfoTooltipTextColor(filterListUpdateTooltipText, true);
            createNewInfoTooltip(filterListUpdateTooltipTextWithColor);
        }
        else if (lastUnfilteredTooltipText != null && !lastUnfilteredTooltipText.isEmpty())
        {
            String peekLastClickTooltipTextWithColor = applyInfoTooltipTextColor(LAST_CLICK + lastUnfilteredTooltipText, true);
            createNewInfoTooltip(peekLastClickTooltipTextWithColor);
        }
    }

    /**
     * Adds a tooltip to tooltipsToFlush.
     * @param tooltip the tooltip to flush
     */
    public void addTooltipToFlush(LingeringClickTooltipsWrapper tooltip)
    {
        tooltipsToFlush.add(tooltip);
    }

    /**
     * Flushes the collected tooltips from the queue. Called once at the beginning of each render.
     */
    public void flushTooltips()
    {
        for (LingeringClickTooltipsWrapper tooltip : tooltipsToFlush)
        {
            tooltips.remove(tooltip);
        }
    }

    public void initialize(LingeringClickTooltipsInputListener inputListener)
    {
        this.inputListener = inputListener;
        tooltips = new LinkedList<>();
        tooltipsToFlush = new LinkedList<>();
    }

    public void destroy()
    {
        inputListener = null;

        tooltips.clear();
        tooltips = null;

        tooltipsToFlush.clear();
        tooltipsToFlush = null;

        fixedLocationTooltip = null;

        infoTooltip = null;

        lastTooltipText = null;
        lastUnfilteredTooltipText = null;
        lastInfoTooltipText = null;
    }

    public void clear(String configKey)
    {
        if (!configKey.equals(OVERLAY_PREFERRED_LOCATION) && !configKey.equals(OVERLAY_PREFERRED_POSITION))
        {
            tooltips.clear();
            tooltipsToFlush.clear();
            fixedLocationTooltip = null;
        }

        if (!configKey.equals(BLACKLIST_CSV) && !configKey.equals(WHITELIST_CSV))
        {
            lastTooltipText = null;
            lastUnfilteredTooltipText = null;
            lastInfoTooltipText = null;
        }
    }
}
