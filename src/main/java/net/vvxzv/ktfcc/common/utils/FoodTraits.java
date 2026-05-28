package net.vvxzv.ktfcc.common.utils;

import net.dries007.tfc.common.capabilities.food.FoodTrait;
import net.minecraft.resources.ResourceLocation;
import net.vvxzv.ktfcc.KaleidoscopeTFCCookery;

public class FoodTraits {
    public static void registerFoodTraits() {
    }

    private static FoodTrait register(String name, float decayModifier) {
        return FoodTrait.register(ResourceLocation.fromNamespaceAndPath(KaleidoscopeTFCCookery.MODID, name), new FoodTrait(decayModifier, "ktfcc.tooltip.food_trait." + name));
    }

    public static final FoodTrait SHAWARMA_COOKED  = register("shawarma_cooked", 0.9F);
    public static final FoodTrait CELLAR_PRESERVED  = register("cellar_preserved", 0.35F);
    public static final FoodTrait CELLAR_PRESERVED_2  = register("cellar_preserved_2", 0.3F);
    public static final FoodTrait CELLAR_PRESERVED_3  = register("cellar_preserved_3", 0.2F);

}
