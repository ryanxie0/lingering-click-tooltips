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

import net.runelite.client.util.Text;

import java.util.List;

import static ryanxie0.runelite.plugin.lingeringclicktooltips.color.LingeringClickTooltipsColorConstants.*;
import static ryanxie0.runelite.plugin.lingeringclicktooltips.filtering.LingeringClickTooltipsFilterMode.*;

public class LingeringClickTooltipsFilteringUtil {

    /**
     * @param tooltipText the tooltip text to inspect for filter list action keywords
     * @return the appropriate filter list action (e.g. BLACKLIST + ADD), returns empty string if none apply
     */
    public static String extractFilterListAction(String tooltipText)
    {
        String filterListAction = "";

        if (tooltipText.contains(BLACKLIST.toString()))
        {
            filterListAction += BLACKLIST.toString();
        }
        else if (tooltipText.contains(WHITELIST.toString()))
        {
            filterListAction += WHITELIST.toString();
        }
        else if (tooltipText.contains(NO_FILTER_MODE_ENABLED))
        {
            filterListAction += NO_FILTER_MODE_ENABLED;
        }

        if (!filterListAction.isEmpty()) // for an added layer of specificity
        {
            if (tooltipText.contains(ADD))
            {
                filterListAction += ADD;
            }
            else if (tooltipText.contains(REMOVE))
            {
                filterListAction += REMOVE;
            }
        }

        return filterListAction;
    }

    /**
     * @param text the text from which tags will be stripped
     * @return text with all tags such as <col=ffffff> (a color tag specifying white text) removed
     */
    public static String removeTags(String text)
    {
        return Text.removeTags(text);
    }

    /**
     * @param csvString the string containing csv
     * @return csv converted to a list
     */
    public static List<String> csvToList(String csvString)
    {
        return Text.fromCSV(csvString);
    }

    /**
     * @param list the list to convert to csv
     * @return string containing csv
     */
    public static String listToCsv(List<String> list)
    {
        return Text.toCSV(list);
    }
}
