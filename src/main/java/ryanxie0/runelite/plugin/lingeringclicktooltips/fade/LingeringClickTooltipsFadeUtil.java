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
package ryanxie0.runelite.plugin.lingeringclicktooltips.fade;

import ryanxie0.runelite.plugin.lingeringclicktooltips.LingeringClickTooltipsConfig;

import static ryanxie0.runelite.plugin.lingeringclicktooltips.fade.LingeringClickTooltipsFadeConstants.*;

public class LingeringClickTooltipsFadeUtil {

    /**
     * @return the tooltip start opacity, adjusted by light mode if applicable
     */
    public static double getMaximumOpacity(LingeringClickTooltipsConfig config)
    {
        if (config.lightMode())
        {
            return config.maximumOpacity() / 100.0 * LIGHT_MODE_MULTIPLIER;
        }
        else
        {
            return config.maximumOpacity() / 100.0;
        }
    }

    /**
     * Returns the fade-in period threshold of a tooltip.
     * @param config the configuration settings for the plugin
     * @param isInfoTooltip whether the tooltip is an info tooltip
     * @return the number of ms since creation of the tooltip at which fade-in will complete
     */
    public static int calculateFadeInPeriodThreshold(LingeringClickTooltipsConfig config, boolean isInfoTooltip)
    {
        if (isInfoTooltip)
        {
            return (int) (config.tooltipDuration() * config.tooltipFadeIn() / 100.0 / INFO_TOOLTIP_MULTIPLIER);
        }
        else if (config.fastMode())
        {
            return (int) (config.tooltipDuration() * config.tooltipFadeIn() / 100.0 / FAST_MODE_MULTIPLIER);
        }
        else
        {
            return (int) (config.tooltipDuration() * config.tooltipFadeIn() / 100.0);
        }
    }

    /**
     * Returns the fadeout period threshold of a tooltip.
     * @param config the configuration settings for the plugin
     * @param isInfoTooltip whether the tooltip is an info tooltip
     * @return the number of ms since creation of the tooltip at which fadeout will begin
     */
    public static int calculateFadeoutPeriodThreshold(LingeringClickTooltipsConfig config, boolean isInfoTooltip)
    {
        int fadeoutPeriodThreshold = config.tooltipDuration();
        if (isInfoTooltip)
        {
            return (int) (fadeoutPeriodThreshold * INFO_TOOLTIP_MULTIPLIER);
        }
        else if (config.fastMode())
        {
            return (int) (fadeoutPeriodThreshold / FAST_MODE_MULTIPLIER);
        }
        return calculateFadeInPeriodThreshold(config, isInfoTooltip) + fadeoutPeriodThreshold;
    }

    /**
     * Modifies the alphaModifier during the fade-in period.
     * @param alphaModifier the alpha modifier to which fade-in will be applied
     * @param sinceCreation the time since the creation of the tooltip
     * @param fadeInPeriodThreshold the number of ms since creation of the tooltip at which fade-in will complete
     * @return alphaModifier adjusted for the fade-in period
     */
    public static double applyFadeIn(double alphaModifier, long sinceCreation, long fadeInPeriodThreshold)
    {
        return alphaModifier * sinceCreation / fadeInPeriodThreshold;
    }

    /**
     * Modifies the alphaModifier during the fadeout period.
     * @param alphaModifier the alpha modifier to which fadeout will be applied
     * @param timeRemaining the time remaining on the tooltip duration
     * @param fadeoutPeriod the total length of the fadeout period
     * @return alphaModifier adjusted for the fadeout period
     */
    public static double applyFadeout(double alphaModifier, long timeRemaining, long fadeoutPeriod)
    {
        return alphaModifier * timeRemaining / fadeoutPeriod;
    }

    /**
     * Returns whether a tooltip is currently in the fade-in period.
     * @param sinceCreation the time since the creation of the tooltip
     * @param fadeInPeriodThreshold the number of ms since creation of the tooltip at which fade-in will complete
     * @return whether the tooltip is currently in the fade-in period
     */
    public static boolean isInFadeInPeriod(long sinceCreation, long fadeInPeriodThreshold)
    {
        return sinceCreation < fadeInPeriodThreshold;
    }

    /**
     * Returns whether a tooltip is currently in the fadeout period.
     * @param sinceCreation the time since the creation of the tooltip
     * @param fadeoutPeriodThreshold the number of ms since creation of the tooltip at which fadeout will begin
     * @return whether the tooltip is currently in the fade-in period
     */
    public static boolean isInFadeoutPeriod(long sinceCreation, long fadeoutPeriodThreshold)
    {
        return sinceCreation > fadeoutPeriodThreshold;
    }
}
