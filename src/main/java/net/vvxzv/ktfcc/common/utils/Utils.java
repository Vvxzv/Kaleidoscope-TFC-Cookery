package net.vvxzv.ktfcc.common.utils;

import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.IFood;
import net.dries007.tfc.common.component.food.Nutrient;
import net.minecraft.resources.ResourceLocation;
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

    public static float[] getFinalNutrients(List<ItemStack> itemStacks, float multi, float maxValue) {
        float[] totalNutrients = new float[]{0f, 0f, 0f, 0f, 0f};
        for (ItemStack itemStack : itemStacks) {
            IFood iFood = FoodCapability.get(itemStack);
            if (iFood != null){
                float[] foodNutrients = iFood.getData().nutrients();

                for (int i = 0; i < totalNutrients.length; i++) {
                    totalNutrients[i] += (foodNutrients[i] * itemStack.getCount());
                }
            }
        }

        float minValue = totalNutrients[0];
        for (float num : totalNutrients) {
            if (num < minValue) {
                minValue = num;
            }
        }

        int minIndex = 0;
        for (int i = 0; i < totalNutrients.length; i++) {
            if (totalNutrients[i] == minValue) {
                minIndex = i;
            }
        }

        float[] resultArray = totalNutrients.clone();
        resultArray[minIndex] = 0f;

        for (int i = 0; i < resultArray.length; i++) {
            resultArray[i] *= multi;
            if(resultArray[i] > maxValue) {
                resultArray[i] = maxValue;
            }
        }

        return resultArray;
    }

    public static Nutrient matchMainNutrient(float[] nutrients) {
        float max = nutrients[0];
        int index = 0;
        for (int i = 1; i < nutrients.length; i++) {
            if (nutrients[i] > max) {
                max = nutrients[i];
                index = i;
            }
        }

        return Nutrient.valueOf(index);
    }

    public static float calculateDecayModifier(List<ItemStack> itemStacks) {
        float decayModifier = 0f;
        for (ItemStack itemStack : itemStacks) {
            if (itemStack != null) {
                IFood iFood = FoodCapability.get(itemStack);
                if (iFood != null){
                    decayModifier = Math.max(decayModifier, iFood.getData().decayModifier());
                }
            }
        }

        return decayModifier * 0.9f;
    }

    public static int getTotalItemCount(List<ItemStack> itemStacks) {
        int count = 0;
        for (ItemStack itemStack : itemStacks) {
            if (itemStack != null) {
                count += itemStack.getCount();
            }
        }

        return count;
    }

    public static long mergeCreationDate(ItemStack stack1, ItemStack stack2) {
        IFood food1 = FoodCapability.get(stack1);
        long food1CreationDate = food1 != null? food1.getCreationDate(): -1L;
        IFood food2 = FoodCapability.get(stack2);
        long food2CreationDate = food2 != null? food2.getCreationDate(): -1L;
        return Math.min(food1CreationDate, food2CreationDate);
    }
}
