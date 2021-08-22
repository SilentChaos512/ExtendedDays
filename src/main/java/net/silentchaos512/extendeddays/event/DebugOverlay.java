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
import net.minecraftforge.fml.common.Mod;
import net.silentchaos512.extendeddays.ExtendedDays;
import net.silentchaos512.extendeddays.client.ClientInfo;
import net.silentchaos512.lib.client.gui.DebugRenderOverlay;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = ExtendedDays.MOD_ID)
public class DebugOverlay extends DebugRenderOverlay {
    private static final float TEXT_SCALE = 0.7f;
    private static final int SPLIT_WIDTH = 130;
    private static DebugOverlay instance;

    public static void init() {
        if (instance == null) {
            instance = new DebugOverlay(); // Also registers on event bus
        } else {
            ExtendedDays.LOGGER.warn("DebugOverlay already initialized!");
            ExtendedDays.LOGGER.catching(new IllegalStateException());
        }
    }

    @Nonnull
    @Override
    public List<String> getDebugText() {
        List<String> list = new ArrayList<>();

        World world = Minecraft.getInstance().level;
        if (world == null) {
            list.add("World not found!");
        } else {
            long dayLength = TimeEvents.getTotalDayLength();

            list.add(String.format("True Time%s%d / %d", SPLITTER,
                    ClientInfo.trueTime % dayLength,
                    dayLength
            ));
            list.add(String.format("Game/Day Time%s%d, %d", SPLITTER,
                    world.getGameTime(),
                    world.getDayTime()
            ));
            list.add(String.format("Day/Night Length%s%d, %d", SPLITTER,
                    TimeEvents.getDaytimeLength(),
                    TimeEvents.getNighttimeLength()
            ));
            list.add(String.format("Dimension%s %s", SPLITTER,
                    world.dimension().location()
            ));
        }
        return list;
    }

    @Override
    public float getTextScale() {
        return TEXT_SCALE;
    }

    @Override
    public boolean isHidden() {
        return false; //!Config.debugMode;
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
