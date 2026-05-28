package net.vvxzv.ktfcc.mixin.block.entity;

import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.ShawarmaSpitBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.ShawarmaSpitBlockEntity;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.component.heat.IHeat;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.vvxzv.ktfcc.common.utils.FoodTraits;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShawarmaSpitBlockEntity.class)
public class ShawarmaSpitBlockEntityMixin extends BaseBlockEntity {

    @Shadow
    public ItemStack cookingItem;

    @Shadow
    public ItemStack cookedItem;

    @Shadow
    public int cookTime;

    public ShawarmaSpitBlockEntityMixin(BlockEntityType<?> entityType, BlockPos pos, BlockState state) {
        super(entityType, pos, state);
    }

    @Inject(method = "onPutCookingItem", at = @At("HEAD"), cancellable = true)
    private void onPutCookingItem(Level level, ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        if (this.cookingItem.isEmpty() && this.cookedItem.isEmpty()) {
            if(FoodCapability.get(itemStack) != null){
                HeatingRecipe recipe = HeatingRecipe.getRecipe(itemStack);
                boolean returnValue = false;
                if(recipe != null) {
                    if(recipe.getTemperature() < 900) {
                        this.cookingItem = itemStack.split(8);
                        this.cookedItem = recipe.assembleItem(itemStack);
                        this.cookedItem.setCount(this.cookingItem.getCount());
                        FoodCapability.updateFoodFromPrevious(this.cookingItem, this.cookedItem);
                        FoodCapability.applyTrait(this.cookedItem, FoodTraits.SHAWARMA_COOKED);
                        this.cookTime = 500;
                        this.refresh();
                        if (level instanceof ServerLevel) {
                            BlockPos pos = this.worldPosition;
                            level.playSound(
                                    null,
                                    pos.getX() + 0.5,
                                    pos.getY() + 0.5,
                                    pos.getZ() + 0.5,
                                    SoundEvents.ITEM_FRAME_ADD_ITEM,
                                    SoundSource.BLOCKS,
                                    0.5F + level.random.nextFloat(),
                                    level.random.nextFloat() * 0.7F + 0.6F
                            );
                        }
                        returnValue = true;
                    }
                }
                cir.setReturnValue(returnValue);
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"), remap = false)
    private void heatingCookedItem(CallbackInfo ci) {
        if(this.getBlockState().getValue(ShawarmaSpitBlock.POWERED)) {
            if(!this.cookedItem.isEmpty()) {
                IHeat heat = HeatCapability.get(this.cookedItem);
                if (heat != null) {
                    HeatCapability.addTemp(heat, 450);
                }
            }
        }
    }
}
