package net.vvxzv.ktfcc.client;

import com.github.ysbbbbbb.kaleidoscopecookery.client.model.ScarecrowModel;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.entity.ScarecrowRender;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.ScarecrowEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.dries007.tfc.common.blocks.devices.LampBlock;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.fluid.FluidComponent;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

public class ScarecrowHandLayer extends ItemInHandLayer<ScarecrowEntity, ScarecrowModel> {
    private final ItemInHandRenderer itemRenderer;
    private final BlockRenderDispatcher blockRenderer;

    public ScarecrowHandLayer(ScarecrowRender entityRenderer, ItemInHandRenderer itemRenderer, BlockRenderDispatcher blockRenderer) {
        super(entityRenderer, itemRenderer);
        this.itemRenderer = itemRenderer;
        this.blockRenderer = blockRenderer;
    }

    @Override
    protected void renderArmWithItem(@NotNull LivingEntity entity, @NotNull ItemStack stack, @NotNull ItemDisplayContext context, @NotNull HumanoidArm arm, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        if (!stack.isEmpty()) {
            poseStack.pushPose();
            this.getParentModel().translateToHand(arm, poseStack);
            poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            boolean isLeft = arm == HumanoidArm.LEFT;
            if (isLeft) {
                if(stack.getItem() instanceof BlockItem blockItem){
                    if(blockItem.getBlock() instanceof LampBlock lampBlock) {
                        poseStack.translate(-0.375, 0.375, -2.0);
                        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                        BlockState blockState = lampBlock.defaultBlockState();
                        poseStack.scale(0.75F, 0.75F, 0.75F);
                        FluidComponent fluidComponent = stack.get(TFCComponents.FLUID);
                        if (fluidComponent != null) {
                            this.blockRenderer.renderSingleBlock(
                                    blockState.setValue(BlockStateProperties.LIT, true),
                                    poseStack,
                                    bufferSource,
                                    0xf000f0,
                                    OverlayTexture.NO_OVERLAY);
                        } else {
                            this.blockRenderer.renderSingleBlock(blockState, poseStack, bufferSource, 0xf00000, OverlayTexture.NO_OVERLAY);
                        }
                        poseStack.popPose();
                        return;
                    }
                }

                poseStack.translate(-0.125, 0.0, -1.375);
                poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
            } else {
                poseStack.translate(0.125, 0.0, -1.375);
                poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0F));
            }

            poseStack.mulPose(Axis.XP.rotationDegrees(85.0F));
            poseStack.scale(0.75F, 0.75F, 0.75F);
            this.itemRenderer.renderItem(entity, stack, context, isLeft, poseStack, bufferSource, packedLight);
            poseStack.popPose();
        }
    }
}
