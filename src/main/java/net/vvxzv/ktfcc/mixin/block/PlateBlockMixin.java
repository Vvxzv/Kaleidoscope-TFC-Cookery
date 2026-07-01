package net.vvxzv.ktfcc.mixin.block;

import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.PlateBlock;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.vvxzv.ktfcc.common.block.entity.DecayingFoodBlockEntity;
import net.vvxzv.ktfcc.common.data.Plate;
import net.vvxzv.ktfcc.common.utils.Decaying;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Mixin(PlateBlock.class)
public abstract class PlateBlockMixin extends HorizontalDirectionalBlock implements EntityBlock {

    @Final
    @Shadow
    protected IntegerProperty servings;

    @Final
    @Shadow
    protected int maxCount;

    protected PlateBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
        public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
            return new DecayingFoodBlockEntity(blockPos, blockState);
    }

    /**
     * @author Vvxzv
     * @reason 修改逻辑
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

        int count = state.getValue(this.servings);
        ItemStack stack = decaying.getStack().copy();

        if(itemInHand.isEmpty()) {
            if (count > 0) {
                ItemHandlerHelper.giveItemToPlayer(player, decaying.getStack().copy());

                level.playSound(player, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
                decaying.setStack(ItemStack.EMPTY);
                level.setBlockAndUpdate(pos, state.setValue(this.servings, count - 1));
                if(count - 1 > 0) {
                    decaying.setStack(stack);
                }
            } else {
                level.destroyBlock(pos, true, player);
            }

            return ItemInteractionResult.SUCCESS;
        }

        ItemStack plateItem = Plate.getPlateItem(itemInHand);
        if(plateItem != null && plateItem.is(this.asItem()) && count < this.maxCount) {
            var food = stack.get(TFCComponents.FOOD);
            long foodCreationDate = food != null? food.getCreationDate(): -1L;
            var handFood = itemInHand.get(TFCComponents.FOOD);
            long handFoodCreationDate = handFood != null? handFood.getCreationDate(): -1L;
            long creationDate = Math.min(foodCreationDate, handFoodCreationDate);
            FoodCapability.setCreationDate(stack, creationDate);
            decaying.setStack(ItemStack.EMPTY);
            level.setBlock(pos, state.setValue(this.servings, count + 1), 2);
            decaying.setStack(stack);
            if(count == 0) {
                decaying.setStack(itemInHand);
            }
            itemInHand.shrink(1);
            level.playSound(player, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    /**
     * @author Vvxzv
     * @reason 移除多余掉落物
     */
    @Overwrite
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.@NotNull Builder params) {
        ResourceKey<LootTable> resourcekey = this.getLootTable();
        if (resourcekey == BuiltInLootTables.EMPTY) {
            return Collections.emptyList();
        } else {
            LootParams lootparams = params.withParameter(LootContextParams.BLOCK_STATE, state).create(LootContextParamSets.BLOCK);
            ServerLevel serverlevel = lootparams.getLevel();
            LootTable loottable = serverlevel.getServer().reloadableRegistries().getLootTable(resourcekey);
            return loottable.getRandomItems(lootparams);
        }
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
