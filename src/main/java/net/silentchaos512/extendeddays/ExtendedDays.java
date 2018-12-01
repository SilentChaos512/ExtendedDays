package net.silentchaos512.extendeddays;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.silentchaos512.extendeddays.command.CommandExtTime;
import net.silentchaos512.extendeddays.config.Config;
import net.silentchaos512.extendeddays.init.ModItems;
import net.silentchaos512.extendeddays.network.MessageSetTime;
import net.silentchaos512.extendeddays.network.MessageSyncTime;
import net.silentchaos512.extendeddays.proxy.CommonProxy;
import net.silentchaos512.lib.base.IModBase;
import net.silentchaos512.lib.network.NetworkHandlerSL;
import net.silentchaos512.lib.registry.SRegistry;
import net.silentchaos512.lib.util.I18nHelper;
import net.silentchaos512.lib.util.LogHelper;

@Mod(modid = ExtendedDays.MOD_ID,
        name = ExtendedDays.MOD_NAME,
        version = ExtendedDays.VERSION,
        dependencies = ExtendedDays.DEPENDENCIES,
        guiFactory = "net.silentchaos512.extendeddays.client.gui.config.GuiFactoryExtendedDays")
@MethodsReturnNonnullByDefault
@SuppressWarnings({"unused", "WeakerAccess"})
public class ExtendedDays implements IModBase {
    public static final String MOD_ID = "extendeddays";
    public static final String MOD_NAME = "Extended Days";
    public static final String VERSION = "0.2.9";
    public static final String VERSION_SILENTLIB = "3.0.9";
    public static final int BUILD_NUM = 0;
    public static final String DEPENDENCIES = "required-after:silentlib@[" + VERSION_SILENTLIB + ",);after:morpheus";
    public static final String RESOURCE_PREFIX = MOD_ID + ":";

    @Instance
    public static ExtendedDays instance;

    @SidedProxy(clientSide = "net.silentchaos512.extendeddays.proxy.ClientProxy", serverSide = "net.silentchaos512.extendeddays.proxy.CommonProxy")
    public static CommonProxy proxy;

    public static LogHelper logHelper = new LogHelper(MOD_NAME, BUILD_NUM);
    public static final I18nHelper i18n = new I18nHelper(MOD_ID, logHelper, true);

    public static SRegistry registry = new SRegistry();
    public static NetworkHandlerSL network;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        registry.setMod(this);
        registry.getRecipeMaker().setJsonHellMode(isDevBuild());

        network = new NetworkHandlerSL(MOD_ID);
        network.register(MessageSyncTime.class, Side.CLIENT);
        network.register(MessageSetTime.class, Side.SERVER);

        Config.INSTANCE.init(event.getSuggestedConfigurationFile());

        registry.addRegistrationHandler(ModItems::registerAll, Item.class);

        proxy.preInit(registry, event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(registry, event);
        Config.INSTANCE.save();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(registry, event);
    }

    @EventHandler
    public void onServerLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandExtTime());
    }

    @Override
    public String getModId() {
        return MOD_ID;
    }

    @Override
    public String getModName() {
        return MOD_NAME;
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public int getBuildNum() {
        return BUILD_NUM;
    }
}
