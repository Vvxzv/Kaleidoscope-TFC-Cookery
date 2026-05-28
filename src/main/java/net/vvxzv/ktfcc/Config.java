package net.vvxzv.ktfcc;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = KaleidoscopeTFCCookery.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.DoubleValue STOVE_TEMPERATURE = BUILDER.comment(" ").comment("The stove provides the highest temperature that can be reached by a cluster of heat sources. (default 450)").comment("炉灶提供群峦热源可达到的最高温度（默认450）").defineInRange("stoveTemperature", 450.0, 200, 2000);

    private static final ModConfigSpec.DoubleValue FLATULENCE_FLY = BUILDER.comment(" ").comment("A value about flatulence effect add movement when shift key down. (default 0)").comment("在胀气效果下，当玩家下蹲时给玩家加的移动速度").defineInRange("flatulenceAddFlyMovement", 0, 0, 0.75);

    private static final ModConfigSpec.BooleanValue FOOD_TOOLTIP = BUILDER.comment(" ").comment("Turn on or turn off food tooltips").comment("开启或关闭食物tooltip").define("foodTooltips", true);

    private static final ModConfigSpec.BooleanValue FARMERS_DELIGHT_COMPAT = BUILDER.comment(" ").comment("Turn on or turn off Farmer's Delight Compat").comment("开关农夫乐事兼容").define("farmersDelightCompat", true);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static double stoveTemperature;
    public static double flatulenceAddFlyMovement;
    public static boolean foodTooltips;
    public static boolean farmersDelightCompat;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        stoveTemperature = STOVE_TEMPERATURE.get();
        flatulenceAddFlyMovement = FLATULENCE_FLY.get();
        foodTooltips = FOOD_TOOLTIP.get();
        farmersDelightCompat = FARMERS_DELIGHT_COMPAT.get();
    }
}
