package net.vvxzv.ktfcc.mixin.block.entity;

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
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(MillstoneBlockEntity.class)
public class MillstoneBlockEntityMixin extends BaseBlockEntity {

    @Unique
    private final RecipeManager.CachedCheck<ItemStackInventory, QuernRecipe> tfcQuernCheck = RecipeManager.createCheck(TFCRecipeTypes.QUERN.get());

    @Shadow(remap = false)
    private ItemStack input;

    @Final
    @Shadow(remap = false)
    private ItemStackHandler outputs;

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

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Ljava/util/Optional;ifPresentOrElse(Ljava/util/function/Consumer;Ljava/lang/Runnable;)V"), index = 1, remap = false)
    private Runnable addQuernRecipe(Runnable emptyAction) {
        if(this.level != null) {
            ItemStackInventory tfcInventory = new ItemStackInventory(this.input);
            Optional<QuernRecipe> quernRecipe = this.tfcQuernCheck.getRecipeFor(tfcInventory, this.level);
            if(quernRecipe.isPresent()) {
                return () -> {
                    QuernRecipe recipe = quernRecipe.get();
                    ItemStack result = recipe.assemble(tfcInventory, this.level.registryAccess());
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
