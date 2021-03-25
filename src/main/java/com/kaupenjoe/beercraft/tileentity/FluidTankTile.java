package com.kaupenjoe.beercraft.tileentity;

import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class FluidTankTile extends TileEntity implements ITickableTileEntity
{
    public final FluidTank fluidTank = createTank();

    public float prevScale;

    private final LazyOptional<IFluidHandler> fluidHandler_left = LazyOptional.of(() -> fluidTank);

    public FluidTankTile(TileEntityType<?> tileEntityTypeIn)
    {
        super(tileEntityTypeIn);
    }

    public FluidTankTile()
    {
        this(ModTileEntities.FLUID_TANK.get());
    }


    @Override
    public void tick()
    {

    }


    public static float getScale(float prevScale, float targetScale, boolean empty, boolean full)
    {
        float difference = Math.abs(prevScale - targetScale);
        if (difference > 0.01) {
            return (9 * prevScale + targetScale) / 10;
        } else if (!empty && full && difference > 0) {
            //If we are full but our difference is less than 0.01 but we want to get our scale all the way up to the target
            // instead of leaving it at a value just under. Note: We also check that are are not empty as we technically may
            // be both empty and full if the current capacity is zero
            return targetScale;
        } else if (!empty && prevScale == 0) {
            //If we have any contents make sure we end up rendering it
            return targetScale;
        }
        if (empty && prevScale < 0.01) {
            //If we are empty and have a very small amount just round it down to empty
            return 0;
        }
        return prevScale;
    }

    private FluidTank createTank()
    {
        return new FluidTank(16000) {
            @Override
            protected void onContentsChanged()
            {
                world.notifyBlockUpdate(getTileEntity().getPos(), getBlockState(), getBlockState(), 10);

                markDirty();
            }
        };
    }
}
