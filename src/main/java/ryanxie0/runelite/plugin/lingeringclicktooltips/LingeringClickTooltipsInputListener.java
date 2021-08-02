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
    public MouseEvent mousePressed(MouseEvent e)
    {
        return processMousePressed(e);
    }

    @Override
    public MouseEvent mouseEntered(MouseEvent e)
    {
        return processMouseEntered(e);
    }

    @Override
    public MouseEvent mouseExited(MouseEvent e)
    {
        return processMouseExited(e);
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        processKeyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        processKeyReleased(e);
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
        processKeyTyped(e);
    }

    protected MouseEvent processMousePressed(MouseEvent e)
    {
        if (e.getButton() == MouseEvent.BUTTON1)
        {
            lastClickPoint = e.getPoint();
        }
        return e;
    }

    protected MouseEvent processMouseEntered(MouseEvent e)
    {
        isMouseOverCanvas = true;
        return e;
    }

    protected MouseEvent processMouseExited(MouseEvent e)
    {
        isMouseOverCanvas = false;
        return e;
    }

    protected void processKeyPressed(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_CONTROL)
        {
            processCtrlPressed(e);
        }
        else if (e.getKeyCode() == KeyEvent.VK_SHIFT)
        {
            processShiftPressed(e);
        }
    }

    protected void processKeyTyped(KeyEvent e)
    {

    }

    /**
     * Processes CTRL being pressed. The CTRL double-tap to toggle tooltips is implemented by tracking the last time
     * the CTRL key was pressed and comparing it to the double-tap delay in the config. lastCtrlPressTime reflects the
     * moment the CTRL key was pressed, and does NOT continuously update as the CTRL key is held down.
     */
    private void processCtrlPressed(KeyEvent e)
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
     * Processes SHIFT being pressed. The SHIFT double-tap to update the filter lists is implemented by tracking
     * the last time the SHIFT key was pressed and comparing it to the double-tap delay in the config. lastCtrlPressTime
     * reflects the moment the SHIFT key was pressed, and does NOT continuously update as the SHIFT key is held down.
     * This is so that holding the SHIFT key (for longer than the SHIFT double-tap delay) to peek filter list actions
     * will accurately track the duration the SHIFT key is held for. The peek tooltip shows as long as SHIFT is held down.
     */
    private void processShiftPressed(KeyEvent e)
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
                    if (config.shiftPeeksFilterListAction())
                    {
                        queueManager.createPeekInfoTooltip();
                    }
                }
            }
            e.consume(); // consume to avoid conflict with SHIFT-drop
        }
    }

    protected void processKeyReleased(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_CONTROL)
        {
            processCtrlReleased(e);
        }
        else if (e.getKeyCode() == KeyEvent.VK_SHIFT)
        {
            processShiftReleased(e);
        }
    }

    /**
     * Processes CTRL being released. If CTRL was held down for too long (>CTRL double-tap delay), the lastCtrlPressTime
     * is nulled, effectively timing out taps that are too long. This enforces a stronger double-tap behavior.
     */
    private void processCtrlReleased(KeyEvent e)
    {
        if (isCtrlPressed && config.ctrlDoubleTapDelay() > 0)
        {
            if (lastCtrlPressTime != null && Duration.between(lastCtrlPressTime, Instant.now()).compareTo(Duration.ofMillis(config.ctrlDoubleTapDelay())) > 0)
            {
                lastCtrlPressTime = null; // we want to time out presses that are too long to enforce a strong double-tap behavior
            }
        }
        isCtrlPressed = false;
    }

    /**
     * Processes SHIFT being released. If SHIFT was held down for too long (>SHIFT double-tap delay), the lastShiftPressTime
     * is nulled, effectively timing out taps that are too long. This enforces a stronger double-tap behavior.
     */
    private void processShiftReleased(KeyEvent e)
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
            isShiftPressed = false;
        }
    }

    public void initialize(LingeringClickTooltipsQueueManager queueManager)
    {
        this.queueManager = queueManager;
    }

    public void destroy()
    {
        queueManager = null;
        lastCtrlPressTime = null;
        lastShiftPressTime = null;
    }
}
