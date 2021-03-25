package com.kaupenjoe.beercraft.block;

import com.kaupenjoe.beercraft.tileentity.ModTileEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.IntegerProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class TankBlock extends Block
{
    public static final IntegerProperty TANK_LEVEL
            = IntegerProperty.create("tank_level", 0, 16);

    public TankBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return ModTileEntities.TANKTEST.get().create();
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }
}
