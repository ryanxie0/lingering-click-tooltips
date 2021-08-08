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
package ryanxie0.runelite.plugin.lingeringclicktooltips.colors;

import java.awt.Color;

import static ryanxie0.runelite.plugin.lingeringclicktooltips.colors.LingeringClickTooltipsTextColorManager.*;
import static ryanxie0.runelite.plugin.lingeringclicktooltips.colors.LingeringClickTooltipsTextColorConstants.*;
import static ryanxie0.runelite.plugin.lingeringclicktooltips.colors.LingeringClickTooltipsColorsUtil.*;
import static ryanxie0.runelite.plugin.lingeringclicktooltips.filtering.LingeringClickTooltipsFilterMode.*;
import static ryanxie0.runelite.plugin.lingeringclicktooltips.filtering.LingeringClickTooltipsFilteringUtil.*;

public class LingeringClickTooltipsColors {

    /**
     * @param tooltipText the tooltip text to which custom text color will be applied
     * @param overrideMenuColors whether the native client menu colors should be overridden
     * @return tooltipText with appropriate color tags, if applicable
     */
    public static String applyCustomTextColor(String tooltipText, boolean overrideMenuColors)
    {
        String tooltipTextColor = getColorTag(Color.WHITE); // defaults to white

        Color customTextColor = getColor(CUSTOM_TEXT_COLOR);
        if (customTextColor != null)
        {
            tooltipTextColor = getColorTag(customTextColor);
            if (overrideMenuColors)
            {
                tooltipText = removeTags(tooltipText);
            }
        }
        return tooltipTextColor + tooltipText;
    }

    /**
     * @param blockedClickText text containing blocked click keywords, e.g. BLOCKED_BY + BLACKLIST
     * @return the keywords in blockedClickText with proper formatting and color tags applied
     */
    public static String getBlockedClickTextWithColor(String blockedClickText)
    {
        String blockedClickTextWithColor = "";
        if (blockedClickText.contains(BYPASS))
        {
            blockedClickText = blockedClickText.substring(BYPASS.length());
            blockedClickTextWithColor += getColorTag(BYPASS) + BYPASS + " ";
        }
        else if (blockedClickText.contains(BLOCKED_BY))
        {
            blockedClickText = blockedClickText.substring(BLOCKED_BY.length());
            blockedClickTextWithColor += getColorTag(BLOCKED_BY) + BLOCKED_BY + " ";
        }

        if (blockedClickText.contains(BLACKLIST.toString()))
        {
            blockedClickTextWithColor += getColorTag(BLACKLIST_TEXT_COLOR) + BLACKLIST;
        }
        else if (blockedClickText.contains(WHITELIST.toString()))
        {
            blockedClickTextWithColor += getColorTag(WHITELIST_TEXT_COLOR) + WHITELIST;
        }

        blockedClickTextWithColor += getColorTag(Color.WHITE) + ": ";

        return blockedClickTextWithColor;
    }

    /**
     * @param infoTooltipText the info tooltip text to which color will be applied
     * @param isPeek whether the method was called from a peek action, used for filter list actions
     * @return infoTooltipText with appropriate formatting and color tags, if applicable
     */
    public static String applyInfoTooltipTextColor(String infoTooltipText, boolean isPeek)
    {
        String hideModeTooltipWithTextColor = getHideModeTooltipTextWithColor(infoTooltipText);
        if (!hideModeTooltipWithTextColor.isEmpty())
        {
            return hideModeTooltipWithTextColor;
        }

        String filterListActionTooltipTextWithColor = getFilterListActionTooltipTextWithColor(infoTooltipText, isPeek);
        if (!filterListActionTooltipTextWithColor.isEmpty())
        {
            return filterListActionTooltipTextWithColor;
        }

        String peekLastClickTooltipTextWithColor = getPeekLastClickTooltipTextWithColor(infoTooltipText);
        if (!peekLastClickTooltipTextWithColor.isEmpty())
        {
            return peekLastClickTooltipTextWithColor;
        }

        return infoTooltipText;
    }

    /**
     * @param tooltipText the tooltip text which may contain hide mode keywords
     * @return tooltipText with color tags applied, empty string if N/A
     */
    private static String getHideModeTooltipTextWithColor(String tooltipText)
    {
        String hideModeTooltipTextWithColor = "";
        if (tooltipText.contains(TOOLTIPS))
        {
            if (tooltipText.contains(HIDDEN))
            {
                hideModeTooltipTextWithColor += getColorTag(Color.WHITE) + TOOLTIPS + " " + getColorTag(HIDDEN) + HIDDEN;
            }
            else if (tooltipText.contains(SHOWN))
            {
                hideModeTooltipTextWithColor += getColorTag(Color.WHITE) + TOOLTIPS + " " + getColorTag(SHOWN) + SHOWN;
            }
        }
        return hideModeTooltipTextWithColor;
    }

    /**
     * @param tooltipText the tooltip text from which filter list actions will be extracted
     * @param isPeek whether the method was called from a peek action
     * @return tooltipText prefixed with the filter list action, formatting and color tags applied, empty string if N/A
     */
    private static String getFilterListActionTooltipTextWithColor(String tooltipText, boolean isPeek)
    {
        String filterListTooltipTextColor = "";
        String filterListAction = extractFilterListAction(tooltipText);

        if (filterListAction.contains(BLACKLIST.toString()))
        {
            tooltipText = tooltipText.substring(BLACKLIST.toString().length());
            filterListTooltipTextColor += getColorTag(BLACKLIST_TEXT_COLOR) + BLACKLIST + " ";
        }
        else if (filterListAction.contains(WHITELIST.toString()))
        {
            tooltipText = tooltipText.substring(WHITELIST.toString().length());
            filterListTooltipTextColor += getColorTag(WHITELIST_TEXT_COLOR) + WHITELIST + " ";
        }
        else if (filterListAction.contains(NO_FILTER_MODE_ENABLED))
        {
            filterListTooltipTextColor += getColorTag(NO_FILTER_MODE_ENABLED_TEXT_COLOR);
        }

        if (filterListAction.contains(ADD))
        {
            tooltipText = tooltipText.substring(ADD.length());
            filterListTooltipTextColor += getColorTag(ADD) + ADD + (isPeek? "? " : "ED ");
        }
        else if (filterListAction.contains(REMOVE))
        {
            tooltipText = tooltipText.substring(REMOVE.length());
            filterListTooltipTextColor += getColorTag(REMOVE) + REMOVE + (isPeek? "? " : "D ");
        }

        return filterListTooltipTextColor.isEmpty()? "" : filterListTooltipTextColor + tooltipText;
    }


    /**
     * @param tooltipText the tooltip text which may contain a peek last click action
     * @return tooltipText prefixed with peek action keywords, formatting and color tags applied, empty string if N/A
     */
    private static String getPeekLastClickTooltipTextWithColor(String tooltipText)
    {
        String peekLastClickTooltipTextColor = "";
        if (tooltipText.contains(LAST_CLICK))
        {
            tooltipText = tooltipText.substring(LAST_CLICK.length());
            peekLastClickTooltipTextColor = getColorTag(LAST_CLICK_TEXT_COLOR) + LAST_CLICK + ": ";
        }
        return peekLastClickTooltipTextColor + tooltipText;
    }

    /**
     * Returns the appropriate background color from all the available sources. Defaults to the overlay background
     * color specified under the RuneLite settings.
     * @param tooltipText the tooltip text which may map to a pre-designated background color
     * @return the background color as selected by the logic
     */
    public static Color getTooltipBackgroundColor(String tooltipText)
    {
        tooltipText = removeTags(tooltipText); // color mappings should not include text color tags, only in scope

        Color filterListActionBackgroundColor = getFilterListActionBackgroundColor(tooltipText);
        if (filterListActionBackgroundColor != null)
        {
            return filterListActionBackgroundColor;
        }

        Color peekLastClickBackgroundColor = getPeekLastClickBackgroundColor(tooltipText);
        if (peekLastClickBackgroundColor != null)
        {
            return peekLastClickBackgroundColor;
        }

        Color textMappedBackgroundColor = getColor(tooltipText);
        if (textMappedBackgroundColor != null)
        {
            return textMappedBackgroundColor;
        }

        Color customBackgroundColor = getColor(CUSTOM_BACKGROUND_COLOR);
        if (customBackgroundColor != null)
        {
            return customBackgroundColor;
        }

        return getColor(OVERLAY_BACKGROUND_COLOR);
    }

    /**
     * @param tooltipText the tooltip text which may contain filter list action keywords
     * @return the background color matching the filter mode, returns null if N/A
     */
    private static Color getFilterListActionBackgroundColor(String tooltipText)
    {
        String filterListAction = extractFilterListAction(tooltipText);
        if (filterListAction.contains(BLACKLIST.toString()))
        {
            return getColor(BLACKLIST_BACKGROUND_COLOR);
        }
        else if (filterListAction.contains(WHITELIST.toString()))
        {
            return getColor(WHITELIST_BACKGROUND_COLOR);
        }
        else if (filterListAction.contains(NO_FILTER_MODE_ENABLED))
        {
            return getColor(NO_FILTER_MODE_ENABLED_BACKGROUND_COLOR);
        }
        return null;
    }

    /**
     * @param tooltipText the tooltip text which may contain a peek last click action
     * @return the background color for peek last click action, returns null if N/A
     */
    private static Color getPeekLastClickBackgroundColor(String tooltipText)
    {
        if (tooltipText.contains(LAST_CLICK))
        {
            return getColor(LAST_CLICK_BACKGROUND_COLOR);
        }
        return null;
    }
}
