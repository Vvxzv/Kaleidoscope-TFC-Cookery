package net.vvxzv.ktfcc.mixin.block;

import com.github.ysbbbbbb.kaleidoscopecookery.block.misc.ChiliRistraBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin(ChiliRistraBlock.class)
public class ChiliRistraBlockMixin extends Block {

    @Final
    @Shadow
    public static BooleanProperty SHEARED;

    public ChiliRistraBlockMixin(Properties properties) {
        super(properties);
    }

    @ModifyArg(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;<init>(Lnet/minecraft/world/level/ItemLike;I)V"),index = 1)
    private int setChili(int pCount){
        return 2;
    }


    @Inject(method = "useItemOn", at = @At(value = "RETURN", ordinal = 2), cancellable = true)
    private void fixGetChili(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<ItemInteractionResult> cir) {
        cir.setReturnValue(ItemInteractionResult.SUCCESS);
    }

    @Inject(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 0))
    private void getStick(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<ItemInteractionResult> cir) {
        ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(Items.STICK));
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.@NotNull Builder params) {
        List<ItemStack> list = new ArrayList<>(List.of(new ItemStack(Items.STICK), new ItemStack(ModItems.RED_CHILI.get(), 2)));
        if(!state.getValue(SHEARED)) {
            list.add(new ItemStack(ModItems.RED_CHILI.get(), 2));
        }

        return list;
    }
}
