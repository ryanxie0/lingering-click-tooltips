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

import static ryanxie0.runelite.plugin.lingeringclicktooltips.colors.LingeringClickTooltipsTextColorManager.getColor;

public class LingeringClickTooltipsColorsUtil {

    /**
     * @param color the color for which a text tag will be obtained, e.g. <col=ffffff> for Color.WHITE
     * @return the color text tag generated for color
     */
    public static String getColorTag(Color color)
    {
        return "<col=" + colorToHex(color).substring(2) + ">";
    }

    /**
     * If this method receives text which maps to no color, returns an empty string.
     * @param mappedText the text which is mapped to a color using LingeringClickTooltipsTextToColorMapper.java
     * @return the color text tag generated for mappedText
     */
    public static String getColorTag(String mappedText)
    {
        Color mappedColor = getColor(mappedText);
        return mappedColor != null? getColorTag(mappedColor) : "";
    }

    /**
     * @param color the color to convert to hex
     * @return the hex value of the color
     */
    public static String colorToHex(Color color)
    {
        return Integer.toHexString(color.getRGB());
    }

    /**
     * Generates a Color from a hex string. Accepts both opaque colors and colors with alpha.
     * @param hex the hex string to convert to a Color, may be prefixed with "#" or "0x".
     * @return the Color converted from hex
     */
    public static Color hexToColor(String hex)
    {
        hex = hex.replaceAll("#", "");
        hex = hex.replaceAll("0x", "");

        String byte0 = "0x" + hex.substring(0, 2);
        String byte1 = "0x" + hex.substring(2, 4);
        String byte2 = "0x" + hex.substring(4, 6);

        if (hex.matches("[0-F]{6}"))
        {
            int r = Integer.decode(byte0);
            int g = Integer.decode(byte1);
            int b = Integer.decode(byte2);
            return new Color(r, g, b);
        }
        else if (hex.matches("[0-F]{8}"))
        {
            String byte3 = "0x" + hex.substring(6, 8);
            int a = Integer.decode(byte0);
            int r = Integer.decode(byte1);
            int g = Integer.decode(byte2);
            int b = Integer.decode(byte3);
            return new Color(r, g, b, a);
        }
        return null;
    }
}
