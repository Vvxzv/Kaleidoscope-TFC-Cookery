package net.vvxzv.ktfcc.common.block;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.item.KitchenShovelItem;
import com.mojang.serialization.MapCodec;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.tooltip.Tooltips;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.vvxzv.ktfcc.common.block.entity.OilPotBlockEntity;
import net.vvxzv.ktfcc.common.data.Oil;
import net.vvxzv.ktfcc.common.registry.Blocks;
import net.vvxzv.ktfcc.common.registry.DataComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OilPotBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<OilPotBlock> CODEC = simpleCodec((p) -> new OilPotBlock());
    public static final BooleanProperty HAS_OIL = com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.OilPotBlock.HAS_OIL;
    private static final VoxelShape AABB = Block.box(5, 0, 5, 11, 10, 11);

    public OilPotBlock() {
        super(Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BELL).instabreak().pushReaction(PushReaction.DESTROY).sound(SoundType.LANTERN));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HAS_OIL, false));
    }

    @Override
    public @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack pStack, @NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if(level.getBlockEntity(pos) instanceof OilPotBlockEntity oilPot) {
            oilPot.checkHasRanOut();
            ItemStack stack = player.getItemInHand(hand);
            if (FluidHelpers.transferBetweenBlockEntityAndItem(stack, oilPot, player, hand)) {
                oilPot.markForSync();
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }

            if (!FluidHelpers.getContainedFluid(player.getItemInHand(hand)).isEmpty()) {
                return ItemInteractionResult.CONSUME;
            }

            if(stack.is(ModItems.KITCHEN_SHOVEL.get())) {
                if(!KitchenShovelItem.hasOil(stack)) {
                    Oil oil = oilPot.getOil();
                    if(oil != null) {
                        FluidStack fluidStack = oilPot.getFluidStack();
                        int consume = oil.consume();
                        if(fluidStack.getAmount() >= consume) {
                            oilPot.drain(consume, IFluidHandler.FluidAction.EXECUTE);
                            oilPot.markForSync();
                            KitchenShovelItem.setHasOil(stack, true);
                            level.playSound(player, pos, SoundEvents.HONEY_BLOCK_BREAK, SoundSource.BLOCKS, 0.8F, 0.8F);
                            return ItemInteractionResult.sidedSuccess(level.isClientSide);
                        }
                    }
                }
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponent.TAG, CustomData.EMPTY).copyTag();
        if(tag.contains("tank")) {
            if(level.getBlockEntity(pos) instanceof OilPotBlockEntity oilPot) {
                if (oilPot.getLevel() != null) {
                    oilPot.loadAdditional(tag, oilPot.getLevel().registryAccess());
                    level.setBlockAndUpdate(pos, state.setValue(OilPotBlock.HAS_OIL, true));
                }
            }
        }

        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new OilPotBlockEntity(pPos, pState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HAS_OIL);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter blockGetter, @NotNull BlockPos pos, @NotNull CollisionContext collisionContext) {
        return AABB;
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState pState, LootParams.Builder pParams) {
        ItemStack oilPotStack = new ItemStack(Blocks.OIL_POT.get());
        BlockEntity blockEntity = pParams.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof OilPotBlockEntity oilPot) {
            FluidStack fluidStack = oilPot.getFluidStack();
            if(!fluidStack.isEmpty()) {
                CompoundTag tag = new CompoundTag();
                if (oilPot.getLevel() != null) {
                    tag.put("tank", oilPot.getFluidHandler().writeToNBT(oilPot.getLevel().registryAccess(), new CompoundTag()));
                }
                oilPotStack.set(DataComponent.TAG, CustomData.of(tag));
            }
        }

        return List.of(oilPotStack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.@NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag)  {
        CompoundTag tag = stack.getOrDefault(DataComponent.TAG, CustomData.EMPTY).copyTag();
        if(tag.contains("tank")) {
            CompoundTag tank = tag.getCompound("tank");
            FluidStack fluidStack = FluidStack.parseOptional(context.registries(), tank);
            if (!fluidStack.isEmpty()) {
                tooltip.add(Tooltips.fluidUnitsOf(fluidStack));
            }
        } else {
            tooltip.add(Component.translatable("tooltip.ktfcc.oil_pot").withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }
}
