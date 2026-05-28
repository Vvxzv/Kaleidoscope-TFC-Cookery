package net.vvxzv.ktfcc.mixin.compat;

import com.github.ysbbbbbb.kaleidoscopecookery.compat.jei.category.StockpotRecipeCategory;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(StockpotRecipeCategory.class)
public class StockpotRecipeCategoryMixin {

    @Redirect(method = "setRecipe(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lcom/github/ysbbbbbb/kaleidoscopecookery/crafting/recipe/StockpotRecipe;Lmezz/jei/api/recipe/IFocusGroup;)V", at = @At(value = "INVOKE", target = "Lmezz/jei/api/gui/builder/IRecipeSlotBuilder;addIngredients(Lnet/minecraft/world/item/crafting/Ingredient;)Lmezz/jei/api/gui/builder/IIngredientAcceptor;", ordinal = 1), remap = false)
    private IIngredientAcceptor removeBucketStack(IRecipeSlotBuilder instance, Ingredient ingredient, IRecipeLayoutBuilder builder, StockpotRecipe recipe, IFocusGroup focuses) {
        ResourceLocation soupBase = recipe.soupBase();
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(soupBase);
        if(fluid != null) {
            FluidStack fluidStack = new FluidStack(fluid, 1000);
            instance.addIngredients(ForgeTypes.FLUID_STACK, List.of(fluidStack));
        }
        return instance;
    }
}
