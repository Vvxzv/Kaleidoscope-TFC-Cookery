package net.vvxzv.ktfcc.common.block;

import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.NinePart;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.food.FoodBiteThreeByThreeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class RottenThreeByThreeBlock extends RottenBlock{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<NinePart> PART = EnumProperty.create("part", NinePart.class);
    private static final VoxelShape LEFT_UP = Block.box(4.0, 0.0, 4.0, 16.0, 2.0, 16.0);
    private static final VoxelShape UP = Block.box(0.0, 0.0, 4.0, 16.0, 2.0, 16.0);
    private static final VoxelShape RIGHT_UP = Block.box(0.0, 0.0, 4.0, 12.0, 2.0, 16.0);
    private static final VoxelShape LEFT_CENTER = Block.box(4.0, 0.0, 0.0, 16.0, 2.0, 16.0);
    private static final VoxelShape CENTER = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
    private static final VoxelShape RIGHT_CENTER = Block.box(0.0, 0.0, 0.0, 12.0, 2.0, 16.0);
    private static final VoxelShape LEFT_DOWN = Block.box(4.0, 0.0, 0.0, 16.0, 2.0, 12.0);
    private static final VoxelShape DOWN = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 12.0);
    private static final VoxelShape RIGHT_DOWN = Block.box(0.0, 0.0, 0.0, 12.0, 2.0, 12.0);

    public RottenThreeByThreeBlock(){
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.SOUTH).setValue(PART, NinePart.CENTER));
    }

    private static void handleRemove(Level world, BlockPos pos, BlockState state, @Nullable Player player) {
        if (!world.isClientSide) {
            NinePart part = state.getValue(PART);
            BlockPos centerPos = pos.subtract(new Vec3i(part.getPosX(), 0, part.getPosY()));
            BlockEntity te = world.getBlockEntity(centerPos);
            if (te instanceof FoodBiteThreeByThreeBlockEntity) {
                for(int i = -1; i < 2; ++i) {
                    for(int j = -1; j < 2; ++j) {
                        BlockPos offsetPos = centerPos.offset(i, 0, j);
                        if (i == 0 && j == 0) {
                            world.destroyBlock(offsetPos, true, player);
                        } else {
                            world.setBlock(offsetPos, Blocks.AIR.defaultBlockState(), 35);
                        }
                    }
                }

            }
        }
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        handleRemove(world, pos, state, player);
        super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public void onBlockExploded(BlockState state, Level world, BlockPos pos, Explosion explosion) {
        handleRemove(world, pos, state, null);
        super.onBlockExploded(state, world, pos, explosion);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos centerPos = context.getClickedPos();

        for(int i = -1; i < 2; ++i) {
            for(int j = -1; j < 2; ++j) {
                BlockPos searchPos = centerPos.offset(i, 0, j);
                if (!context.getLevel().getBlockState(searchPos).canBeReplaced(context)) {
                    return null;
                }
            }
        }

        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        if (!worldIn.isClientSide) {
            for(int i = -1; i < 2; ++i) {
                for(int j = -1; j < 2; ++j) {
                    BlockPos searchPos = pos.offset(i, 0, j);
                    NinePart part = NinePart.getPartByPos(i, j);
                    if (part != null) {
                        if(!part.isCenter()){
                            worldIn.setBlock(searchPos, state.setValue(PART, part), 3);
                        }

                        super.setPlacedBy(worldIn, searchPos, worldIn.getBlockState(searchPos), placer, stack);
                    }
                }
            }

        }
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        NinePart value = pState.getValue(PART);
        VoxelShape var10000;
        switch (value) {
            case LEFT_UP -> var10000 = LEFT_UP;
            case UP -> var10000 = UP;
            case RIGHT_UP -> var10000 = RIGHT_UP;
            case LEFT_CENTER -> var10000 = LEFT_CENTER;
            case CENTER -> var10000 = CENTER;
            case RIGHT_CENTER -> var10000 = RIGHT_CENTER;
            case LEFT_DOWN -> var10000 = LEFT_DOWN;
            case DOWN -> var10000 = DOWN;
            case RIGHT_DOWN -> var10000 = RIGHT_DOWN;
            default -> throw new IncompatibleClassChangeError();
        }

        return var10000;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, PART);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder pParams) {
        return state.getValue(PART).isCenter() ? super.getDrops(state, pParams): Collections.emptyList();
    }
}
