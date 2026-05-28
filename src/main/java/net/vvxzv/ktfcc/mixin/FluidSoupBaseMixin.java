package net.vvxzv.ktfcc.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.FluidSoupBase;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.items.FluidContainerItem;
import net.dries007.tfc.util.Helpers;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
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
    @Shadow(remap = false)
    protected Fluid fluid;

    @Final
    @Shadow(remap = false)
    protected Item bucketItem;

    @Inject(method = "isSoupBase", at = @At("HEAD"), cancellable = true, remap = false)
    private void isSoupBase(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.is(this.bucketItem)) {
            cir.setReturnValue(true);
            return;
        }

        cir.setReturnValue(Utils.isSameFluidInItem(stack, this.fluid, 100));
    }

    @Inject(method = "isContainer", at = @At("HEAD"), cancellable = true, remap = false)
    private void isContainer(ItemStack stack, CallbackInfoReturnable<Boolean> cir){
        if (stack.is(Items.BUCKET)) {
            cir.setReturnValue(true);
            return;
        }

        cir.setReturnValue(Utils.matchFluidStack(stack, this.fluid, 1000));
    }

    @Inject(method = "getReturnContainer", at = @At("HEAD"), cancellable = true, remap = false)
    private void getReturnContainer(Level level, LivingEntity user, ItemStack soupBase, CallbackInfoReturnable<ItemStack> cir){
        SoundEvent sound = this.fluid.getFluidType().getSound(user, SoundActions.BUCKET_EMPTY);
        if (sound != null) {
            level.playSound(null, user.getX(), user.getY() + 0.5, user.getZ(), sound, SoundSource.BLOCKS, 1.0f, 1.0f);
        }

        Item item = soupBase.getItem();
        if (item instanceof FluidContainerItem) {
            cir.setReturnValue(new ItemStack(item));
            return;
        }

        cir.setReturnValue(new ItemStack(Items.BUCKET));
    }

    @Inject(method = "getReturnSoupBase", at = @At("HEAD"), cancellable = true, remap = false)
    private void getReturnSoupBase(Level level, LivingEntity user, ItemStack container, CallbackInfoReturnable<ItemStack> cir){
        SoundEvent sound = this.fluid.getFluidType().getSound(user, SoundActions.BUCKET_FILL);
        if (sound != null) {
            level.playSound(null, user.getX(), user.getY() + 0.5, user.getZ(), sound, SoundSource.BLOCKS, 1.0f, 1.0f);
        }

        if(container.getItem() instanceof FluidContainerItem){
            ItemStack filledContainer = container.copy();
            IFluidHandlerItem itemHandler = Helpers.getCapability(filledContainer, Capabilities.FLUID_ITEM);
            if (itemHandler != null){
                itemHandler.fill(new FluidStack(this.fluid, 1000), IFluidHandler.FluidAction.EXECUTE);
                cir.setReturnValue(filledContainer);
                return;
            }
        }

        cir.setReturnValue(this.bucketItem.getDefaultInstance());
    }
}