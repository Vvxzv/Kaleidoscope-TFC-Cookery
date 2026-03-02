package net.vvxzv.ktfcc.client.render;

import com.github.ysbbbbbb.kaleidoscopecookery.client.model.ColdCutHamSlicesModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.vvxzv.ktfcc.common.block.decay.DecayingFoodBiteThreeByThreeBlock;
import net.vvxzv.ktfcc.common.blockentity.DecayingThreeFoodBlockEntity;

public class ThreeDecayingRender implements BlockEntityRenderer<DecayingThreeFoodBlockEntity> {
    @SuppressWarnings("removal")
    private static final ResourceLocation COLD_CUT_HAM_SLICES_TEXTURE = new ResourceLocation("kaleidoscope_cookery", "textures/block/cold_cut_ham_slices.png");
    private final ColdCutHamSlicesModel coldCutHamSlicesModel;

    public ThreeDecayingRender(BlockEntityRendererProvider.Context context){
        this.coldCutHamSlicesModel = new ColdCutHamSlicesModel(context.bakeLayer(ColdCutHamSlicesModel.LAYER_LOCATION));
    }

    @Override
    public void render(DecayingThreeFoodBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        BlockState blockState = pBlockEntity.getBlockState();
        if (
                blockState.getValue(DecayingFoodBiteThreeByThreeBlock.PART).isCenter() &&
                blockState.getBlock() instanceof DecayingFoodBiteThreeByThreeBlock block
        ) {
            Direction facing = blockState.getValue(DecayingFoodBiteThreeByThreeBlock.FACING);
            int facingDeg = facing.get2DDataValue() * 90;
            int bites = blockState.getValue(block.getBites());
            pPoseStack.pushPose();
            this.coldCutHamSlicesModel.updateBites(bites);
            pPoseStack.translate(0.5, 1.5, 0.5);
            pPoseStack.mulPose(Axis.ZN.rotationDegrees(180.0F));
            pPoseStack.mulPose(Axis.YN.rotationDegrees((float)(180 - facingDeg)));
            VertexConsumer checkerBoardBuff = pBuffer.getBuffer(RenderType.entityCutoutNoCull(COLD_CUT_HAM_SLICES_TEXTURE));
            this.coldCutHamSlicesModel.renderToBuffer(pPoseStack, checkerBoardBuff, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
            this.coldCutHamSlicesModel.resetBites();
            pPoseStack.popPose();
        }

    }

    @Override
    public boolean shouldRenderOffScreen(DecayingThreeFoodBlockEntity pBlockEntity) {
        return true;
    }
}
