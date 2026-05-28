package net.vvxzv.ktfcc.mixin.item;

import com.github.ysbbbbbb.kaleidoscopecookery.item.SickleItem;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.SimpleTier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SickleItem.class)
public class SickleItemMixin {
    @SuppressWarnings("unused")
    @Shadow(remap = false)
    private static SimpleTier SICKLE_TIER = new SimpleTier(BlockTags.INCORRECT_FOR_STONE_TOOL, 150, 4.0F, 1.0F, 5, () -> Ingredient.of(Items.FLINT));

    @Redirect(method = "harvest", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/CropBlock;playerDestroy(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/item/ItemStack;)V"))
    private void harvest(net.minecraft.world.level.block.CropBlock instance, Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack) {
        level.destroyBlock(pos, true);
    }
}
