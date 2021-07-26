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

import ryanxie0.runelite.plugin.lingeringclicktooltips.components.AlphaTooltipComponent;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import ryanxie0.runelite.plugin.lingeringclicktooltips.util.LingeringClickTooltipsUtil;
import ryanxie0.runelite.plugin.lingeringclicktooltips.util.LingeringClickTooltipsWrapper;
import javax.inject.Inject;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.time.Duration;
import java.time.Instant;
import java.util.Queue;

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

    @Inject
    LingeringClickTooltipsOverlay()
    {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPriority(OverlayPriority.HIGHEST);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        plugin.flushTooltips();
        renderInfoTooltip(plugin.getInfoTooltip(), graphics);
        renderTooltips(plugin.getTooltips(), graphics);
        return null;
    }

    private void renderTooltips(Queue<LingeringClickTooltipsWrapper> tooltips, Graphics2D graphics)
    {
        int queuePosition = 0;
        for (LingeringClickTooltipsWrapper tooltip : tooltips)
        {
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
                plugin.getTooltipsToFlush().add(tooltip);
            }
        }
    }

    private void renderInfoTooltip(LingeringClickTooltipsWrapper infoTooltip, Graphics2D graphics)
    {
        if (infoTooltip != null && !infoTooltip.isFaded())
        {
            renderTooltip(infoTooltip, graphics, calculateTooltipFade(infoTooltip));
        }
    }

    private void renderTooltip(LingeringClickTooltipsWrapper tooltip, Graphics2D graphics, double alphaModifier)
    {
        if (alphaModifier > 0.0)
        {
            if (tooltip.isInfoTooltip() || config.anchorTooltips() || config.trackerMode())
            {
                if (plugin.isMouseOverViewport())
                {
                    tooltipManager.addFront(new Tooltip(getTooltipComponent(tooltip, alphaModifier)));
                }
                else
                {
                    tooltipManager.addFront(new Tooltip(getTooltipComponent(tooltip, 0.0)));
                }
            }
            else
            {
                getTooltipComponent(tooltip, alphaModifier).render(graphics);
            }
        }
        else
        {
            tooltip.setFaded(true);
        }
    }

    private double calculateTooltipFade(LingeringClickTooltipsWrapper tooltip)
    {
        double alphaModifier = config.tooltipStartOpacity() / 100.0;

        Duration tooltipDuration = getAdjustedTooltipDuration();
        double fadeout = getAdjustedTooltipFadeout(tooltip, tooltipDuration);

        Duration since = Duration.between(tooltip.getTimeOfCreation(), Instant.now());
        long timeRemaining = (tooltipDuration.minus(since)).toMillis();

        if (timeRemaining <= 0) // to deal with imprecise time calculations
        {
            alphaModifier = 0.0;
        }
        else if (since.compareTo(tooltipDuration) < 0 && timeRemaining < fadeout && fadeout > 0)
        {
            alphaModifier = Math.min(config.tooltipStartOpacity() / 100.0, timeRemaining / fadeout);
        }

        return alphaModifier;
    }

    private double calculateTooltipFade(int queuePosition, int queueSize)
    {
        return config.tooltipStartOpacity() / 100.0 * (double) queuePosition / (double) queueSize;
    }

    private void clampTooltip(LingeringClickTooltipsWrapper tooltip, Graphics2D graphics)
    {
        Dimension dimension = getTooltipComponent(tooltip, 0.0).render(graphics);
        tooltip.setLocation(LingeringClickTooltipsUtil.getClampedLocation(dimension, client, tooltip.getLocation()));
        tooltip.setClamped(true);
    }

    private LayoutableRenderableEntity getTooltipComponent(LingeringClickTooltipsWrapper tooltip, double alphaModifier)
    {
        AlphaTooltipComponent tooltipComponent = new AlphaTooltipComponent();
        tooltipComponent.setText(tooltip.getText());
        tooltipComponent.setModIcons(client.getModIcons());
        tooltipComponent.setAlphaModifier(alphaModifier);
        tooltipComponent.setBackgroundColor(tooltip.getBackgroundColor());
        if (tooltip.getLocation() != null)
        {
            tooltipComponent.setPreferredLocation(tooltip.getLocation());
        }
        return tooltipComponent;
    }

    private Duration getAdjustedTooltipDuration()
    {
        int baseTooltipDuration = config.tooltipDuration();
        if (config.fastMode())
        {
            return Duration.ofMillis(baseTooltipDuration / 2);
        }
        else
        {
            return Duration.ofMillis(baseTooltipDuration);
        }
    }

    private double getAdjustedTooltipFadeout(LingeringClickTooltipsWrapper tooltip, Duration tooltipDuration)
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
}
