package net.vvxzv.ktfcc.common.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.vvxzv.ktfcc.KTFCC;

public class KCreativeTAB {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, KTFCC.MODID);

    public static final RegistryObject<CreativeModeTab> KTFCC_TAB;

    static {
        KTFCC_TAB = CREATIVE_MODE_TAB.register(
                "ktfcc_tab",
                () -> CreativeModeTab.builder()
                        .title(Component.translatable("ktfcc.tab.name"))
                        .icon(() -> new ItemStack(KBlocks.PAN.get()))
                        .displayItems((pParameters, pOutput) -> {
                            KItems.ITEMS.getEntries().forEach(item -> {
                                pOutput.accept(item.get());
                            });
                        })
                        .build()
        );
    }
}
