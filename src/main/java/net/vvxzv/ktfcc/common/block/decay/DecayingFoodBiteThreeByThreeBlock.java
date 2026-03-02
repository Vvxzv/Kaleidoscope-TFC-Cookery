package net.vvxzv.ktfcc.common.block.decay;

import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.NinePart;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodData;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.common.capabilities.food.TFCFoodData;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.vvxzv.ktfcc.common.blockentity.DecayingFoodBlockEntity;
import net.vvxzv.ktfcc.common.blockentity.DecayingThreeFoodBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class DecayingFoodBiteThreeByThreeBlock extends DecayingFoodBiteBlock {
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
    
    public DecayingFoodBiteThreeByThreeBlock(ExtendedProperties properties, Supplier<? extends Block> rotted, Supplier<? extends Item> item) {
        super(properties, rotted, item, 8);
        this.registerDefaultState(this.stateDefinition.any().setValue(this.bites, 0).setValue(FACING, Direction.SOUTH).setValue(PART, NinePart.CENTER));
    }

    private static void handleRemove(Level world, BlockPos pos, BlockState state, @Nullable Player player) {
        if (!world.isClientSide) {
            NinePart part = state.getValue(PART);
            BlockPos centerPos = pos.subtract(new Vec3i(part.getPosX(), 0, part.getPosY()));
            BlockEntity te = world.getBlockEntity(centerPos);
            if (te instanceof DecayingThreeFoodBlockEntity) {
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
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(blockEntity instanceof DecayingThreeFoodBlockEntity decaying && decaying.isRotten()){
            return InteractionResult.PASS;
        }
        NinePart part = state.getValue(PART);
        BlockPos centerPos = pos.subtract(new Vec3i(part.getPosX(), 0, part.getPosY()));
        BlockState centerState = level.getBlockState(centerPos);
        if (!centerState.is(this)) {
            return InteractionResult.PASS;
        } else {
            ItemStack itemInHand = player.getItemInHand(hand);
            int bites = centerState.getValue(this.bites);
            if (bites >= this.getMaxBites()) {
                handleRemove(level, centerPos, centerState, player);
                return InteractionResult.SUCCESS;
            } else {
                if (level.isClientSide) {
                    if (this.eat(level, centerPos, centerState, player).consumesAction()) {
                        return InteractionResult.SUCCESS;
                    }

                    if (itemInHand.isEmpty()) {
                        return InteractionResult.CONSUME;
                    }
                }

                return this.eat(level, centerPos, centerState, player);
            }
        }
    }

    protected InteractionResult eat(Level level, BlockPos pos, BlockState state, Player player) {
        BlockEntity entity = level.getBlockEntity(pos);
        if(entity instanceof DecayingThreeFoodBlockEntity decaying && decaying.isRotten()){
            return InteractionResult.PASS;
        }

        if(player.getFoodData() instanceof TFCFoodData foodData){
            IFood iFood = FoodCapability.get(this.getItem());
            if (iFood != null) {
                FoodData i = iFood.getData();
                FoodData data = this.cFoodData(i);
                foodData.eat(data);
            }
        }

        level.playSound(null, pos, SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 0.5F, level.getRandom().nextFloat() * 0.1F + 0.9F);
        int bites = state.getValue(this.bites);
        level.gameEvent(player, GameEvent.EAT, pos);
        if (bites < this.getMaxBites()) {
            level.setBlock(pos, state.setValue(this.bites, bites + 1), 3);
        }

        return InteractionResult.SUCCESS;
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
        IFood food = FoodCapability.get(context.getItemInHand());
        return food != null && food.isRotten() ? this.getRottedBlock().defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()) : this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, PART);
    }

    @Override
    protected void createBitesBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(this.bites, FACING, PART);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DecayingThreeFoodBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        NinePart value = pState.getValue(PART);
        VoxelShape v;
        switch (value) {
            case LEFT_UP -> v = LEFT_UP;
            case UP -> v = UP;
            case RIGHT_UP -> v = RIGHT_UP;
            case LEFT_CENTER -> v = LEFT_CENTER;
            case CENTER -> v = CENTER;
            case RIGHT_CENTER -> v = RIGHT_CENTER;
            case LEFT_DOWN -> v = LEFT_DOWN;
            case DOWN -> v = DOWN;
            case RIGHT_DOWN -> v = RIGHT_DOWN;
            default -> throw new IncompatibleClassChangeError();
        }

        return v;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder pParams) {
        return state.getValue(PART).isCenter() ? super.getDrops(state, pParams): Collections.emptyList();
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof DecayingFoodBlockEntity decaying) {
            if(state.getValue(bites) == 0 && state.getValue(PART).isCenter()){
                if (!Helpers.isBlock(state, newState.getBlock())) {
                    Helpers.spawnItem(level, pos, decaying.getStack());
                }
            }
        }

        if (state.hasBlockEntity() && (!state.is(newState.getBlock()) || !newState.hasBlockEntity())) {
            level.removeBlockEntity(pos);
        }
    }
}
