package net.vvxzv.ktfcc.mixin.compat;
import com.github.ysbbbbbb.kaleidoscopecookery.api.recipe.soupbase.ISoupBase;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.jei.category.StockpotRecipeCategory;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.*;

import java.util.List;

@Mixin(StockpotRecipeCategory.class)
public class StockpotRecipeCategoryMixin {

    @Final
    @Shadow(remap = false)
    private IDrawable slotDraw;
    /**
     * @author Vvxzv
     * @reason fluidStack replace fluid bucket
     */
    @Overwrite(remap = false)
    public void setRecipe(IRecipeLayoutBuilder builder, StockpotRecipe recipe, IFocusGroup focuses) {
        NonNullList<Ingredient> inputs = recipe.getIngredients();
        ItemStack output = recipe.result();

        for(int i = 0; i < inputs.size(); ++i) {
            int xOffset = i % 3 * 18 + 15;
            int yOffset = i / 3 * 18 + 25;
            builder.addSlot(RecipeIngredientRole.INPUT, xOffset, yOffset).addIngredients(inputs.get(i)).setBackground(this.slotDraw, -1, -1);
        }

        ResourceLocation soupBase = recipe.soupBase();
        Fluid fluid = BuiltInRegistries.FLUID.get(soupBase);
        if (fluid == null) {
            throw new RuntimeException("No soup found for " + soupBase);
        } else {
            FluidStack fluidStack = new FluidStack(fluid, 1000);
            if (!fluidStack.isEmpty()) {
                builder.addSlot(RecipeIngredientRole.INPUT, 72, 61).addIngredients(ForgeTypes.FLUID_STACK, List.of(fluidStack));
            }

            if (!recipe.carrier().isEmpty()) {
                builder.addSlot(RecipeIngredientRole.INPUT, 133, 18).addIngredients(recipe.carrier());
            }

            builder.addSlot(RecipeIngredientRole.OUTPUT, 143, 60).addItemStack(output);
        }
    }
}
