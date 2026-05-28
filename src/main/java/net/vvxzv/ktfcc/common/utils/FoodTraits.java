package net.vvxzv.ktfcc.common.utils;

import net.dries007.tfc.common.component.food.FoodTrait;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.vvxzv.ktfcc.KaleidoscopeTFCCookery;

import java.util.function.Supplier;

public class FoodTraits {
    public static final DeferredRegister<FoodTrait> TRAITS = DeferredRegister.create(net.dries007.tfc.common.component.food.FoodTraits.KEY, KaleidoscopeTFCCookery.MODID);

    private static DeferredHolder<FoodTrait, FoodTrait> register(String name, Supplier<Double> decayModifier) {
        return TRAITS.register(name, () -> new FoodTrait(decayModifier, "ktfcc.tooltip.food_trait." + name));
    }

    public static final DeferredHolder<FoodTrait, FoodTrait> SHAWARMA_COOKED = register("shawarma_cooked", () -> 0.9);
    public static final DeferredHolder<FoodTrait, FoodTrait> CELLAR_PRESERVED = register("cellar_preserved", () -> 0.35);
    public static final DeferredHolder<FoodTrait, FoodTrait> CELLAR_PRESERVED_2 = register("cellar_preserved_2", () -> 0.3);
    public static final DeferredHolder<FoodTrait, FoodTrait> CELLAR_PRESERVED_3 = register("cellar_preserved_3", () -> 0.2);
}
