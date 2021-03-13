package com.kaupenjoe.beercraft.tileentity;

import com.kaupenjoe.beercraft.BeerCraft;
import com.kaupenjoe.beercraft.block.TankTestBlock;
import com.kaupenjoe.beercraft.network.packet.TileGuiPacket;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class FluidTankTest extends TileEntity implements ITickableTileEntity
{
    private final FluidTank tank_left = createTank();
    private final FluidTank tank_right = createTank();

    private final ItemStackHandler items = createHandler();

    private final LazyOptional<IFluidHandler> fluidHandler_left = LazyOptional.of(() -> tank_left);
    private final LazyOptional<IFluidHandler> fluidHandler_right = LazyOptional.of(() -> tank_right);

    private final LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> items);

    private int tick = 0;

    public FluidTankTest(TileEntityType<?> tileEntityTypeIn)
    {
        super(tileEntityTypeIn);
    }

    public FluidTankTest()
    {
        this(ModTileEntities.TANKTEST.get());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
    {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            if(side.getIndex() == 4) // LEFT WEST
            {
                return fluidHandler_left.cast();
            }
            else if(side.getIndex() == 5) // RIGHT EAST
            {
                return fluidHandler_right.cast();
            }
        }

        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            return itemHandler.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void tick()
    {
        if(world.isRemote){
            return;
        }

        tick++;
        if(tick > 2) // CONFIG
        {
            if(this.items.getStackInSlot(0).getItem() == Items.WATER_BUCKET && tank_left.getSpace() > 0)
            {
                items.extractItem(0,1, false); // "Deletes" the item
                getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, Direction.WEST).ifPresent(f ->
                        f.fill(new FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE));

                world.setBlockState(getTileEntity().getPos(),
                        getTileEntity().getBlockState().with(TankTestBlock.TANK_LEVEL_L, tank_left.getFluidAmount()/1000));
            }

            if(this.items.getStackInSlot(1).getItem() == Items.WATER_BUCKET && tank_right.getSpace() > 0)
            {
                items.extractItem(1,1, false); // "Deletes" the item
                getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, Direction.EAST).ifPresent(f ->
                        f.fill(new FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE));

                world.setBlockState(getTileEntity().getPos(),
                        getTileEntity().getBlockState().with(TankTestBlock.TANK_LEVEL_R, tank_right.getFluidAmount()/1000));
            }

            tick = 0;
            markDirty();
        }
    }

    private ItemStackHandler createHandler()
    {
        return new ItemStackHandler(2)
        {
            @Override
            protected void onContentsChanged(int slot)
            {
                // To make sure the TE persists when the chunk is saved later we need to
                // mark it dirty every time the item handler changes
                markDirty();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack)
            {
                return stack.getItem() == Items.WATER_BUCKET;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
            {
                if(!isItemValid(slot, stack))
                {
                    return stack;
                }

                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    @Override
    public void read(BlockState state, CompoundNBT tag)
    {
        FluidStack fluid_l = FluidStack.loadFluidStackFromNBT(tag);
        FluidStack fluid_r = FluidStack.loadFluidStackFromNBT(tag);

        tank_left.setFluid(fluid_l);
        tank_right.setFluid(fluid_r);

        items.deserializeNBT(tag.getCompound("inv"));

        super.read(state, tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag)
    {
        tank_left.writeToNBT(tag);
        tank_right.writeToNBT(tag);

        tag.put("inv", items.serializeNBT());

        return super.write(tag);
    }

    @Override
    public void remove() {
        super.remove();

        fluidHandler_right.invalidate();
        fluidHandler_left.invalidate();
        itemHandler.invalidate();
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        CompoundNBT tag = new CompoundNBT();
        write(tag);
        return new SUpdateTileEntityPacket(getPos(), 0, tag);
    }

    @Override
    public CompoundNBT getUpdateTag()
    {
        CompoundNBT tag = new CompoundNBT();
        return write(tag);
    }

    private FluidTank createTank()
    {
        return new FluidTank(10000) {
            @Override
            protected void onContentsChanged()
            {
                world.notifyBlockUpdate(getTileEntity().getPos(), getBlockState(), getBlockState(), 10);

                markDirty();
            }
        };
    }

    public void sendGuiNetworkData(Container container, IContainerListener player)
    {
        if (player instanceof ServerPlayerEntity && (!(player instanceof FakePlayer))) {
            TileGuiPacket.sendToClientFluid(this, (ServerPlayerEntity) player);
        }
    }

    public PacketBuffer getGuiPacket(PacketBuffer buffer)
    {
        buffer.writeInt(tank_left.getFluid().getAmount());
        buffer.writeInt(tank_right.getFluid().getAmount());

        return buffer;
    }

    public void handleGuiPacket(PacketBuffer buffer)
    {
        tank_left.setFluid(new FluidStack(Fluids.WATER, buffer.readInt()));
        tank_right.setFluid(new FluidStack(Fluids.WATER, buffer.readInt()));
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
    {
        super.onDataPacket(net, pkt);

        ModelDataManager.requestModelDataRefresh(this);
    }

    public void handleControlPacket(PacketBuffer buffer)
    {
        ModelDataManager.requestModelDataRefresh(this);
    }

    public void handleStatePacket(PacketBuffer buffer)
    {
        ModelDataManager.requestModelDataRefresh(this);
    }
}
