package net.vvxzv.ktfcc.mixin.item;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.PotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.StockpotContainer;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.FlexPotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.FlexStockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.PotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopecookery.item.RecipeItem;
import net.dries007.tfc.common.items.TFCItems;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.vvxzv.ktfcc.Config;
import net.vvxzv.ktfcc.common.registry.Items;
import net.vvxzv.ktfcc.common.utils.AllTags;
import net.vvxzv.ktfcc.common.utils.Utils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;

@Mixin(RecipeItem.class)
public class RecipeItemMixin {

    @Unique
    private static Constructor<?> RECIPE_RESULT_CONSTRUCTOR;

    static {
        try {
            Class<?> recipeResultClass = Class.forName("com.github.ysbbbbbb.kaleidoscopecookery.item.RecipeItem$RecipeResult");
            RECIPE_RESULT_CONSTRUCTOR = recipeResultClass.getDeclaredConstructor(ItemStack.class, boolean.class);
            RECIPE_RESULT_CONSTRUCTOR.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Unique
    private Object createRecipeResult(ItemStack output) {
        try {
            return RECIPE_RESULT_CONSTRUCTOR.newInstance(output, false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Unique
    private float getMaxDynamicNutrient() {
        return (float) Config.maxDynamicNutrient;
    }

    @Inject(method = "getPotRecipeResult", at = @At("HEAD"), cancellable = true, remap = false)
    private void getPotRecipeResult(Level level, RecipeManager recipeManager, PotBlockEntity pot, List<ItemStack> inputs, ItemStack recordStack, CallbackInfoReturnable<Object> cir) {
        SimpleContainer container = pot.getContainer();
        Optional<PotRecipe> potRecipe = recipeManager.getRecipeFor(ModRecipes.POT_RECIPE, container, level);
        Optional<FlexPotRecipe> flexPotRecipe = recipeManager.getRecipeFor(ModRecipes.FLEX_POT_RECIPE, container, level);

        if(potRecipe.isEmpty() && flexPotRecipe.isEmpty() && Utils.isValidItems(inputs, AllTags.Items.POT_INGREDIENT)) {
            float[] nutrients = Utils.getFinalNutrients(inputs, 0.8f, this.getMaxDynamicNutrient());
            ItemStack stack = new ItemStack(Items.DISHES.get(Utils.matchMainNutrient(nutrients)).get());
            cir.setReturnValue(this.createRecipeResult(stack));
            cir.cancel();
        }

    }

    @Inject(method = "getStockpotRecipeResult", at = @At("HEAD"), cancellable = true, remap = false)
    private void getStockpotRecipeResult(Level level, RecipeManager recipeManager, StockpotBlockEntity stockpot, List<ItemStack> inputs, ItemStack recordStack, CallbackInfoReturnable<Object> cir) {
        StockpotContainer container = stockpot.getContainer();
        Optional<StockpotRecipe> stockpotRecipe = recipeManager.getRecipeFor(ModRecipes.STOCKPOT_RECIPE, container, level);
        Optional<FlexStockpotRecipe> flexStockpotRecipe = recipeManager.getRecipeFor(ModRecipes.FLEX_STOCKPOT_RECIPE, container, level);

        if(stockpotRecipe.isEmpty() && flexStockpotRecipe.isEmpty() && Utils.isValidItems(inputs, AllTags.Items.STOCKPOT_INGREDIENT)) {
            float[] nutrients = Utils.getFinalNutrients(inputs, 0.85f, this.getMaxDynamicNutrient());
            ItemStack stack = new ItemStack(TFCItems.SOUPS.get(Utils.matchMainNutrient(nutrients)).get());
            cir.setReturnValue(this.createRecipeResult(stack));
            cir.cancel();
        }
    }
}
