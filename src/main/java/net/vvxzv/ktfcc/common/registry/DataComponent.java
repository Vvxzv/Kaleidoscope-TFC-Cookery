package net.vvxzv.ktfcc.common.registry;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.vvxzv.ktfcc.KaleidoscopeTFCCookery;

public class DataComponent {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, KaleidoscopeTFCCookery.MODID);

    public static final DeferredHolder<DataComponentType<?>,DataComponentType<CustomData>> TAG = DATA_COMPONENT_TYPES.register("tag", () -> DataComponentType.<CustomData>builder().persistent(CustomData.CODEC).build());
}
