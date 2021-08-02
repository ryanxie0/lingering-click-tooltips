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
import ryanxie0.runelite.plugin.lingeringclicktooltips.components.wrapper.LingeringClickTooltipsWrapper;
import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static ryanxie0.runelite.plugin.lingeringclicktooltips.colors.LingeringClickTooltipsColors.*;
import static ryanxie0.runelite.plugin.lingeringclicktooltips.colors.LingeringClickTooltipsTextColorConstants.*;
import static ryanxie0.runelite.plugin.lingeringclicktooltips.components.wrapper.LingeringClickTooltipsWrapperUtil.*;
import static ryanxie0.runelite.plugin.lingeringclicktooltips.filtering.LingeringClickTooltipsFiltering.*;
import static ryanxie0.runelite.plugin.lingeringclicktooltips.filtering.LingeringClickTooltipsTrivialClicksManager.*;

public class LingeringClickTooltipsQueueManager {

    @Inject
    private LingeringClickTooltipsConfig config;

    @Getter(AccessLevel.PUBLIC)
    private Queue<LingeringClickTooltipsWrapper> tooltips;

    @Getter(AccessLevel.PUBLIC)
    private LingeringClickTooltipsWrapper fixedLocationTooltip;

    @Getter(AccessLevel.PUBLIC)
    private LingeringClickTooltipsWrapper infoTooltip;

    private LingeringClickTooltipsInputListener inputListener;

    @Getter(AccessLevel.PACKAGE)
    private String lastUnfilteredTooltipText; // used for peeking while no filter mode is enabled
    private String lastTooltipText; // used for managing filter lists
    private String lastInfoTooltipText; // used for optimizing info tooltips

    private List<LingeringClickTooltipsWrapper> tooltipsToFlush;

    /**
     * Called each time the user selects a menu option. The most recent tooltip text is tracked by lastTooltipText
     * unless the action is already handled by the trivial click system. Here, config.maximumTooltipsShown is enforced
     * by flushing excess tooltips from the front of the queue.
     * @param menuOption the menu option selected by the user
     * @param menuTarget the menu target selected by the user
     */
    public void addTooltip(String menuOption, String menuTarget)
    {
        String tooltipText = getTooltipText(menuOption, menuTarget);
        String tooltipTextWithCustomColor = applyCustomTextColor(tooltipText, config.overrideMenuOptionColor());
        lastTooltipText = isHandledByTrivialClicks(tooltipText)? lastTooltipText : tooltipTextWithCustomColor;
        if (shouldProcessClick(tooltipText, inputListener.isHide(), inputListener.isCtrlPressed(), config))
        {
            if (config.tooltipLocation() == LingeringClickTooltipsLocation.FIXED)
            {
                fixedLocationTooltip = buildTooltipWrapper(
                    tooltipTextWithCustomColor,
                    null,
                    getTooltipBackgroundColor(tooltipTextWithCustomColor),
                    false
                );
            }
            else
            {
                tooltips.add(buildTooltipWrapper(
                    tooltipTextWithCustomColor,
                    getOffsetLocation(inputListener.getLastClickPoint(), config),
                    getTooltipBackgroundColor(tooltipTextWithCustomColor),
                    false
                ));
                if (tooltips.size() > config.maximumTooltipsShown())
                {
                    tooltipsToFlush.add(tooltips.peek());
                }
            }
            lastUnfilteredTooltipText = tooltipTextWithCustomColor;
        }
    }

    /**
     * Creates a new info tooltip, which will begin rendering at the next render cycle. If the info tooltip text did
     * not change, we can simply refresh the tooltip by setting isFaded to true and timeOfCreation to now instead of
     * reconstructing a new tooltip from scratch. This is possible because infoTooltip never gets flushed.
     * @param infoTooltipText the text of the info tooltip, contains color tags
     */
    private void createNewInfoTooltip(String infoTooltipText)
    {
        if (lastInfoTooltipText != null && !lastInfoTooltipText.isEmpty() && infoTooltipText.equals(lastInfoTooltipText))
        {
            refreshInfoTooltip(infoTooltip);
        }
        else
        {
            infoTooltip = buildTooltipWrapper(
                infoTooltipText,
                null,
                getTooltipBackgroundColor(infoTooltipText),
                true
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
     * Creates an info tooltip regarding a filter list update. If no filter list action is detected (no keywords such as
     * BLACKLIST + ADD), indicates to the user that no filter mode is enabled.
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
     * Creates an info tooltip peeking a relevant action. If no filter list action is detected (no keywords such as
     * BLACKLIST + ADD), creates a peek info tooltip containing the last unfiltered action.
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
     * Any tooltip that needs to be flushed is collected in a list in order to avoid concurrent modification.
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
        tooltips.clear();
        tooltips = null;
        tooltipsToFlush.clear();
        tooltipsToFlush = null;
        fixedLocationTooltip = null;
        infoTooltip = null;
        lastTooltipText = null;
        lastUnfilteredTooltipText = null;
        lastInfoTooltipText = null;
        inputListener = null;
    }

    public void clear()
    {
        tooltips.clear();
        tooltipsToFlush.clear();
        fixedLocationTooltip = null;
    }
}
