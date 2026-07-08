package net.vvxzv.ktfcc.compat.jei.recipes;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.compat.jei.JEIIntegration;
import net.dries007.tfc.compat.jei.category.SoupPotRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.vvxzv.ktfcc.KaleidoscopeTFCCookery;
import net.vvxzv.ktfcc.common.utils.AllTags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StockpotRecipeCategory implements IRecipeCategory<StockpotRecipeCategory.StockpotRecipe> {
    private final IDrawable icon;
    protected final IDrawableStatic slot;
    protected final IDrawableStatic fire;
    protected final IDrawableAnimated fireAnimated;
    protected final IDrawableStatic arrow;
    protected final IDrawableAnimated arrowAnimated;

    public StockpotRecipeCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableIngredient(JEIIntegration.ITEM_STACK, new ItemStack(ModBlocks.STOCKPOT.get()));
        this.slot = helper.getSlotDrawable();
        this.fire = helper.createDrawable(SoupPotRecipeCategory.ICONS, 0, 0, 14, 14);
        IDrawableStatic fireAnimated = helper.createDrawable(SoupPotRecipeCategory.ICONS, 14, 0, 14, 14);
        this.fireAnimated = helper.createAnimatedDrawable(fireAnimated, 160, IDrawableAnimated.StartDirection.TOP, true);
        this.arrow = helper.createDrawable(SoupPotRecipeCategory.ICONS, 0, 14, 22, 16);
        this.arrowAnimated = helper.createAnimatedDrawable(helper.createDrawable(SoupPotRecipeCategory.ICONS, 22, 14, 22, 16), 80, IDrawableAnimated.StartDirection.LEFT, false);
    }

    public static RecipeType<StockpotRecipe> recipeType() {
        return new RecipeType<>(ResourceLocation.fromNamespaceAndPath(
                KaleidoscopeTFCCookery.MODID, "stockpot"),
                StockpotRecipe.class
        );
    }

    public static List<StockpotRecipe> getRecipes() {
        return List.of(new StockpotRecipe());
    }

    @Override
    public @NotNull RecipeType<StockpotRecipe> getRecipeType() {
        return recipeType();
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("ktfcc.jei.stockpot");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public int getWidth() {
        return 175;
    }

    @Override
    public int getHeight() {
        return 50;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull StockpotRecipe recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 46, 6)
                .addIngredients(Ingredient.of(AllTags.Items.STOCKPOT_INGREDIENT))
                .setBackground(this.slot, -1, -1);

        Fluid water = BuiltInRegistries.FLUID.get(ResourceLocation.fromNamespaceAndPath("minecraft", "water"));
        builder.addSlot(RecipeIngredientRole.INPUT, 46, 26)
                .addIngredients(ForgeTypes.FLUID_STACK, List.of(new FluidStack(water, 1000)))
                .setBackground(this.slot, -1, -1);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 126, 6)
                .addItemStacks(TFCItems.SOUPS.values().stream().map((reg) -> new ItemStack(reg.get())).toList())
                .setBackground(this.slot, -1, -1);
    }

    @Override
    public void draw(@NotNull StockpotRecipe recipe, @NotNull IRecipeSlotsView recipeSlots, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.fire.draw(guiGraphics, 86, 25);
        this.fireAnimated.draw(guiGraphics, 86, 25);
        this.arrow.draw(guiGraphics, 86, 7);
        this.arrowAnimated.draw(guiGraphics, 86, 7);
    }

    public static class StockpotRecipe {

    }
}
