/*
 * Extended Days -- BloodmoonCompat
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

package net.silentchaos512.extendeddays.compat;

import lumien.bloodmoon.client.ClientBloodmoonHandler;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.Loader;

/**
 * Compatibility with Bloodmoon mod. The functions in this class are safe to call even if Bloodmoon
 * is not installed.
 */
public class BloodmoonCompat {
    public static final BloodmoonCompat INSTANCE = new BloodmoonCompat();

    private boolean isModLoaded;

    private BloodmoonCompat() {
        isModLoaded = Loader.isModLoaded("bloodmoon");
    }

    public Vec3d moonColorHook(Vec3d moonColor) {
        if (isModLoaded) {
            if (ClientBloodmoonHandler.INSTANCE.isBloodmoonActive()) {
                return new Vec3d(0.8, 0.0, 0.0);
            }
        }
        return moonColor;
    }

    public Vec3d skyColorHook(Vec3d skyColor) {
        if (isModLoaded) {
            ClientBloodmoonHandler.INSTANCE.skyColorHook(skyColor);
        }
        return skyColor;
    }

    public boolean isBloodmoonActive() {
        if (isModLoaded) {
            return ClientBloodmoonHandler.INSTANCE.isBloodmoonActive();
        }
        return false;
    }
}
