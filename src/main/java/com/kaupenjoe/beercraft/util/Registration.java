package com.kaupenjoe.beercraft.util;

import com.kaupenjoe.beercraft.BeerCraft;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.debug.BeeDebugRenderer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Registration
{
    public static final DeferredRegister<Block> BLOCKS
            = DeferredRegister.create(ForgeRegistries.BLOCKS, BeerCraft.MOD_ID);

    public static final DeferredRegister<Item> ITEMS
            = DeferredRegister.create(ForgeRegistries.ITEMS, BeerCraft.MOD_ID);

    public static final DeferredRegister<Fluid> FLUIDS
            = DeferredRegister.create(ForgeRegistries.FLUIDS, BeerCraft.MOD_ID);

    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES
            = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, BeerCraft.MOD_ID);

    public static final DeferredRegister<ContainerType<?>> CONTAINERS
            = DeferredRegister.create(ForgeRegistries.CONTAINERS, BeerCraft.MOD_ID);

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS
            = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BeerCraft.MOD_ID);

    public static final DeferredRegister<Enchantment> ENCHANTMENTS
            = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, BeerCraft.MOD_ID);

    public static void init() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        FLUIDS.register(eventBus);
        TILE_ENTITY_TYPES.register(eventBus);
        CONTAINERS.register(eventBus);
        SOUND_EVENTS.register(eventBus);
        ENCHANTMENTS.register(eventBus);
    }
}
