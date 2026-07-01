package net.vvxzv.ktfcc.mixin.block.entity;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.MillstoneBlockEntity;
import net.dries007.tfc.common.recipes.QuernRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MillstoneBlockEntity.class)
public class MillstoneBlockEntityMixin extends BaseBlockEntity {

    @Shadow
    private ItemStack input;

    @Final
    @Shadow
    private ItemStackHandler outputs;

    @Shadow
    private int progress;

    @Shadow
    private float rotSpeedTick;

    public MillstoneBlockEntityMixin(BlockEntityType<?> entityType, BlockPos pos, BlockState state) {
        super(entityType, pos, state);
    }

    @Inject(
            method = "onPutItem",
            at = @At(
                    value = "RETURN",
                    ordinal = 2
            ),
            cancellable = true,
            remap = false
    )
    private void addQuernRecipeItem(Level level, ItemStack putOnItem, CallbackInfoReturnable<Boolean> cir) {
        boolean value = cir.getReturnValue();
        if(!value){
            boolean returnValue = false;
            QuernRecipe recipe = QuernRecipe.getRecipe(putOnItem);
            if(recipe != null) {
                this.input = putOnItem.split(8);
                this.progress = Math.max(Math.round(this.rotSpeedTick), 1);
                this.refresh();
                level.playSound(null, this.worldPosition, SoundEvents.STONE_HIT, SoundSource.BLOCKS, 0.8F, level.random.nextFloat() * 0.2F + 0.9F);
                returnValue = true;
            }

            cir.setReturnValue(returnValue);
        }
    }

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Ljava/util/Optional;ifPresentOrElse(Ljava/util/function/Consumer;Ljava/lang/Runnable;)V"), index = 1)
    private Runnable addQuernRecipe(Runnable emptyAction) {
        if(this.level != null) {
            QuernRecipe recipe = QuernRecipe.getRecipe(this.input);
            if(recipe != null) {
                return () -> {
                    ItemStack result = recipe.assemble(this.input);
                    for(int i = 0; i < this.input.getCount(); ++i) {
                        ItemHandlerHelper.insertItemStacked(this.outputs, result.copy(), false);
                    }
                    this.input = ItemStack.EMPTY;
                    this.refresh();
                };
            }
        }
        return emptyAction;
    }
}
