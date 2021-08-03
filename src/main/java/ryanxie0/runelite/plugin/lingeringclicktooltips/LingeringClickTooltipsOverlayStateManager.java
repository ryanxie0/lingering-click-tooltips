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

import net.runelite.client.ui.overlay.OverlayPosition;

import java.awt.Point;

public class LingeringClickTooltipsOverlayStateManager {

    private LingeringClickTooltipsOverlay managedOverlay;

    private OverlayPosition lastPosition;
    private OverlayPosition lastPreferredPosition;
    private Point lastPreferredLocation;

    public void save()
    {
        lastPosition = managedOverlay.getPosition();
        lastPreferredPosition = managedOverlay.getPreferredPosition();
        lastPreferredLocation = managedOverlay.getPreferredLocation();
    }

    public void load()
    {
        managedOverlay.setPosition(lastPosition);
        managedOverlay.setPreferredPosition(lastPreferredPosition);
        managedOverlay.setPreferredLocation(lastPreferredLocation);
    }

    public void setDynamic()
    {
        managedOverlay.setPosition(OverlayPosition.DYNAMIC);
        managedOverlay.setPreferredPosition(null);
    }

    public boolean isDynamic()
    {
        return managedOverlay.getPosition() == OverlayPosition.DYNAMIC;
    }

    public void forceDynamic()
    {
        if (isDynamic() && managedOverlay.getPreferredPosition() != null)
        {
            setDynamic();
        }
    }

    public void initialize(LingeringClickTooltipsOverlay overlay)
    {
        managedOverlay = overlay;
        save();
    }

    public void destroy()
    {
        this.managedOverlay = null;
        this.lastPosition = null;
        this.lastPreferredPosition = null;
        this.lastPreferredLocation = null;
    }
}
