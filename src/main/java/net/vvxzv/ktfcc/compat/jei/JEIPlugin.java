package net.vvxzv.ktfcc.compat.jei;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.PlateRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.dries007.tfc.compat.jei.JEIIntegration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.vvxzv.ktfcc.KaleidoscopeTFCCookery;
import net.vvxzv.ktfcc.compat.jei.recipes.PotRecipeCategory;
import net.vvxzv.ktfcc.compat.jei.recipes.StockpotRecipeCategory;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(KaleidoscopeTFCCookery.MODID, "jei_plugin");
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.MILLSTONE.get()), JEIIntegration.QUERN);

        registry.addRecipeCatalyst(new ItemStack(ModBlocks.POT.get()), PotRecipeCategory.recipeType());
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.STOCKPOT.get()), StockpotRecipeCategory.recipeType());
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        registration.addRecipes(PotRecipeCategory.recipeType(), PotRecipeCategory.getRecipes());
        registration.addRecipes(StockpotRecipeCategory.recipeType(), StockpotRecipeCategory.getRecipes());
    }

    @Override
    public void registerCategories(@NotNull IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new PotRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new StockpotRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }
}
