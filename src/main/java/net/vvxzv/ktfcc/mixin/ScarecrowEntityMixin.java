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
import net.minecraft.world.level.block.LanternBlock;
import net.minecraftforge.items.ItemHandlerHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ScarecrowEntity.class)
public abstract class ScarecrowEntityMixin extends LivingEntity {
    @Shadow(remap = false)
    private int cooldown;

    @Shadow(remap = false)
    protected abstract boolean swapHand(InteractionHand hand, Player player, ItemStack itemInHand);

    @Shadow(remap = false)
    public abstract ItemStack getItemBySlot(EquipmentSlot slot);

    @Shadow(remap = false)
    public abstract void setItemSlot(EquipmentSlot slot, ItemStack stack);

    protected ScarecrowEntityMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    /**
     * @author Vvxzv
     * @reason add tfc lamp
     */
    @Overwrite(remap = false)
    private InteractionResult handleHandItems(Player player, ItemStack itemInHand) {
        this.cooldown = 5;
        if (itemInHand.isEmpty()) {
            ItemStack mainhand = this.getItemInHand(InteractionHand.MAIN_HAND);
            ItemStack offhand = this.getItemInHand(InteractionHand.OFF_HAND);
            if (!mainhand.isEmpty()) {
                this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                ItemHandlerHelper.giveItemToPlayer(player, mainhand);
                return InteractionResult.SUCCESS;
            } else if (!offhand.isEmpty()) {
                this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                ItemHandlerHelper.giveItemToPlayer(player, offhand);
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.PASS;
            }
        } else {
            Item offhand = itemInHand.getItem();
            if (offhand instanceof BlockItem blockItem) {
                if ((blockItem.getBlock() instanceof LanternBlock || blockItem.getBlock() instanceof LampBlock) && this.swapHand(InteractionHand.OFF_HAND, player, itemInHand)) {
                    this.level().playSound(null, this.blockPosition(), SoundEvents.LANTERN_PLACE, this.getSoundSource());
                    return InteractionResult.SUCCESS;
                }
            }

            if (itemInHand.getItem().canBeDepleted() && this.swapHand(InteractionHand.MAIN_HAND, player, itemInHand)) {
                this.level().playSound(null, this.blockPosition(), SoundEvents.ITEM_FRAME_ADD_ITEM, this.getSoundSource());
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.PASS;
            }
        }
    }
}
