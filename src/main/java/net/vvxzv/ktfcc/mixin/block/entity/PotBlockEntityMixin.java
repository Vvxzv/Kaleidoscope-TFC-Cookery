package net.vvxzv.ktfcc.mixin.block.entity;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.PotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.capabilities.food.DynamicBowlHandler;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodData;
import net.dries007.tfc.common.capabilities.food.IFood;
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

    @Shadow(remap = false)
    private NonNullList<ItemStack> inputs;

    @Shadow(remap = false)
    private Ingredient carrier;

    @Shadow(remap = false)
    private ItemStack result;

    @Shadow(remap = false)
    private int status;

    @Shadow(remap = false)
    private int currentTick;

    @Shadow(remap = false)
    private int stirFryCount;

    public PotBlockEntityMixin(BlockEntityType<?> entityType, BlockPos pos, BlockState state) {
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
        if(belowState.hasProperty(BlockStateProperties.LIT) && belowState.getValue(BlockStateProperties.LIT)){
            cir.setReturnValue(true);
            return;
        }
        cir.setReturnValue(belowState.is(AllTags.Blocks.HEAT_SOURCE));
    }

    @Inject(method = "takeOutWithCarrier", at = @At("HEAD"), remap = false)
    public void ktfcc$bindBowlData(Level level, LivingEntity user, ItemStack mainHandItem, ItemStack finallyResult, CallbackInfoReturnable<Boolean> cir) {
        IFood food = FoodCapability.get(finallyResult);
        if (food instanceof DynamicBowlHandler handler) {
            handler.setBowl(mainHandItem);
        }
    }

    @Shadow(remap = false)
    protected abstract void applySuspiciousRecipe();

    @Redirect(method = "startCooking", at = @At(value = "INVOKE", target = "Lcom/github/ysbbbbbb/kaleidoscopecookery/blockentity/kitchen/PotBlockEntity;applySuspiciousRecipe()V"), remap = false)
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

        IFood food = FoodCapability.get(resultItem);
        if(food instanceof DynamicBowlHandler handler) {
            long created = FoodCapability.getRoundedCreationDate();
            handler.setCreationDate(created);

            handler.setIngredients(new ArrayList<>(this.inputs));

            FoodData tfcFoodData = FoodData.create(
                    4,
                    nutrients[1] + nutrients[2],
                    0.8f * Utils.getTotalItemCount(this.inputs),
                    nutrients,
                    Utils.calculateDecayModifier(this.inputs)
            );
            handler.setFood(tfcFoodData);
        }

        this.carrier = Ingredient.of(TFCTags.Items.SOUP_BOWLS);
        this.result = resultItem;
        this.currentTick = 200;
        this.stirFryCount = 3;
        this.status = 1;
        this.refresh();
    }
}
