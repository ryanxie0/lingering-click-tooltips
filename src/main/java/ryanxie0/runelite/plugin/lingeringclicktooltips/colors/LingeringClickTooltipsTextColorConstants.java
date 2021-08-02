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

import static ryanxie0.runelite.plugin.lingeringclicktooltips.filtering.LingeringClickTooltipsFilterMode.*;
import static ryanxie0.runelite.plugin.lingeringclicktooltips.colors.LingeringClickTooltipsColorsUtil.hexToColor;

public class LingeringClickTooltipsTextColorConstants {

    // config key names
    public static final String OVERLAY_BACKGROUND_COLOR = "overlayBackgroundColor";
    public static final String USE_CUSTOM_BACKGROUND_COLOR = "useCustomBackgroundColor";
    public static final String CUSTOM_BACKGROUND_COLOR = "customBackgroundColor";
    public static final String USE_CUSTOM_TEXT_COLOR = "useCustomTextColor";
    public static final String CUSTOM_TEXT_COLOR = "customTextColor";

    // color hex codes
    private static final String TRANSPARENT_HEX = "0x00000000"; // fully transparent
    private static final String LIGHT_GREEN_HEX = "0x55ED55"; // lighter and less intense green
    private static final String LIGHT_RED_HEX = "0xED5555"; // lighter and less intense red
    private static final String DARKER_GRAY_HEX = "0xC8202020"; // between Color.BLACK and Color.DARK_GRAY, alpha = 200
    private static final String MEDIUM_GRAY_HEX = "0xC8606060"; // between Color.DARK_GRAY AND Color.GRAY, alpha = 200
    private static final String LIGHTER_GRAY_HEX = "0xE0E0E0"; // between Color.LIGHT_GRAY and Color.WHITE

    // colors derived from hex codes
    public static final Color TRANSPARENT = hexToColor(TRANSPARENT_HEX);
    public static final Color LIGHT_GREEN = hexToColor(LIGHT_GREEN_HEX);
    public static final Color LIGHT_RED = hexToColor(LIGHT_RED_HEX);
    public static final Color DARKER_GRAY = hexToColor(DARKER_GRAY_HEX);
    public static final Color MEDIUM_GRAY = hexToColor(MEDIUM_GRAY_HEX);
    public static final Color LIGHTER_GRAY = hexToColor(LIGHTER_GRAY_HEX);

    // keywords to indicate where to apply the color
    public static final String BACKGROUND_COLOR = ".backgroundColor";
    public static final String TEXT_COLOR = ".textColor";

    // keywords used for text color mappings/building tooltip text
    public static final String TOOLTIPS = "Tooltips";
    public static final String HIDDEN = "HIDDEN";
    public static final String SHOWN = "SHOWN";
    public static final String TOOLTIPS_HIDDEN = TOOLTIPS + " " + HIDDEN;
    public static final String TOOLTIPS_SHOWN = TOOLTIPS + " " + SHOWN;
    public static final String BLACKLIST_BACKGROUND_COLOR = BLACKLIST + BACKGROUND_COLOR;
    public static final String WHITELIST_BACKGROUND_COLOR = WHITELIST + BACKGROUND_COLOR;
    public static final String BLACKLIST_TEXT_COLOR = BLACKLIST + TEXT_COLOR;
    public static final String WHITELIST_TEXT_COLOR = WHITELIST + TEXT_COLOR;
    public static final String ADD = "ADD";
    public static final String REMOVE = "REMOVE";
    public static final String NO_FILTER_MODE_ENABLED = "No filter mode enabled";
    public static final String NO_FILTER_MODE_ENABLED_BACKGROUND_COLOR = NO_FILTER_MODE_ENABLED + BACKGROUND_COLOR;
    public static final String NO_FILTER_MODE_ENABLED_TEXT_COLOR = NO_FILTER_MODE_ENABLED + TEXT_COLOR;
    public static final String LAST_CLICK = "Last click";
    public static final String LAST_CLICK_BACKGROUND_COLOR = LAST_CLICK + BACKGROUND_COLOR;
    public static final String LAST_CLICK_TEXT_COLOR = LAST_CLICK + TEXT_COLOR;
}
