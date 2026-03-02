package net.vvxzv.ktfcc;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = KTFCC.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.DoubleValue STOVE_TEMPERATURE = BUILDER.comment(" ").comment("The stove provides the highest temperature that can be reached by a cluster of heat sources (default 450)").comment("炉灶提供群峦热源可达到的最高温度（默认450）").defineInRange("stoveTemperature", 450.0, 200, 2000);

    private static final ForgeConfigSpec.DoubleValue FLATULENCE_FLY = BUILDER.comment(" ").comment("A value about flatulence effect add movement when shift key down(default 0)").comment("在胀气效果下，当玩家下蹲时给玩家加的移动速度").defineInRange("flatulenceAddFlyMovement", 0, 0, 0.75);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static double stoveTemperature;
    public static double flatulenceAddFlyMovement;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        stoveTemperature = STOVE_TEMPERATURE.get();
        flatulenceAddFlyMovement = FLATULENCE_FLY.get();
    }
}
