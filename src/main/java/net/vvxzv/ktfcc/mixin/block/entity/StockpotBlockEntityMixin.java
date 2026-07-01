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
import net.dries007.tfc.common.capabilities.food.DynamicBowlHandler;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodData;
import net.dries007.tfc.common.capabilities.food.IFood;
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

import java.util.ArrayList;

@Mixin(StockpotBlockEntity.class)
public abstract class StockpotBlockEntityMixin extends BaseBlockEntity {

    @Shadow(remap = false)
    private ResourceLocation recipeId;

    @Shadow(remap = false)
    private ItemStack result;

    @Shadow(remap = false)
    public StockpotVisuals visuals;

    @Shadow(remap = false)
    private int currentTick;

    @Shadow(remap = false)
    private int takeoutCount;

    @Shadow(remap = false)
    private NonNullList<ItemStack> inputs;

    @Shadow(remap = false)
    private int status;

    @Shadow(remap = false)
    public Entity renderEntity;

    @Shadow(remap = false)
    private ResourceLocation soupBaseId;

    public StockpotBlockEntityMixin(BlockEntityType<?> entityType, BlockPos pos, BlockState state) {
        super(entityType, pos, state);
    }

    @Inject(method = "addIngredient", at = @At("HEAD"), cancellable = true, remap = false)
    public void addIngredient(Level level, LivingEntity user, ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        IFood iFood = FoodCapability.get(itemStack);
        if(iFood != null && iFood.isRotten()){
            cir.setReturnValue(false);
        }
        if(itemStack.getItem() instanceof FluidContainerItem){
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "hasHeatSource", at = @At("RETURN"), cancellable = true, remap = false)
    private void heatSource(Level level, CallbackInfoReturnable<Boolean> cir){
        BlockState belowState = level.getBlockState(this.worldPosition.below());
        cir.setReturnValue(belowState.hasProperty(BlockStateProperties.LIT)? belowState.getValue(BlockStateProperties.LIT) : belowState.is(AllTags.Blocks.HEAT_SOURCE));
    }

    @Shadow(remap = false)
    public abstract boolean hasLid();

    @Shadow(remap = false)
    protected abstract Either<StockpotRecipe, FlexStockpotRecipe> getRecipeById(Level level, ResourceLocation recipeId);

    @Shadow(remap = false)
    protected abstract void sendActionBarMessage(LivingEntity user, String key, Object... args);

    /**
     * @author Vvxzv
     * @reason 适配动态营养汤配方
     */
    @Overwrite(remap = false)
    public boolean takeOutProduct(Level level, LivingEntity user, ItemStack stack) {
        if (this.hasLid()) {
            return false;
        } else if (this.status == 3 && !this.result.isEmpty() && this.takeoutCount > 0) {
            Ingredient carrier = StockpotRecipeSerializer.DEFAULT_CARRIER;
            if(this.recipeId.equals(ResourceLocation.fromNamespaceAndPath(KaleidoscopeTFCCookery.MODID, "dynamic"))) {
                carrier = Ingredient.of(TFCTags.Items.SOUP_BOWLS);
            } else {
                Either<StockpotRecipe, FlexStockpotRecipe> recipe = this.getRecipeById(level, this.recipeId);
                if (recipe != null) {
                    carrier = recipe.map(StockpotRecipe::carrier, FlexStockpotRecipe::carrier);
                }
            }

            if (!carrier.isEmpty() && !carrier.test(stack)) {
                Component carrierName = carrier.getItems()[0].getHoverName();
                this.sendActionBarMessage(user, "tip.kaleidoscope_cookery.pot.need_carrier", carrierName);
                return false;
            } else {
                ItemStack resultCopy = this.result.copyWithCount(1);
                if(resultCopy.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("tfc", "soups")))) {
                    IFood food = FoodCapability.get(resultCopy);
                    if(food instanceof DynamicBowlHandler handler) {
                        handler.setBowl(stack);
                    }
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

    @Shadow(remap = false)
    protected abstract void applySuspiciousRecipe();

    @Redirect(method = "setRecipe", at = @At(value = "INVOKE", target = "Lcom/github/ysbbbbbb/kaleidoscopecookery/blockentity/kitchen/StockpotBlockEntity;applySuspiciousRecipe()V"), remap = false)
    private void addTFCPotRecipe(StockpotBlockEntity instance) {
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
        float[] nutrients = Utils.getFinalNutrients(this.inputs, 0.85f, this.getMaxDynamicNutrient());
        ItemStack resultItem = new ItemStack(TFCItems.SOUPS.get(Utils.matchMainNutrient(nutrients)).get());

        IFood food = FoodCapability.get(resultItem);
        if(food instanceof DynamicBowlHandler handler) {
            long created = FoodCapability.getRoundedCreationDate();
            handler.setCreationDate(created);

            handler.setIngredients(new ArrayList<>(this.inputs));

            FoodData tfcFoodData = FoodData.create(
                    4,
                    (nutrients[1] + nutrients[2]) * 5 + 20,
                    0.8f * Utils.getTotalItemCount(this.inputs),
                    nutrients,
                    Utils.calculateDecayModifier(this.inputs)
            );
            handler.setFood(tfcFoodData);
        }

        this.recipeId = ResourceLocation.fromNamespaceAndPath(KaleidoscopeTFCCookery.MODID, "dynamic");
        this.visuals = StockpotVisuals.DEFAULT;
        this.result = resultItem;
        this.currentTick = 300;
        this.takeoutCount = 1;
    }
}
