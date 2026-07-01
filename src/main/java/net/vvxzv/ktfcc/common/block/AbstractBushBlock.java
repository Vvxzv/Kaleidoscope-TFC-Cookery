package net.vvxzv.ktfcc.common.block;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.plant.fruit.Lifecycle;
import net.dries007.tfc.common.blocks.plant.fruit.StationaryBerryBushBlock;
import net.dries007.tfc.util.climate.ClimateRange;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.vvxzv.ktfcc.common.registry.BlockEntities;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public abstract class AbstractBushBlock extends StationaryBerryBushBlock {
    public AbstractBushBlock(Supplier<? extends Item> productItem, ResourceLocation id, Lifecycle[] lifecycle) {
        super(
                ExtendedProperties.of(MapColor.PLANT).strength(0.6F).noOcclusion().randomTicks().sound(SoundType.SWEET_BERRY_BUSH).blockEntity(BlockEntities.BUSH).flammableLikeLeaves(),
                productItem,
                lifecycle,
                ClimateRange.MANAGER.getReference(id)
        );
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

        list.add(Component.translatable("tfc.tooltip.plantable.climate"));
        list.add(Component.translatable("tfc.tooltip.plantable.climate.temperature", minTemperature, maxTemperature));
        list.add(Component.translatable("tfc.tooltip.plantable.climate.hydration", minHydration, maxHydration));
    }
}
