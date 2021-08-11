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
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.MouseAdapter;

import javax.inject.Inject;

public class LingeringClickTooltipsInputListener extends MouseAdapter implements KeyListener
{
    @Inject
    private LingeringClickTooltipsPlugin plugin;

    @Inject
    private LingeringClickTooltipsConfig config;

    @Getter(AccessLevel.PACKAGE)
    private Point lastClickPoint;

    @Getter(AccessLevel.PACKAGE)
    private boolean isCtrlPressed;

    @Getter(AccessLevel.PACKAGE)
    private boolean isShiftPressed;

    @Getter(AccessLevel.PACKAGE)
    private boolean isMouseOverCanvas;

    @Getter(AccessLevel.PACKAGE)
    private boolean isHide;

    private LingeringClickTooltipsQueueManager queueManager;

    private Instant lastCtrlPressTime;
    private Instant lastShiftPressTime;

    @Override
    public MouseEvent mousePressed(MouseEvent event)
    {
        if (event.getButton() == MouseEvent.BUTTON1)
        {
            lastClickPoint = event.getPoint();
        }
        return event;
    }

    @Override
    public MouseEvent mouseEntered(MouseEvent event)
    {
        isMouseOverCanvas = true;
        return event;
    }

    @Override
    public MouseEvent mouseExited(MouseEvent event)
    {
        isMouseOverCanvas = false;
        return event;
    }

    @Override
    public void keyPressed(KeyEvent event)
    {
        if (event.getKeyCode() == KeyEvent.VK_CONTROL)
        {
            processCtrlPressed(event);
        }
        else if (event.getKeyCode() == KeyEvent.VK_SHIFT)
        {
            processShiftPressed(event);
        }
    }

    @Override
    public void keyReleased(KeyEvent event)
    {
        if (event.getKeyCode() == KeyEvent.VK_CONTROL)
        {
            processCtrlReleased(event);
        }
        else if (event.getKeyCode() == KeyEvent.VK_SHIFT)
        {
            processShiftReleased(event);
        }
    }

    @Override
    public void keyTyped(KeyEvent event)
    {

    }

    /**
     * Processes CTRL being pressed. Manages double-tap CTRL to toggle hide.
     * @param event the KeyEvent from a CTRL press
     */
    private void processCtrlPressed(KeyEvent event)
    {
        if (!isCtrlPressed)
        {
            if (lastCtrlPressTime != null && config.ctrlDoubleTapDelay() > 0
                && Duration.between(lastCtrlPressTime, Instant.now()).compareTo(Duration.ofMillis(config.ctrlDoubleTapDelay())) < 0)
            {
                isHide = !isHide;
                queueManager.createHideModeInfoTooltip(isHide);
                lastCtrlPressTime = null;
            }
            else
            {
                isCtrlPressed = true;
                lastCtrlPressTime = Instant.now();
            }
        }
    }

    /**
     * Processes SHIFT being pressed. Manages double-tap SHIFT to update filter lists and hold SHIFT to peek.
     * @param event the KeyEvent from a SHIFT press, consumed to avoid SHIFT-drop conflicts
     */
    private void processShiftPressed(KeyEvent event)
    {
        if (isCtrlPressed)
        {
            if (!isShiftPressed)
            {
                if (lastShiftPressTime != null && config.shiftDoubleTapDelay() > 0
                    && Duration.between(lastShiftPressTime, Instant.now()).compareTo(Duration.ofMillis(config.shiftDoubleTapDelay())) < 0)
                {
                    queueManager.createFilterListUpdateInfoTooltip();
                    lastShiftPressTime = null;
                }
                else
                {
                    isShiftPressed = true;
                    lastShiftPressTime = Instant.now();
                }
            }
            else
            {
                if (lastShiftPressTime != null && config.shiftDoubleTapDelay() > 0
                    && Duration.between(lastShiftPressTime, Instant.now()).compareTo(Duration.ofMillis(config.shiftDoubleTapDelay())) > 0)
                {
                    if (config.shiftPeeks())
                    {
                        queueManager.createPeekInfoTooltip();
                    }
                }
            }
            event.consume(); // consume to avoid conflict with SHIFT-drop
        }
    }

    /**
     * Processes CTRL being released. Manages the strong double-tap behavior by timing out long keypresses.
     * @param event the KeyEvent from a CTRL release
     */
    private void processCtrlReleased(KeyEvent event)
    {
        if (isCtrlPressed && config.ctrlDoubleTapDelay() > 0)
        {
            if (lastCtrlPressTime != null && Duration.between(lastCtrlPressTime, Instant.now()).compareTo(Duration.ofMillis(config.ctrlDoubleTapDelay())) > 0)
            {
                lastCtrlPressTime = null; // we want to time out presses that are too long to enforce a strong double-tap behavior
            }
        }
        isCtrlPressed = false;
        isShiftPressed = false;
    }

    /**
     * Processes SHIFT being released. Manages the strong double-tap behavior by timing out long keypresses.
     * @param event the KeyEvent from a SHIFT release
     */
    private void processShiftReleased(KeyEvent event)
    {
        if (isCtrlPressed)
        {
            if (isShiftPressed && config.shiftDoubleTapDelay() > 0)
            {
                if (lastShiftPressTime != null && Duration.between(lastShiftPressTime, Instant.now()).compareTo(Duration.ofMillis(config.shiftDoubleTapDelay())) > 0)
                {
                    lastShiftPressTime = null; // we want to time out presses that are too long to enforce a strong double-tap behavior
                }
            }
        }
        isShiftPressed = false;
    }

    public void initialize(LingeringClickTooltipsQueueManager queueManager)
    {
        this.queueManager = queueManager;
        isCtrlPressed = false;
        isShiftPressed = false;
        isMouseOverCanvas = true;
        isHide = false;
    }

    public void destroy()
    {
        queueManager = null;
        lastCtrlPressTime = null;
        lastShiftPressTime = null;
    }
}
