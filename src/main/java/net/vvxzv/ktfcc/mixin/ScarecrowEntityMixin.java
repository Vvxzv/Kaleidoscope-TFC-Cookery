package net.vvxzv.ktfcc.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.entity.ScarecrowEntity;
import net.dries007.tfc.common.blocks.devices.LampBlock;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScarecrowEntity.class)
public abstract class ScarecrowEntityMixin extends LivingEntity {

    @Shadow(remap = false)
    protected abstract boolean swapHand(InteractionHand hand, Player player, ItemStack itemInHand);

    @Shadow(remap = false)
    public abstract @NotNull ItemStack getItemBySlot(@NotNull EquipmentSlot slot);

    @Shadow(remap = false)
    public abstract void setItemSlot(@NotNull EquipmentSlot slot, @NotNull ItemStack stack);

    protected ScarecrowEntityMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "handleHandItems", at = @At("HEAD"), cancellable = true, remap = false)
    private void handleHandItems(Player player, ItemStack itemInHand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!itemInHand.isEmpty()) {
            Item offhand = itemInHand.getItem();
            if (offhand instanceof BlockItem blockItem) {
                if (blockItem.getBlock() instanceof LampBlock && this.swapHand(InteractionHand.OFF_HAND, player, itemInHand)) {
                    this.level().playSound(null, this.blockPosition(), SoundEvents.LANTERN_PLACE, this.getSoundSource());
                    cir.setReturnValue(InteractionResult.SUCCESS);
                }
            }
        }
    }
}
