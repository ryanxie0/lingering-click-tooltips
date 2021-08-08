package ryanxie0.runelite.plugin.lingeringclicktooltips.fade;

import ryanxie0.runelite.plugin.lingeringclicktooltips.LingeringClickTooltipsConfig;

public class LingeringClickTooltipsFadeUtil {

    /**
     * @return the tooltip start opacity, adjusted by light mode if applicable
     */
    public static double getTooltipStartOpacity(LingeringClickTooltipsConfig config)
    {
        if (config.lightMode())
        {
            return config.maximumOpacity() / 100.0 * 0.75;
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
            return (int) (config.tooltipDuration() / 1.5 * config.tooltipFadeIn() / 100.0);
        }
        else if (config.fastMode())
        {
            return (int) (config.tooltipDuration() / 2.0 * config.tooltipFadeIn() / 100.0);
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
            return (int) (fadeoutPeriodThreshold * 1.5);
        }
        else if (config.fastMode())
        {
            return (int) (fadeoutPeriodThreshold / 2.0);
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
