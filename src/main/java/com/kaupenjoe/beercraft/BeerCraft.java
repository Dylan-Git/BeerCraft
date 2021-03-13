package com.kaupenjoe.beercraft;

import com.kaupenjoe.beercraft.block.ModBlocks;
import com.kaupenjoe.beercraft.block.ModFluids;
import com.kaupenjoe.beercraft.client.gui.ModGUIs;
import com.kaupenjoe.beercraft.item.ModItems;
import com.kaupenjoe.beercraft.network.NetworkHandler;
import com.kaupenjoe.beercraft.network.packet.TileGuiPacket;
import com.kaupenjoe.beercraft.packets.FluidPacket;
import com.kaupenjoe.beercraft.setup.ClientProxy;
import com.kaupenjoe.beercraft.setup.IProxy;
import com.kaupenjoe.beercraft.setup.ServerProxy;
import com.kaupenjoe.beercraft.tileentity.ModTileEntities;
import com.kaupenjoe.beercraft.util.Config;
import com.kaupenjoe.beercraft.util.Registration;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(BeerCraft.MOD_ID)
public class BeerCraft
{
    public static final String MOD_ID = "beercraft";

    public static IProxy proxy;

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public static NetworkHandler NETWORK_HANDLER
            = new NetworkHandler(new ResourceLocation(MOD_ID, "general"));

    public BeerCraft()
    {
        registerPackets();

        proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        registerModAdditions();

        MinecraftForge.EVENT_BUS.register(this);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void registerConfigs()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);
    }

    private void loadConfigs()
    {
        Config.loadConfigFile(Config.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve("beercraft-client.toml").toString());
        Config.loadConfigFile(Config.SERVER_CONFIG, FMLPaths.CONFIGDIR.get().resolve("beercraft-server.toml").toString());
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        registerConfigs();
        proxy.init();
        loadConfigs();
    }

    private void registerPackets()
    {
        NETWORK_HANDLER.registerPacket(1, TileGuiPacket::new);
    }

    // Really important Comment!
    private void registerModAdditions()
    {
        // Inits the registration of our additions
        Registration.init();

        // registers items, blocks etc added by our mod
        ModItems.register();
        ModBlocks.register();
        ModFluids.register();

        ModTileEntities.register();
        ModGUIs.register();

        // ModSoundEvents.register();

        // register mod events
        // MinecraftForge.EVENT_BUS.register(new ModEvents());
    }
}
