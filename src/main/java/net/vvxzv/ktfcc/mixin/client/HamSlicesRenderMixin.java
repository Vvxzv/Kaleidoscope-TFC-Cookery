package net.vvxzv.ktfcc.mixin.client;

import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteThreeByThreeBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.food.FoodBiteThreeByThreeBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.block.FoodBiteThreeByThreeBlockEntityRender;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodBiteThreeByThreeBlockEntityRender.class)
public class HamSlicesRenderMixin {

    @Inject(method = "render(Lcom/github/ysbbbbbb/kaleidoscopecookery/blockentity/food/FoodBiteThreeByThreeBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V", at = @At("HEAD"), cancellable = true, remap = false)
    public void render(FoodBiteThreeByThreeBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, CallbackInfo ci) {
        BlockState blockState = be.getBlockState();
        if(!blockState.getValue(FoodBiteThreeByThreeBlock.PART).isCenter()) {
            ci.cancel();
        }
    }
}
