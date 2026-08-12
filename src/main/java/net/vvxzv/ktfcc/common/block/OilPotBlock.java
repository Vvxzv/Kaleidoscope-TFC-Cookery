package net.vvxzv.ktfcc.common.block;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.item.KitchenShovelItem;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Tooltips;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.vvxzv.ktfcc.common.block.entity.OilPotBlockEntity;
import net.vvxzv.ktfcc.common.data.Oil;
import net.vvxzv.ktfcc.common.registry.Blocks;
import net.vvxzv.ktfcc.common.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class OilPotBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final BooleanProperty HAS_OIL = com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.OilPotBlock.HAS_OIL;
    private static final VoxelShape AABB = Block.box(5, 0, 5, 11, 10, 11);

    public OilPotBlock() {
        super(Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BELL).instabreak().pushReaction(PushReaction.DESTROY).sound(SoundType.LANTERN));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HAS_OIL, false));
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState pState, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if(level.getBlockEntity(pos) instanceof OilPotBlockEntity oilPot) {
            ItemStack stack = player.getItemInHand(hand);
            if (FluidHelpers.transferBetweenBlockEntityAndItem(stack, oilPot, player, hand)) {
                oilPot.markForSync();
                return InteractionResult.sidedSuccess(level.isClientSide);
            }

            if (!Utils.getContainedFluid(player.getItemInHand(hand)).isEmpty()) {
                return InteractionResult.CONSUME;
            }

            if(stack.is(ModItems.KITCHEN_SHOVEL.get())) {
                if(!KitchenShovelItem.hasOil(stack)) {
                    Oil oil = oilPot.getOil();
                    if(oil != null) {
                        FluidStack fluidStack = oilPot.getFluidStack();
                        int consume = oil.getConsumeAmount();
                        if(fluidStack.getAmount() >= consume) {
                            oilPot.drain(consume, IFluidHandler.FluidAction.EXECUTE);
                            oilPot.markForSync();
                            KitchenShovelItem.setHasOil(stack, true);
                            level.playSound(player, pos, SoundEvents.HONEY_BLOCK_BREAK, SoundSource.BLOCKS, 0.8F, 0.8F);
                            return InteractionResult.sidedSuccess(level.isClientSide);
                        }
                    }
                }
            }

            oilPot.checkHasRanOut();
        }
        return InteractionResult.PASS;
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if(tag.contains("tank")) {
            if(level.getBlockEntity(pos) instanceof OilPotBlockEntity oilPot) {
                oilPot.loadAdditional(tag);
                level.setBlockAndUpdate(pos, state.setValue(OilPotBlock.HAS_OIL, true));
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
                CompoundTag tag = oilPotStack.getOrCreateTag();
                tag.put("tank", oilPot.getFluidHandler().writeToNBT(new CompoundTag()));
                oilPotStack.setTag(tag);
            }
        }

        return List.of(oilPotStack);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable BlockGetter pLevel, @NotNull List<Component> pTooltip, @NotNull TooltipFlag pFlag) {
        if(pStack.hasTag()) {
            CompoundTag tag = pStack.getOrCreateTag();
            if(tag.contains("tank")) {
                CompoundTag tank = tag.getCompound("tank");
                FluidStack fluidStack = FluidStack.loadFluidStackFromNBT(tank);
                if (!fluidStack.isEmpty()) {
                    pTooltip.add(Tooltips.fluidUnitsOf(fluidStack));
                }
            }
        } else {
            pTooltip.add(Component.translatable("tooltip.ktfcc.oil_pot").withStyle(ChatFormatting.GRAY));
        }
    }
}
