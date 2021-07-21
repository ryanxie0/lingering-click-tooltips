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
package ryanxie0.runelite.plugin.lingeringclicktooltips.util;

import net.runelite.client.util.Text;
import ryanxie0.runelite.plugin.lingeringclicktooltips.LingeringClickTooltipsConfig;
import java.awt.Point;
import java.awt.Color;
import java.time.Instant;

import static ryanxie0.runelite.plugin.lingeringclicktooltips.util.LingeringClickTooltipsTextToColorMapper.*;

public class LingeringClickTooltipsUtil {

    public static LingeringClickTooltipsWrapper buildTooltipWrapper(String tooltipText, boolean isInfoTooltip, Point location)
    {
        LingeringClickTooltipsWrapper tooltipWrapper = new LingeringClickTooltipsWrapper();
        tooltipWrapper.setFaded(false);
        tooltipWrapper.setInfoTooltip(isInfoTooltip);
        tooltipWrapper.setText(tooltipText);
        tooltipWrapper.setBackgroundColor(getTooltipBackgroundColor(tooltipText));
        tooltipWrapper.setTime(Instant.now());
        tooltipWrapper.setLocation(location);
        return tooltipWrapper;
    }

    public static String getTooltipText(String optionTags, String targetTags)
    {
        String option = Text.removeTags(optionTags);
        String target = Text.removeTags(targetTags);
        String tooltipText;

        if (option.equals(target))
        {
            tooltipText = option;
        }
        else {
            tooltipText = option + " " + target;
        }

        tooltipText = tooltipText.replaceAll("\\s+$", ""); // trim any trailing whitespace

        return tooltipText;
    }

    private static Color getTooltipBackgroundColor(String tooltipText)
    {
        Color tooltipTextMappedColor = LingeringClickTooltipsTextToColorMapper.getColor(tooltipText);
        Color customBackgroundColor = LingeringClickTooltipsTextToColorMapper.getColor(CUSTOM_BACKGROUND_COLOR);
        if (tooltipTextMappedColor != null)
        {
            return tooltipTextMappedColor;
        }
        else if (customBackgroundColor != null)
        {
            return customBackgroundColor;
        }
        else
        {
            return LingeringClickTooltipsTextToColorMapper.getColor(DEFAULT_OVERLAY_BACKGROUND_COLOR);
        }
    }

    public static boolean shouldProcessClick(String tooltipText, boolean isHide, boolean isHotkeyPressed, LingeringClickTooltipsConfig config)
    {
        return !(LingeringClickTooltipsTrivialClicksMapper.defaultContains(tooltipText))
            && !(isTrivialClick(tooltipText, config.hideTrivialClicks()) && !config.trackerMode())
            && !(isHide && !(isHotkeyPressed && config.hotkeyHideToggle()));
    }

    private static boolean isTrivialClick(String tooltipText, boolean hideTrivialClicks)
    {
        return hideTrivialClicks && LingeringClickTooltipsTrivialClicksMapper.contains(tooltipText);
    }

    public static Point getOffsetLocation(Point location, LingeringClickTooltipsConfig config)
    {
        location.x += config.tooltipXOffset();
        location.y += config.tooltipYOffset();
        return location;
    }
}
