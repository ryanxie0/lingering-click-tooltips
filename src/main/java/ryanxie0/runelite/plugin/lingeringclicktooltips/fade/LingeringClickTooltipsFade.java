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
import ryanxie0.runelite.plugin.lingeringclicktooltips.wrapper.LingeringClickTooltipsWrapper;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static ryanxie0.runelite.plugin.lingeringclicktooltips.fade.LingeringClickTooltipsFadeUtil.*;

public class LingeringClickTooltipsFade {

    /**
     * Calculates the alpha value for a tooltip. It is dependent on which phase of fade the tooltip is in.
     * @param tooltip the tooltip for which fade will be calculated
     * @param config the configuration settings for the plugin
     * @return the tooltip alpha modifier which will be used during rendering
     */
    public static double calculateAlphaModifier(LingeringClickTooltipsWrapper tooltip, LingeringClickTooltipsConfig config)
    {
        double alphaModifier = getMaximumOpacity(config);

        Duration sinceCreation = Duration.between(tooltip.getTimeOfCreation(), Instant.now());

        int fadeInPeriodThreshold = calculateFadeInPeriodThreshold(config, tooltip.isInfoTooltip());
        int fadeOutPeriodThreshold = calculateFadeoutPeriodThreshold(config, tooltip.isInfoTooltip());

        if (isInFadeInPeriod(sinceCreation.toMillis(), fadeInPeriodThreshold))
        {
            alphaModifier = applyFadeIn(alphaModifier, sinceCreation.toMillis(), fadeInPeriodThreshold);
        }
        else if (isInFadeoutPeriod(sinceCreation.toMillis(), fadeOutPeriodThreshold))
        {
            Duration totalTooltipDuration = tooltip.getTooltipDuration();
            long timeRemaining = totalTooltipDuration.minus(sinceCreation).toMillis();
            long fadeoutPeriod = totalTooltipDuration.toMillis() - fadeOutPeriodThreshold;
            alphaModifier = applyFadeout(alphaModifier, timeRemaining, fadeoutPeriod);
        }

        return alphaModifier;
    }

    /**
     * Used for refreshing an info tooltip in the case that its text did not change.
     * @param infoTooltip the info tooltip to refresh
     */
    public static void refreshInfoTooltip(LingeringClickTooltipsWrapper infoTooltip, LingeringClickTooltipsConfig config)
    {
        if (infoTooltip.isFaded())
        {   // if faded, must reset isFaded and set time of creation to now
            infoTooltip.setFaded(false);
            infoTooltip.setTimeOfCreation(Instant.now());
            return;
        }

        Duration sinceCreation = Duration.between(infoTooltip.getTimeOfCreation(), Instant.now());
        int fadeInPeriodThreshold = calculateFadeInPeriodThreshold(config, infoTooltip.isInfoTooltip());
        if (!isInFadeInPeriod(sinceCreation.toMillis(), fadeInPeriodThreshold))
        {   // if not yet faded and not fading in, resets time of creation excluding the fade-in period
            infoTooltip.setTimeOfCreation(Instant.now().minus(fadeInPeriodThreshold, ChronoUnit.MILLIS));
        }
    }

    /**
     * Resets the time of creation of a tooltip wrapper to now.
     * @param tooltip the tooltip to reset
     */
    public static void refreshTooltipTimeOfCreation(LingeringClickTooltipsWrapper tooltip)
    {
        tooltip.setTimeOfCreation(Instant.now());
    }
}
