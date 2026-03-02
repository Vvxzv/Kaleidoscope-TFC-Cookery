package net.vvxzv.ktfcc.mixin.effect;

import com.github.ysbbbbbb.kaleidoscopecookery.event.effect.FlatulenceEvent;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import net.vvxzv.ktfcc.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FlatulenceEvent.class)
public class FlatulenceEventMixin {
    @Unique
    private static double getMovement() {
        return Config.flatulenceAddFlyMovement - 0.75;
    }

    @Inject(
            method = "onShiftKeyPressed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;addDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V",
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void onShiftKeyPressed(InputEvent.Key event, CallbackInfo ci, KeyMapping keyShift, LocalPlayer player) {
        player.addDeltaMovement(new Vec3(0, getMovement(), 0));
    }
}
