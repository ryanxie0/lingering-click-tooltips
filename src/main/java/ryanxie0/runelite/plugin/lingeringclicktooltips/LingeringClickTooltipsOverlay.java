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
import ryanxie0.runelite.plugin.lingeringclicktooltips.components.alpha.AlphaTooltipComponent;
import ryanxie0.runelite.plugin.lingeringclicktooltips.components.wrapper.LingeringClickTooltipsWrapper;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.time.Duration;
import java.time.Instant;
import java.util.Queue;
import javax.inject.Inject;

import static ryanxie0.runelite.plugin.lingeringclicktooltips.LingeringClickTooltipsLocation.*;
import static ryanxie0.runelite.plugin.lingeringclicktooltips.components.wrapper.LingeringClickTooltipsWrapperUtil.*;

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
     * Renders the custom location tooltip. It only shows the most recent action, and renders at the tooltip start
     * opacity set in the config. Returns the dimension of the tooltip.
     * @param fixedLocationTooltip the custom location tooltip
     * @param graphics engine used to render
     * @return the dimensions of the custom location tooltip, null if not rendered
     */
    private Dimension renderFixedLocationTooltip(LingeringClickTooltipsWrapper fixedLocationTooltip, Graphics2D graphics)
    {
        if (inputListener.isHide() || fixedLocationTooltip == null)
        {
            return null;
        }

        if (fixedLocationTooltip.getRenderableComponent() == null)
        {
            buildAlphaTooltipComponent(fixedLocationTooltip, client);
        }

        if (!fixedLocationTooltip.isClamped() && fixedLocationTooltip.getLocation() != null)
        {
            clampTooltip(fixedLocationTooltip, graphics);
        }

        double alphaModifier = calculateTooltipFade(fixedLocationTooltip);
        if (config.permanentTooltips() || config.trackerMode())
        {
            alphaModifier = config.tooltipStartOpacity() / 100.0;
        }
        fixedLocationTooltip.getRenderableComponent().setAlphaModifier(alphaModifier);

        return fixedLocationTooltip.getRenderableComponent().render(graphics);
    }

    /**
     * Logic for rendering all tooltips currently in the queue. Queue size is managed by
     * LingeringClickTooltipsQueueManager.java. Each tooltip will be clamped if necessary. When tooltips
     * are permanent, fade is calculated specially using a ratio of queuePosition:queueSize so that only the most
     * recent permanent tooltip (back of the queue) is rendered as most opaque. Any tooltip which is marked as faded
     * is collected here for flushing.
     * @param tooltips the queue of tooltips currently being rendered, front of the queue is oldest
     * @param graphics engine used to render
     */
    private void renderTooltips(Queue<LingeringClickTooltipsWrapper> tooltips, Graphics2D graphics)
    {
        if (inputListener.isHide())
        {
            return;
        }

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
                if (config.permanentTooltips() || config.trackerMode())
                {
                    renderTooltip(tooltip, graphics, calculateTooltipFade(queuePosition, tooltips.size()));
                }
                else
                {
                    renderTooltip(tooltip, graphics, calculateTooltipFade(tooltip));
                }
            }

            if (tooltip.isFaded())
            {
                queueManager.addTooltipToFlush(tooltip);
            }
        }
    }

    /**
     * Logic for rendering the info tooltip managed by LingeringClickTooltipsQueueManager.java. Only one info tooltip
     * is shown at any given time, and info tooltips use their own special colors, increased duration, and decreased
     * fadeout. They are also anchored, cannot be permanent, and show even when hide mode is enabled. They reside as a
     * reference on LingeringClickTooltipsQueueManager.java separate from the queue other tooltips are stored in.
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
                renderTooltip(infoTooltip, graphics, calculateTooltipFade(infoTooltip));
            }
        }
    }

    /**
     * Performs rendering of visible components. For anchored tooltips, this method ensures that the mouse is currently
     * over the canvas, otherwise the components are rendered transparent. Otherwise, any tooltip passed in with a
     * transparent alpha modifier is marked as faded.
     * @param tooltip the tooltip wrapper which is used to construct a renderable component
     * @param graphics engine used to render
     * @param alphaModifier the opacity of the tooltip
     */
    private void renderTooltip(LingeringClickTooltipsWrapper tooltip, Graphics2D graphics, double alphaModifier)
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
                renderableComponent.render(graphics);
            }
        }
        else
        {
            tooltip.setFaded(true);
        }
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
        Point clampedLocation = getClampedLocation(dimension, tooltip.getLocation(), client, config);
        renderableComponent.setPosition(clampedLocation);
        renderableComponent.setPreferredLocation(clampedLocation);
        tooltip.setClamped(true);
    }

    /**
     * There are 3 periods of fade relevant to a tooltip. The first is the starting opacity, which it will remain at
     * until the fadeout% duration is reached. After fade begins, the tooltip gradually fades to transparent until
     * finally the time remaining on the tooltip duration becomes 0 or negative, where opacity is returned as 0.0 such
     * that the wrapper becomes marked as faded and later flushed by the plugin.
     * @param tooltip the tooltip for which fade will be calculated
     * @return the tooltip alpha modifier which will be used during rendering
     */
    private double calculateTooltipFade(LingeringClickTooltipsWrapper tooltip)
    {
        double alphaModifier = config.tooltipStartOpacity() / 100.0;

        Duration tooltipDuration = calculateTooltipDuration(tooltip);
        double fadeout = calculateTooltipFadeout(tooltip, tooltipDuration);

        Duration since = Duration.between(tooltip.getTimeOfCreation(), Instant.now());
        long timeRemaining = (tooltipDuration.minus(since)).toMillis();

        if (timeRemaining <= 0) // to deal with imprecise time calculations
        {
            alphaModifier = 0.0;
        }
        else if (since.compareTo(tooltipDuration) < 0 && timeRemaining < fadeout)
        {
            alphaModifier = Math.min(config.tooltipStartOpacity() / 100.0, timeRemaining / fadeout);
        }

        return alphaModifier;
    }

    /**
     * This method only matters when there are multiple permanent tooltips showing.
     * Essentially, permanent tooltips become more faded as they "age", i.e. get closer to the front of the queue.
     * @param queuePosition the queue position of the tooltip in question
     * @param queueSize the current size of the queue
     * @return the fade of the tooltip as determined by its queue position and the queue size
     */
    private double calculateTooltipFade(int queuePosition, int queueSize)
    {
        return config.tooltipStartOpacity() / 100.0 * (double) queuePosition / (double) queueSize;
    }

    /**
     * Info tooltip duration is 50% more than the config value and is not affected by fast mode.
     * @param tooltip the tool wrapper which is used to construct a renderable component
     * @return the adjusted tooltip duration
     */
    private Duration calculateTooltipDuration(LingeringClickTooltipsWrapper tooltip)
    {
        int baseTooltipDuration = config.tooltipDuration();
        if (tooltip.isInfoTooltip())
        {
            return Duration.ofMillis(baseTooltipDuration * 3L / 2L);
        }
        else if (config.fastMode())
        {
            return Duration.ofMillis(baseTooltipDuration / 2L);
        }
        else
        {
            return Duration.ofMillis(baseTooltipDuration);
        }
    }

    /**
     * Info tooltip fadeout is half of the config value and is not affected by light mode.
     * Light mode doubles the fadeout value, which essentially causes affected tooltips to begin disappearing twice as soon.
     * Fadeout can technically reach values over 100% by combining light mode with a fadeout config setting > 50%. In this case,
     * the tooltip will have already faded some amount at the moment it first appears. This causes behavior which could be confused
     * with tooltip start opacity.
     * @param tooltip the tooltip wrapper which is used to construct a renderable component
     * @param tooltipDuration the adjusted tooltip duration
     * @return the adjusted tooltip fadeout
     */
    private double calculateTooltipFadeout(LingeringClickTooltipsWrapper tooltip, Duration tooltipDuration)
    {
        double baseFadeout = tooltipDuration.toMillis() * config.tooltipFadeout() / 100.0;
        if (tooltip.isInfoTooltip())
        {
            return baseFadeout / 2.0;
        }
        else if (config.lightMode())
        {
            return baseFadeout * 2.0;
        }
        else
        {
            return baseFadeout;
        }
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

    /**
     * Initializes the state manager used to switch between the tooltip locations.
     */
    private void initializeStateManager()
    {
        stateManager = new LingeringClickTooltipsOverlayStateManager();
        stateManager.initialize(this);
        updateFromConfig(TOOLTIP_LOCATION_CONFIG_KEY);
    }

    /**
     * Updates the overlay from config.
     * @param configKey the key name of the config that was changed
     */
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
