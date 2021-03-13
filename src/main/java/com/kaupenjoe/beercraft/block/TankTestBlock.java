package com.kaupenjoe.beercraft.block;

import com.kaupenjoe.beercraft.client.gui.TankContainer;
import com.kaupenjoe.beercraft.tileentity.FluidTankTest;
import com.kaupenjoe.beercraft.tileentity.ModTileEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.network.NetworkHooks;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicInteger;

public class TankTestBlock extends Block
{
    public static final IntegerProperty TANK_LEVEL_L = IntegerProperty.create("tank_level_l", 0, 10);
    public static final IntegerProperty TANK_LEVEL_R = IntegerProperty.create("tank_level_r", 0, 10);

    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

    public TankTestBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn,
                                             BlockPos pos, PlayerEntity player,
                                             Hand handIn, BlockRayTraceResult hit)
    {
        if (!worldIn.isRemote())
        {
            Direction face = hit.getFace();
            Direction facing = worldIn.getBlockState(pos).get(FACING);

            FluidTankTest tileEntity = (FluidTankTest) worldIn.getTileEntity(pos);

            if (player.getHeldItemMainhand().getItem() == Items.WATER_BUCKET)
            {
                tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, getFaceDirection(face, facing)).ifPresent(f ->
                {
                    // Making sure that there is still space
                    if(((FluidTank)f).getSpace() != 0)
                    {
                        f.fill(new FluidStack(Fluids.WATER,1000), IFluidHandler.FluidAction.EXECUTE);
                        player.setHeldItem(Hand.MAIN_HAND, new ItemStack(Items.BUCKET));
                    }
                });

                redrawTexture(worldIn, pos);
                return ActionResultType.SUCCESS;
            }

            if (player.getHeldItemMainhand().getItem() == Items.BUCKET)
            {
                tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, getFaceDirection(face, facing)).ifPresent(f ->
                {
                    // Making sure that the Fluid Tank is not empty
                    if(!((FluidTank)f).isEmpty())
                    {
                        f.drain(1000, IFluidHandler.FluidAction.EXECUTE);
                        player.setHeldItem(Hand.MAIN_HAND, new ItemStack(Items.WATER_BUCKET));
                    }
                });

                redrawTexture(worldIn, pos);
                return ActionResultType.SUCCESS;
            }

            if (player.getHeldItemMainhand().getItem() == Items.DIAMOND)
            {
                tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, getFaceDirection(face, facing)).ifPresent(f ->
                {
                    LogManager.getLogger().info("Fluid: " + f.getFluidInTank(0).getTranslationKey());
                    LogManager.getLogger().info("Filled: " + f.getFluidInTank(0).getAmount());
                    LogManager.getLogger().info("Cap: " + f.getTankCapacity(0));
                });

                redrawTexture(worldIn, pos);
                return ActionResultType.SUCCESS;
            }

            INamedContainerProvider containerProvider = new INamedContainerProvider() {
                @Override
                public ITextComponent getDisplayName() {
                    return new TranslationTextComponent("screen.mccourse.test_tank");
                }

                @Override
                public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                    return new TankContainer(i, worldIn, pos, playerInventory, playerEntity);
                }
            };
            NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, tileEntity.getPos());
        }

        return ActionResultType.SUCCESS;
    }

    private Direction getFaceDirection(Direction clickedFace, Direction facing)
    {
        if(facing == Direction.NORTH)
        {
            return clickedFace.getOpposite();
        }
        if(facing == Direction.SOUTH)
        {
            return clickedFace;
        }

        if(facing == Direction.WEST)
        {
            switch(clickedFace)
            {
                case NORTH: return Direction.WEST;
                case SOUTH: return Direction.EAST;
            }
        }

        if(facing == Direction.EAST)
        {
            switch(clickedFace)
            {
                case NORTH: return Direction.EAST;
                case SOUTH: return Direction.WEST;
            }
        }

        return null;
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

    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return this.getDefaultState()
                .with(TANK_LEVEL_L, getTankLevel(context.getWorld(), 0, context.getPos(), Direction.WEST))
                .with(TANK_LEVEL_R, getTankLevel(context.getWorld(), 0, context.getPos(), Direction.EAST))
                .with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(TANK_LEVEL_L, TANK_LEVEL_R, FACING);
    }

    @Override
    @SuppressWarnings("all")
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    @SuppressWarnings("all")
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    // Should probably be renamed!
    private void redrawTexture(World world, BlockPos pos)
    {
        int level_l = getTankLevel(world,0, pos, Direction.WEST);
        int level_r = getTankLevel(world,0, pos, Direction.EAST);

        world.setBlockState(pos, world.getBlockState(pos).with(TANK_LEVEL_L, level_l), 3);
        world.setBlockState(pos, world.getBlockState(pos).with(TANK_LEVEL_R, level_r), 3);
    }

    private int getTankLevel(World world, int tank, BlockPos pos, Direction dir)
    {
        int tanklevel = 0;

        if (!world.isRemote())
        {
            AtomicInteger level = new AtomicInteger();

            FluidTankTest tileEntity =
                    (FluidTankTest)world.getTileEntity(pos);

            if(tileEntity != null)
            {
                tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir).ifPresent(f ->
                {
                    level.set(f.getFluidInTank(tank).getAmount()/1000);

                    LogManager.getLogger().info("Fluid amount: " + f.getFluidInTank(tank).getAmount()/1000);
                    LogManager.getLogger().info("Level " + level);
                });
            }
            else
            {
                tanklevel = 0;
            }

            tanklevel = level.get();
        }

        return tanklevel;
    }
}
