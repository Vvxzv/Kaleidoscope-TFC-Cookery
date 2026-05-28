package net.vvxzv.ktfcc.common.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.vvxzv.ktfcc.KaleidoscopeTFCCookery;
import net.vvxzv.ktfcc.common.block.Tea;

public class CreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, KaleidoscopeTFCCookery.MODID);

    public static final RegistryObject<CreativeModeTab> KaleidoscopeTFCCookery_TAB;

    static {
        KaleidoscopeTFCCookery_TAB = CREATIVE_MODE_TAB.register(
                "ktfcc_tab",
                () -> CreativeModeTab.builder()
                        .title(Component.translatable("ktfcc.tab.name"))
                        .icon(() -> new ItemStack(Blocks.WILD_TEA_TREES.get(Tea.BILUOCHUN).get()))
                        .displayItems((pParameters, pOutput) -> {
                            Items.ITEMS.getEntries().forEach(item -> {
                                pOutput.accept(item.get());
                            });
                        })
                        .build()
        );
    }
}
