package net.vvxzv.ktfcc.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.FluidSoupBase;
import net.dries007.tfc.common.items.FluidContainerItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.vvxzv.ktfcc.common.utils.Utils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FluidSoupBase.class)
public abstract class FluidSoupBaseMixin {
    @Final
    @Shadow
    protected Fluid fluid;

    @Final
    @Shadow
    protected Item bucketItem;

    @Inject(method = "isSoupBase", at = @At("RETURN"), cancellable = true)
    private void isSoupBase(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.is(this.bucketItem)) {
            cir.setReturnValue(true);
            return;
        }

        cir.setReturnValue(Utils.isSameFluidInItem(stack, this.fluid, 1000));
    }

    @Inject(method = "isContainer", at = @At("RETURN"), cancellable = true)
    private void isContainer(ItemStack stack, CallbackInfoReturnable<Boolean> cir){
        if (stack.is(Items.BUCKET)) {
            cir.setReturnValue(true);
            return;
        }

        cir.setReturnValue(Utils.matchFluidStack(stack, this.fluid, 1000));
    }

    @Inject(method = "getReturnContainer", at = @At("RETURN"), cancellable = true)
    private void getReturnContainer(Level level, LivingEntity user, ItemStack soupBase, CallbackInfoReturnable<ItemStack> cir){
        Item item = soupBase.getItem();

        if (item instanceof FluidContainerItem) {
            cir.setReturnValue(new ItemStack(item));
            return;
        }

        cir.setReturnValue(new ItemStack(Items.BUCKET));
    }

    @Inject(method = "getReturnSoupBase", at = @At("RETURN"), cancellable = true)
    private void getReturnSoupBase(Level level, LivingEntity user, ItemStack container, CallbackInfoReturnable<ItemStack> cir){
        if(container.getItem() instanceof FluidContainerItem) {
            ItemStack filledContainer = container.copy();
            IFluidHandler handler = filledContainer.getCapability(Capabilities.FluidHandler.ITEM);
            if (handler != null){
                handler.fill(new FluidStack(this.fluid, 1000), IFluidHandler.FluidAction.EXECUTE);
                cir.setReturnValue(filledContainer);
                return;
            }
        }

        cir.setReturnValue(this.bucketItem.getDefaultInstance());
    }
}