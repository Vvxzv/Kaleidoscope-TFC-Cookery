package net.vvxzv.ktfcc.mixin.block;

import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteOneByTwoBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteAnimateTicks;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.vvxzv.ktfcc.common.block.entity.DecayingFoodBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodBiteOneByTwoBlock.class)
public class FoodBiteOneByTwoBlockMixin extends FoodBiteBlock implements EntityBlock {

    @Final
    @Shadow(remap = false)
    public static IntegerProperty POSITION;

    @Final
    @Shadow(remap = false)
    public static int LEFT = 0;

    @Final
    @Shadow(remap = false)
    public static int RIGHT = 1;

    public FoodBiteOneByTwoBlockMixin(FoodProperties foodProperties, int maxBites, FoodBiteAnimateTicks.@Nullable AnimateTick animateTick) {
        super(foodProperties, maxBites, animateTick);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new DecayingFoodBlockEntity(pPos, pState);
    }

    @Inject(method = "setPlacedBy", at = @At("TAIL"))
    private void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack, CallbackInfo ci) {
        Direction facing = pState.getValue(FACING);
        BlockPos leftPos = pPos.relative(facing.getClockWise());
        BlockState leftState = pState.setValue(POSITION, LEFT);
        this.setTwoPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        this.setTwoPlacedBy(pLevel, leftPos, leftState, pPlacer, pStack);
    }

    @Unique
    private void setTwoPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        super.setPlacedBy(level, pos, state, entity, stack);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof DecayingFoodBlockEntity decaying) {
            decaying.setStack(stack);
        }
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        Direction facing = state.getValue(FACING);
        BlockPos leftPos = pos.relative(facing.getClockWise());
        this.destroyedByPlayer(level, pos, player);
        this.destroyedByPlayer(level, leftPos, player);

        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Unique
    private void destroyedByPlayer(Level level, BlockPos pos, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof DecayingFoodBlockEntity decaying) {
            if (player.isCreative()) {
                decaying.setStack(ItemStack.EMPTY);
            }
        }
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

        if (state.hasBlockEntity() && (!state.is(newState.getBlock()) || !newState.hasBlockEntity())) {
            level.removeBlockEntity(pos);
        }
    }
}
