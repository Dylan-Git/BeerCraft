package com.kaupenjoe.beercraft.block;

import com.kaupenjoe.beercraft.BeerCraft;
import com.kaupenjoe.beercraft.item.ModItems;
import com.kaupenjoe.beercraft.util.BeerTabs;
import com.kaupenjoe.beercraft.util.Registration;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;

public class ModFluids
{
    public static final ResourceLocation OIL_STILL_RL = new ResourceLocation(BeerCraft.MOD_ID,
            "block/oil_still");
    public static final ResourceLocation OIL_FLOWING_RL = new ResourceLocation(BeerCraft.MOD_ID,
            "block/oil_flowing");
    public static final ResourceLocation OIL_OVERLAY_RL = new ResourceLocation(BeerCraft.MOD_ID,
            "block/oil_overlay");


    public static final RegistryObject<FlowingFluid> OIL_FLUID
            = Registration.FLUIDS.register("oil_fluid",
            () -> new ForgeFlowingFluid.Source(ModFluids.OIL_PROPERTIES));

    public static final RegistryObject<FlowingFluid> OIL_FLOWING
            = Registration.FLUIDS.register("oil_flowing",
            () -> new ForgeFlowingFluid.Flowing(ModFluids.OIL_PROPERTIES));

    public static final ForgeFlowingFluid.Properties OIL_PROPERTIES = new ForgeFlowingFluid.Properties(
            () -> OIL_FLUID.get(), () -> OIL_FLOWING.get(), FluidAttributes.builder(OIL_STILL_RL, OIL_FLOWING_RL)
            .density(15).luminosity(2).rarity(Rarity.RARE).sound(SoundEvents.ITEM_HONEY_BOTTLE_DRINK).overlay(OIL_OVERLAY_RL)
            .viscosity(5)).slopeFindDistance(3).levelDecreasePerBlock(3)
            .block(() -> ModFluids.OIL_BLOCK.get()).bucket(() -> ModItems.OIL_BUCKET.get());


    public static final RegistryObject<FlowingFluidBlock> OIL_BLOCK = Registration.BLOCKS.register("oil",
            () -> new FlowingFluidBlock(() -> ModFluids.OIL_FLUID.get(), AbstractBlock.Properties.create(Material.WATER)
            .doesNotBlockMovement().hardnessAndResistance(100f).noDrops()));

    public static void register() { }

}
