package net.vvxzv.ktfcc.mixin.block;

import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.StoveBlock;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.vvxzv.ktfcc.common.block.entity.StoveBlockEntity;
import net.vvxzv.ktfcc.common.registry.BlockEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StoveBlock.class)
public class StoveBlockMixin extends HorizontalDirectionalBlock implements EntityBlock {

    @Final
    @Shadow(remap = false)
    public static BooleanProperty LIT;

    protected StoveBlockMixin(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new StoveBlockEntity(pPos, pState);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        return pBlockEntityType == BlockEntities.STOVE.get() ? (level, pos, state, be) -> ((StoveBlockEntity) be).serverTick(level, pos, state) : null;
    }

    @Override
    public void onRemove(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof StoveBlockEntity stove) {
            if (!Helpers.isBlock(state, newState.getBlock())) {
                for (ItemStack fuel : stove.getFuels()) {
                    Helpers.spawnItem(level, pos, fuel);
                }
            }
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }

    /**
     * @author Vvxzv
     * @reason 炉子熄灭停止燃料燃烧
     */
    @Overwrite
    public void randomTick(BlockState blockState, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (blockState.getValue(LIT) && level.isRainingAt(pos.above())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(blockEntity instanceof StoveBlockEntity stove) {
                stove.extinguish(level, pos, blockState);
            }

            level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }
}
