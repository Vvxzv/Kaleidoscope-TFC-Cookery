package net.vvxzv.ktfcc.mixin.block;

import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.EnamelBasinBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.item.KitchenShovelItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnamelBasinBlock.class)
public class EnamelBasinBlockMixin extends Block {

    public EnamelBasinBlockMixin(Properties pProperties) {
        super(pProperties);
    }

    @Inject(method = "onShovelClick", at = @At("HEAD"), cancellable = true, remap = false)
    private void onShovelClick(BlockState state, Level level, BlockPos pos, Player player, ItemStack mainHandItem, CallbackInfoReturnable<InteractionResult> cir) {
        if (KitchenShovelItem.hasOil(mainHandItem)) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }
}
