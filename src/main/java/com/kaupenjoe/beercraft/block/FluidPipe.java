package com.kaupenjoe.beercraft.block;

import com.kaupenjoe.beercraft.tileentity.FluidPipeTile;
import com.kaupenjoe.beercraft.tileentity.ModTileEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

public class FluidPipe extends Block
{
    public static final IntegerProperty NEIGHBOURS =
            IntegerProperty.create("neighbours", 0, 63);

    public static final IntegerProperty TANK_LEVEL =
            IntegerProperty.create("tank_level", 0, 1);

    // EWS NUD
    // 000 000

    private final VoxelShape SHAPE_NONE = Block.makeCuboidShape(4, 4, 4, 12, 12, 12);

    private final VoxelShape SHAPE_N = Block.makeCuboidShape(4, 4, 0, 12, 12, 12);
    private final VoxelShape SHAPE_S = Block.makeCuboidShape(4, 4, 4, 12, 12, 16);
    private final VoxelShape SHAPE_W = Block.makeCuboidShape(0, 4, 4, 12, 12, 12);
    private final VoxelShape SHAPE_E = Block.makeCuboidShape(4, 4, 4, 16, 12, 12);

    public FluidPipe(Properties properties)
    {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn,
                               BlockPos pos, ISelectionContext context)
    {
        VoxelShape COMBINED = SHAPE_NONE;

        // NORTH ACTIVE
        if((state.get(NEIGHBOURS) & 4) == 4) {
            COMBINED = VoxelShapes.or(COMBINED, SHAPE_N);
        }

        // EAST ACTIVE
        if((state.get(NEIGHBOURS) & 32) == 32) {
            COMBINED = VoxelShapes.or(COMBINED, SHAPE_E);
        }

        // SOUTH ACTIVE
        if((state.get(NEIGHBOURS) & 8) == 8) {
            COMBINED = VoxelShapes.or(COMBINED, SHAPE_S);
        }

        // WEST ACTIVE
        if((state.get(NEIGHBOURS) & 16) == 16) {
            COMBINED = VoxelShapes.or(COMBINED, SHAPE_W);
        }

        return COMBINED;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if(worldIn.isRemote())
            return ActionResultType.PASS;

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

        return ActionResultType.PASS;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntities.FLUID_PIPE.get().create();
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        updateNeighbours(context.getWorld(), context.getPos(), this.getDefaultState());
        return this.getDefaultState().with(NEIGHBOURS, 0);
    }

    // TODO: You can currently still place the Pipe inside the Player which doesn't update neighbours
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

        worldIn.setBlockState(pos, state.with(NEIGHBOURS, neighbours), 11);
    }

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
                                 BlockPos pos, BlockPos neighbor) {
        updateNeighbours(((World)world), pos, world.getBlockState(pos));
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos,
                                Block blockIn, BlockPos fromPos, boolean isMoving) {

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

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(NEIGHBOURS, TANK_LEVEL);
    }
}
