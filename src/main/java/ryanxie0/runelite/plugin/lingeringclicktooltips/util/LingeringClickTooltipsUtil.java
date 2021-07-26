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

import net.runelite.api.Client;
import ryanxie0.runelite.plugin.lingeringclicktooltips.LingeringClickTooltipsConfig;
import java.awt.Color;
import java.awt.Point;
import java.awt.Dimension;
import java.time.Instant;

import static ryanxie0.runelite.plugin.lingeringclicktooltips.util.LingeringClickTooltipsTextToColorMapper.*;

public class LingeringClickTooltipsUtil {

    public static LingeringClickTooltipsWrapper buildTooltipWrapper(String tooltipText, Point location, boolean isInfoTooltip)
    {
        LingeringClickTooltipsWrapper tooltipWrapper = new LingeringClickTooltipsWrapper();
        tooltipWrapper.setFaded(false);
        tooltipWrapper.setInfoTooltip(isInfoTooltip);
        tooltipWrapper.setClamped(false);
        tooltipWrapper.setText(tooltipText);
        tooltipWrapper.setBackgroundColor(getTooltipBackgroundColor(tooltipText));
        tooltipWrapper.setTimeOfCreation(Instant.now());
        tooltipWrapper.setLocation(location);
        return tooltipWrapper;
    }

    public static String getTooltipText(String option, String target)
    {
        String tooltipText = option + (target.equals("") || option.equals(target) ? "" : " " + target);
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

    public static String getTooltipTextWithColor(String tooltipText, LingeringClickTooltipsConfig config)
    {
        String tooltipTextWithColor = "";
        Color customTextColor = LingeringClickTooltipsTextToColorMapper.getColor(CUSTOM_TEXT_COLOR);
        if (customTextColor != null)
        {
            tooltipTextWithColor += "<col=" + colorToHex(customTextColor) + ">";
            if (config.overrideMenuOptionColor())
            {
                tooltipText = tooltipText.replaceAll("<col=[0-f]{0,6}>", "");
            }
        }
        tooltipTextWithColor += tooltipText;
        return tooltipTextWithColor;
    }

    private static String colorToHex(Color color)
    {
        return Integer.toHexString(color.getRGB()).substring(2);
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
        if (config.anchorTooltips() || config.trackerMode()) // no need to calculate location for anchored tooltips
        {
            return null;
        }
        location.translate(config.tooltipXOffset(), config.tooltipYOffset());
        return location;
    }

    public static Point getClampedLocation(Dimension dimension, Client client, Point location)
    {
        Point clampOffset = new Point(location.x, location.y);

        int xMin = client.getViewportXOffset();
        int xMax = client.getViewportWidth() + xMin;
        int yMin = client.getViewportYOffset();
        int yMax = client.getViewportHeight() + yMin;

        if (clampOffset.x < xMin)
        {
            clampOffset.x = xMin;
        }
        else if (clampOffset.x + dimension.width > xMax)
        {
            clampOffset.x = xMax - dimension.width;
        }

        if (clampOffset.y < yMin)
        {
            clampOffset.y = yMin;
        }
        else if (clampOffset.y + dimension.height > yMax)
        {
            clampOffset.y = yMax - dimension.height;
        }
        return clampOffset;
    }
}
