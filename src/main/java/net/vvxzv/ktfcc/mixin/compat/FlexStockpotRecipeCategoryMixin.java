package net.vvxzv.ktfcc.mixin.compat;

import com.github.ysbbbbbb.kaleidoscopecookery.compat.jei.category.FlexStockpotRecipeCategory;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.FlexStockpotRecipe;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(FlexStockpotRecipeCategory.class)
public class FlexStockpotRecipeCategoryMixin {

    @Redirect(method = "setRecipe(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lcom/github/ysbbbbbb/kaleidoscopecookery/crafting/recipe/FlexStockpotRecipe;Lmezz/jei/api/recipe/IFocusGroup;)V", at = @At(value = "INVOKE", target = "Lmezz/jei/api/gui/builder/IRecipeSlotBuilder;addIngredients(Lnet/minecraft/world/item/crafting/Ingredient;)Lmezz/jei/api/gui/builder/IIngredientAcceptor;", ordinal = 1), remap = false)
    private IIngredientAcceptor removeBucketStack(IRecipeSlotBuilder instance, Ingredient ingredient, IRecipeLayoutBuilder builder, FlexStockpotRecipe recipe, IFocusGroup focuses) {
        Fluid fluid = BuiltInRegistries.FLUID.get(recipe.soupBase());
        FluidStack fluidStack = new FluidStack(fluid, 1000);
        return instance.addIngredients(ForgeTypes.FLUID_STACK, List.of(fluidStack));
    }
}
