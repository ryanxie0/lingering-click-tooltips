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
package ryanxie0.runelite.plugin.lingeringclicktooltips.util;

import ryanxie0.runelite.plugin.lingeringclicktooltips.LingeringClickTooltipsConfig;
import java.util.HashSet;
import java.util.Set;

public class LingeringClickTooltipsTrivialClicksMapper {

    private static Set<String> defaultTrivialClicks;
    private static Set<String> trivialClicks;

    private static final String WALK_HERE = "Walk here";
    private static final String CONTINUE = "Continue";
    private static final String SHIFT_DROP = "Drop";
    private static final String USE = "Use";
    private static final String EAT = "Eat";
    private static final String MOVE = "Move";

    public LingeringClickTooltipsTrivialClicksMapper(LingeringClickTooltipsConfig config)
    {
        defaultTrivialClicks = new HashSet<>();
        trivialClicks = new HashSet<>();
        initialize(config);
    }

    private void initialize(LingeringClickTooltipsConfig config)
    {
        defaultTrivialClicks.add("Cancel");
        defaultTrivialClicks.add("Play");
        defaultTrivialClicks.add("Logout");
        updateTrivialClicks(config.hideWalkHere(), WALK_HERE);
        updateTrivialClicks(config.hideContinue(), CONTINUE);
        updateTrivialClicks(config.hideShiftDrop(), SHIFT_DROP);
        updateTrivialClicks(config.hideUseInitiate(), USE);
        updateTrivialClicks(config.hideEat(), EAT);
        updateTrivialClicks(config.hidePuzzles(), MOVE);
    }

    public void destroy()
    {
        defaultTrivialClicks.clear();
        defaultTrivialClicks = null;
        trivialClicks.clear();
        trivialClicks = null;
    }

    public static boolean contains(String text)
    {
        return trivialClicks.contains(text);
    }

    public static boolean defaultContains(String text)
    {
        return defaultTrivialClicks.contains(text);
    }

    public void update(LingeringClickTooltipsConfig config, String configKeyName)
    {
        switch(configKeyName)
        {
            case "hideWalkHere":
                updateTrivialClicks(config.hideWalkHere(), WALK_HERE);
                break;
            case "hideContinue":
                updateTrivialClicks(config.hideContinue(), CONTINUE);
                break;
            case "hideShiftDrop":
                updateTrivialClicks(config.hideShiftDrop(), SHIFT_DROP);
                break;
            case "hideUseInitiate":
                updateTrivialClicks(config.hideUseInitiate(), USE);
                break;
            case "hideEat":
                updateTrivialClicks(config.hideEat(), EAT);
                break;
            case "hidePuzzles":
                updateTrivialClicks(config.hidePuzzles(), MOVE);
                break;
            default:
                break;
        }
    }

    private void updateTrivialClicks(boolean configEnabled, String text)
    {
        if (configEnabled)
        {
            trivialClicks.add(text);
        }
        else
        {
            trivialClicks.remove(text);
        }
    }
}
