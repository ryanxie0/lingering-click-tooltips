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

import net.runelite.client.config.RuneLiteConfig;
import ryanxie0.runelite.plugin.lingeringclicktooltips.LingeringClickTooltipsConfig;

import javax.inject.Inject;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import static ryanxie0.runelite.plugin.lingeringclicktooltips.colors.LingeringClickTooltipsTextColorConstants.*;

public class LingeringClickTooltipsTextColorManager {

    @Inject
    private RuneLiteConfig runeLiteConfig;

    @Inject
    private LingeringClickTooltipsConfig config;

    private static Map<String, Color> textToColorMap;

    public void updateFromConfig(String configKey)
    {
        switch (configKey)
        {
            case USE_CUSTOM_BACKGROUND_COLOR:
                modifyTextToColorMap(config.useCustomBackgroundColor(), CUSTOM_BACKGROUND_COLOR, config.customBackgroundColor());
                break;
            case USE_CUSTOM_TEXT_COLOR:
                modifyTextToColorMap(config.useCustomTextColor(), CUSTOM_TEXT_COLOR, config.customTextColor());
                break;
            case OVERLAY_BACKGROUND_COLOR:
                modifyTextToColorMap(true, OVERLAY_BACKGROUND_COLOR, runeLiteConfig.overlayBackgroundColor());
            default:
                break;
        }
    }

    private void modifyTextToColorMap(boolean configEnabled, String text, Color color)
    {
        if (configEnabled)
        {
            textToColorMap.put(text, color);
        }
        else
        {
            textToColorMap.remove(text);
        }
    }

    public static Color getColor(String text)
    {
        return textToColorMap.get(text);
    }

    public void initialize()
    {
        initializeTextToColorMap();
    }

    public void destroy()
    {
        textToColorMap.clear();
        textToColorMap = null;
    }

    private void initializeTextToColorMap()
    {
        textToColorMap = new HashMap<>();
        textToColorMap.put(TOOLTIPS_SHOWN, TRANSPARENT);
        textToColorMap.put(TOOLTIPS_HIDDEN, TRANSPARENT);
        textToColorMap.put(SHOWN, LIGHT_GREEN);
        textToColorMap.put(HIDDEN, LIGHT_RED);
        textToColorMap.put(BLACKLIST_BACKGROUND_COLOR, DARKER_GRAY);
        textToColorMap.put(WHITELIST_BACKGROUND_COLOR, MEDIUM_GRAY);
        textToColorMap.put(BLACKLIST_TEXT_COLOR, Color.YELLOW);
        textToColorMap.put(WHITELIST_TEXT_COLOR, Color.ORANGE);
        textToColorMap.put(ADD, LIGHT_GREEN);
        textToColorMap.put(REMOVE, LIGHT_RED);
        textToColorMap.put(NO_FILTER_MODE_ENABLED_BACKGROUND_COLOR, TRANSPARENT);
        textToColorMap.put(NO_FILTER_MODE_ENABLED_TEXT_COLOR, LIGHT_RED);
        textToColorMap.put(LAST_CLICK_BACKGROUND_COLOR, TRANSPARENT);
        textToColorMap.put(LAST_CLICK_TEXT_COLOR, LIGHTER_GRAY);
        textToColorMap.put(BLOCKED_BY, LIGHT_RED);
        textToColorMap.put(BYPASS, LIGHT_GREEN);

        modifyTextToColorMap(config.useCustomBackgroundColor(), CUSTOM_BACKGROUND_COLOR, config.customBackgroundColor());
        modifyTextToColorMap(config.useCustomTextColor(), CUSTOM_TEXT_COLOR, config.customTextColor());

        modifyTextToColorMap(true, OVERLAY_BACKGROUND_COLOR, runeLiteConfig.overlayBackgroundColor());
    }
}
