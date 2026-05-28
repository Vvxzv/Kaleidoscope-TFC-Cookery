package net.vvxzv.ktfcc.mixin.compat;

import com.github.ysbbbbbb.kaleidoscopecookery.compat.jei.category.TeapotRecipeCategory;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.TeapotRecipe;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IIngredientConsumer;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(TeapotRecipeCategory.class)
public class TeapotRecipeCategoryMixin {

    @Redirect(method = "setRecipe(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lcom/github/ysbbbbbb/kaleidoscopecookery/crafting/recipe/TeapotRecipe;Lmezz/jei/api/recipe/IFocusGroup;)V", at = @At(value = "INVOKE", target = "Lmezz/jei/api/gui/builder/IRecipeSlotBuilder;addItemLike(Lnet/minecraft/world/level/ItemLike;)Lmezz/jei/api/gui/builder/IIngredientConsumer;"), remap = false)
    private IIngredientConsumer removeBucketStack(IRecipeSlotBuilder instance, ItemLike itemLike, IRecipeLayoutBuilder builder, TeapotRecipe recipe, IFocusGroup focuses) {
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(recipe.teaFluid());
        if(fluid != null) {
            FluidStack fluidStack = new FluidStack(fluid, 1000);
            instance.addIngredients(ForgeTypes.FLUID_STACK, List.of(fluidStack));
        }
        return instance;
    }
}
