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
import net.runelite.client.config.Alpha;
import ryanxie0.runelite.plugin.lingeringclicktooltips.filtering.LingeringClickTooltipsFilterMode;

import java.awt.Color;

import static ryanxie0.runelite.plugin.lingeringclicktooltips.colors.LingeringClickTooltipsTextColorConstants.*;
import static ryanxie0.runelite.plugin.lingeringclicktooltips.filtering.LingeringClickTooltipsTrivialClicksConstants.*;

@ConfigGroup(LingeringClickTooltipsConfig.GROUP_NAME)
public interface LingeringClickTooltipsConfig extends Config
{
	String GROUP_NAME = "lingeringclicktooltips";
	String OVERLAY_PREFERRED_POSITION = "LingeringClickTooltipsOverlay_preferredPosition";
	String OVERLAY_PREFERRED_LOCATION = "LingeringClickTooltipsOverlay_preferredLocation";

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
		name = "Filter lists",
		description = "User-managed lists for filtering tooltips",
		position = 3,
		closedByDefault = true
	)
	String filterLists = "filterLists";

	@ConfigSection(
		name = "Location",
		description = "Modify location of tooltips",
		position = 4,
		closedByDefault = true
	)
	String location = "location";

	@ConfigSection(
		name = "Hotkeys",
		description = "Configure hotkey functionality",
		position = 5,
		closedByDefault = true
	)
	String hotkeys = "hotkeys";

	@ConfigSection(
		name = "Trivial clicks",
		description = "Declare which clicks are considered trivial",
		position = 6,
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
	@Range(min = 1)
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
		keyName = USE_CUSTOM_TEXT_COLOR,
		name = "Use custom text color",
		description = "Choose whether to apply the custom text color below to non-info tooltips",
		position = 0,
		section = appearance
	)
	default boolean useCustomTextColor() { return false; }

	@ConfigItem(
		keyName = CUSTOM_TEXT_COLOR,
		name = "Text color",
		description = "Applied to non-info tooltips only",
		position = 1,
		section = appearance
	)
	default Color customTextColor() { return Color.WHITE; }

	@ConfigItem(
		keyName = USE_CUSTOM_BACKGROUND_COLOR,
		name = "Use custom background color",
		description = "Choose whether to apply the custom background color below to non-info tooltips",
		position = 2,
		section = appearance
	)
	default boolean useCustomBackgroundColor() { return false; }

	@Alpha
	@ConfigItem(
		keyName = CUSTOM_BACKGROUND_COLOR,
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
		keyName = "filterMode",
		name = "Filter mode",
		description = "Select the mode used for filtering tooltips based on user-managed lists",
		position = 3,
		section = modes
	)
	default LingeringClickTooltipsFilterMode filterMode() { return LingeringClickTooltipsFilterMode.NONE; }

	@ConfigItem(
		keyName = "blacklist",
		name = "Blacklist",
		description = "Tooltips matching text in this list will NOT show",
		position = 0,
		section = filterLists
	)
	default String blacklist() { return ""; }

	@ConfigItem(
		keyName = "blacklist",
		name = "",
		description = ""
	)
	void setBlacklist(String key);

	@ConfigItem(
		keyName = "whitelist",
		name = "Whitelist",
		description = "ONLY tooltips matching text in this list will show",
		position = 1,
		section = filterLists
	)
	default String whitelist() { return ""; }

	@ConfigItem(
		keyName = "whitelist",
		name = "",
		description = ""
	)
	void setWhitelist(String key);

	@ConfigItem(
		keyName = LingeringClickTooltipsLocation.TOOLTIP_LOCATION_CONFIG_KEY,
		name = "Tooltip location",
		description = "Lingering remains at the click point, anchored follows the mouse cursor, custom stays at a fixed location",
		position = 0,
		section = location
	)
	default LingeringClickTooltipsLocation tooltipLocation() { return LingeringClickTooltipsLocation.LINGERING; }

	@ConfigItem(
		keyName = "tooltipXOffset",
		name = "Tooltip x offset",
		description = "Horizontal offset for lingering tooltips, higher values move the tooltip further right",
		position = 1,
		section = location
	)
	@Range(max = 100)
	default int tooltipXOffset() { return 0; }

	@ConfigItem(
		keyName = "tooltipYOffset",
		name = "Tooltip y offset",
		description = "Vertical offset for lingering tooltips, positive values move the tooltip down",
		position = 2,
		section = location
	)
	@Range(min = -100, max = 100)
	default int tooltipYOffset() { return -20; }

	@ConfigItem(
		keyName = "clampXPadding",
		name = "Clamp x padding",
		description = "The minimum distance between tooltip and left/right window border, 0 means no gap",
		position = 3,
		section = location
	)
	@Range(max = 30)
	default int clampXPadding() { return 5; }

	@ConfigItem(
		keyName = "clampYPadding",
		name = "Clamp y padding",
		description = "The minimum distance between tooltip and top/bottom window border, 0 means no gap",
		position = 4,
		section = location
	)
	@Range(max = 30)
	default int clampYPadding() { return 5; }

	@ConfigItem(
		keyName = "ctrlDoubleTapDelay",
		name = "CTRL double-tap delay",
		description = "Double-tap delay for CTRL to toggle tooltips, 0 to disable",
		position = 0,
		section = hotkeys
	)
	@Units(Units.MILLISECONDS)
	@Range(max = 500)
	default int ctrlDoubleTapDelay() { return 250; }

	@ConfigItem(
		keyName = "ctrlTogglesHide",
		name = "CTRL toggles hide",
		description = "Choose whether holding CTRL shows tooltips normally",
		position = 1,
		section = hotkeys
	)
	default boolean ctrlTogglesHide() { return true; }

	@ConfigItem(
		keyName = "shiftDoubleTapDelay",
		name = "SHIFT double-tap delay",
		description = "Double-tap delay for SHIFT to black/whitelist tooltips, must be holding CTRL, 0 to disable",
		position = 3,
		section = hotkeys
	)
	@Units(Units.MILLISECONDS)
	@Range(max = 600)
	default int shiftDoubleTapDelay() { return 300; }

	@ConfigItem(
		keyName = "shiftPeeksFilterListAction",
		name = "SHIFT peek",
		description = "Choose whether holding SHIFT for more than SHIFT double-tap delay peeks filter list actions, must be holding CTRL",
		position = 4,
		section = hotkeys
	)
	default boolean shiftPeeksFilterListAction() { return true; }

	@ConfigItem(
		keyName = HIDE_TRIVIAL_CLICKS,
		name = "Hide trivial clicks",
		description = "Choose whether to hide trivial clicks such as walk",
		position = 0,
		section = trivialClicks
	)
	default boolean hideTrivialClicks() { return true; }

	@ConfigItem(
		keyName = HIDE_WALK_HERE,
		name = "Walk here",
		description = "Choose whether walk should be hidden",
		position = 1,
		section = trivialClicks
	)
	default boolean hideWalkHere() { return true; }

	@ConfigItem(
		keyName = HIDE_WALK_HERE_WITH_TARGET,
		name = "Walk here (with target)",
		description = "Choose whether walk with a target should be hidden",
		position = 2,
		section = trivialClicks
	)
	default boolean hideWalkHereWithTarget() { return true; }

	@ConfigItem(
		keyName = HIDE_WIELD,
		name = "Wield",
		description = "Choose whether wield (not from menu) should be hidden",
		position = 3,
		section = trivialClicks
	)
	default boolean hideWield() { return true; }

	@ConfigItem(
		keyName = HIDE_WEAR,
		name = "Wear",
		description = "Choose whether wear (not from menu) should be hidden",
		position = 4,
		section = trivialClicks
	)
	default boolean hideWear() { return true; }

	@ConfigItem(
		keyName = HIDE_TOGGLE_RUN,
		name = "Toggle Run",
		description = "Choose whether toggle run should be hidden",
		position = 5,
		section = trivialClicks
	)
	default boolean hideToggleRun() { return true; }

	@ConfigItem(
		keyName = HIDE_QUICK_PRAYERS,
		name = "Quick-prayers",
		description = "Choose whether toggle quick-prayers should be hidden",
		position = 6,
		section = trivialClicks
	)
	default boolean hideQuickPrayers() { return true; }

	@ConfigItem(
		keyName = HIDE_PRAYERS,
		name = "Prayers",
		description = "Choose whether toggling prayers from the prayer panel should be hidden",
		position = 7,
		section = trivialClicks
	)
	default boolean hidePrayers() { return true; }

	@ConfigItem(
		keyName = HIDE_SHIFT_DROP,
		name = "Shift drop",
		description = "Choose whether shift drop should be hidden",
		position = 8,
		section = trivialClicks
	)
	default boolean hideShiftDrop() { return true; }

	@ConfigItem(
		keyName = HIDE_USE_INITIATE,
		name = "Use",
		description = "Choose whether use initiate (not from menu) should be hidden",
		position = 9,
		section = trivialClicks
	)
	default boolean hideUseInitiate() { return true; }

	@ConfigItem(
		keyName = HIDE_EAT,
		name = "Eat",
		description = "Choose whether eat (not from menu) should be hidden",
		position = 10,
		section = trivialClicks
	)
	default boolean hideEat() { return true; }

	@ConfigItem(
		keyName = HIDE_PUZZLES,
		name = "Puzzles",
		description = "Choose whether puzzles should be hidden",
		position = 11,
		section = trivialClicks
	)
	default boolean hidePuzzles() { return true; }

	@ConfigItem(
		keyName = HIDE_PANELS,
		name = "Panels (group)",
		description = "Choose whether most clicks on panels should be hidden",
		position = 12,
		section = trivialClicks
	)
	default boolean hidePanels() { return true; }
}
