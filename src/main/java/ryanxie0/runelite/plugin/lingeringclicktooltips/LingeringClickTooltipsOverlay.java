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
package ryanxie0.runelite.plugin.lingeringclicktooltips;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import ryanxie0.runelite.plugin.lingeringclicktooltips.renderable.alpha.AlphaTooltipComponent;
import ryanxie0.runelite.plugin.lingeringclicktooltips.wrapper.LingeringClickTooltipsWrapper;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Queue;
import javax.inject.Inject;

import static ryanxie0.runelite.plugin.lingeringclicktooltips.LingeringClickTooltipsLocation.*;
import static ryanxie0.runelite.plugin.lingeringclicktooltips.fade.LingeringClickTooltipsFade.*;
import static ryanxie0.runelite.plugin.lingeringclicktooltips.fade.LingeringClickTooltipsFadeUtil.*;
import static ryanxie0.runelite.plugin.lingeringclicktooltips.wrapper.LingeringClickTooltipsWrapperUtil.*;

public class LingeringClickTooltipsOverlay extends Overlay
{
    @Inject
    private LingeringClickTooltipsPlugin plugin;

    @Inject
    private LingeringClickTooltipsConfig config;

    @Inject
    private Client client;

    @Inject
    private TooltipManager tooltipManager;

    private LingeringClickTooltipsInputListener inputListener;
    private LingeringClickTooltipsQueueManager queueManager;
    private LingeringClickTooltipsOverlayStateManager stateManager;

    @Inject
    LingeringClickTooltipsOverlay()
    {
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        setPriority(OverlayPriority.HIGHEST);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        stateManager.forceDynamic();
        queueManager.flushTooltips();
        renderInfoTooltip(queueManager.getInfoTooltip(), graphics);
        if (config.tooltipLocation() == FIXED)
        {
            return renderFixedLocationTooltip(queueManager.getFixedLocationTooltip(), graphics);
        }
        else
        {
            renderTooltips(queueManager.getTooltips(), graphics);
        }
        return null;
    }

    /**
     * Renders the fixed location tooltip and returns its dimensions.
     * @param fixedLocationTooltip the fixed location tooltip from queueManager
     * @param graphics engine used to render
     * @return the dimensions of the fixed location tooltip, null if not rendered
     */
    private Dimension renderFixedLocationTooltip(LingeringClickTooltipsWrapper fixedLocationTooltip, Graphics2D graphics)
    {
        if (fixedLocationTooltip == null)
        {
            return null;
        }

        if (fixedLocationTooltip.getRenderableComponent() == null)
        {
            buildAlphaTooltipComponent(fixedLocationTooltip, client);
        }

        double alphaModifier;
        if (fixedLocationTooltip.isFaded())
        {   // fixedLocationTooltip continues to render transparent so that the overlay bounds still shows
            return fixedLocationTooltip.getRenderableComponent().render(graphics);
        }
        else if (config.permanentTooltips() || config.trackerMode())
        {
            alphaModifier = getMaximumOpacity(config);
        }
        else
        {
            alphaModifier = calculateAlphaModifier(fixedLocationTooltip, config);
        }

        return renderTooltip(fixedLocationTooltip, graphics, alphaModifier);
    }

    /**
     * Logic for preparing to render all tooltips in the queue from queueManager. Any tooltip marked as faded is
     * collected here for flushing.
     * @param tooltips the queue of tooltips currently being rendered, front of the queue is oldest
     * @param graphics engine used to render
     */
    private void renderTooltips(Queue<LingeringClickTooltipsWrapper> tooltips, Graphics2D graphics)
    {
        int queuePosition = 0;
        for (LingeringClickTooltipsWrapper tooltip : tooltips)
        {
            if (tooltip.getRenderableComponent() == null)
            {
                buildAlphaTooltipComponent(tooltip, client);
            }

            if (!tooltip.isClamped() && tooltip.getLocation() != null)
            {
                clampTooltip(tooltip, graphics);
            }

            if (queuePosition++ < config.maximumTooltipsShown())
            {
                double alphaModifier;
                if (config.permanentTooltips() || config.trackerMode())
                {
                    alphaModifier = getMaximumOpacity(config) * queuePosition / tooltips.size();
                }
                else
                {
                    alphaModifier = calculateAlphaModifier(tooltip, config);
                }
                renderTooltip(tooltip, graphics, alphaModifier);
            }

            if (tooltip.isFaded())
            {
                queueManager.addTooltipToFlush(tooltip);
            }
        }
    }

    /**
     * Logic for preparing to render the info tooltip from queueManager.
     * @param infoTooltip the info tooltip that will be rendered
     * @param graphics engine used to render
     */
    private void renderInfoTooltip(LingeringClickTooltipsWrapper infoTooltip, Graphics2D graphics)
    {
        if (infoTooltip != null)
        {
            if (!infoTooltip.isFaded())
            {
                if (infoTooltip.getRenderableComponent() == null)
                {
                    buildAlphaTooltipComponent(infoTooltip, client);
                }
                renderTooltip(infoTooltip, graphics, calculateAlphaModifier(infoTooltip, config));
            }
        }
    }

    /**
     * Performs actual rendering of tooltips. Tooltips are marked here as faded if alphaModifier is transparent.
     * @param tooltip the tooltip wrapper which is used to construct a renderable component
     * @param graphics engine used to render
     * @param alphaModifier the opacity of the tooltip
     */
    private Dimension renderTooltip(LingeringClickTooltipsWrapper tooltip, Graphics2D graphics, double alphaModifier)
    {
        if (alphaModifier > 0.0)
        {
            AlphaTooltipComponent renderableComponent = tooltip.getRenderableComponent();
            if (tooltip.isInfoTooltip() || config.tooltipLocation() == ANCHORED || config.trackerMode())
            {
                renderableComponent.setAlphaModifier(inputListener.isMouseOverCanvas()? alphaModifier : 0.0);
                tooltipManager.addFront(new Tooltip(renderableComponent));
            }
            else
            {
                renderableComponent.setAlphaModifier(alphaModifier);
                return renderableComponent.render(graphics);
            }
        }
        else
        {
            tooltip.setFaded(true);
        }
        return null;
    }

    /**
     * Clamps tooltips by first rendering an invisible component to obtain its dimensions, then setting the tooltip
     * location to the new clamped location. Sets clamped to true so that clamping is only calculated once per component.
     * @param tooltip the tooltip wrapper which is used to construct a renderable component
     * @param graphics engine used to render
     */
    private void clampTooltip(LingeringClickTooltipsWrapper tooltip, Graphics2D graphics)
    {
        AlphaTooltipComponent renderableComponent = tooltip.getRenderableComponent();
        renderableComponent.setAlphaModifier(0.0);
        Dimension dimension = renderableComponent.render(graphics);
        renderableComponent.setPosition(getClampedLocation(dimension, tooltip.getLocation(), client, config));
        tooltip.setClamped(true);
    }

    public void initialize(LingeringClickTooltipsInputListener inputListener, LingeringClickTooltipsQueueManager queueManager)
    {
        this.inputListener = inputListener;
        this.queueManager = queueManager;
        initializeStateManager();
    }

    public void destroy()
    {
        inputListener = null;
        queueManager = null;
        stateManager.destroy();
        stateManager = null;
    }

    private void initializeStateManager()
    {
        stateManager = new LingeringClickTooltipsOverlayStateManager();
        stateManager.initialize(this);
        updateFromConfig(TOOLTIP_LOCATION_CONFIG_KEY);
    }

    public void updateFromConfig(String configKey)
    {
        if (configKey.equals(TOOLTIP_LOCATION_CONFIG_KEY))
        {
            if (config.tooltipLocation() == FIXED)
            {
                stateManager.load();
            }
            else
            {
                if (!stateManager.isDynamic())
                {
                    stateManager.save(); // state should only be saved when switching off of Fixed
                }
                stateManager.setDynamic();
            }
        }
    }
}
