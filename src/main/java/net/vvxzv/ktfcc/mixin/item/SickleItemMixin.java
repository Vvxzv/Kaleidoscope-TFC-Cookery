package net.vvxzv.ktfcc.mixin.item;

import com.github.ysbbbbbb.kaleidoscopecookery.api.event.SickleHarvestEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.github.ysbbbbbb.kaleidoscopecookery.item.SickleItem;
import net.dries007.tfc.common.blocks.crop.CropBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SickleItem.class)
public class SickleItemMixin {
    @SuppressWarnings("unused")
    @Shadow(remap = false)
    private static ForgeTier SICKLE_TIER = new ForgeTier(1, 150, 4.0F, 1.0F, 5, BlockTags.NEEDS_STONE_TOOL, () -> Ingredient.of(Items.FLINT));

    /**
     * @author Vvxzv
     * @reason 收割作物改成破坏方块
     */
    @Overwrite(remap = false)
    private boolean harvest(BlockPos pos, int x, int y, int z, Level level, Player player, ItemStack stack) {
        BlockPos newPos = pos.offset(x, y, z);
        if (!level.mayInteract(player, newPos)) {
            return false;
        } else {
            BlockState blockState = level.getBlockState(newPos);
            if (blockState.isAir()) {
                return false;
            } else if (blockState.is(TagMod.SICKLE_HARVEST_BLACKLIST)) {
                return false;
            } else {
                Block block = blockState.getBlock();
                SickleHarvestEvent event = new SickleHarvestEvent(player, stack, newPos, blockState);
                if (MinecraftForge.EVENT_BUS.post(event)) {
                    return event.isCostDurability();
                } else if (block instanceof CropBlock cropBlock) {
                    if (cropBlock.isMaxAge(blockState)) {
                        level.destroyBlock(newPos, true);
                        level.levelEvent(null, 2001, newPos, Block.getId(blockState));
                        return true;
                    } else {
                        return false;
                    }
                } else if (block instanceof BushBlock && player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.gameMode.destroyBlock(newPos);
                    level.levelEvent(null, 2001, newPos, Block.getId(blockState));
                    return true;
                } else {
                    return false;
                }
            }
        }
    }
}
