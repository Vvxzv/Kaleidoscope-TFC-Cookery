package net.vvxzv.ktfcc.mixin.blockentity;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.SteamerBlockEntity;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(SteamerBlockEntity.class)
public class SteamerBlockEntityMixin extends BaseBlockEntity {

    public SteamerBlockEntityMixin(BlockEntityType<?> entityType, BlockPos pos, BlockState state) {
        super(entityType, pos, state);
    }

    @Inject(
            method = "placeFood",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void placeFood(Level level, LivingEntity user, ItemStack food, CallbackInfoReturnable<Boolean> cir) {
        IFood iFood = FoodCapability.get(food);
        if(iFood != null && iFood.isRotten()){
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "cookingTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/NonNullList;set(ILjava/lang/Object;)Ljava/lang/Object;",
                    shift = At.Shift.BEFORE
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void cookingTick(Level level, BlockPos pos, BlockState state, SteamerBlockEntity steamer, CallbackInfo ci, BlockState aboveState, boolean aboveIsSteamer, boolean hasCooking, int i, ItemStack stack, int progress, Container container, ItemStack resultStack) {
        IFood iFood = FoodCapability.get(stack);
        IFood resultIFood = FoodCapability.get(resultStack);
        if(iFood != null && resultIFood != null){
            resultIFood.setCreationDate(iFood.getCreationDate());
        }
    }

    @Inject(method = "hasHeatSource", at = @At("RETURN"), cancellable = true, remap = false)
    private void heatSource(Level level, CallbackInfoReturnable<Boolean> cir){
        BlockState belowState = level.getBlockState(this.worldPosition.below());
        if(belowState.hasProperty(BlockStateProperties.LIT) && belowState.getValue(BlockStateProperties.LIT)){
            cir.setReturnValue(true);
            return;
        }
        cir.setReturnValue(belowState.is(KTags.HEAT_SOURCE));
    }
}
