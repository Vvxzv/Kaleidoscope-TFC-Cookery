package net.vvxzv.ktfcc.mixin.block;

import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.PlateBlock;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.ItemHandlerHelper;
import net.vvxzv.ktfcc.common.block.entity.DecayingFoodBlockEntity;
import net.vvxzv.ktfcc.common.data.Plate;
import net.vvxzv.ktfcc.common.utils.Decaying;
import net.vvxzv.ktfcc.common.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(PlateBlock.class)
public class PlateBlockMixin extends HorizontalDirectionalBlock implements EntityBlock {

    @Final
    @Shadow(remap = false)
    protected IntegerProperty servings;

    @Final
    @Shadow(remap = false)
    protected int maxCount;

    public PlateBlockMixin(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new DecayingFoodBlockEntity(pPos, pState);
    }

    /**
     * @author Vvxzv
     * @reason 修改逻辑
     */
    @Overwrite
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(!(blockEntity instanceof Decaying decaying)) {
            return InteractionResult.PASS;
        }

        int count = state.getValue(this.servings);
        ItemStack stack = decaying.getStack().copy();
        ItemStack itemInHand = player.getItemInHand(hand);

        if(itemInHand.isEmpty()) {
            if (count > 0) {
                ItemHandlerHelper.giveItemToPlayer(player, stack.copy());

                level.playSound(player, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
                decaying.setStack(ItemStack.EMPTY);
                level.setBlockAndUpdate(pos, state.setValue(this.servings, count - 1));
                if(count - 1 > 0) {
                    decaying.setStack(stack);
                }
            } else {
                level.destroyBlock(pos, true, player);
            }

            return InteractionResult.SUCCESS;
        }

        ItemStack plateItem = Plate.getPlateItem(itemInHand);
        if(plateItem != null && plateItem.is(this.asItem()) && count < this.maxCount) {
            long creationDate = Utils.mergeCreationDate(stack, itemInHand);
            FoodCapability.setCreationDate(stack, creationDate);
            decaying.setStack(ItemStack.EMPTY);
            level.setBlock(pos, state.setValue(this.servings, count + 1), 2);
            decaying.setStack(stack);
            if(count == 0) {
                decaying.setStack(itemInHand);
            }
            itemInHand.shrink(1);
            level.playSound(player, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    /**
     * @author Vvxzv
     * @reason 移除多余掉落物
     */
    @Overwrite
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState pState, LootParams.@NotNull Builder pParams) {
        return super.getDrops(pState, pParams);
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity entity, @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, entity, stack);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof DecayingFoodBlockEntity decaying) {
            ItemStack itemInPlate = Plate.getItemInPlate(stack);
            if (itemInPlate != null) {
                decaying.setStack(itemInPlate);
            }
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
            int count = state.getValue(this.servings);
            resultItem.setCount(count);
            Helpers.spawnItem(level, pos, resultItem);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
