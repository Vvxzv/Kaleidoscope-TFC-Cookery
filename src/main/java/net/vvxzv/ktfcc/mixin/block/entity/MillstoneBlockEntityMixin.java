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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MillstoneBlockEntity.class)
public class MillstoneBlockEntityMixin extends BaseBlockEntity {

    @Shadow(remap = false)
    private ItemStack input;

    @Shadow(remap = false)
    private ItemStack output;

    @Shadow(remap = false)
    private int progress;

    @Shadow(remap = false)
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

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/crafting/RecipeManager$CachedCheck;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void addQuernRecipeHandle(Level level, CallbackInfo ci){
        if (!this.input.isEmpty() && this.output.isEmpty() && this.progress <= 0) {
            QuernRecipe recipe = QuernRecipe.getRecipe(this.input);
            if(recipe != null) {
                this.output = recipe.assemble(this.input);
                int outputCount = Math.min(recipe.getResultItem(level.registryAccess()).getCount() * this.input.getCount(), this.output.getMaxStackSize());
                this.output.setCount(outputCount);
                this.input = ItemStack.EMPTY;
                this.refresh();
                ci.cancel();
            }
        }
    }
}
