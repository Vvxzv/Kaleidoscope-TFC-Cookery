package net.vvxzv.ktfcc.mixin.blockentity;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.ChoppingBoardBlockEntity;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChoppingBoardBlockEntity.class)
public abstract class ChoppingBoardBlockEntityMixin extends BaseBlockEntity {
    public ChoppingBoardBlockEntityMixin(BlockEntityType<?> entityType, BlockPos pos, BlockState state) {
        super(entityType, pos, state);
    }

    @Shadow(remap = false)
    private ItemStack currentCutStack;

    @Shadow(remap = false)
    private ItemStack result;

    @Shadow(remap = false)
    protected abstract void resetBoardData();

    @Inject(method = "onCutItem", at = @At("HEAD"), cancellable = true, remap = false)
    private void returnRottenCutItem(Level level, LivingEntity user, ItemStack cutterItem, CallbackInfoReturnable<Boolean> cir) {
        if(!this.result.isEmpty()){
            IFood iFood = FoodCapability.get(this.currentCutStack);
            if(iFood != null && iFood.isRotten()){
                ChoppingBoardBlockEntity.popResource(level, this.worldPosition, this.currentCutStack.copy());
                this.resetBoardData();
                cir.setReturnValue(false);
            }
        }
    }
}
