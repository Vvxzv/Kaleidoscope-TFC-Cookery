package net.vvxzv.ktfcc.common.block.decay;

import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteAnimateTicks;
import com.mojang.datafixers.util.Pair;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodData;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.common.capabilities.food.TFCFoodData;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.vvxzv.ktfcc.common.blockentity.DecayingFoodBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class DecayingFoodBiteBlock extends DecayingFoodBlock{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public final Supplier<? extends Item> item;
    protected final IntegerProperty bites;
    protected final int maxBites;
    @Nullable
    protected final FoodBiteAnimateTicks.@Nullable AnimateTick animateTick;
    protected VoxelShape aabb;

    public DecayingFoodBiteBlock(ExtendedProperties properties, Supplier<? extends Block> rotted, Supplier<? extends Item> item, int maxBites, @Nullable FoodBiteAnimateTicks.@Nullable AnimateTick animateTick) {
        super(properties, rotted);
        this.aabb = AABB;
        this.maxBites = maxBites;
        this.item = item;
        this.bites = IntegerProperty.create("bites", 0, maxBites);
        this.animateTick = animateTick;
        StateDefinition.Builder<Block, BlockState> builder = new StateDefinition.Builder<>(this);
        this.createBitesBlockStateDefinition(builder);
        this.stateDefinition = builder.create(Block::defaultBlockState, BlockState::new);
        this.registerDefaultState(this.stateDefinition.any().setValue(this.bites, 0).setValue(FACING, Direction.SOUTH));
    }

    public DecayingFoodBiteBlock(ExtendedProperties properties, Supplier<? extends Block> rotted, Supplier<? extends Item> item, int maxBites) {
        this(properties, rotted, item, maxBites, null);
    }

    public DecayingFoodBiteBlock(ExtendedProperties properties, Supplier<? extends Block> rotted, Supplier<? extends Item> item) {
        this(properties, rotted, item, 3);
    }

    public DecayingFoodBiteBlock setAABB(VoxelShape aabb) {
        this.aabb = aabb;
        return this;
    }

    public IntegerProperty getBites() {
        return this.bites;
    }

    public int getMaxBites() {
        return this.maxBites;
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (this.animateTick != null) {
            this.animateTick.animateTick(state, level, pos, random);
        }

    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity entity = level.getBlockEntity(pos);
        if(entity instanceof DecayingFoodBlockEntity decaying && decaying.isRotten()){
            return InteractionResult.PASS;
        }
        ItemStack itemInHand = player.getItemInHand(hand);
        int bites = state.getValue(this.bites);
        if (bites >= this.getMaxBites()) {
            level.destroyBlock(pos, true, player);
            return InteractionResult.SUCCESS;
        } else {
            if (level.isClientSide) {
                if (this.eat(level, pos, state, player).consumesAction()) {
                    return InteractionResult.SUCCESS;
                }

                if (itemInHand.isEmpty()) {
                    return InteractionResult.CONSUME;
                }
            }

            return this.eat(level, pos, state, player);
        }
    }

    protected InteractionResult eat(Level level, BlockPos pos, BlockState state, Player player) {
        BlockEntity entity = level.getBlockEntity(pos);
        if(entity instanceof DecayingFoodBlockEntity decaying && decaying.isRotten()){
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

        for(Pair<MobEffectInstance, Float> pair : this.getItem().getItem().getFoodProperties().getEffects()) {
            if (!level.isClientSide && pair.getFirst() != null && level.random.nextFloat() < pair.getSecond()) {
                player.addEffect(new MobEffectInstance(pair.getFirst()));
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

    protected FoodData cFoodData(FoodData data) {
        int hunger = data.hunger() / this.maxBites;
        float water = data.water() / this.maxBites;
        float saturation = data.saturation() / this.maxBites;
        float[] n = data.nutrients();
        for (int i = 0; i < n.length; i++){
            n[i] = n[i] / this.maxBites * 1.2F;
        }
        return new FoodData(hunger, water, saturation, n[0], n[1], n[2], n[3], n[4], 0.0F);
    }

    public ItemStack getItem(){
        return new ItemStack(this.item.get());
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return this.aabb;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    protected void createBitesBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(this.bites, FACING);
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        int value = state.getValue(this.bites);
        return (3 - value) * 5;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter blockGetter, BlockPos pos, PathComputationType pathType) {
        return false;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        IFood food = FoodCapability.get(context.getItemInHand());
        return food != null && food.isRotten() ? this.getRottedBlock().defaultBlockState() : this.defaultBlockState();
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof DecayingFoodBlockEntity decaying) {
            if(state.getValue(bites) == 0){
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
