package net.vvxzv.ktfcc.common.block.entity;

import net.dries007.tfc.common.blockentities.TFCBlockEntity;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.util.Fuel;
import net.dries007.tfc.util.calendar.Calendars;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.vvxzv.ktfcc.common.registry.BlockEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StoveBlockEntity extends TFCBlockEntity {
    private final ItemStack[] fuels;
    private float maxTemperature;
    private float temperature;
    private long burntTick;

    public StoveBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntities.STOVE.get(), pPos, pBlockState);
        this.fuels = new ItemStack[]{ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY};
        this.maxTemperature = 0;
        this.temperature = 0;
        this.burntTick = 0;
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        boolean isStoveLit = state.getValue(BlockStateProperties.LIT);

        long calendarTick = Calendars.get().getCalendarTicks();

        if (this.temperature != this.maxTemperature) {
            this.temperature = HeatCapability.adjustTempTowards(this.temperature, this.maxTemperature);
        }

        if(isStoveLit){
            if(this.burntTick - calendarTick < 10) {
                ItemStack stack = this.burnFuel(calendarTick);

                if(this.burntTick > calendarTick) {
                    stack.shrink(1);
                }
            }
            this.heatAbove(level, pos, this.temperature);
        }

        if(this.burntTick < calendarTick) {
            this.maxTemperature = 0;
            level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.LIT, false));
        }

        this.markForSync();
    }

    private void heatAbove(Level level, BlockPos pos, float temperature) {
        BlockEntity above = level.getBlockEntity(pos.above());
        if (above != null) {
            above.getCapability(HeatCapability.BLOCK_CAPABILITY).ifPresent((cap) -> {
                float currentTemp = cap.getTemperature();
                cap.setTemperature(HeatCapability.adjustTempTowards(currentTemp, temperature));
            });
        }
    }

    public boolean lit(Level level, BlockPos pos, BlockState state) {
        boolean isStoveLit = state.getValue(BlockStateProperties.LIT);
        if(!isStoveLit) {
            long calendarTick = Calendars.SERVER.getCalendarTicks();
            ItemStack stack = this.burnFuel(calendarTick);

            if(this.burntTick > calendarTick) {
                stack.shrink(1);
                level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.LIT, true));
                return true;
            }
        }

        return false;
    }

    private ItemStack burnFuel(long calendarTick) {
        ItemStack stack = this.getFuel();
        Fuel fuel = Fuel.get(stack);
        if(fuel != null) {
            if(fuel.getTemperature() > 900) {
                this.maxTemperature = 900;
                this.burntTick = calendarTick + fuel.getDuration() + (long) (fuel.getTemperature() - 900) * 20;
            } else {
                this.burntTick = calendarTick + fuel.getDuration();
                this.maxTemperature = fuel.getTemperature();
            }
        }
        return stack;
    }

    public float getFuelFillPercentage() {
        int filled = 0;
        for (int i = 0; i < 4; i++) {
            if(!this.fuels[i].isEmpty()) {
                filled++;
            }
        }
        return filled / 4.0f;
    }

    public boolean addFuel(ItemStack stack) {
        Fuel fuel = Fuel.get(stack);
        if(fuel != null) {
            for (int i = 0; i < 4; i++) {
                if(this.fuels[i].isEmpty()) {
                    this.fuels[i] = stack.copyWithCount(1);
                    return true;
                }
            }
        }

        return false;
    }

    public ItemStack getFuel() {
        for (ItemStack stack: this.fuels) {
            if(!stack.isEmpty()) {
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    public float getTemperature() {
        return this.temperature;
    }

    public @Nullable String getTimeText() {
        long calendarTick = Calendars.SERVER.getCalendarTicks();
        if(this.burntTick - calendarTick > 0) {
            long burningTicks = this.burntTick - calendarTick;
            int totalSeconds = Math.toIntExact(burningTicks / 20);

            int hours = totalSeconds / 3600;
            int mins = (totalSeconds % 3600) / 60;
            int secs = totalSeconds % 60;

            if(hours > 0) {
                return String.format("%dh %02dmin %02ds", hours, mins, secs);
            } else if(mins > 0) {
                return String.format("%dmin %02ds", mins, secs);
            } else if(secs > 0) {
                return secs + "s";
            }
        }
        return null;
    }

    public ItemStack[] getFuels() {
        return this.fuels;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        ListTag items = new ListTag();
        for (int i = 0; i < 4; i++) {
            items.addTag(i, this.fuels[i].save(new CompoundTag()));
        }
        tag.put("fuels", items);
        tag.putFloat("maxTemperature", this.maxTemperature);
        tag.putFloat("temperature", this.temperature);
        tag.putLong("burntTick", this.burntTick);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag) {
        super.loadAdditional(tag);
        ListTag items = (ListTag) tag.get("fuels");
        if (items != null) {
            for(int i = 0; i < items.size(); i++) {
                this.fuels[i] = ItemStack.of(items.getCompound(i));
            }
        }
        this.maxTemperature = tag.getFloat("maxTemperature");
        this.temperature = tag.getFloat("temperature");
        this.burntTick = tag.getLong("burntTick");
    }

}
