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

import net.runelite.client.config.RuneLiteConfig;
import ryanxie0.runelite.plugin.lingeringclicktooltips.LingeringClickTooltipsConfig;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class LingeringClickTooltipsTextToColorMapper {

    public static final String TOOLTIPS_HIDDEN = "Tooltips HIDDEN";
    public static final String TOOLTIPS_SHOWN = "Tooltips SHOWN";
    public static final String DEFAULT_OVERLAY_BACKGROUND_COLOR = "runeLiteConfig.overlayBackgroundColor";
    public static final String CUSTOM_BACKGROUND_COLOR = "customBackgroundColor";

    private static Map<String, Color> textToColorMap;

    public LingeringClickTooltipsTextToColorMapper(RuneLiteConfig runeLiteConfig, LingeringClickTooltipsConfig config)
    {
        textToColorMap = new HashMap<>();
        initialize(runeLiteConfig, config);
    }

    private void initialize(RuneLiteConfig runeLiteConfig, LingeringClickTooltipsConfig config)
    {
        textToColorMap.put(TOOLTIPS_SHOWN, Color.decode("#5252FF")); // Lighter blue
        textToColorMap.put(TOOLTIPS_HIDDEN, Color.decode("#FF5252")); // Lighter red
        update(runeLiteConfig);
        update(config);
    }

    public void destroy()
    {
        textToColorMap.clear();
        textToColorMap = null;
    }

    public static Color getColor(String text)
    {
        return textToColorMap.get(text);
    }

    public void update(RuneLiteConfig runeLiteConfig)
    {
        textToColorMap.put(DEFAULT_OVERLAY_BACKGROUND_COLOR, runeLiteConfig.overlayBackgroundColor());
    }

    public void update(LingeringClickTooltipsConfig config)
    {
        updateTextToColorMap(config.useCustomBackgroundColor(), CUSTOM_BACKGROUND_COLOR, config.customBackgroundColor());
    }

    private void updateTextToColorMap(boolean configEnabled, String text, Color color)
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
}
