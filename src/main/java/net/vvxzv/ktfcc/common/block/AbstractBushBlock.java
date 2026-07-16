package net.vvxzv.ktfcc.common.block;

import net.dries007.tfc.common.blockentities.BerryBushBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.plant.fruit.Lifecycle;
import net.dries007.tfc.common.blocks.plant.fruit.StationaryBerryBushBlock;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateRange;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.vvxzv.ktfcc.common.registry.BlockEntities;
import net.vvxzv.ktfcc.common.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public abstract class AbstractBushBlock extends StationaryBerryBushBlock {
    private final Lifecycle[] lifecycle;

    public AbstractBushBlock(Supplier<? extends Item> productItem, ResourceLocation id, Lifecycle[] lifecycle) {
        super(
                ExtendedProperties.of(MapColor.PLANT).strength(0.6F).noOcclusion().randomTicks().sound(SoundType.SWEET_BERRY_BUSH).blockEntity(BlockEntities.BUSH).flammableLikeLeaves(),
                productItem,
                lifecycle,
                ClimateRange.MANAGER.getReference(id)
        );
        this.lifecycle = lifecycle;
    }

    @Override
    public void onUpdate(Level level, @NotNull BlockPos pos, @NotNull BlockState state) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof BerryBushBlockEntity plant)) return;

        Lifecycle current = state.getValue(LIFECYCLE);
        Lifecycle expect = getLifecycleForCurrentMonth(level, pos);

        if (checkAndSetDormant(level, pos, state, current, expect)) return;

        ClimateRange range = climateRange.get();
        BlockPos stemPos = plant.getStemPos();
        int hydration = getFruitBushHydrationFromRootPos(level, stemPos.below());
        float temp = Climate.getAverageTemperature(level, stemPos);

        if (!range.checkBoth(hydration, temp, false)) {
            level.setBlockAndUpdate(pos, state.setValue(LIFECYCLE, Lifecycle.DORMANT));
            return;
        }

        long nowTick = Calendars.SERVER.getTicks();
        if (nowTick - plant.getLastUpdateTick() < 28800 * 3) {
            return;
        }

        Lifecycle next = current.advanceTowards(expect);
        if (next != current) {
            BlockState newState = state.setValue(LIFECYCLE, next);
            if (next != Lifecycle.FLOWERING || nowTick - plant.getLastPickedTick() > TFCConfig.SERVER.fruitPickBloomDelayTicks.get()) {
                level.setBlock(pos, newState, 3);
            }
        }
    }

    @Override
    public @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if(hand == InteractionHand.MAIN_HAND) {
            ItemStack handItem = player.getItemInHand(hand);
            if(handItem.is(Tags.Items.TOOLS_SHEAR)) {
                if(state.getValue(STAGE) == 2) {
                    level.playSound(null, pos, SoundEvents.CAVE_VINES_PICK_BERRIES, SoundSource.BLOCKS, 1, level.getRandom().nextFloat() + 0.7F + 0.3F);
                    BlockState newState = state.setValue(STAGE, 0);
                    level.setBlockAndUpdate(pos, newState);
                    if (!level.isClientSide()) {
                        handItem.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                        ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(this.asItem()));
                    }

                    return ItemInteractionResult.SUCCESS;
                }
            }
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }


    @Override
    protected void growAndPropagate(BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random, int cycles, int growthsRemaining) {
        int oldStage = state.getValue(STAGE);
        if (growthsRemaining <= 0) {
            return;
        }
        if (oldStage < 2) {
            BlockState newState = state.setValue(STAGE, oldStage + 1);
            this.placeBlockAndResetCounter(level, pos, newState, cycles, growthsRemaining);
        }
    }

    public ClimateRange getClimateRange() {
        return this.climateRange.get();
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return PLANT_SHAPE;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> list, @NotNull TooltipFlag flag) {
        ClimateRange range = this.getClimateRange();
        float minTemperature = range.minTemperature();
        float maxTemperature = range.maxTemperature();
        float minHydration = range.minHydration();
        float maxHydration = range.maxHydration();

        if(flag.hasShiftDown()) {
            list.add(Component.translatable("tfc.tooltip.plantable.climate").withStyle(ChatFormatting.GRAY));
            list.add(Component.translatable("tfc.tooltip.plantable.climate.temperature_range", minTemperature, maxTemperature));
            list.add(Component.translatable("tfc.tooltip.plantable.climate.hydration", minHydration, maxHydration));
            list.add(Component.literal(" "));
            list.add(Component.translatable("tfc.tooltip.plantable.lifecycle").withStyle(ChatFormatting.GRAY));
            list.add(
                    Component.translatable("tfc.tooltip.plantable.lifecycle.healthy").withStyle(ChatFormatting.GREEN)
                            .append(" ")
                            .append(Utils.getTranslationWithLifecycles(this.lifecycle, Lifecycle.HEALTHY))
            );
            list.add(
                    Component.translatable("tfc.tooltip.plantable.lifecycle.fruiting").withStyle(ChatFormatting.DARK_PURPLE)
                            .append(" ")
                            .append(Utils.getTranslationWithLifecycles(this.lifecycle, Lifecycle.FRUITING))
            );
        } else {
            list.add(Component.translatable("tfc.tooltip.plantable.hold_shift").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        }
    }
}
