package net.vvxzv.ktfcc.mixin.block;

import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteThreeByThreeBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.NinePart;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.food.FoodBiteThreeByThreeBlockEntity;
import net.dries007.tfc.util.Helpers;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.vvxzv.ktfcc.common.utils.Decaying;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FoodBiteThreeByThreeBlock.class)
public class FoodBiteThreeByThreeBlockMixin extends FoodBiteBlock {

    @Final
    @Shadow
    public static EnumProperty<NinePart> PART;

    public FoodBiteThreeByThreeBlockMixin(FoodProperties foodProperties) {
        super(foodProperties);
    }

    @Inject(method = "useWithoutItem", at = @At("HEAD"), cancellable = true)
    private void use(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        BlockEntity entity = level.getBlockEntity(pos);
        if(entity instanceof Decaying decaying && decaying.isRotten()){
            player.displayClientMessage(Component.translatable("ktfcc.eat.rotten_block").withStyle(ChatFormatting.GRAY), true);
            cir.setReturnValue(InteractionResult.PASS);
        }
    }

    @Inject(method = "newBlockEntity", at = @At("HEAD"), cancellable = true)
    private void newBlockEntity(BlockPos pos, BlockState state, CallbackInfoReturnable<BlockEntity> cir) {
        cir.setReturnValue(new FoodBiteThreeByThreeBlockEntity(pos, state));
    }

    @Inject(method = "handleRemove", at = @At("HEAD"), cancellable = true)
    private static void handleRemove(Level world, BlockPos pos, BlockState state, Player player, CallbackInfo ci) {
        if (!world.isClientSide) {
            NinePart part = state.getValue(PART);
            BlockPos centerPos = pos.subtract(new Vec3i(part.getPosX(), 0, part.getPosY()));
            BlockEntity blockEntity = world.getBlockEntity(centerPos);
            if (blockEntity instanceof FoodBiteThreeByThreeBlockEntity) {
                for(int i = -1; i < 2; ++i) {
                    for(int j = -1; j < 2; ++j) {
                        BlockPos offsetPos = centerPos.offset(i, 0, j);
                        if (i == 0 && j == 0) {
                            world.destroyBlock(offsetPos, true, player);
                        } else {
                            world.setBlock(offsetPos, Blocks.AIR.defaultBlockState(), 35);
                        }
                    }
                }
            }
            ci.cancel();
        }
    }

    @Inject(method = "setPlacedBy", at = @At("HEAD"), cancellable = true)
    private void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack, CallbackInfo ci) {
        if (!worldIn.isClientSide) {
            for(int i = -1; i < 2; ++i) {
                for(int j = -1; j < 2; ++j) {
                    BlockPos searchPos = pos.offset(i, 0, j);
                    NinePart part = NinePart.getPartByPos(i, j);
                    if (part != null) {
                        if(!part.isCenter()){
                            worldIn.setBlockAndUpdate(searchPos, state.setValue(PART, part));
                        }

                        BlockEntity blockEntity = worldIn.getBlockEntity(searchPos);
                        if (blockEntity instanceof Decaying decaying) {
                            decaying.setStack(stack);
                        }
                    }
                }
            }
        }
        ci.cancel();
    }

    @Override
    public void onRemove(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof Decaying decaying) {
            if(state.getValue(bites) == 0 && state.getValue(PART).isCenter()){
                if (!Helpers.isBlock(state, newState.getBlock())) {
                    Helpers.spawnItem(level, pos, decaying.getStack());
                }
            }
        }

        if (state.hasBlockEntity() && (!state.is(newState.getBlock()) || !newState.hasBlockEntity())) {
            level.removeBlockEntity(pos);
        }
    }
}