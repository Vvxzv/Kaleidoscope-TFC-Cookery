package net.vvxzv.ktfcc.mixin.block.entity;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.food.FoodBiteThreeByThreeBlockEntity;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.vvxzv.ktfcc.common.utils.Decaying;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodBiteThreeByThreeBlockEntity.class)
public class FoodBiteThreeByThreeBlockEntityMixin extends BaseBlockEntity implements Decaying {

    @Unique
    private ItemStack stack;

    public FoodBiteThreeByThreeBlockEntityMixin(BlockEntityType<?> entityType, BlockPos pos, BlockState state) {
        super(entityType, pos, state);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(BlockPos pos, BlockState state, CallbackInfo ci) {
        this.stack = ItemStack.EMPTY;
    }

    @Override
    public final void load(CompoundTag tag) {
        this.stack = ItemStack.of(tag.getCompound("item"));
        super.load(tag);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("item", this.stack.save(new CompoundTag()));
    }

    @Override
    public boolean isRotten() {
        return this.stack.isEmpty() || FoodCapability.isRotten(this.stack);
    }

    @Override
    public ItemStack getStack() {
        return this.stack;
    }

    @Override
    public void setStack(ItemStack stack) {
        this.stack = stack.copyWithCount(1);
    }
}
