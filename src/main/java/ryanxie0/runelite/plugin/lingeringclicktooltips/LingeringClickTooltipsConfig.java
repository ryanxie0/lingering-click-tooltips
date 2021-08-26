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

import static ryanxie0.runelite.plugin.lingeringclicktooltips.color.LingeringClickTooltipsColorConstants.*;
import static ryanxie0.runelite.plugin.lingeringclicktooltips.filtering.LingeringClickTooltipsFilteringConstants.*;

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
		description = "Configure the filter mode behavior",
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
		description = "The duration for which tooltips will render at max opacity",
		position = 0,
		section = lifespan
	)
	@Units(Units.MILLISECONDS)
	@Range(min = 1, max = 5000)
	default int tooltipDuration() { return 600;}

	@ConfigItem(
		keyName = "tooltipFadeIn",
		name = "Tooltip fade-in",
		description = "Adds a fade-in period equal to a percentage of tooltip duration, 0 to disable",
		position = 1,
		section = lifespan
	)
	@Units(Units.PERCENT)
	@Range(max = 100)
	default int tooltipFadeIn() { return 20; }

	@ConfigItem(
		keyName = "tooltipFadeout",
		name = "Tooltip fadeout",
		description = "Adds a fadeout period equal to a percentage of tooltip duration, 0 to disable",
		position = 2,
		section = lifespan
	)
	@Units(Units.PERCENT)
	@Range(max = 100)
	default int tooltipFadeout() { return 40; }

	@ConfigItem(
		keyName = "maximumTooltipsShown",
		name = "Max tooltips shown",
		description = "The maximum number of tooltips shown at any given time",
		position = 3,
		section = lifespan
	)
	@Range(min = 1)
	default int maximumTooltipsShown() { return 1; }

	@ConfigItem(
		keyName = "permanentTooltips",
		name = "Permanent tooltips",
		description = "Choose whether to use tooltips that do not disappear",
		position = 4,
		section = lifespan
	)
	default boolean permanentTooltips() { return false; }

	@ConfigItem(
		keyName = USE_CUSTOM_TEXT_COLOR,
		name = "Use custom text color",
		description = "Choose whether to apply the text color below to non-info tooltips",
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
		description = "Choose whether to apply the background color below to non-info tooltips",
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
		keyName = "overrideMenuColors",
		name = "Override menu colors",
		description = "Choose whether the custom text color, if enabled, should override menu colors",
		position = 4,
		section = appearance
	)
	default boolean overrideMenuColors() { return false; }

	@ConfigItem(
		keyName = "maximumOpacity",
		name = "Max opacity",
		description = "The maximum opacity of tooltips",
		position = 5,
		section = appearance
	)
	@Units(Units.PERCENT)
	@Range(max = 100)
	default int maximumOpacity() { return 100; }

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
		description = "Max opacity reduced by 25% of its current value",
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
		keyName = "tickSyncMode",
		name = "Tick sync mode",
		description = "Tooltips process at the next game tick instead of immediately",
		position = 3,
		section = modes
	)
	default boolean tickSyncMode() { return false; }

	@ConfigItem(
		keyName = "filterMode",
		name = "Filter mode",
		description = "Select the mode used for filtering tooltips based on user-managed lists",
		position = 4,
		section = modes
	)
	default LingeringClickTooltipsFilterMode filterMode() { return LingeringClickTooltipsFilterMode.NONE; }

	@ConfigItem(
		keyName = BLACKLIST_CSV,
		name = "Blacklist",
		description = "Tooltips matching text in this list will NOT show",
		position = 0,
		section = filterLists
	)
	default String blacklist() { return ""; }

	@ConfigItem(
		keyName = BLACKLIST_CSV,
		name = "",
		description = ""
	)
	void setBlacklist(String key);

	@ConfigItem(
		keyName = WHITELIST_CSV,
		name = "Whitelist",
		description = "Tooltips NOT matching text in this list will NOT show",
		position = 1,
		section = filterLists
	)
	default String whitelist() { return ""; }

	@ConfigItem(
		keyName = WHITELIST_CSV,
		name = "",
		description = ""
	)
	void setWhitelist(String key);

	@ConfigItem(
		keyName = "blockFilteredClicks",
		name = "Block filtered clicks",
		description = "Choose whether filtered clicks should be consumed, preventing native client processing",
		position = 2,
		section = filterLists
	)
	default boolean blockFilteredClicks() { return false; }

	@ConfigItem(
		keyName = "showBlockedClicks",
		name = "Show blocked clicks",
		description = "Choose whether tooltips appear for consumed clicks",
		position = 3,
		section = filterLists
	)
	default boolean showBlockedClicks() { return true; }

	@ConfigItem(
		keyName = LingeringClickTooltipsLocation.TOOLTIP_LOCATION_CONFIG_KEY,
		name = "Tooltip location",
		description = "Lingering remains at the click point, anchored follows the mouse cursor, fixed stays at a static location",
		position = 0,
		section = location
	)
	default LingeringClickTooltipsLocation tooltipLocation() { return LingeringClickTooltipsLocation.LINGERING; }

	@ConfigItem(
		keyName = "tooltipXOffset",
		name = "Tooltip x offset",
		description = "Horizontal offset for tooltips, lingering location only, higher values move the tooltip further right",
		position = 1,
		section = location
	)
	@Range(max = 100)
	default int tooltipXOffset() { return 0; }

	@ConfigItem(
		keyName = "tooltipYOffset",
		name = "Tooltip y offset",
		description = "Vertical offset for tooltips, lingering location only, positive values move the tooltip down",
		position = 2,
		section = location
	)
	@Range(min = -100, max = 100)
	default int tooltipYOffset() { return -20; }

	@ConfigItem(
		keyName = "clampXPadding",
		name = "Clamp x padding",
		description = "The minimum distance between tooltips and the left/right window borders, lingering location only, 0 means no gap",
		position = 3,
		section = location
	)
	@Range(max = 30)
	default int clampXPadding() { return 5; }

	@ConfigItem(
		keyName = "clampYPadding",
		name = "Clamp y padding",
		description = "The minimum distance between tooltips and the top/bottom window borders, lingering location only, 0 means no gap",
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
	default int ctrlDoubleTapDelay() { return 300; }

	@ConfigItem(
		keyName = "ctrlTogglesHide",
		name = "CTRL toggles hide",
		description = "Choose whether holding CTRL shows tooltips normally",
		position = 1,
		section = hotkeys
	)
	default boolean ctrlTogglesHide() { return true; }

	@ConfigItem(
		keyName = "ctrlBypassesBlock",
		name = "CTRL bypasses block",
		description = "Choose whether holding CTRL will allow blocked clicks to process",
		position = 2,
		section = hotkeys
	)
	default boolean ctrlBypassesBlock() { return true; }

	@ConfigItem(
		keyName = "shiftDoubleTapDelay",
		name = "SHIFT double-tap delay",
		description = "Double-tap delay for SHIFT to blacklist/whitelist tooltips, must be holding CTRL, 0 to disable",
		position = 3,
		section = hotkeys
	)
	@Units(Units.MILLISECONDS)
	@Range(max = 500)
	default int shiftDoubleTapDelay() { return 350; }

	@ConfigItem(
		keyName = "shiftPeeks",
		name = "SHIFT peeks",
		description = "Choose whether holding SHIFT for more than SHIFT double-tap delay produces peek tooltips, must be holding CTRL",
		position = 4,
		section = hotkeys
	)
	default boolean shiftPeeks() { return true; }

	@ConfigItem(
		keyName = "shiftBlocks",
		name = "SHIFT blocks",
		description = "Choose whether clicks should be consumed while holding SHIFT, must have filter mode set to blacklist/whitelist",
		position = 5,
		section = hotkeys
	)
	default boolean shiftBlocks() { return true; }

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
			keyName = HIDE_SPECIAL_ATTACK,
			name = "Special attack",
			description = "Choose whether using special attack should be hidden",
			position = 7,
			section = trivialClicks
	)
	default boolean hideSpecialAttack() { return true; }

	@ConfigItem(
		keyName = HIDE_QUICK_PRAYERS,
		name = "Quick-prayers",
		description = "Choose whether toggle quick-prayers should be hidden",
		position = 6,
		section = trivialClicks
	)
	default boolean hideQuickPrayers() { return true; }

	@ConfigItem(
		keyName = HIDE_PANEL_PRAYERS,
		name = "Panel prayers",
		description = "Choose whether toggling prayers from the prayer panel should be hidden",
		position = 8,
		section = trivialClicks
	)
	default boolean hidePanelPrayers() { return true; }

	@ConfigItem(
		keyName = HIDE_PANELS_GROUP,
		name = "Panels (group)",
		description = "Choose whether most clicks on panels should be hidden",
		position = 9,
		section = trivialClicks
	)
	default boolean hidePanelsGroup() { return true; }
}
