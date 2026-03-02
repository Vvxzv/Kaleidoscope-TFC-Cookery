package net.vvxzv.ktfcc.mixin.item;

import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.StoveBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.vvxzv.ktfcc.common.registry.KBlocks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
    @Mutable
    @Final
    @Shadow
    private Block block;

    @Shadow
    public abstract BlockPlaceContext updatePlacementContext(BlockPlaceContext pContext);

    @Shadow
    protected abstract BlockState getPlacementState(BlockPlaceContext pContext);

    @Inject(method = "placeBlock", at = @At("RETURN"), cancellable = true)
    protected void placeStove(BlockPlaceContext pContext, BlockState pState, CallbackInfoReturnable<Boolean> cir) {
        if(this.block instanceof StoveBlock){
            this.block = KBlocks.STOVE.get();
            BlockPlaceContext blockplacecontext = this.updatePlacementContext(pContext);
            BlockState blockstate = this.getPlacementState(blockplacecontext);
            cir.setReturnValue(pContext.getLevel().setBlock(pContext.getClickedPos(), blockstate, 11));
        }
    }
}
