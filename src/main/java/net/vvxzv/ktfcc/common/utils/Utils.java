package net.vvxzv.ktfcc.common.utils;

import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.util.Helpers;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.vvxzv.ktfcc.common.data.FoodEffect;

import java.util.List;

public class Utils {

    public static FoodProperties foodPropertiesRemoveEffect(FoodProperties foodproperties) {
        FoodProperties.Builder builder = new FoodProperties.Builder();
        builder = builder.nutrition(foodproperties.getNutrition());
        builder = builder.saturationMod(foodproperties.getSaturationModifier());
        if(foodproperties.canAlwaysEat()) {
            builder = builder.alwaysEat();
        }
        if(foodproperties.isFastFood()) {
            builder = builder.fast();
        }
        if(foodproperties.isMeat()) {
            builder = builder.meat();
        }
        return builder.build();
    }

    public static void applyFoodEffect(ItemStack stack, LivingEntity entity) {
        FoodEffect foodEffect = FoodEffect.get(stack);
        if(foodEffect != null) {
            List<MobEffectInstance> effects = foodEffect.getEffects();
            if(!effects.isEmpty()) {
                for (MobEffectInstance effect: effects) {
                    entity.addEffect(new MobEffectInstance(effect));
                }
            }
        }
    }

    public static boolean isSameFluidInItem(ItemStack stack, Fluid fluid, int amount) {
        IFluidHandlerItem handler = Helpers.getCapability(stack, Capabilities.FLUID_ITEM);
        if(handler != null) {
            FluidStack fluidInItem = handler.drain(amount, IFluidHandler.FluidAction.SIMULATE);
            if(!fluidInItem.isEmpty()) {
                return fluidInItem.getFluid().isSame(fluid);
            }
        }

        return false;
    }

    public static boolean matchFluidStack(ItemStack stack, Fluid fluid, int amount) {
        IFluidHandlerItem handler = Helpers.getCapability(stack, Capabilities.FLUID_ITEM);
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
