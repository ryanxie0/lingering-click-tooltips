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
package ryanxie0.runelite.plugin.lingeringclicktooltips.filtering;

public class LingeringClickTooltipsFilteringConstants {

    // config key names
    public static final String HIDE_TRIVIAL_CLICKS = "hideTrivialClicks";
    public static final String HIDE_WALK_HERE = "hideWalkHere";
    public static final String HIDE_WALK_HERE_WITH_TARGET = "hideWalkHereWithTarget";
    public static final String HIDE_WIELD = "hideWield";
    public static final String HIDE_WEAR = "hideWear";
    public static final String HIDE_TOGGLE_RUN = "hideToggleRun";
    public static final String HIDE_SPECIAL_ATTACK = "hideSpecialAttack";
    public static final String HIDE_QUICK_PRAYERS = "hideQuickPrayers";
    public static final String HIDE_PANEL_PRAYERS = "hidePanelPrayers";
    public static final String HIDE_PANELS_GROUP = "hidePanelsGroup";
    public static final String WHITELIST_CSV = "whitelist";
    public static final String BLACKLIST_CSV = "blacklist";

    // default trivial clicks
    public static final String CANCEL = "Cancel";
    public static final String CONTINUE = "Continue";
    public static final String PLAY = "Play";
    public static final String LOGOUT = "Logout";
    public static final String CLOSE = "Close";
    public static final String WORLD_SWITCHER = "World Switcher";
    public static final String SELECT = "Select";
    public static final String SHIFT_DROP = "Drop";
    public static final String USE_INITIATE = "Use";
    public static final String EAT = "Eat";
    public static final String MOVE = "Move";

    // panel trivial clicks group
    public static final String COMBAT_OPTIONS = "Combat Options";
    public static final String SKILLS = "Skills";
    public static final String CHARACTER_SUMMARY = "Character Summary";
    public static final String QUEST_LIST = "Quest List";
    public static final String ACHIEVEMENT_DIARIES = "Achievement Diaries";
    public static final String KOUREND_FAVOUR = "Kourend Favour";
    public static final String INVENTORY = "Inventory";
    public static final String WORN_EQUIPMENT = "Worn Equipment";
    public static final String PRAYER = "Prayer";
    public static final String MAGIC = "Magic";
    public static final String CHAT_CHANNEL = "Chat-channel";
    public static final String YOUR_CLAN = "Your Clan";
    public static final String VIEW_ANOTHER_CLAN = "View another clan";
    public static final String GROUPING = "Grouping";
    public static final String FRIENDS_LIST = "Friends List";
    public static final String IGNORE_LIST = "Ignore List";
    public static final String ACCOUNT_MANAGEMENT = "Account Management";
    public static final String SETTINGS = "Settings";
    public static final String EMOTES = "Emotes";
    public static final String MUSIC_PLAYER = "Music Player";

    // configurable trivial clicks
    public static final String WALK_HERE = "Walk here";
    public static final String WALK_HERE_WITH_TARGET = WALK_HERE + "[TARGET]"; // special case placeholder, does not match text
    public static final String WIELD = "Wield";
    public static final String WEAR = "Wear";
    public static final String TOGGLE_RUN = "Toggle Run";
    public static final String USE_SPECIAL_ATTACK = "Use Special Attack";
    public static final String ACTIVATE = "Activate";
    public static final String DEACTIVATE = "Deactivate";
    public static final String QUICK_PRAYERS = "Quick-prayers";
    public static final String ACTIVATE_QUICK_PRAYERS = ACTIVATE + " " + QUICK_PRAYERS;
    public static final String DEACTIVATE_QUICK_PRAYERS = DEACTIVATE + " " + QUICK_PRAYERS;
    public static final String TOGGLE_PANEL_PRAYER = ACTIVATE + DEACTIVATE + "[PRAYER]"; // special case placeholder, does not match text
}
