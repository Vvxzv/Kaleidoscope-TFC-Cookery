package net.vvxzv.ktfcc.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.SimpleSoupBase;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.items.FluidContainerItem;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.apache.commons.lang3.function.TriFunction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(SimpleSoupBase.class)
public class SimpleSoupBaseMixin {
    @Final
    @Shadow(remap = false)
    protected ResourceLocation name;

    @Final
    @Shadow(remap = false)
    protected Predicate<ItemStack> soupBasePredicate;

    @Final
    @Shadow(remap = false)
    protected Predicate<ItemStack> containerPredicate;

    @Final
    @Shadow(remap = false)
    protected TriFunction<Level, LivingEntity, ItemStack, ItemStack> returnContainerFunction;

    @Final
    @Shadow(remap = false)
    protected TriFunction<Level, LivingEntity, ItemStack, ItemStack> returnSoupBaseFunction;

    @Inject(method = "isSoupBase", at = @At("RETURN"), cancellable = true, remap = false)
    private void isSoupBase(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (this.soupBasePredicate.test(stack)) {
            cir.setReturnValue(true);
            return;
        }

        IFluidHandlerItem itemHandler = Helpers.getCapability(stack, Capabilities.FLUID_ITEM);
        if (itemHandler == null) {
            cir.setReturnValue(false);
            return;
        }

        FluidStack fluidInItem = itemHandler.drain(1000, IFluidHandler.FluidAction.SIMULATE);
        if (fluidInItem.isEmpty()) {
            cir.setReturnValue(false);
            return;
        }

        Fluid fluid = BuiltInRegistries.FLUID.get(this.name);
        cir.setReturnValue(fluidInItem.getFluid().isSame(fluid));
    }

    @Inject(method = "isContainer", at = @At("RETURN"), cancellable = true, remap = false)
    private void isContainer(ItemStack stack, CallbackInfoReturnable<Boolean> cir){
        if (this.containerPredicate.test(stack)) {
            cir.setReturnValue(true);
            return;
        }

        IFluidHandlerItem itemHandler = Helpers.getCapability(stack, Capabilities.FLUID_ITEM);
        if (itemHandler == null) {
            cir.setReturnValue(false);
            return;
        }

        FluidStack fluidInItem = itemHandler.drain(1000, IFluidHandler.FluidAction.SIMULATE);
        if (!fluidInItem.isEmpty()) {
            cir.setReturnValue(false);
            return;
        }

        Fluid fluid = BuiltInRegistries.FLUID.get(this.name);
        FluidStack testFluid = new FluidStack(fluid, 1000);
        int filled = itemHandler.fill(testFluid, IFluidHandler.FluidAction.SIMULATE);
        cir.setReturnValue(filled == 1000);
    }

    @Inject(method = "getReturnContainer", at = @At("RETURN"), cancellable = true, remap = false)
    private void getReturnContainer(Level level, LivingEntity user, ItemStack soupBase, CallbackInfoReturnable<ItemStack> cir){
        Item item = soupBase.getItem();

        if (item instanceof FluidContainerItem) {
            cir.setReturnValue(new ItemStack(item));
            return;
        }

        cir.setReturnValue(this.returnContainerFunction.apply(level, user, soupBase));
    }

    @Inject(method = "getReturnSoupBase", at = @At("RETURN"), cancellable = true, remap = false)
    private void getReturnSoupBase(Level level, LivingEntity user, ItemStack container, CallbackInfoReturnable<ItemStack> cir){
        if(container.getItem() instanceof FluidContainerItem){
            ItemStack filledContainer = container.copy();
            IFluidHandlerItem itemHandler = Helpers.getCapability(filledContainer, Capabilities.FLUID_ITEM);
            if (itemHandler != null){
                Fluid fluid = BuiltInRegistries.FLUID.get(this.name);
                itemHandler.fill(new FluidStack(fluid, 1000), IFluidHandler.FluidAction.EXECUTE);
                cir.setReturnValue(filledContainer);
                return;
            }
        }

        cir.setReturnValue(this.returnSoupBaseFunction.apply(level, user, container));
    }
}
