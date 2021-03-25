package com.kaupenjoe.beercraft.block;

import com.kaupenjoe.beercraft.BeerCraft;
import com.kaupenjoe.beercraft.tileentity.FluidPipeTile;
import com.kaupenjoe.beercraft.tileentity.FluidTankTest;
import com.kaupenjoe.beercraft.tileentity.ModTileEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.AxisRotation;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.CallbackI;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class FluidPipe extends Block
{
    public static final IntegerProperty NEIGHBOURS =
            IntegerProperty.create("neighbours", 0, 63);

    public static final IntegerProperty TANK_LEVEL =
            IntegerProperty.create("tank_level", 0, 1);

    // EWS NUD
    // 000 000

    // TODO: VoxelShapes

    private final VoxelShape SHAPE_NONE = Stream.of(
            Block.makeCuboidShape(4, 5, 5, 5, 11, 11),
            Block.makeCuboidShape(5, 5, 11, 11, 11, 12),
            Block.makeCuboidShape(5, 5, 4, 11, 11, 5),
            Block.makeCuboidShape(11, 5, 5, 12, 11, 11),
            Block.makeCuboidShape(5, 4, 5, 11, 5, 11),
            Block.makeCuboidShape(5, 11, 5, 11, 12, 11),
            Block.makeCuboidShape(5, 11, 4, 11, 12, 5),
            Block.makeCuboidShape(4, 11, 5, 5, 12, 11),
            Block.makeCuboidShape(11, 11, 5, 12, 12, 11),
            Block.makeCuboidShape(5, 11, 11, 11, 12, 12),
            Block.makeCuboidShape(4, 4, 5, 5, 5, 11),
            Block.makeCuboidShape(5, 4, 11, 11, 5, 12),
            Block.makeCuboidShape(11, 4, 5, 12, 5, 11),
            Block.makeCuboidShape(11, 5, 11, 12, 11, 12),
            Block.makeCuboidShape(11, 5, 4, 12, 11, 5),
            Block.makeCuboidShape(5, 4, 4, 11, 5, 5),
            Block.makeCuboidShape(4, 5, 11, 5, 11, 12),
            Block.makeCuboidShape(4, 5, 4, 5, 11, 5)
    ).reduce((v1, v2) -> {return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);}).get();

    private final VoxelShape SHAPE_N = Stream.of(
            Block.makeCuboidShape(4, 5, 1, 5, 11, 11),
            Block.makeCuboidShape(5, 5, 11, 11, 11, 12),
            Block.makeCuboidShape(11, 5, 1, 12, 11, 11),
            Block.makeCuboidShape(5, 4, 1, 11, 5, 11),
            Block.makeCuboidShape(5, 11, 1, 11, 12, 11),
            Block.makeCuboidShape(5, 11, 0, 11, 12, 1),
            Block.makeCuboidShape(4, 11, 1, 5, 12, 11),
            Block.makeCuboidShape(11, 11, 1, 12, 12, 11),
            Block.makeCuboidShape(5, 11, 11, 11, 12, 12),
            Block.makeCuboidShape(4, 4, 1, 5, 5, 11),
            Block.makeCuboidShape(5, 4, 11, 11, 5, 12),
            Block.makeCuboidShape(11, 4, 1, 12, 5, 11),
            Block.makeCuboidShape(11, 5, 11, 12, 11, 12),
            Block.makeCuboidShape(11, 5, 0, 12, 11, 1),
            Block.makeCuboidShape(5, 4, 0, 11, 5, 1),
            Block.makeCuboidShape(4, 5, 11, 5, 11, 12),
            Block.makeCuboidShape(4, 5, 0, 5, 11, 1)
    ).reduce((v1, v2) -> {return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);}).get();

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn,
                               BlockPos pos, ISelectionContext context)
    {
        if((state.get(NEIGHBOURS) & 4) == 4) // NORTH ACTIVE
        {
            System.out.println("NORTH getSHAPE");
            return VoxelShapes.combine(SHAPE_NONE, SHAPE_N, IBooleanFunction.AND);
        }

        if((state.get(NEIGHBOURS) & 32) == 32) // EAST ACTIVE
        {

        }

        if((state.get(NEIGHBOURS) & 8) == 8) // SOUTH ACTIVE
        {

        }

        if((state.get(NEIGHBOURS) & 16) == 16) // WEST ACTIVE
        {

        }

        return SHAPE_NONE;
    }

    private static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{ shape, VoxelShapes.empty() };

        int times = (to.getHorizontalIndex() - from.getHorizontalIndex() + 4) % 4;
        for (int i = 0; i < times; i++) {
            buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] =
                    VoxelShapes.or(buffer[1], VoxelShapes.create(1-maxZ, minY, minX, 1-minZ, maxY, maxX)));
            buffer[0] = buffer[1];
            buffer[1] = VoxelShapes.empty();
        }

        return buffer[0];
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if(worldIn.isRemote())
            return ActionResultType.SUCCESS;

        if (player.getHeldItemMainhand().getItem() == Items.DIAMOND)
        {
            FluidPipeTile tileEntity = (FluidPipeTile) worldIn.getTileEntity(pos);

            tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, Direction.NORTH).ifPresent(f ->
            {
                LogManager.getLogger().info("Fluid: " + f.getFluidInTank(0).getTranslationKey());
                LogManager.getLogger().info("Filled: " + f.getFluidInTank(0).getAmount());
                LogManager.getLogger().info("Cap: " + f.getTankCapacity(0));
            });

            return ActionResultType.SUCCESS;
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return ModTileEntities.FLUID_PIPE.get().create();
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        updateNeighbours(context.getWorld(), context.getPos(), this.getDefaultState());
        return super.getStateForPlacement(context);
    }

    private void updateNeighbours(World worldIn, BlockPos pos, BlockState state)
    {
        if(worldIn.isRemote()) {
            return;
        }

        final BlockPos NorthNeighbour = new BlockPos(pos.getX(),
                pos.getY(), pos.getZ() - 1);
        final BlockPos SouthNeighbour = new BlockPos(pos.getX(),
                pos.getY(), pos.getZ() + 1);
        final BlockPos WestNeighbour = new BlockPos(pos.getX() - 1,
                pos.getY(), pos.getZ());
        final BlockPos EastNeighbour = new BlockPos(pos.getX() + 1,
                pos.getY(), pos.getZ());

        boolean isBlockN = worldIn.getBlockState(NorthNeighbour).getBlock()
                .hasTileEntity(worldIn.getBlockState(NorthNeighbour));
        boolean isBlockS = worldIn.getBlockState(SouthNeighbour).getBlock()
                .hasTileEntity(worldIn.getBlockState(SouthNeighbour));
        boolean isBlockW = worldIn.getBlockState(WestNeighbour).getBlock()
                .hasTileEntity(worldIn.getBlockState(WestNeighbour));
        boolean isBlockE = worldIn.getBlockState(EastNeighbour).getBlock()
                .hasTileEntity(worldIn.getBlockState(EastNeighbour));

        int neighbours = 0;
        neighbours = (isBlockN) ? neighbours + 4 : neighbours;
        neighbours = (isBlockS) ? neighbours + 8 : neighbours;
        neighbours = (isBlockW) ? neighbours + 16 : neighbours;
        neighbours = (isBlockE) ? neighbours + 32 : neighbours;

        // System.out.println("NEIGH: " + neighbours);

        worldIn.setBlockState(pos, state.with(NEIGHBOURS, neighbours), 11);
        redrawTexture(worldIn, pos);
    }

    // Should probably be renamed!
    private void redrawTexture(World world, BlockPos pos)
    {
        int level = getTankLevel(world, 0, pos, Direction.NORTH);

        world.setBlockState(pos, world.getBlockState(pos).with(TANK_LEVEL, level), 27);
    }

    private int getTankLevel(World world, int tank, BlockPos pos, Direction dir)
    {
        int tankLevel = 0;

        if (!world.isRemote())
        {
            AtomicInteger level = new AtomicInteger();

            FluidPipeTile tileEntity =
                    (FluidPipeTile)world.getTileEntity(pos);

            if(tileEntity != null)
            {
                tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir).ifPresent(f ->
                        level.set(f.getFluidInTank(tank).getAmount() > 0 ? 1 : 0));
            }

            tankLevel = level.get();
        }

        System.out.println("TANK LEVEL PIPE: " + tankLevel);

        return tankLevel;
    }

    @Override
    public void onNeighborChange(BlockState state, IWorldReader world,
                                 BlockPos pos, BlockPos neighbor)
    {
        updateNeighbours(((World)world), pos, world.getBlockState(pos));
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos,
                                Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        if(blockIn instanceof FluidPipe) {
            updateNeighbours(worldIn, pos, worldIn.getBlockState(pos));
        }

        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }

    public FluidPipe(Properties properties)
    {
        super(properties);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(NEIGHBOURS, TANK_LEVEL);
    }
}
