package net.vvxzv.ktfcc.common.utils;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.vvxzv.ktfcc.common.data.FoodEffect;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static FoodProperties foodPropertiesRemoveEffect(FoodProperties foodproperties) {
        return new FoodProperties(foodproperties.nutrition(), foodproperties.saturation(), foodproperties.canAlwaysEat(), foodproperties.eatSeconds(), foodproperties.usingConvertsTo(), new ArrayList<>());
    }

    public static void applyFoodEffect(FoodEffect foodEffect, LivingEntity entity) {
        List<MobEffectInstance> effects = foodEffect.getEffects();
        if(!effects.isEmpty()) {
            for (MobEffectInstance effect: effects) {
                entity.addEffect(new MobEffectInstance(effect));
            }
        }
    }

    public static boolean isSameFluidInItem(ItemStack stack, Fluid fluid, int amount) {
        IFluidHandler handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if(handler != null) {
            FluidStack fluidInItem = handler.drain(amount, IFluidHandler.FluidAction.SIMULATE);
            if(!fluidInItem.isEmpty()) {
                return fluidInItem.getFluid().isSame(fluid);
            }
        }

        return false;
    }

    public static boolean matchFluidStack(ItemStack stack, Fluid fluid, int amount) {
        IFluidHandler handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if(handler != null) {
            FluidStack fluidInItem = handler.drain(amount, IFluidHandler.FluidAction.SIMULATE);
            if(fluidInItem.isEmpty()) {
                FluidStack testFluid = new FluidStack(fluid, amount);
                int filled = handler.fill(testFluid, IFluidHandler.FluidAction.SIMULATE);
                return filled == amount;
            }
        }

        return false;
    }
}
