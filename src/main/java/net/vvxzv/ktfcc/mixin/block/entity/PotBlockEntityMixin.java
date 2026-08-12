package net.vvxzv.ktfcc.mixin.block.entity;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.PotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.component.Bowl;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.FoodData;
import net.dries007.tfc.common.component.food.IFood;
import net.dries007.tfc.common.component.item.ItemListComponent;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.vvxzv.ktfcc.Config;
import net.vvxzv.ktfcc.common.registry.Items;
import net.vvxzv.ktfcc.common.utils.AllTags;
import net.vvxzv.ktfcc.common.utils.Utils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

@Mixin(PotBlockEntity.class)
public abstract class PotBlockEntityMixin extends BaseBlockEntity {

    @Shadow
    private NonNullList<ItemStack> inputs;

    @Shadow
    private Ingredient carrier;

    @Shadow
    private ItemStack result;

    @Shadow
    private int status;

    @Shadow
    private int currentTick;

    @Shadow
    private int stirFryCount;

    public PotBlockEntityMixin(BlockEntityType<?> entityType, BlockPos pos, BlockState state) {
        super(entityType, pos, state);
    }

    @Inject(method = "addIngredient", at = @At("HEAD"), cancellable = true)
    public void addIngredient(Level level, LivingEntity user, ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        IFood iFood = FoodCapability.get(itemStack);
        if(iFood != null && iFood.isRotten()){
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "hasHeatSource", at = @At("RETURN"), cancellable = true)
    private void heatSource(Level level, CallbackInfoReturnable<Boolean> cir){
        BlockState belowState = level.getBlockState(this.worldPosition.below());
        if(belowState.hasProperty(BlockStateProperties.LIT)){
            cir.setReturnValue(belowState.getValue(BlockStateProperties.LIT));
            return;
        }
        cir.setReturnValue(belowState.is(AllTags.Blocks.HEAT_SOURCE));
    }

    @Inject(method = "takeOutWithCarrier", at = @At("HEAD"), remap = false)
    public void ktfcc$bindBowlData(Level level, LivingEntity user, ItemStack mainHandItem, ItemStack finallyResult, CallbackInfoReturnable<Boolean> cir) {
        if(finallyResult.is(AllTags.Items.DISHES)) {
            finallyResult.set(TFCComponents.BOWL, Bowl.of(mainHandItem));
        }
    }

    @Shadow
    protected abstract void applySuspiciousRecipe();

    @Redirect(method = "startCooking", at = @At(value = "INVOKE", target = "Lcom/github/ysbbbbbb/kaleidoscopecookery/blockentity/kitchen/PotBlockEntity;applySuspiciousRecipe()V"))
    private void addDishRecipe(PotBlockEntity instance) {
        if(this.isValidItems()) {
            this.setDynamicNutrientRecipe();
        } else {
            this.applySuspiciousRecipe();
        }
    }

    @Unique
    private boolean isValidItems() {
        for (ItemStack itemStack: this.inputs) {
            if(!itemStack.isEmpty()) {
                if(!itemStack.is(AllTags.Items.POT_INGREDIENT)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Unique
    private float getMaxDynamicNutrient() {
        return (float) Config.maxDynamicNutrient;
    }

    @Unique
    private void setDynamicNutrientRecipe() {
        float[] nutrients = Utils.getFinalNutrients(this.inputs, 0.8f, this.getMaxDynamicNutrient());
        ItemStack resultItem = new ItemStack(Items.DISHES.get(Utils.matchMainNutrient(nutrients)).get());

        FoodData tfcFoodData = new FoodData(
                4,
                nutrients[1] + nutrients[2],
                0.8f * Utils.getTotalItemCount(this.inputs),
                0,
                nutrients,
                Utils.calculateDecayModifier(this.inputs)
        );
        FoodCapability.setFoodForDynamicItemOnCreate(resultItem, tfcFoodData);
        resultItem.set(TFCComponents.INGREDIENTS, ItemListComponent.of(this.inputs));

        this.carrier = Ingredient.of(TFCTags.Items.SOUP_BOWLS);
        this.result = resultItem;
        this.currentTick = 200;
        this.stirFryCount = 3;
        this.status = 1;
        this.refresh();
    }
}
