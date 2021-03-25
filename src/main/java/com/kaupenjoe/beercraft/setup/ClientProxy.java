package com.kaupenjoe.beercraft.setup;

import com.kaupenjoe.beercraft.BeerCraft;
import com.kaupenjoe.beercraft.block.ModBlocks;
import com.kaupenjoe.beercraft.client.gui.ModGUIs;
import com.kaupenjoe.beercraft.client.gui.TankScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BeerCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientProxy implements IProxy
{
    @Override
    public void init()
    {
        RenderTypeLookup.setRenderLayer(ModBlocks.TANK_BLOCK.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.FLUID_PIPE.get(), RenderType.getCutout());

        ScreenManager.registerFactory(ModGUIs.TANK_CONTAINER.get(), TankScreen::new);
    }

    @Override
    public World getClientWorld()
    {
        return Minecraft.getInstance().world;
    }
}
