package net.vvxzv.ktfcc.common.block;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModTrigger;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.github.ysbbbbbb.kaleidoscopecookery.item.KitchenShovelItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.BlockHitResult;
import net.vvxzv.ktfcc.common.blockentity.StoveBlockEntity;
import net.vvxzv.ktfcc.common.registry.KBlockEntity;
import org.jetbrains.annotations.Nullable;

public class StoveBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    
    public StoveBlock() {
        super(
                Properties.of()
                        .mapColor(MapColor.STONE)
                        .sound(SoundType.STONE)
                        .requiresCorrectToolForDrops()
                        .lightLevel((state) -> (Boolean)state.getValue(LIT) ? 13 : 0)
                        .randomTicks()
                        .strength(1.5F, 6.0F)
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.SOUTH).setValue(LIT, false));
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT)) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5;
            if (random.nextInt(10) == 0) {
                level.playLocalSound(x, y, z, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.6F, false);
            }

            level.addParticle(ParticleTypes.SMOKE, x + random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1), y + 0.5 + random.nextDouble() / 3.0, z + random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1), 0.0F, 0.02, 0.0F);
            Direction direction = state.getValue(FACING);
            Direction.Axis axis = direction.getAxis();
            double offsetRandom = random.nextDouble() * 0.6 - 0.3;
            double xOffset = axis == Direction.Axis.X ? direction.getStepX() * 0.52 : offsetRandom;
            double yOffset = 0.25 + random.nextDouble() * 6.0 / 16.0;
            double zOffset = axis == Direction.Axis.Z ? direction.getStepZ() * 0.52 : offsetRandom;
            level.addParticle(ParticleTypes.FLAME, x + xOffset, pos.getY() + yOffset, z + zOffset, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel level, BlockPos pos, RandomSource random) {
        if (blockState.getValue(LIT) && level.isRainingAt(pos.above())) {
            level.setBlockAndUpdate(pos, blockState.setValue(LIT, false));
            level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor levelAccessor, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(LIT) && levelAccessor.isWaterAt(pos.above()) && levelAccessor instanceof ServerLevel serverLevel) {
            serverLevel.setBlockAndUpdate(pos, state.setValue(LIT, false));
            serverLevel.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        return super.updateShape(state, direction, neighborState, levelAccessor, pos, neighborPos);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack itemInHand = player.getItemInHand(hand);
        if (!(Boolean)state.getValue(LIT) && itemInHand.is(TagMod.LIT_STOVE)) {
            level.setBlockAndUpdate(pos, state.setValue(LIT, true));
            if (itemInHand.is(Items.FIRE_CHARGE)) {
                level.playSound(player, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
                itemInHand.shrink(1);
            } else {
                level.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
                itemInHand.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
            }

            ModTrigger.EVENT.trigger(player, "lit_the_stove");
            return InteractionResult.SUCCESS;
        } else if (state.getValue(LIT) && itemInHand.is(TagMod.EXTINGUISH_STOVE)) {
            if (itemInHand.is(ModItems.KITCHEN_SHOVEL.get()) && KitchenShovelItem.hasOil(itemInHand)) {
                KitchenShovelItem.setHasOil(itemInHand, false);
            }

            level.setBlockAndUpdate(pos, state.setValue(LIT, false));
            level.playSound(player, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);
            itemInHand.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
            return InteractionResult.SUCCESS;
        } else {
            return super.use(state, level, pos, player, hand, hitResult);
        }
    }

    @Override
    public void onProjectileHit(Level level, BlockState state, BlockHitResult hitResult, Projectile projectile) {
        BlockPos hitBlockPos = hitResult.getBlockPos();
        if (!level.isClientSide && projectile.isOnFire() && projectile.mayInteract(level, hitBlockPos) && !(Boolean)state.getValue(LIT)) {
            level.setBlock(hitBlockPos, state.setValue(BlockStateProperties.LIT, true), 11);
            Entity var7 = projectile.getOwner();
            if (var7 instanceof Player player) {
                ModTrigger.EVENT.trigger(player, "lit_the_stove");
            }
        }

    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, FACING);
    }

    @Override
    public @Nullable BlockPathTypes getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
        return BlockPathTypes.DANGER_FIRE;
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(FACING, pRot.rotate(pState.getValue(FACING)));
    }
    
    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new StoveBlockEntity(pPos, pState);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if(level.isClientSide()){
            return null;
        }
        return createTickerHelper(blockEntityType, KBlockEntity.STOVE.get(), (l, blockPos, blockState, blockEntity) -> blockEntity.serverTick(level, blockPos, blockState));
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }
}
