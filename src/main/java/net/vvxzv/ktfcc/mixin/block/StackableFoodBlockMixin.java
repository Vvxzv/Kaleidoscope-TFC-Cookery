package net.vvxzv.ktfcc.mixin.block;

import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.StackableFoodBlock;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.vvxzv.ktfcc.common.block.entity.DecayingFoodBlockEntity;
import net.vvxzv.ktfcc.common.utils.Decaying;
import net.vvxzv.ktfcc.common.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.function.Supplier;

@Mixin(StackableFoodBlock.class)
public abstract class StackableFoodBlockMixin extends HorizontalDirectionalBlock implements EntityBlock {

    @Final
    @Shadow
    protected IntegerProperty countProperty;

    @Final
    @Shadow
    protected int maxCount;

    @Final
    @Shadow
    protected Supplier<Item> item;

    protected StackableFoodBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new DecayingFoodBlockEntity(blockPos, blockState);
    }

    /**
     * @author Vvxzv
     * @reason 放置逻辑
     */
    @Overwrite
    public @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack itemInHand, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if (hand != InteractionHand.MAIN_HAND) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(!(blockEntity instanceof Decaying decaying)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if(decaying.isRotten()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }int count = state.getValue(this.countProperty);
        ItemStack stack = decaying.getStack().copy();

        if(itemInHand.isEmpty()) {
            ItemHandlerHelper.giveItemToPlayer(player, stack.copy());
            if (count > 1) {
                SoundType soundType = state.getSoundType(level, pos, player);
                SoundEvent sound = soundType.getPlaceSound();
                level.playSound(player, pos, sound, SoundSource.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);

                decaying.setStack(ItemStack.EMPTY);
                level.setBlockAndUpdate(pos, state.setValue(this.countProperty, count - 1));
                decaying.setStack(stack);
            } else {
                decaying.setStack(ItemStack.EMPTY);
                level.removeBlock(pos, false);
            }

            return ItemInteractionResult.SUCCESS;
        } else {
            if(itemInHand.is(this.asItem()) && count < this.maxCount) {
                long creationDate = Utils.mergeCreationDate(stack, itemInHand);
                FoodCapability.setCreationDate(stack, creationDate);
                decaying.setStack(ItemStack.EMPTY);
                level.setBlockAndUpdate(pos, state.setValue(this.countProperty, count + 1));
                decaying.setStack(stack);
                itemInHand.shrink(1);
                level.playSound(player, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
                return ItemInteractionResult.SUCCESS;
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    /**
     * @author Vvxzv
     * @reason 移除多余掉落物
     */
    @Overwrite
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.@NotNull Builder params) {
        return super.getDrops(state, params);
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity entity, @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, entity, stack);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof DecayingFoodBlockEntity decaying) {
            decaying.setStack(stack);
        }
    }

    @Override
    public boolean onDestroyedByPlayer(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, boolean willHarvest, @NotNull FluidState fluid) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof DecayingFoodBlockEntity decaying) {
            if (player.isCreative()) {
                decaying.setStack(ItemStack.EMPTY);
            }
        }

        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof DecayingFoodBlockEntity decaying) {
            ItemStack stack = decaying.getStack();
            ItemStack resultItem = stack.copy();
            int count = state.getValue(this.countProperty);
            resultItem.setCount(count);
            Helpers.spawnItem(level, pos, resultItem);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
