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
package ryanxie0.runelite.plugin.lingeringclicktooltips.components.wrapper;

import net.runelite.api.Client;
import ryanxie0.runelite.plugin.lingeringclicktooltips.LingeringClickTooltipsConfig;
import ryanxie0.runelite.plugin.lingeringclicktooltips.components.alpha.AlphaTooltipComponent;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.time.Instant;

import static ryanxie0.runelite.plugin.lingeringclicktooltips.LingeringClickTooltipsLocation.*;

public class LingeringClickTooltipsWrapperUtil {

    /**
     * @param tooltipText the text of the tooltip, includes color tags and the full action as seen by the user
     * @param location the location of the tooltip, includes config offset but NOT clamping (clamp is handled by the overlay as it requires Dimension)
     * @param backgroundColor the background color of the tooltip as chosen by the various different sources, see LingeringClickTooltipsColorUtil.java
     * @param isInfoTooltip whether the tooltip is an info tooltip, used later for determining adjusted duration, fadeout, permanence bypass, etc.
     * @return the tooltip wrapper which is used to construct a renderable component
     */
    public static LingeringClickTooltipsWrapper buildTooltipWrapper(String tooltipText, Point location, Color backgroundColor, boolean isInfoTooltip)
    {
        LingeringClickTooltipsWrapper tooltipWrapper = new LingeringClickTooltipsWrapper();
        tooltipWrapper.setFaded(false);
        tooltipWrapper.setInfoTooltip(isInfoTooltip);
        tooltipWrapper.setClamped(false);
        tooltipWrapper.setText(tooltipText);
        tooltipWrapper.setBackgroundColor(backgroundColor);
        tooltipWrapper.setTimeOfCreation(Instant.now());
        tooltipWrapper.setLocation(location);
        return tooltipWrapper;
    }

    /**
     * This method returns tooltip text = option when target is empty (i.e. there was no target) or if target == option.
     * The latter case is meant to avoid confusing text for actions such as a use initiate, where the text would
     * otherwise appear as "Use Use".
     * @param option the menu option selected by the user, e.g. "Walk here"
     * @param target the menu target selected by the user, e.g. "Door" or "Tree"
     * @return the tooltip text produced by combining the option and the target
     */
    public static String getTooltipText(String option, String target)
    {
        String tooltipText = option + (target.equals("") || option.equals(target) ? "" : " " + target);
        tooltipText = tooltipText.replaceAll("\\s+$", ""); // trim any trailing whitespace
        return tooltipText;
    }

    /**
     * This method returns null for cases where location is not necessary, i.e. tooltips handled by the TooltipManager.
     * A null location is later used to avoid further unnecessary calculations regarding tooltip location
     * @param location the exact location of the click input by the user
     * @param config the configuration settings for the plugin
     * @return location adjusted according to the offsets specified by the user in the config
     */
    public static Point getOffsetLocation(Point location, LingeringClickTooltipsConfig config)
    {
        if (config.tooltipLocation() == ANCHORED || config.trackerMode()) // no need to calculate location for anchored tooltips
        {
            return null;
        }
        location.translate(config.tooltipXOffset(), config.tooltipYOffset());
        return location;
    }

    /**
     * This method ensures that tooltips do not render offscreen/cutoff by providing the location at which the tooltip
     * can render while remaining within the specified boundaries. It applies only to tooltips which are not
     * anchored, i.e. handled by TooltipManager.
     * @param dimension the dimension of the component, contains the necessary height and width of the tooltips
     * @param location the location the component would like to render at
     * @param client the RuneLite client API, contains the necessary height and width of the canvas
     * @param config the configuration settings for the plugin
     * @return the adjusted location of the tooltip accounting for component dimension/location, canvas dimensions, and padding
     */
    public static Point getClampedLocation(Dimension dimension, Point location, Client client, LingeringClickTooltipsConfig config)
    {
        int clampedX = location.x;
        int clampedY = location.y;

        int xPadding = config.clampXPadding();
        int yPadding = config.clampYPadding();

        int xMax = client.getCanvasWidth();
        int yMax = client.getCanvasHeight();

        if (clampedX < xPadding)
        {
            clampedX = xPadding;
        }
        else if (clampedX + dimension.width + xPadding > xMax)
        {
            clampedX = xMax - dimension.width - xPadding;
        }

        if (clampedY < yPadding)
        {
            clampedY = yPadding;
        }
        else if (clampedY + dimension.height + yPadding > yMax)
        {
            clampedY = yMax - dimension.height - yPadding;
        }

        return new Point(clampedX, clampedY);
    }

    /**
     * Used to build the renderable component, called once per new tooltip wrapper. It does not need to set the alpha
     * modifier because the rendering code calculates and sets the alpha just before calling render.
     * @param tooltip the tooltip wrapper which is used to construct a renderable component
     * @param client the RuneLite client API
     */
    public static void buildAlphaTooltipComponent(LingeringClickTooltipsWrapper tooltip, Client client)
    {
        AlphaTooltipComponent alphaTooltipComponent = new AlphaTooltipComponent();
        alphaTooltipComponent.setText(tooltip.getText());
        alphaTooltipComponent.setModIcons(client.getModIcons());
        alphaTooltipComponent.setBackgroundColor(tooltip.getBackgroundColor());
        if (tooltip.getLocation() != null)
        {
            alphaTooltipComponent.setPosition(tooltip.getLocation());
            alphaTooltipComponent.setPreferredLocation(tooltip.getLocation());
        }
        tooltip.setRenderableComponent(alphaTooltipComponent);
    }

    /**
     * Used for refreshing an info tooltip in the case that its text did not change.
     * @param infoTooltip the info tooltip to refresh
     */
    public static void refreshInfoTooltip(LingeringClickTooltipsWrapper infoTooltip)
    {
        infoTooltip.setFaded(false);
        infoTooltip.setTimeOfCreation(Instant.now());
    }
}
