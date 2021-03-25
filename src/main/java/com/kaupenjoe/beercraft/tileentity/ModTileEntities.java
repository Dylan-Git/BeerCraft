package com.kaupenjoe.beercraft.tileentity;

import com.kaupenjoe.beercraft.block.ModBlocks;
import com.kaupenjoe.beercraft.util.Registration;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;

public class ModTileEntities
{
    public static final RegistryObject<TileEntityType<FluidTankTest>> TANKTEST
            = Registration.TILE_ENTITY_TYPES.register("tank_test_tile",
            () -> TileEntityType.Builder.create(
                    () -> new FluidTankTest(), ModBlocks.TANK_TEST.get()).build(null));

    public static final RegistryObject<TileEntityType<FluidTankTile>> FLUID_TANK
            = Registration.TILE_ENTITY_TYPES.register("fluid_tank",
            () -> TileEntityType.Builder.create(
                    () -> new FluidTankTile(), ModBlocks.TANK_BLOCK.get()).build(null));


    public static final RegistryObject<TileEntityType<FluidPipeTile>> FLUID_PIPE
            = Registration.TILE_ENTITY_TYPES.register("fluid_pipe_tile",
            () -> TileEntityType.Builder.create(
                    () -> new FluidPipeTile(), ModBlocks.FLUID_PIPE.get()).build(null));


    public static void register() { }

}
