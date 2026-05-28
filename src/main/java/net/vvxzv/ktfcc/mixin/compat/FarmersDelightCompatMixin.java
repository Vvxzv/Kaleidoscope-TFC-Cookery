package net.vvxzv.ktfcc.mixin.compat;

import com.github.ysbbbbbb.kaleidoscopecookery.compat.farmersdelight.FarmersDelightCompat;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import net.minecraft.world.level.Level;
import net.vvxzv.ktfcc.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(FarmersDelightCompat.class)
public class FarmersDelightCompatMixin {

    @Unique
    private static boolean isCompat(){
        return Config.farmersDelightCompat;
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    private static void fdCompat(CallbackInfo ci){
        if(!isCompat()){
            FarmersDelightCompat.IS_LOADED = false;
            ci.cancel();
        }
    }

    @Inject(method = "getTransformRecipeForJei", at = @At("HEAD"), cancellable = true)
    private static void recipeForJei(Level level, List<StockpotRecipe> recipes, CallbackInfo ci) {
        if(!isCompat()){
            FarmersDelightCompat.IS_LOADED = false;
            ci.cancel();
        }
    }
}
