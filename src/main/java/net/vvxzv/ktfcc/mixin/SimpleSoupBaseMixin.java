package net.vvxzv.ktfcc.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.SimpleSoupBase;
import net.dries007.tfc.common.items.FluidContainerItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.vvxzv.ktfcc.common.utils.Utils;
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
    @Shadow
    protected ResourceLocation name;

    @Final
    @Shadow
    protected Predicate<ItemStack> soupBasePredicate;

    @Final
    @Shadow
    protected Predicate<ItemStack> containerPredicate;

    @Final
    @Shadow
    protected TriFunction<Level, LivingEntity, ItemStack, ItemStack> returnContainerFunction;

    @Final
    @Shadow
    protected TriFunction<Level, LivingEntity, ItemStack, ItemStack> returnSoupBaseFunction;

    @Inject(method = "isSoupBase", at = @At("RETURN"), cancellable = true)
    private void isSoupBase(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (this.soupBasePredicate.test(stack)) {
            cir.setReturnValue(true);
            return;
        }

        Fluid fluid = BuiltInRegistries.FLUID.get(this.name);
        cir.setReturnValue(Utils.isSameFluidInItem(stack, fluid, 1000));
    }

    @Inject(method = "isContainer", at = @At("RETURN"), cancellable = true)
    private void isContainer(ItemStack stack, CallbackInfoReturnable<Boolean> cir){
        if (this.containerPredicate.test(stack)) {
            cir.setReturnValue(true);
            return;
        }

        Fluid fluid = BuiltInRegistries.FLUID.get(this.name);
        cir.setReturnValue(Utils.matchFluidStack(stack, fluid, 1000));
    }

    @Inject(method = "getReturnContainer", at = @At("RETURN"), cancellable = true)
    private void getReturnContainer(Level level, LivingEntity user, ItemStack soupBase, CallbackInfoReturnable<ItemStack> cir){
        Item item = soupBase.getItem();

        if (item instanceof FluidContainerItem) {
            cir.setReturnValue(new ItemStack(item));
            return;
        }

        cir.setReturnValue(this.returnContainerFunction.apply(level, user, soupBase));
    }

    @Inject(method = "getReturnSoupBase", at = @At("RETURN"), cancellable = true)
    private void getReturnSoupBase(Level level, LivingEntity user, ItemStack container, CallbackInfoReturnable<ItemStack> cir){
        if(container.getItem() instanceof FluidContainerItem){
            ItemStack filledContainer = container.copy();
            IFluidHandler handler = filledContainer.getCapability(Capabilities.FluidHandler.ITEM);
            if (handler != null){
                Fluid fluid = BuiltInRegistries.FLUID.get(this.name);
                handler.fill(new FluidStack(fluid, 1000), IFluidHandler.FluidAction.EXECUTE);
                cir.setReturnValue(filledContainer);
                return;
            }
        }

        cir.setReturnValue(this.returnSoupBaseFunction.apply(level, user, container));
    }
}
