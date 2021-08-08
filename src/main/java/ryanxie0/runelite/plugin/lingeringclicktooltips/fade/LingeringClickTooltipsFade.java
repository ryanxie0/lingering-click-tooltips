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
        double alphaModifier = getTooltipStartOpacity(config);

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
