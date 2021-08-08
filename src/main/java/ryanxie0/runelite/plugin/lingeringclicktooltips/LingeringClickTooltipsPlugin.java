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

import com.google.inject.Provides;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import ryanxie0.runelite.plugin.lingeringclicktooltips.colors.LingeringClickTooltipsTextColorManager;
import ryanxie0.runelite.plugin.lingeringclicktooltips.filtering.LingeringClickTooltipsTrivialClicksManager;

import javax.inject.Inject;

@PluginDescriptor(
	name = "Lingering Click Tooltips",
	description = "Generate configurable tooltips with left click mouse actions",
	tags = {"overlay","tooltip","text","hover","click","action","toast"}
)
public class LingeringClickTooltipsPlugin extends Plugin
{
	@Inject
	private RuneLiteConfig runeLiteConfig;

	@Inject
	private LingeringClickTooltipsConfig config;

	@Inject
	private LingeringClickTooltipsOverlay overlay;

	@Inject
	private MouseManager mouseManager;

	@Inject
	private KeyManager keyManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private LingeringClickTooltipsQueueManager queueManager;

	@Inject
	private LingeringClickTooltipsInputListener inputListener;

	@Inject
	private LingeringClickTooltipsTrivialClicksManager trivialClicksManager;

	@Inject
	private LingeringClickTooltipsTextColorManager textColorManager;

	@Override
	protected void startUp() throws Exception
	{
		mouseManager.registerMouseListener(inputListener);
		keyManager.registerKeyListener(inputListener);
		inputListener.initialize(queueManager);

		queueManager.initialize(inputListener);

		overlayManager.add(overlay);
		overlay.initialize(inputListener, queueManager);

		trivialClicksManager.initialize();

		textColorManager.initialize();
	}

	@Override
	protected void shutDown() throws Exception
	{
		mouseManager.unregisterMouseListener(inputListener);
		keyManager.unregisterKeyListener(inputListener);
		inputListener.destroy();

		queueManager.destroy();

		overlayManager.remove(overlay);
		overlay.destroy();

		trivialClicksManager.destroy();

		textColorManager.destroy();
	}

	@Provides
	LingeringClickTooltipsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(LingeringClickTooltipsConfig.class);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getKey().equals(LingeringClickTooltipsConfig.OVERLAY_PREFERRED_LOCATION)
			&& !event.getKey().equals(LingeringClickTooltipsConfig.OVERLAY_PREFERRED_POSITION))
		{
			queueManager.clear(); // not called when the user is moving the fixed tooltip location
		}
		if (event.getGroup().equals(LingeringClickTooltipsConfig.GROUP_NAME))
		{
			trivialClicksManager.updateFromConfig(event.getKey());
			textColorManager.updateFromConfig(event.getKey());
			overlay.updateFromConfig(event.getKey());
		}
		else if (event.getGroup().equals(runeLiteConfig.GROUP_NAME))
		{
			textColorManager.updateFromConfig(event.getKey());
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		queueManager.createNewTooltip(event.getMenuOption(), event.getMenuTarget());
		if (queueManager.isConsumeEvent())
		{
			event.consume();
			queueManager.setConsumeEvent(false);
		}
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		queueManager.processTick();
	}
}
