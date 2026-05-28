package net.vvxzv.ktfcc.common.block;

import net.dries007.tfc.client.overworld.SolarCalculator;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.crop.WildCropBlock;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.Month;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WildTeaTreeBlock extends WildCropBlock {

    public WildTeaTreeBlock() {
        super(ExtendedProperties.of(MapColor.PLANT).noCollission().randomTicks().strength(0.4F).sound(SoundType.CROP).flammable(60, 30).randomTicks());
    }

    public static boolean isMature(Level level, BlockPos pos) {
        int month = Calendars.get(level).getHemispheralCalendarMonthOfYear(SolarCalculator.getInNorthernHemisphere(pos, level)).ordinal();
        return month >= Month.MARCH.ordinal() && month <= Month.MAY.ordinal();
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(MATURE, isMature(context.getLevel(), context.getClickedPos()));
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return box(2, 0, 2, 14, 8, 14);
    }
}
