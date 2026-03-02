package net.vvxzv.ktfcc.common.block.decay;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.vvxzv.ktfcc.common.blockentity.DecayingFoodBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class DecayingFoodBiteOneByTwoBlock extends DecayingFoodBiteBlock{
    public static final IntegerProperty POSITION = IntegerProperty.create("position", 0, 1);
    public static final int LEFT = 0;
    public static final int RIGHT = 1;

    public DecayingFoodBiteOneByTwoBlock(ExtendedProperties properties, Supplier<? extends Block> rotted, Supplier<? extends Item> item, int maxBites) {
        super(properties, rotted, item, maxBites);
        this.registerDefaultState(this.stateDefinition.any().setValue(this.bites, 0).setValue(FACING, Direction.SOUTH).setValue(POSITION, RIGHT));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        int position = state.getValue(POSITION);
        Direction facing = state.getValue(FACING);
        if (position == LEFT && direction == facing.getCounterClockWise() || position == RIGHT && direction == facing.getClockWise()) {
            if (!neighborState.is(this) || neighborState.getValue(FACING) != facing || neighborState.getValue(POSITION) == position) {
                return Blocks.AIR.defaultBlockState();
            }

            int neighborBites = neighborState.getValue(this.bites);
            if (neighborBites != state.getValue(this.bites)) {
                return state.setValue(this.bites, neighborBites);
            }
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && player.isCreative() && state.getValue(POSITION) == LEFT) {
            BlockPos right = pos.relative(state.getValue(FACING).getCounterClockWise());
            BlockState rightState = level.getBlockState(right);
            if (rightState.is(state.getBlock()) && rightState.getValue(POSITION) == RIGHT) {
                BlockState airBlockState = rightState.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                level.setBlock(right, airBlockState, 35);
                level.levelEvent(player, 2001, right, Block.getId(rightState));
            }
        }

        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos rightPos = context.getClickedPos();
        Direction facing = context.getHorizontalDirection().getOpposite();
        Level level = context.getLevel();
        BlockPos leftPos = rightPos.relative(facing.getClockWise());
        IFood food = FoodCapability.get(context.getItemInHand());
        return level.getBlockState(leftPos).canBeReplaced(context) ? food != null && food.isRotten() ? this.getRottedBlock().defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()) : this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()) : null;
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        Direction facing = pState.getValue(FACING);
        BlockPos leftPos = pPos.relative(facing.getClockWise());
        BlockState leftState = pState.setValue(POSITION, LEFT);
        pLevel.setBlock(leftPos, leftState, 3);
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        super.setPlacedBy(pLevel, leftPos, leftState, pPlacer, pStack);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POSITION);
    }

    @Override
    protected void createBitesBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(this.bites, FACING, POSITION);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder pParams) {
        return state.getValue(POSITION) == LEFT ? Collections.emptyList() : super.getDrops(state, pParams);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DecayingFoodBlockEntity(pos, state);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof DecayingFoodBlockEntity decaying) {
            if(state.getValue(bites) == 0 && state.getValue(POSITION) == RIGHT){
                if (!Helpers.isBlock(state, newState.getBlock())) {
                    Helpers.spawnItem(level, pos, decaying.getStack());
                }
            }
        }
    }
}
