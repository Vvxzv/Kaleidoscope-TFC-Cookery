package net.vvxzv.ktfcc.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModArmorMaterials;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ModArmorMaterials.class)
public class ModArmorMaterialsMixin {

    @ModifyArg(
            method = "lambda$static$0", // () -> ArmorMaterial(...)
            at = @At(value = "INVOKE", target = "Ljava/util/HashMap;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 2),
            index = 1
    )
    private static Object modifyFarmerArmorLeggings(Object key) {
        return 3;
    }
}