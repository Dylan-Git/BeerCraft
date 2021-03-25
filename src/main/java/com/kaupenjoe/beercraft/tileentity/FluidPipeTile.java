package com.kaupenjoe.beercraft.tileentity;

import com.kaupenjoe.beercraft.block.FluidPipe;
import com.kaupenjoe.beercraft.packets.FluidPacket;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.CallbackI;

import java.rmi.server.ExportException;

public class FluidPipeTile extends TileEntity implements ITickableTileEntity
{
    private final FluidTank tank = createTank();
    private final LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> tank);

    private int tick = 0;

    public FluidPipeTile(TileEntityType<?> tileEntityTypeIn)
    {
        super(tileEntityTypeIn);
    }

    public FluidPipeTile()
    {
        this(ModTileEntities.FLUID_PIPE.get());
    }

    @Override
    public void tick()
    {
        if(world.isRemote){
            return;
        }

        // PULL FROM NORTH PUSH TO SOUTH
        // tick++;
        if(tick > 10) // CONFIG
        {
            if((this.getBlockState().get(FluidPipe.NEIGHBOURS) & 4) == 4)
            {
                TileEntity tile = getWorld().getTileEntity(getNeighbourPos(Direction.NORTH, this.pos));

                if(tile != null)
                {
                    System.out.println("TILE NORTH: " + tile);

                    tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, Direction.EAST).ifPresent(h -> {
                        if(h.getFluidInTank(0).getAmount() > 0)
                        {
                            System.out.println("EAST FLUID: " + h.getFluidInTank(0).getAmount());

                            this.tank.fill(h.drain(1000, IFluidHandler.FluidAction.EXECUTE),
                                    IFluidHandler.FluidAction.EXECUTE); // 1000 mB per Second ADJUST
                        }
                    });
                    System.out.println("VALID NEIGHBOUR TO THE NORTH LETS GO!");
                }
            }

            if((this.getBlockState().get(FluidPipe.NEIGHBOURS) & 8) == 8)
            {
                TileEntity tile = getWorld().getTileEntity(getNeighbourPos(Direction.SOUTH, this.pos));

                if(tile != null)
                {
                    tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, Direction.WEST).ifPresent(h ->
                    {
                        if (((FluidTank) h).getSpace() != 0 && this.tank.getFluidAmount() > 0)
                        {
                            System.out.println("AA: " + this.tank.getFluidAmount());

                            h.fill(this.tank.drain(1000, IFluidHandler.FluidAction.EXECUTE),
                                    IFluidHandler.FluidAction.EXECUTE);
                        }
                    });
                    System.out.println("VALID NEIGHBOUR TO THE SOUTH LETS GO!");
                }
            }

            tick = 0;
            markDirty();
        }
    }

    private BlockPos getNeighbourPos(Direction side, BlockPos pos)
    {
        if(side == Direction.NORTH)
            return new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1);
        if(side == Direction.SOUTH)
            return new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1);

        return null;
    }

    @Override
    public void read(BlockState state, CompoundNBT tag)
    {
        FluidStack fluid = FluidStack.loadFluidStackFromNBT(tag);

        tank.setFluid(fluid);

        super.read(state, tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag)
    {
        tank.writeToNBT(tag);

        return super.write(tag);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
    {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            if(side == Direction.NORTH || side == Direction.SOUTH)
            {
                return fluidHandler.cast();
            }
        }

        return null;
    }

    @Override
    public void remove() {
        super.remove();
        fluidHandler.invalidate();
    }

    private FluidTank createTank() {

        return new FluidTank(2000) {
            @Override
            protected void onContentsChanged()
            {
                world.notifyBlockUpdate(getTileEntity().getPos(), getBlockState(), getBlockState(), 10);

                markDirty();
            }
        };
    }
}
