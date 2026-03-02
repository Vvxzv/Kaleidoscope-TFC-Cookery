package net.vvxzv.ktfcc.compat.jei;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.vvxzv.ktfcc.KTFCC;
import net.vvxzv.ktfcc.common.utils.JEIUtil;

import java.util.List;

@JeiPlugin
public class JEIHideItem implements IModPlugin {
    @Override
    @SuppressWarnings("removal")
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(KTFCC.MODID, "hide_item");
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime){
        JEIUtil util = new JEIUtil(jeiRuntime);
        util.removeItemStacks(List.of(
                ModItems.COLD_CUT_HAM_SLICES.get()
        ));
        FoodBiteRegistry.FOOD_DATA_MAP.keySet().forEach(resourceLocation -> {
            boolean isDarkCuisine = resourceLocation == FoodBiteRegistry.DARK_CUISINE;
            boolean isSuspiciousStirFry = resourceLocation == FoodBiteRegistry.SUSPICIOUS_STIR_FRY;
            boolean isSlimeBallMeal = resourceLocation == FoodBiteRegistry.SLIME_BALL_MEAL;
            if(!isDarkCuisine && !isSuspiciousStirFry && !isSlimeBallMeal){
                Item foodItem = ForgeRegistries.ITEMS.getValue(resourceLocation);
                if (foodItem != null) {
                    util.removeItemStack(foodItem);
                }
            }
        });
    }
}
