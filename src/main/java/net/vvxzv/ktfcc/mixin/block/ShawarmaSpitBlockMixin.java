package net.vvxzv.ktfcc.mixin.block;

import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.ShawarmaSpitBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ShawarmaSpitBlock.class)
public class ShawarmaSpitBlockMixin extends HorizontalDirectionalBlock {

    @Final
    @Shadow(remap = false)
    public static BooleanProperty WATERLOGGED;

    @Final
    @Shadow(remap = false)
    public static EnumProperty<DoubleBlockHalf> HALF;

    @Final
    @Shadow(remap = false)
    public static BooleanProperty POWERED;

    protected ShawarmaSpitBlockMixin(Properties pProperties) {
        super(pProperties);
    }

    @Inject(method = "updateShape", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    private void updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor levelAccessor, BlockPos currentPos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
        DoubleBlockHalf half = state.getValue(HALF);
        boolean power = this.isWholeBlockPower(levelAccessor, currentPos, half);
        BlockState returnState = neighborState.is(this) && neighborState.getValue(HALF) != half
                ? state.setValue(FACING, neighborState.getValue(FACING)).setValue(POWERED, power)
                : Blocks.AIR.defaultBlockState();
        cir.setReturnValue(returnState);
    }

    @Redirect(method = "neighborChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean neighborChanged(Level instance, BlockPos pPos, BlockState pNewState, int pFlags, BlockState state) {
        DoubleBlockHalf half = state.getValue(HALF);
        boolean power = this.isWholeBlockPower(instance, pPos, half);
        return instance.setBlock(pPos, state.setValue(POWERED, power), pFlags);
    }

    @Inject(method = "neighborChanged", at = @At("TAIL"))
    private void onBelowBlockUpdate(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving, CallbackInfo ci) {
        BlockPos lowerPos = getLowerPos(pos, state);
        if (fromPos.equals(lowerPos.below())) {
            refreshWholeSpit(level, lowerPos);
        }
    }

    @Inject(method = "getStateForPlacement", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    private void getStateForPlacement(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        FluidState fluidState = context.getLevel().getFluidState(pos);
        boolean power = this.isWholeBlockPower(level, pos, DoubleBlockHalf.LOWER);
        BlockState returnState = this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection())
                .setValue(POWERED, power)
                .setValue(HALF, DoubleBlockHalf.LOWER)
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
        cir.setReturnValue(returnState);
    }

    @Unique
    private BlockPos getLowerPos(BlockPos pos, BlockState state) {
        DoubleBlockHalf half = state.getValue(HALF);
        return half == DoubleBlockHalf.LOWER ? pos : pos.below();
    }

    @Unique
    private void refreshWholeSpit(Level level, BlockPos lowerPos) {
        BlockPos upperPos = lowerPos.above();
        BlockState lowerState = level.getBlockState(lowerPos);
        BlockState upperState = level.getBlockState(upperPos);

        boolean newPower = isWholeBlockPower(level, lowerPos, DoubleBlockHalf.LOWER);

        if (lowerState.is((ShawarmaSpitBlock)(Object)this) && lowerState.getValue(POWERED) != newPower) {
            level.setBlock(lowerPos, lowerState.setValue(POWERED, newPower), 2);
        }
        if (upperState.is((ShawarmaSpitBlock)(Object)this) && upperState.getValue(POWERED) != newPower) {
            level.setBlock(upperPos, upperState.setValue(POWERED, newPower), 2);
        }
    }

    @Unique
    private boolean isWholeBlockPower(LevelAccessor level, BlockPos pos, DoubleBlockHalf half) {
        BlockPos lowerPos;
        if (half == DoubleBlockHalf.LOWER) {
            lowerPos = pos;
        } else {
            lowerPos = pos.below();
        }

        BlockPos heatPos = lowerPos.below();
        BlockState heatState = level.getBlockState(heatPos);
        boolean validHeat = heatState.hasProperty(BlockStateProperties.LIT) && heatState.getValue(BlockStateProperties.LIT);

        BlockPos upperPos = lowerPos.above();
        boolean lowerSignal = level.hasNeighborSignal(lowerPos);
        boolean upperSignal = level.hasNeighborSignal(upperPos);
        boolean hasAnyRedstone = lowerSignal || upperSignal;

        return validHeat && hasAnyRedstone;
    }

    @Inject(method = "appendHoverText", at = @At("TAIL"))
    private void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag, CallbackInfo ci) {
        tooltip.add(Component.translatable("tooltip.ktfcc.shawarma_spit").withStyle(ChatFormatting.GRAY));
    }
}
