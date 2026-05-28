package net.vvxzv.ktfcc.mixin.compat;

import com.github.ysbbbbbb.kaleidoscopecookery.compat.jei.category.TeapotRecipeCategory;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.TeapotRecipe;
import mezz.jei.api.gui.builder.IIngredientConsumer;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(TeapotRecipeCategory.class)
public class TeapotRecipeCategoryMixin {

    @Redirect(method = "setRecipe(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lnet/minecraft/world/item/crafting/RecipeHolder;Lmezz/jei/api/recipe/IFocusGroup;)V", at = @At(value = "INVOKE", target = "Lmezz/jei/api/gui/builder/IRecipeSlotBuilder;addItemLike(Lnet/minecraft/world/level/ItemLike;)Lmezz/jei/api/gui/builder/IIngredientConsumer;"))
    private IIngredientConsumer removeBucketStack(IRecipeSlotBuilder instance, ItemLike itemLike, IRecipeLayoutBuilder builder, RecipeHolder<TeapotRecipe> holder, IFocusGroup focuses) {
        Fluid fluid = BuiltInRegistries.FLUID.get(holder.value().teaFluid());
        FluidStack fluidStack = new FluidStack(fluid, 1000);
        return instance.addIngredients(NeoForgeTypes.FLUID_STACK, List.of(fluidStack));
    }
}
