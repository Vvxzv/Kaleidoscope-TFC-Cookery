package net.vvxzv.ktfcc.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.item.armor.FarmerArmorMaterial;
import net.minecraft.world.item.ArmorItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FarmerArmorMaterial.class)
public class FarmerArmorMaterialMixin {

    @Inject(method = "getDefenseForType", at = @At("RETURN"), cancellable = true)
    public void getDefenseForType(ArmorItem.Type type, CallbackInfoReturnable<Integer> cir) {
        int b;
        switch (type) {
            case HELMET -> b = 1;
            case CHESTPLATE -> b = 4;
            case LEGGINGS -> b = 3;
            case BOOTS -> b = 2;
            default -> throw new IncompatibleClassChangeError();
        }
        cir.setReturnValue(b);
    }
}
