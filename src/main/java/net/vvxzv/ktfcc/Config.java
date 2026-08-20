package net.vvxzv.ktfcc;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = KaleidoscopeTFCCookery.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.DoubleValue FLATULENCE_FLY = BUILDER.comment(" ", "A value about flatulence effect add movement when shift key down. (default 0)", "在胀气效果下，当玩家下蹲时给玩家加的移动速度").defineInRange("flatulenceAddFlyMovement", 0, 0, 0.75);

    private static final ModConfigSpec.BooleanValue FOOD_TOOLTIP = BUILDER.comment(" ", "Turn on or turn off food tooltips", "开启或关闭食物tooltip").define("foodTooltips", true);

    private static final ModConfigSpec.BooleanValue FARMERS_DELIGHT_COMPAT = BUILDER.comment(" ", "Turn on or turn off Farmer's Delight compat", "开关农夫乐事兼容").define("farmersDelightCompat", true);

    private static final ModConfigSpec.DoubleValue MAX_DYNAMIC_NUTRIENT = BUILDER.comment(" ", "Dynamic nutrient value cap about soup or dish. (default 5)", "汤与炒菜的动态营养值上限").defineInRange("maxDynamicNutrient", 5.0, 0, 100.0);

    private static final ModConfigSpec.DoubleValue DYNAMIC_NUTRIENT_MULTIPLIER = BUILDER.comment(" ", "Dynamic nutrient multiplier value about soup or dish.", "汤与炒菜的营养乘数").defineInRange("dynamicNutrientMultiplier", 0.8, 0, 1);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static double flatulenceAddFlyMovement;
    public static boolean foodTooltips;
    public static boolean farmersDelightCompat;
    public static double maxDynamicNutrient;
    public static double dynamicNutrientMultiplier;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        flatulenceAddFlyMovement = FLATULENCE_FLY.get();
        foodTooltips = FOOD_TOOLTIP.get();
        farmersDelightCompat = FARMERS_DELIGHT_COMPAT.get();
        maxDynamicNutrient = MAX_DYNAMIC_NUTRIENT.get();
        dynamicNutrientMultiplier = DYNAMIC_NUTRIENT_MULTIPLIER.get();
    }
}
