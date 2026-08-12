package net.vvxzv.ktfcc.mixin.block.entity;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.FlexStockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotVisuals;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.StockpotRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModSoupBases;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import com.mojang.datafixers.util.Either;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.component.Bowl;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.FoodData;
import net.dries007.tfc.common.component.food.IFood;
import net.dries007.tfc.common.component.item.ItemListComponent;
import net.dries007.tfc.common.items.FluidContainerItem;
import net.dries007.tfc.common.items.TFCItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.vvxzv.ktfcc.Config;
import net.vvxzv.ktfcc.KaleidoscopeTFCCookery;
import net.vvxzv.ktfcc.common.utils.AllTags;
import net.vvxzv.ktfcc.common.utils.Utils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StockpotBlockEntity.class)
public abstract class StockpotBlockEntityMixin extends BaseBlockEntity {

    @Shadow
    private ResourceLocation recipeId;

    @Shadow
    private ItemStack result;

    @Shadow
    public StockpotVisuals visuals;

    @Shadow
    private int currentTick;

    @Shadow
    private int takeoutCount;

    @Shadow
    private NonNullList<ItemStack> inputs;

    @Shadow
    private int status;

    @Shadow
    public Entity renderEntity;

    @Shadow
    private ResourceLocation soupBaseId;

    public StockpotBlockEntityMixin(BlockEntityType<?> entityType, BlockPos pos, BlockState state) {
        super(entityType, pos, state);
    }

    @Inject(method = "addIngredient", at = @At("HEAD"), cancellable = true)
    private void addIngredient(Level level, LivingEntity user, ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        IFood iFood = FoodCapability.get(itemStack);
        if(iFood != null && iFood.isRotten()){
            cir.setReturnValue(false);
        }
        if(itemStack.getItem() instanceof FluidContainerItem){
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "hasHeatSource", at = @At("RETURN"), cancellable = true)
    private void heatSource(Level level, CallbackInfoReturnable<Boolean> cir) {
        BlockState belowState = level.getBlockState(this.worldPosition.below());
        if(belowState.hasProperty(BlockStateProperties.LIT)){
            cir.setReturnValue(belowState.getValue(BlockStateProperties.LIT));
            return;
        }
        cir.setReturnValue(belowState.is(AllTags.Blocks.HEAT_SOURCE));
    }

    @Shadow
    protected abstract void applySuspiciousRecipe();

    @Redirect(method = "setRecipe", at = @At(value = "INVOKE", target = "Lcom/github/ysbbbbbb/kaleidoscopecookery/blockentity/kitchen/StockpotBlockEntity;applySuspiciousRecipe()V"))
    private void addTFCPotRecipe(StockpotBlockEntity instance) {
        if(this.isValidItems()) {
            this.setDynamicNutrientRecipe();
        } else {
            this.applySuspiciousRecipe();
        }
    }

    @Shadow
    public abstract boolean hasLid();

    @Shadow
    protected abstract Either<RecipeHolder<StockpotRecipe>, RecipeHolder<FlexStockpotRecipe>> getRecipeById(Level level, ResourceLocation recipeId);

    @Shadow
    protected abstract void sendActionBarMessage(LivingEntity user, String key, Object... args);

    /**
     * @author Vvxzv
     * @reason 适配动态营养汤配方
     */
    @Overwrite
    public boolean takeOutProduct(Level level, LivingEntity user, ItemStack stack) {
        if (this.hasLid()) {
            return false;
        } else if (this.status == 3 && !this.result.isEmpty() && this.takeoutCount > 0) {
            Ingredient carrier = StockpotRecipeSerializer.DEFAULT_CARRIER;
            if(this.recipeId.equals(ResourceLocation.fromNamespaceAndPath(KaleidoscopeTFCCookery.MODID, "dynamic"))) {
                carrier = Ingredient.of(TFCTags.Items.BOWLS);
            } else {
                Either<RecipeHolder<StockpotRecipe>, RecipeHolder<FlexStockpotRecipe>> recipe = this.getRecipeById(level, this.recipeId);
                if (recipe != null) {
                    carrier = recipe.map((left) -> left.value().carrier(), (right) -> right.value().carrier());
                }
            }

            if (!carrier.isEmpty() && !carrier.test(stack)) {
                Component carrierName = carrier.getItems()[0].getHoverName();
                this.sendActionBarMessage(user, "tip.kaleidoscope_cookery.pot.need_carrier", carrierName);
                return false;
            } else {
                ItemStack resultCopy = this.result.copyWithCount(1);
                if(resultCopy.is(AllTags.Items.SOUPS)) {
                    resultCopy.set(TFCComponents.BOWL, Bowl.of(stack));
                }
                ItemUtils.getItemToLivingEntity(user, resultCopy);

                if (!carrier.isEmpty()) {
                    stack.shrink(1);
                }

                --this.takeoutCount;
                if (this.takeoutCount <= 0) {
                    this.status = 0;
                    this.inputs.clear();
                    this.recipeId = StockpotRecipeSerializer.EMPTY_ID;
                    this.soupBaseId = ModSoupBases.WATER;
                    this.result = ItemStack.EMPTY;
                    this.currentTick = -1;
                    this.renderEntity = null;
                }

                this.refresh();
                return true;
            }
        } else {
            return false;
        }
    }

    @Unique
    private boolean isValidItems() {
        for (ItemStack itemStack: this.inputs) {
            if(!itemStack.isEmpty()) {
                if(!itemStack.is(AllTags.Items.STOCKPOT_INGREDIENT)) {
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
        ItemStack resultItem = new ItemStack(TFCItems.SOUPS.get(Utils.matchMainNutrient(nutrients)));

        FoodData tfcFoodData = new FoodData(
                4,
                (nutrients[1] + nutrients[2]) * 5 + 20,
                0.8f * Utils.getTotalItemCount(this.inputs),
                0,
                nutrients,
                Utils.calculateDecayModifier(this.inputs)
        );
        FoodCapability.setFoodForDynamicItemOnCreate(resultItem, tfcFoodData);
        resultItem.set(TFCComponents.INGREDIENTS, ItemListComponent.of(this.inputs));

        this.recipeId = ResourceLocation.fromNamespaceAndPath(KaleidoscopeTFCCookery.MODID, "dynamic");
        this.visuals = StockpotVisuals.DEFAULT;
        this.result = resultItem;
        this.currentTick = 300;
        this.takeoutCount = 1;
    }
}
