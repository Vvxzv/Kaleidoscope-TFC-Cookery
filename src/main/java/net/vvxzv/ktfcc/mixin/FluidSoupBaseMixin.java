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
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FluidSoupBase.class)
public abstract class FluidSoupBaseMixin {
    @Final
    @Shadow(remap = false)
    protected Fluid fluid;

    @Final
    @Shadow(remap = false)
    protected Item bucketItem;

    /**
     * @author Vvxzv
     * @reason 重写汤底匹配逻辑：通过流体类型匹配，兼容所有模组的流体容器
     */
    @Overwrite(remap = false)
    public boolean isSoupBase(ItemStack stack) {
        if (stack.is(this.bucketItem)) {
            return true;
        }

        IFluidHandlerItem itemHandler = Helpers.getCapability(stack, Capabilities.FLUID_ITEM);
        if (itemHandler == null) {
            return false;
        }

        FluidStack fluidInItem = itemHandler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
        if (fluidInItem.isEmpty()) {
            return false;
        }

        return fluidInItem.getFluid().isSame(this.fluid);
    }

    /**
     * @author Vvxzv
     * @reason 重写 isContainer 方法，兼容其他模组的空桶
     */
    @Overwrite(remap = false)
    public boolean isContainer(ItemStack stack) {
        if (stack.is(Items.BUCKET)) {
            return true;
        }

        IFluidHandlerItem itemHandler = Helpers.getCapability(stack, Capabilities.FLUID_ITEM);
        if (itemHandler == null) {
            return false;
        }

        FluidStack fluidInItem = itemHandler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
        if (!fluidInItem.isEmpty()) {
            return false;
        }

        FluidStack testFluid = new FluidStack(this.fluid, 1000);
        int filled = itemHandler.fill(testFluid, IFluidHandler.FluidAction.SIMULATE);
        return filled == 1000;
    }

    /**
     * @author Vvxzv
     * @reason 让倒出流体后，返回对应容器本身
     */
    @Overwrite(remap = false)
    public ItemStack getReturnContainer(Level level, LivingEntity user, ItemStack soupBase) {
        SoundEvent sound = this.fluid.getFluidType().getSound(user, SoundActions.BUCKET_EMPTY);
        if (sound != null) {
            Vec3 position = user.position();
            level.playSound(null, position.x(), position.y() + 0.5, position.z(), sound, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        Item item = soupBase.getItem();

        if (item instanceof FluidContainerItem) {
            return new ItemStack(item);
        }

        return new ItemStack(Items.BUCKET);
    }

    /**
     * @author Vvxzv
     * @reason 让容器可以装流体
     */
    @Overwrite(remap = false)
    public ItemStack getReturnSoupBase(Level level, LivingEntity user, ItemStack container) {
        SoundEvent sound = this.fluid.getFluidType().getSound(user, SoundActions.BUCKET_FILL);
        if (sound != null) {
            Vec3 position = user.position();
            level.playSound(null, position.x(), position.y() + 0.5, position.z(), sound, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        if(container.getItem() instanceof FluidContainerItem){
            ItemStack filledContainer = container.copy();
            IFluidHandlerItem itemHandler = Helpers.getCapability(filledContainer, Capabilities.FLUID_ITEM);
            if (itemHandler != null){
                itemHandler.fill(new FluidStack(this.fluid, 1000), IFluidHandler.FluidAction.EXECUTE);
                return filledContainer;
            }
        }

        return this.bucketItem.getDefaultInstance();
    }
}