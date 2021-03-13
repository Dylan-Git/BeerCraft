package com.kaupenjoe.beercraft.client.gui;

import com.kaupenjoe.beercraft.block.ModBlocks;
import com.kaupenjoe.beercraft.block.TankTestBlock;
import com.kaupenjoe.beercraft.tileentity.FluidTankTest;
import io.netty.util.SuppressForbidden;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TankContainer extends Container
{
    private TileEntity tileEntity;
    private PlayerEntity playerEntity;
    private IItemHandler playerInventory;

    private static Field field =
            ObfuscationReflectionHelper.findField(Container.class, "listeners");

    public TankContainer(int windowId, World world, BlockPos pos,
                         PlayerInventory playerInventory, PlayerEntity player)
    {
        super(ModGUIs.TANK_CONTAINER.get(), windowId);
        tileEntity = world.getTileEntity(pos);
        this.playerEntity = player;
        this.playerInventory = new InvWrapper(playerInventory);

        if (tileEntity != null) {
            tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
                addSlot(new SlotItemHandler(h, 0, 8, 8));
                addSlot(new SlotItemHandler(h, 1, 152, 8));
            });

            //tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILIT).ifPresent(f -> {
                // Add Tanks here somehow!
            //});
        }

        layoutPlayerInventorySlots(8, 84);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn)
    {
        return isWithinUsableDistance(IWorldPosCallable.of(tileEntity.getWorld(),
                tileEntity.getPos()), playerEntity, ModBlocks.TANK_TEST.get());
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        field.setAccessible(true);

        try
        {
            List<IContainerListener> listOfListeners = (List<IContainerListener>) field.get(this);

            for (IContainerListener listener : listOfListeners) {
                ((FluidTankTest)tileEntity).sendGuiNetworkData(this, listener);
            }

        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    public int getTankLevel(Direction dir)
    {
        int tanklevel = 0;
        World world = tileEntity.getWorld();
        BlockPos pos = tileEntity.getPos();
        int tank = 0;

        AtomicInteger level = new AtomicInteger();

        FluidTankTest tileEntity =
                (FluidTankTest)world.getTileEntity(pos);

        if(tileEntity != null)
        {
            tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir)
                    .ifPresent(f -> level.set(f.getFluidInTank(tank).getAmount() / 1000));
        }

        tanklevel = level.get();
        return tanklevel;
    }

    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0 ; i < amount ; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0 ; j < verAmount ; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    private void layoutPlayerInventorySlots(int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }
}
