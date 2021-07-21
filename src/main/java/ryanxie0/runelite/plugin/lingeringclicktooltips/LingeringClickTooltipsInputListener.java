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

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.time.Duration;
import java.time.Instant;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.MouseAdapter;

public class LingeringClickTooltipsInputListener extends MouseAdapter implements KeyListener
{
    private static final int HOTKEY = KeyEvent.VK_CONTROL;
    private Instant lastPress;

    @Inject
    private LingeringClickTooltipsPlugin plugin;

    @Inject
    private LingeringClickTooltipsConfig config;

    @Getter(AccessLevel.PACKAGE)
    private Point lastClickPos;

    @Override
    public MouseEvent mousePressed(MouseEvent e)
    {
        lastClickPos = e.getPoint();
        return e;
    }

    @Override
    public void keyTyped(KeyEvent e)
    {

    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        if (e.getKeyCode() == HOTKEY)
        {
            if (lastPress != null && !plugin.isHotkeyPressed() && config.hotkeyToggleDelay() > 0 && Duration.between(lastPress, Instant.now()).compareTo(Duration.ofMillis(config.hotkeyToggleDelay())) < 0)
            {
                plugin.setHide(!plugin.isHide());
                plugin.showToggledHideModeTooltip();
                lastPress = null;
            }
            else {
                plugin.setHotkeyPressed(true);
                lastPress = Instant.now();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        if (e.getKeyCode() == HOTKEY)
        {
            plugin.setHotkeyPressed(false);
        }
    }
}
