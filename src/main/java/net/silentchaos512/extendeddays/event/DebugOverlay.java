/*
 * Extended Days -- DebugOverlay
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.extendeddays.event;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.silentchaos512.extendeddays.ExtendedDays;
import net.silentchaos512.extendeddays.config.Config;
import net.silentchaos512.lib.client.gui.DebugRenderOverlay;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class DebugOverlay extends DebugRenderOverlay {
    private static final float TEXT_SCALE = 0.7f;
    private static final int SPLIT_WIDTH = 130;
    private static DebugOverlay instance;

    private int lastCapacity = 5;

    public static void init() {
        if (instance == null) {
            instance = new DebugOverlay(); // Also registers on event bus
        } else {
            ExtendedDays.logHelper.warn("DebugOverlay already initialized!");
            ExtendedDays.logHelper.catching(new IllegalStateException());
        }
    }

    @Nonnull
    @Override
    public List<String> getDebugText() {
        List<String> list = new ArrayList<>(lastCapacity);

        World world = Minecraft.getMinecraft().world;
        if (world == null || world.provider == null) {
            list.add("World not found!");
        } else {
            list.add(String.format("Time (MC, Ext)%s%d, %d %s", SPLITTER,
                    world.getWorldTime(),
                    TimeEvents.INSTANCE.getExtendedTime(),
                    (TimeEvents.INSTANCE.isInExtendedPeriod(world) ? "(E)" : "")));
            list.add(String.format("Effective Time%s%d / %d", SPLITTER,
                    TimeEvents.INSTANCE.getCurrentTime(world),
                    TimeEvents.INSTANCE.getTotalDayLength(world)));
            list.add(String.format("Day/Night Length%s%d, %d", SPLITTER,
                    TimeEvents.INSTANCE.getDaytimeLength(world),
                    TimeEvents.INSTANCE.getNighttimeLength(world)));
            list.add(String.format("ClientEvents#worldTime%s%d", SPLITTER,
                    ClientEvents.worldTime));
            list.add(String.format("Dimension%s%d, %s", SPLITTER,
                    world.provider.getDimension(),
                    world.provider.getDimensionType()));

            lastCapacity = list.size();
        }
        return list;
    }

    @Override
    public float getTextScale() {
        return TEXT_SCALE;
    }

    @Override
    public boolean isHidden() {
        return !Config.debugMode;
    }

    @Override
    public int getStartX() {
        return 5;
    }

    @Override
    public int getStartY() {
        return 30;
    }

    @Override
    public int getUpdateFrequency() {
        return 0;
    }

    @Override
    public int getSplitWidth() {
        return SPLIT_WIDTH;
    }
}
