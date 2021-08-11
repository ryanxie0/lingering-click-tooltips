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
package ryanxie0.runelite.plugin.lingeringclicktooltips.filtering;

import ryanxie0.runelite.plugin.lingeringclicktooltips.LingeringClickTooltipsConfig;
import java.util.LinkedList;
import java.util.List;

import static ryanxie0.runelite.plugin.lingeringclicktooltips.color.LingeringClickTooltipsColorConstants.*;
import static ryanxie0.runelite.plugin.lingeringclicktooltips.filtering.LingeringClickTooltipsFilterMode.*;
import static ryanxie0.runelite.plugin.lingeringclicktooltips.filtering.LingeringClickTooltipsFilteringUtil.*;
import static ryanxie0.runelite.plugin.lingeringclicktooltips.filtering.LingeringClickTooltipsFilteringConstants.*;
import static ryanxie0.runelite.plugin.lingeringclicktooltips.filtering.LingeringClickTooltipsTrivialClicksManager.*;

public class LingeringClickTooltipsFiltering {

    /**
     * @param tooltipText the tooltip text to check for processing
     * @param isHide whether the plugin is currently in hide mode, where tooltips do not show
     * @param isCtrlPressed whether the CTRL key is currently held down, may show tooltips normally during hide mode
     * @param config the configuration settings for the plugin
     * @return whether the click should render a tooltip
     */
    public static boolean shouldRenderTooltip(String tooltipText, boolean isHide, boolean isCtrlPressed, LingeringClickTooltipsConfig config)
    {
        String filterableText = removeTags(tooltipText); // filtering should never include tags

        boolean isHideMode = isHideMode(isHide, isCtrlPressed, config.ctrlTogglesHide());
        boolean isTrivialClick = isTrivialClick(filterableText, config);
        boolean isFilteredByList = isFilteredByList(filterableText, config);
        boolean isBlockedClickTooltip = isBlockedClickTooltip(filterableText);
        boolean isHiddenBlockedClickTooltip = isBlockedClickTooltip && !config.showBlockedClicks();

        return !isHideMode && !isTrivialClick && !(isFilteredByList && !isBlockedClickTooltip) && !isHiddenBlockedClickTooltip;
    }

    /**
     * @param isHide the current hide mode state
     * @param isCtrlPressed whether the CTRL key is currently pressed
     * @param ctrlTogglesHide whether the CTRL key can toggle hide mode while pressed
     * @return whether tooltips are currently hidden
     */
    public static boolean isHideMode(boolean isHide, boolean isCtrlPressed, boolean ctrlTogglesHide)
    {
        return isHide && !(isCtrlPressed && ctrlTogglesHide);
    }

    /**
     * @param tooltipText the tooltip text to check for triviality
     * @param config the configuration settings for the plugin
     * @return whether tooltipText is a trivial click based on the current config (or a default trivial click)
     */
    public static boolean isTrivialClick(String tooltipText, LingeringClickTooltipsConfig config)
    {
        if (defaultContains(tooltipText))
        {
            return true;
        }
        else if (config.hideTrivialClicks() && !config.trackerMode())
        {
            if (tooltipText.contains(WALK_HERE) && tooltipText.length() > WALK_HERE.length())
            {
                return configurableContains(WALK_HERE_WITH_TARGET);
            }
            else if ((tooltipText.contains(ACTIVATE) || tooltipText.contains(DEACTIVATE)) && !tooltipText.contains(QUICK_PRAYERS))
            {
                return configurableContains(TOGGLE_PANEL_PRAYER);
            }
            else return configurableContains(tooltipText);
        }
        else
        {
            return false;
        }
    }

    /**
     * @param tooltipText the tooltip text to check for filtering
     * @param config the configuration settings for the plugin
     * @return whether tooltipText is filtered based on the current filter mode
     */
    public static boolean isFilteredByList(String tooltipText, LingeringClickTooltipsConfig config)
    {
        if (config.filterMode() == BLACKLIST)
        {
            List<String> blacklist = csvToList(config.blacklist());
            return blacklist.contains(tooltipText);
        }
        else if (config.filterMode() == WHITELIST)
        {
            List<String> whitelist = csvToList(config.whitelist());
            return !whitelist.contains(tooltipText);
        }
        else
        {
            return false;
        }
    }

    /**
     * @param tooltipText text which may contain blocked click keywords
     * @return whether tooltipText is for a blocked or bypass click
     */
    public static boolean isBlockedClickTooltip(String tooltipText)
    {
        return (tooltipText.contains(BYPASS) || tooltipText.contains(BLOCKED_BY))
            && (tooltipText.contains(BLACKLIST.toString()) || tooltipText.contains(WHITELIST.toString()) || tooltipText.contains(SHIFT));
    }

    /**
     * Updates the appropriate filter list, then indicates the action by returning a String with keywords.
     * @param config the configuration settings for the plugin
     * @param tooltipText the last tooltip text detected by the plugin, including all trivial clicks
     * @param isPeek whether the method was called from a peek action
     * @return tooltipText prefixed with the appropriate filter list action keywords
     */
    public static String updateFilterLists(LingeringClickTooltipsConfig config, String tooltipText, boolean isPeek)
    {
        String infoTooltipText = "";
        if (tooltipText == null || tooltipText.isEmpty())
        {
            return infoTooltipText;
        }
        LingeringClickTooltipsFilterMode filterMode = config.filterMode();
        String filterableText = removeTags(tooltipText);
        if (filterMode == BLACKLIST)
        {
            List<String> blacklist = new LinkedList<>(csvToList(config.blacklist()));
            if (blacklist.contains(filterableText))
            {
                blacklist.remove(filterableText);
                infoTooltipText += BLACKLIST + REMOVE + tooltipText;
            }
            else
            {
                blacklist.add(filterableText);
                infoTooltipText += BLACKLIST + ADD + tooltipText;
            }

            if (!isPeek) // peek actions do not commit changes to config
            {
                config.setBlacklist(listToCsv(blacklist));
            }
        }
        else if (filterMode == WHITELIST)
        {
            List<String> whitelist = new LinkedList<>(csvToList(config.whitelist()));
            if (whitelist.contains(filterableText))
            {
                whitelist.remove(filterableText);
                infoTooltipText += WHITELIST + REMOVE + tooltipText;
            }
            else
            {
                whitelist.add(filterableText);
                infoTooltipText += WHITELIST + ADD + tooltipText;
            }

            if (!isPeek) // peek actions do not commit changes to config
            {
                config.setWhitelist(listToCsv(whitelist));
            }
        }
        else if (filterMode == NONE)
        {
            infoTooltipText += NO_FILTER_MODE_ENABLED;
        }
        return infoTooltipText;
    }
}
