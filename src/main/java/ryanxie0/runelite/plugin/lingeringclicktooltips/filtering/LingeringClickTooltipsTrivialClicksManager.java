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

import ryanxie0.runelite.plugin.lingeringclicktooltips.LingeringClickTooltipsConfig;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

import static ryanxie0.runelite.plugin.lingeringclicktooltips.filtering.LingeringClickTooltipsFilteringConstants.*;

public class LingeringClickTooltipsTrivialClicksManager {

    @Inject
    private LingeringClickTooltipsConfig config;

    private static Set<String> configurableTrivialClicks;
    private static Set<String> defaultTrivialClicks;
    private static Set<String> panelTrivialClicks;

    public void updateFromConfig(String configKey)
    {
        switch (configKey)
        {
            case HIDE_WALK_HERE:
                modifyConfigurableTrivialClicks(config.hideWalkHere(), WALK_HERE);
                break;
            case HIDE_WALK_HERE_WITH_TARGET:
                modifyConfigurableTrivialClicks(config.hideWalkHereWithTarget(), WALK_HERE_WITH_TARGET);
                break;
            case HIDE_WIELD:
                modifyConfigurableTrivialClicks(config.hideWield(), WIELD);
                break;
            case HIDE_WEAR:
                modifyConfigurableTrivialClicks(config.hideWear(), WEAR);
                break;
            case HIDE_TOGGLE_RUN:
                modifyConfigurableTrivialClicks(config.hideToggleRun(), TOGGLE_RUN);
                break;
            case HIDE_SPECIAL_ATTACK:
                modifyConfigurableTrivialClicks(config.hideSpecialAttack(), USE_SPECIAL_ATTACK);
                break;
            case HIDE_QUICK_PRAYERS:
                modifyConfigurableTrivialClicks(config.hideQuickPrayers(), ACTIVATE_QUICK_PRAYERS);
                modifyConfigurableTrivialClicks(config.hideQuickPrayers(), DEACTIVATE_QUICK_PRAYERS);
                break;
            case HIDE_PANEL_PRAYERS:
                modifyConfigurableTrivialClicks(config.hidePanelPrayers(), TOGGLE_PANEL_PRAYER);
                break;
            case HIDE_PANELS_GROUP:
                modifyConfigurableTrivialClicks(config.hidePanelsGroup(), panelTrivialClicks);
                break;
            default:
                break;
        }
    }

    private static void modifyConfigurableTrivialClicks(boolean configEnabled, String text)
    {
        if (configEnabled)
        {
            configurableTrivialClicks.add(text);
        }
        else
        {
            configurableTrivialClicks.remove(text);
        }
    }

    private static void modifyConfigurableTrivialClicks(boolean configEnabled, Set<String> textGroup)
    {
        if (configEnabled)
        {
            configurableTrivialClicks.addAll(textGroup);
        }
        else
        {
            configurableTrivialClicks.removeAll(textGroup);
        }
    }

    public static boolean defaultContains(String text)
    {
        return defaultTrivialClicks.contains(text);
    }

    public static boolean configurableContains(String text)
    {
        return configurableTrivialClicks.contains(text);
    }

    public void initialize()
    {
        initializeDefault();
        initializePanelGroup();
        initializeConfigurable();
    }

    public void destroy()
    {
        defaultTrivialClicks.clear();
        defaultTrivialClicks = null;
        panelTrivialClicks.clear();
        panelTrivialClicks = null;
        configurableTrivialClicks.clear();
        configurableTrivialClicks = null;
    }

    private void initializeDefault()
    {
        defaultTrivialClicks = new HashSet<>();
        defaultTrivialClicks.add(CANCEL);
        defaultTrivialClicks.add(CONTINUE);
        defaultTrivialClicks.add(PLAY);
        defaultTrivialClicks.add(LOGOUT);
        defaultTrivialClicks.add(CLOSE);
        defaultTrivialClicks.add(WORLD_SWITCHER);
        defaultTrivialClicks.add(SELECT);
        defaultTrivialClicks.add(SHIFT_DROP);
        defaultTrivialClicks.add(USE_INITIATE);
        defaultTrivialClicks.add(EAT);
        defaultTrivialClicks.add(MOVE_SLIDING_PIECE);
    }

    private void initializePanelGroup()
    {
        panelTrivialClicks = new HashSet<>();
        panelTrivialClicks.add(COMBAT_OPTIONS);
        panelTrivialClicks.add(SKILLS);
        panelTrivialClicks.add(CHARACTER_SUMMARY);
        panelTrivialClicks.add(QUEST_LIST);
        panelTrivialClicks.add(ACHIEVEMENT_DIARIES);
        panelTrivialClicks.add(KOUREND_FAVOUR);
        panelTrivialClicks.add(INVENTORY);
        panelTrivialClicks.add(WORN_EQUIPMENT);
        panelTrivialClicks.add(PRAYER);
        panelTrivialClicks.add(MAGIC);
        panelTrivialClicks.add(CHAT_CHANNEL);
        panelTrivialClicks.add(YOUR_CLAN);
        panelTrivialClicks.add(VIEW_ANOTHER_CLAN);
        panelTrivialClicks.add(GROUPING);
        panelTrivialClicks.add(FRIENDS_LIST);
        panelTrivialClicks.add(IGNORE_LIST);
        panelTrivialClicks.add(ACCOUNT_MANAGEMENT);
        panelTrivialClicks.add(SETTINGS);
        panelTrivialClicks.add(EMOTES);
        panelTrivialClicks.add(MUSIC_PLAYER);
    }

    private void initializeConfigurable()
    {
        configurableTrivialClicks = new HashSet<>();
        modifyConfigurableTrivialClicks(config.hideWalkHere(), WALK_HERE);
        modifyConfigurableTrivialClicks(config.hideWalkHereWithTarget(), WALK_HERE_WITH_TARGET);
        modifyConfigurableTrivialClicks(config.hideWield(), WIELD);
        modifyConfigurableTrivialClicks(config.hideWear(), WEAR);
        modifyConfigurableTrivialClicks(config.hideToggleRun(), TOGGLE_RUN);
        modifyConfigurableTrivialClicks(config.hideSpecialAttack(), USE_SPECIAL_ATTACK);
        modifyConfigurableTrivialClicks(config.hideQuickPrayers(), ACTIVATE_QUICK_PRAYERS);
        modifyConfigurableTrivialClicks(config.hideQuickPrayers(), DEACTIVATE_QUICK_PRAYERS);
        modifyConfigurableTrivialClicks(config.hidePanelPrayers(), TOGGLE_PANEL_PRAYER);
        modifyConfigurableTrivialClicks(config.hidePanelsGroup(), panelTrivialClicks);
    }
}
