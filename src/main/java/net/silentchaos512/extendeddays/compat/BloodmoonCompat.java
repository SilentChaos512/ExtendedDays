package net.silentchaos512.extendeddays.compat;

import lumien.bloodmoon.client.ClientBloodmoonHandler;
import lumien.bloodmoon.server.BloodmoonHandler;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.Loader;
import net.silentchaos512.extendeddays.ExtendedDays;

/**
 * Compatibility with Bloodmoon mod. The functions in this class are safe to call even if Bloodmoon is not installed.
 * 
 * @author Silent
 *
 */
public class BloodmoonCompat {

  public static final BloodmoonCompat INSTANCE = new BloodmoonCompat();

  boolean isModLoaded;

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
