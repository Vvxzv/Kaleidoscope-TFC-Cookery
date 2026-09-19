package net.vvxzv.ktfcc.mixin.client;

import com.github.ysbbbbbb.kaleidoscopecookery.client.model.ScarecrowModel;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.entity.ScarecrowRender;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.ScarecrowEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.vvxzv.ktfcc.client.ScarecrowHandLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ScarecrowRender.class)
public abstract class ScarecrowRenderMixin extends LivingEntityRenderer<ScarecrowEntity, ScarecrowModel> {
    public ScarecrowRenderMixin(EntityRendererProvider.Context pContext, ScarecrowModel pModel, float pShadowRadius) {
        super(pContext, pModel, pShadowRadius);
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/github/ysbbbbbb/kaleidoscopecookery/client/render/entity/ScarecrowRender;addLayer(Lnet/minecraft/client/renderer/entity/layers/RenderLayer;)Z", ordinal = 0))
    private boolean init(ScarecrowRender instance, RenderLayer renderLayer, EntityRendererProvider.Context context) {
        return this.addLayer(new ScarecrowHandLayer((ScarecrowRender)(Object)this, context.getItemInHandRenderer(), context.getBlockRenderDispatcher()));
    }
}
