package net.vvxzv.ktfcc.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.client.render.entity.layer.ScarecrowHandLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.dries007.tfc.common.blocks.devices.LampBlock;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScarecrowHandLayer.class)
public class ScarecrowHandLayerMixin {
    @Final
    @Shadow(remap = false)
    private BlockRenderDispatcher blockRenderer;

    @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BlockItem;getBlock()Lnet/minecraft/world/level/block/Block;"))
    protected void renderArmWithItem(LivingEntity entity, ItemStack stack, ItemDisplayContext context, HumanoidArm arm, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        if(stack.getItem() instanceof BlockItem blockItem){
            if(blockItem.getBlock() instanceof LampBlock lampBlock){
                poseStack.translate(-0.375, 0.375, -2.0);
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                BlockState blockState = lampBlock.defaultBlockState();
                poseStack.scale(0.75F, 0.75F, 0.75F);
                if (stack.getTag() != null && stack.getTag().contains("fluid")) {
                    this.blockRenderer.renderSingleBlock(
                            blockState.setValue(BlockStateProperties.LIT, true),
                            poseStack,
                            bufferSource,
                            0xf000f0,
                            OverlayTexture.NO_OVERLAY);
                }
                else this.blockRenderer.renderSingleBlock(blockState, poseStack, bufferSource, 0xf00000, OverlayTexture.NO_OVERLAY);
                poseStack.popPose();
            }
        }
    }
}
