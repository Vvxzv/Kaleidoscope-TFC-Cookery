package net.vvxzv.ktfcc.common.block.entity;

import net.dries007.tfc.common.blockentities.TFCBlockEntity;
import net.dries007.tfc.common.capabilities.FluidTankCallback;
import net.dries007.tfc.common.capabilities.InventoryFluidTank;
import net.dries007.tfc.common.capabilities.SidedHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.vvxzv.ktfcc.common.block.OilPotBlock;
import net.vvxzv.ktfcc.common.data.Oil;
import net.vvxzv.ktfcc.common.registry.BlockEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OilPotBlockEntity extends TFCBlockEntity implements FluidTankCallback {
    protected InventoryFluidTank tank;
    private final SidedHandler<IFluidHandler> sidedFluidInventory;

    public OilPotBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.OIL_POT.get(), pos, state);
        this.tank = new InventoryFluidTank(2000, (stack) -> Oil.get(stack.getFluid()) != null, this);
        this.sidedFluidInventory = new SidedHandler<>(this.tank);
    }

    public InventoryFluidTank getFluidHandler() {
        return this.tank;
    }

    public FluidStack getFluidStack() {
        return this.tank.getFluid();
    }

    public @Nullable Oil getOil() {
        assert this.level != null;
        return Oil.get(this.tank.getFluid().getFluid());
    }

    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        return this.tank.fill(resource, action);
    }

    public @NotNull FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        return this.tank.drain(resource, action);
    }

    public @NotNull FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        return this.tank.drain(maxDrain, action);
    }

    @Override
    public void fluidTankChanged() {
        this.checkHasRanOut();
        this.markForSync();
    }

    public void checkHasRanOut() {
        assert this.level != null;

        FluidStack fluidStack = this.getFluidStack();
        BlockState state = this.getBlockState();
        if (fluidStack.isEmpty()) {
            this.level.setBlockAndUpdate(this.worldPosition, state.setValue(OilPotBlock.HAS_OIL, false));
        } else {
            this.level.setBlockAndUpdate(this.worldPosition, state.setValue(OilPotBlock.HAS_OIL, true));
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        this.tank.readFromNBT(provider, tag.getCompound("tank"));
        super.loadAdditional(tag, provider);
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        tag.put("tank", this.tank.writeToNBT(provider, new CompoundTag()));
        super.saveAdditional(tag, provider);
    }

    public @Nullable IFluidHandler getSidedFluidInventory(@Nullable Direction context) {
        return this.sidedFluidInventory.get(context);
    }
}
