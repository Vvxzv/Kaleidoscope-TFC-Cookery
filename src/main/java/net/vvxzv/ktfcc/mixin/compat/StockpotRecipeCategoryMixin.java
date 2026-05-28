package net.vvxzv.ktfcc.mixin.compat;

import com.github.ysbbbbbb.kaleidoscopecookery.compat.jei.category.StockpotRecipeCategory;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(StockpotRecipeCategory.class)
public class StockpotRecipeCategoryMixin {

    @Redirect(method = "setRecipe(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lnet/minecraft/world/item/crafting/RecipeHolder;Lmezz/jei/api/recipe/IFocusGroup;)V", at = @At(value = "INVOKE", target = "Lmezz/jei/api/gui/builder/IRecipeSlotBuilder;addIngredients(Lnet/minecraft/world/item/crafting/Ingredient;)Lmezz/jei/api/gui/builder/IIngredientAcceptor;", ordinal = 1))
    private IIngredientAcceptor removeBucketStack(IRecipeSlotBuilder instance, Ingredient ingredient, IRecipeLayoutBuilder builder, RecipeHolder<StockpotRecipe> holder, IFocusGroup focuses) {
        Fluid fluid = BuiltInRegistries.FLUID.get(holder.value().soupBase());
        FluidStack fluidStack = new FluidStack(fluid, 1000);
        return instance.addIngredients(NeoForgeTypes.FLUID_STACK, List.of(fluidStack));
    }
}
