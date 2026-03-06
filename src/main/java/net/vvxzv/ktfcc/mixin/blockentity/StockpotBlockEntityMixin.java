package net.vvxzv.ktfcc.mixin.blockentity;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.vvxzv.ktfcc.common.registry.KTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StockpotBlockEntity.class)
public class StockpotBlockEntityMixin extends BaseBlockEntity {
    public StockpotBlockEntityMixin(BlockEntityType<?> entityType, BlockPos pos, BlockState state) {
        super(entityType, pos, state);
    }

    @Inject(method = "addIngredient", at = @At("HEAD"), cancellable = true, remap = false)
    public void addIngredient(Level level, LivingEntity user, ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        IFood iFood = FoodCapability.get(itemStack);
        if(iFood != null && iFood.isRotten()){
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "hasHeatSource", at = @At("RETURN"), cancellable = true, remap = false)
    private void heatSource(Level level, CallbackInfoReturnable<Boolean> cir){
        BlockState belowState = level.getBlockState(this.worldPosition.below());
        cir.setReturnValue(belowState.hasProperty(BlockStateProperties.LIT)? belowState.getValue(BlockStateProperties.LIT) : belowState.is(KTags.HEAT_SOURCE));
    }
}
