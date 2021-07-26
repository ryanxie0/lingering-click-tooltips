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

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Units;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;
import java.awt.Color;

@ConfigGroup(LingeringClickTooltipsConfig.CONFIG_GROUP)
public interface LingeringClickTooltipsConfig extends Config
{
	String CONFIG_GROUP = "lingeringclicktooltips";

	@ConfigSection(
		name = "Lifespan",
		description = "Modify lifespan of tooltips",
		position = 0
	)
	String lifespan = "lifespan";

	@ConfigSection(
		name = "Appearance",
		description = "Modify appearance of tooltips",
		position = 1
	)
	String appearance = "appearance";

	@ConfigSection(
		name = "Modes",
		description = "Convenient operating modes for tooltips",
		position = 2
	)
	String modes = "modes";

	@ConfigSection(
		name = "Location",
		description = "Modify location of tooltips",
		position = 3,
		closedByDefault = true
	)
	String location = "location";

	@ConfigSection(
		name = "Hotkey",
		description = "Configure CTRL hotkey functionality",
		position = 4,
		closedByDefault = true
	)
	String hotkey = "hotkey";

	@ConfigSection(
		name = "Trivial Clicks",
		description = "Declare which clicks are considered trivial",
		position = 5,
		closedByDefault = true
	)
	String trivialClicks = "trivialClicks";

	@ConfigItem(
		keyName = "tooltipDuration",
		name = "Tooltip duration",
		description = "The duration of the tooltip before it disappears",
		position = 0,
		section = lifespan
	)
	@Units(Units.MILLISECONDS)
	@Range(min = 1, max = 5000)
	default int tooltipDuration()
	{
		return 1000;
	}

	@ConfigItem(
		keyName = "tooltipFadeout",
		name = "Tooltip fadeout",
		description = "Start fading out the tooltip at x% of the remaining duration",
		position = 1,
		section = lifespan
	)
	@Units(Units.PERCENT)
	@Range(max = 100)
	default int tooltipFadeout() { return 50; }

	@ConfigItem(
		keyName = "maximumTooltipsShown",
		name = "Max tooltips shown",
		description = "The maximum number of tooltips shown at any given time",
		position = 2,
		section = lifespan
	)
	default int maximumTooltipsShown() { return 1; }

	@ConfigItem(
		keyName = "permanentTooltips",
		name = "Permanent tooltips",
		description = "Use tooltips that do not disappear",
		position = 3,
		section = lifespan
	)
	default boolean permanentTooltips() { return false; }

	@ConfigItem(
		keyName = "useCustomTextColor",
		name = "Use custom text color",
		description = "Choose whether to apply the custom text color below to non-info tooltips",
		position = 0,
		section = appearance
	)
	default boolean useCustomTextColor() { return false; }

	@ConfigItem(
		keyName = "customTextColor",
		name = "Text color",
		description = "Applied to non-info tooltips only",
		position = 1,
		section = appearance
	)
	default Color customTextColor() { return Color.WHITE; }

	@ConfigItem(
		keyName = "useCustomBackgroundColor",
		name = "Use custom background color",
		description = "Choose whether to apply the custom background color below to non-info tooltips",
		position = 2,
		section = appearance
	)
	default boolean useCustomBackgroundColor() { return false; }

	@ConfigItem(
		keyName = "customBackgroundColor",
		name = "Background color",
		description = "Applied to non-info tooltips only",
		position = 3,
		section = appearance
	)
	default Color customBackgroundColor() { return Color.BLACK; }

	@ConfigItem(
		keyName = "overrideMenuOptionColor",
		name = "Override menu option color",
		description = "Choose whether custom text color should override menu option color",
		position = 4,
		section = appearance
	)
	default boolean overrideMenuOptionColor() { return false; }

	@ConfigItem(
		keyName = "tooltipStartOpacity",
		name = "Tooltip start opacity",
		description = "Opacity of tooltips when they first appear",
		position = 5,
		section = appearance
	)
	@Units(Units.PERCENT)
	@Range(max = 100)
	default int tooltipStartOpacity() { return 100; }

	@ConfigItem(
		keyName = "fastMode",
		name = "Fast mode",
		description = "Tooltip duration cut by half",
		position = 0,
		section = modes
	)
	default boolean fastMode() { return false; }

	@ConfigItem(
		keyName = "lightMode",
		name = "Light mode",
		description = "Tooltip fadeout doubled",
		position = 1,
		section = modes
	)
	default boolean lightMode() { return false; }

	@ConfigItem(
		keyName = "trackerMode",
		name = "Tracker mode",
		description = "Tooltips become permanent and follow the cursor, trivial clicks shown",
		position = 2,
		section = modes
	)
	default boolean trackerMode() { return false; }

	@ConfigItem(
		keyName = "anchorTooltips",
		name = "Anchor tooltips",
		description = "Choose whether tooltips follow the mouse cursor",
		position = 0,
		section = location
	)
	default boolean anchorTooltips() { return false; }

	@ConfigItem(
		keyName = "tooltipXOffset",
		name = "Tooltip x offset",
		description = "Horizontal offset, higher values move the tooltip further right",
		position = 1,
		section = location
	)
	@Range(max = 100)
	default int tooltipXOffset() { return 0; }

	@ConfigItem(
		keyName = "tooltipYOffset",
		name = "Tooltip y offset",
		description = "Vertical offset, positive values move the tooltip down",
		position = 2,
		section = location
	)
	@Range(min = -100, max = 100)
	default int tooltipYOffset() { return -20; }

	@ConfigItem(
		keyName = "hotkeyToggleDelay",
		name = "Hotkey toggle delay",
		description = "Double-tap delay for the CTRL key to toggle tooltips, 0 to disable",
		position = 0,
		section = hotkey
	)
	@Units(Units.MILLISECONDS)
	default int hotkeyToggleDelay() { return 250; }

	@ConfigItem(
		keyName = "hotkeyHideToggle",
		name = "Hotkey hide toggle",
		description = "Choose whether holding down the CTRL key shows tooltips",
		position = 1,
		section = hotkey
	)
	default boolean hotkeyHideToggle() { return true; }

	@ConfigItem(
		keyName = "hideTrivialClicks",
		name = "Hide trivial clicks",
		description = "Choose whether to hide trivial clicks such as walk",
		position = 0,
		section = trivialClicks
	)
	default boolean hideTrivialClicks() { return true; }

	@ConfigItem(
		keyName = "hideWalkHere",
		name = "Walk here",
		description = "Choose whether walk should be hidden",
		position = 1,
		section = trivialClicks
	)
	default boolean hideWalkHere() { return true; }

	@ConfigItem(
		keyName = "hideContinue",
		name = "Continue",
		description = "Choose whether continue should be hidden",
		position = 2,
		section = trivialClicks
	)
	default boolean hideContinue() { return true; }

	@ConfigItem(
		keyName = "hideWield",
		name = "Wield",
		description = "Choose whether wield (not from menu) should be hidden",
		position = 3,
		section = trivialClicks
	)
	default boolean hideWield() { return true; }

	@ConfigItem(
		keyName = "hideWear",
		name = "Wear",
		description = "Choose whether wear (not from menu) should be hidden",
		position = 4,
		section = trivialClicks
	)
	default boolean hideWear() { return true; }

	@ConfigItem(
		keyName = "hideShiftDrop",
		name = "Shift drop",
		description = "Choose whether shift drop should be hidden",
		position = 5,
		section = trivialClicks
	)
	default boolean hideShiftDrop() { return true; }

	@ConfigItem(
		keyName = "hideUseInitiate",
		name = "Use",
		description = "Choose whether use initiate (not from menu) should be hidden",
		position = 6,
		section = trivialClicks
	)
	default boolean hideUseInitiate() { return true; }

	@ConfigItem(
		keyName = "hideEat",
		name = "Eat",
		description = "Choose whether eat (not from menu) should be hidden",
		position = 7,
		section = trivialClicks
	)
	default boolean hideEat() { return true; }

	@ConfigItem(
		keyName = "hidePuzzles",
		name = "Puzzles",
		description = "Choose whether puzzles should be hidden",
		position = 8,
		section = trivialClicks
	)
	default boolean hidePuzzles() { return true; }
}
