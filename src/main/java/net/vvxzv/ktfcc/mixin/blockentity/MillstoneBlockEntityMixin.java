package net.vvxzv.ktfcc.mixin.blockentity;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.MillstoneBlockEntity;
import net.dries007.tfc.common.recipes.QuernRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(MillstoneBlockEntity.class)
public class MillstoneBlockEntityMixin extends BaseBlockEntity {
    @Unique
    private final RecipeManager.CachedCheck<ItemStackInventory, QuernRecipe> tfcQuernCheck = RecipeManager.createCheck(TFCRecipeTypes.QUERN.get());
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
    private void addQuernRecipeItem(Level level, ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        boolean value = cir.getReturnValue();
        if(!value){
            ItemStackInventory inventory = new ItemStackInventory(itemStack);
            boolean returnValue = this.tfcQuernCheck.getRecipeFor(inventory, level).map((recipe) -> {
                this.input = itemStack.split(8);
                this.progress = Math.max(Math.round(this.rotSpeedTick), 1);
                this.refresh();
                level.playSound(null, this.worldPosition, SoundEvents.STONE_HIT, SoundSource.BLOCKS, 0.8F, level.random.nextFloat() * 0.2F + 0.9F);
                return true;
            }).orElse(false);

            cir.setReturnValue(returnValue);
        }
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/crafting/RecipeManager$CachedCheck;getRecipeFor(Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void addQuernRecipeHandle(Level level, CallbackInfo ci){
        if (!this.input.isEmpty() && this.output.isEmpty() && this.progress <= 0) {
            ItemStackInventory tfcInventory = new ItemStackInventory(this.input);
            Optional<QuernRecipe> quernRecipe = this.tfcQuernCheck.getRecipeFor(tfcInventory, level);

            if (quernRecipe.isPresent()) {
                QuernRecipe recipe = quernRecipe.get();
                this.output = recipe.assemble(tfcInventory, level.registryAccess());
                int outputCount = Math.min(recipe.getResultItem(level.registryAccess()).getCount() * this.input.getCount(), this.output.getMaxStackSize());
                this.output.setCount(outputCount);
                this.input = ItemStack.EMPTY;
                this.refresh();
                ci.cancel();
            }
        }
    }
}
