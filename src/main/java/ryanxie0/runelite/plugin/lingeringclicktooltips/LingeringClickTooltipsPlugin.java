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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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
import ryanxie0.runelite.plugin.lingeringclicktooltips.util.LingeringClickTooltipsTextToColorMapper;
import ryanxie0.runelite.plugin.lingeringclicktooltips.util.LingeringClickTooltipsTrivialClicksMapper;
import ryanxie0.runelite.plugin.lingeringclicktooltips.util.LingeringClickTooltipsUtil;
import ryanxie0.runelite.plugin.lingeringclicktooltips.util.LingeringClickTooltipsWrapper;
import javax.inject.Inject;

import static ryanxie0.runelite.plugin.lingeringclicktooltips.util.LingeringClickTooltipsTextToColorMapper.*;

@Slf4j
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
	private LingeringClickTooltipsInputListener inputListener;

	@Inject
	private MouseManager mouseManager;

	@Inject
	private KeyManager keyManager;

	@Inject
	private OverlayManager overlayManager;

	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	private LingeringClickTooltipsWrapper tooltip;

	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	private boolean isHotkeyPressed;

	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	private boolean isHide;

	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	private LingeringClickTooltipsTrivialClicksMapper trivialClicksMapper;

	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	private LingeringClickTooltipsTextToColorMapper textToColorMapper;

	@Override
	protected void startUp() throws Exception
	{
		mouseManager.registerMouseListener(inputListener);
		keyManager.registerKeyListener(inputListener);
		overlayManager.add(overlay);
		trivialClicksMapper = new LingeringClickTooltipsTrivialClicksMapper(config);
		textToColorMapper = new LingeringClickTooltipsTextToColorMapper(runeLiteConfig, config);
	}

	@Override
	protected void shutDown() throws Exception
	{
		mouseManager.unregisterMouseListener(inputListener);
		keyManager.unregisterKeyListener(inputListener);
		overlayManager.remove(overlay);
		trivialClicksMapper.destroy();
		trivialClicksMapper = null;
		textToColorMapper.destroy();
		textToColorMapper = null;
		tooltip = null;
	}

	@Provides
	LingeringClickTooltipsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(LingeringClickTooltipsConfig.class);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals(LingeringClickTooltipsConfig.CONFIG_GROUP))
		{
			trivialClicksMapper.update(config, event.getKey());
			textToColorMapper.update(config);
		}
		if (event.getKey().equals(RuneLiteConfig.overlaySettings))
		{
			textToColorMapper.update(runeLiteConfig);
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		String tooltipText = LingeringClickTooltipsUtil.getTooltipText(event.getMenuOption(), event.getMenuTarget());
		if (LingeringClickTooltipsUtil.shouldProcessClick(tooltipText, isHide, isHotkeyPressed, config))
		{
			this.tooltip = LingeringClickTooltipsUtil.buildTooltipWrapper(
				tooltipText,
				false,
				LingeringClickTooltipsUtil.getOffsetLocation(inputListener.getLastClickPos(), config)
			);
		}
	}

	protected void showInfoTooltip(String infoTooltipText)
	{
		this.tooltip = LingeringClickTooltipsUtil.buildTooltipWrapper(
			infoTooltipText,
			true,
			null
		);
	}

	protected void showToggledHideModeTooltip()
	{
		showInfoTooltip(isHide? TOOLTIPS_HIDDEN : TOOLTIPS_SHOWN);
	}
}
