package net.vvxzv.ktfcc.mixin.item;

import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.item.BowlFoodBlockItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.vvxzv.ktfcc.Config;
import net.vvxzv.ktfcc.common.data.FoodEffect;
import net.vvxzv.ktfcc.common.utils.Utils;
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

@Mixin(BowlFoodBlockItem.class)
public abstract class BowlFoodBlockItemMixin extends BlockItem {

    @Final
    @Shadow
    private List<MobEffectInstance> effectInstances;

    public BowlFoodBlockItemMixin(Block block, Properties properties) {
        super(block, properties);
    }

    @Unique
    private static boolean hasTooltip(){
        return Config.foodTooltips;
    }

    @Redirect(method = "appendHoverText", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0))
    private boolean text(List<Object> instance, Object e) {
        if(hasTooltip()) {
            return instance.add(e);
        }
        return false;
    }

    @Inject(method = "appendHoverText", at = @At("HEAD"))
    private void effectTooltip(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag, CallbackInfo ci) {
        FoodEffect foodEffect = FoodEffect.get(stack);
        if(foodEffect != null) {
            this.effectInstances.clear();
            List<MobEffectInstance> effects = foodEffect.getEffects();
            if(!effects.isEmpty()) {
                effectInstances.addAll(effects);
            }
        }
    }

//    @Redirect(method = "finishUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BlockItem;finishUsingItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;"))
//    private ItemStack finishUsingItem(BlockItem instance, ItemStack stack, Level level, LivingEntity entity) {
//        FoodEffect foodEffect = FoodEffect.get(stack);
//        if(foodEffect != null) {
//            FoodProperties foodproperties = stack.getFoodProperties(entity);
//            if(foodproperties != null) {
//                FoodProperties foodProperties = Utils.foodPropertiesRemoveEffect(foodproperties);
//                Utils.applyFoodEffect(foodEffect, entity);
//                return entity.eat(level, stack, foodProperties);
//            }
//        }
//        return instance.finishUsingItem(stack, level, entity);
//    }

    @Shadow
    protected abstract List<ItemStack> getDrops(BlockState state, LootParams.Builder params);

    @Inject(method = "finishUsingItem", at = @At("HEAD"), cancellable = true)
    private void finishUsingItem(ItemStack stack, Level level, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir) {
        if (level instanceof ServerLevel serverLevel) {
            Block var6 = this.getBlock();
            if (var6 instanceof FoodBiteBlock foodBiteBlock) {
                LootParams.Builder builder = (new LootParams.Builder(serverLevel)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(entity.blockPosition())).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.THIS_ENTITY, entity).withOptionalParameter(LootContextParams.BLOCK_ENTITY, null);
                BlockState state = foodBiteBlock.defaultBlockState().setValue(foodBiteBlock.getBites(), foodBiteBlock.getMaxBites());
                List<ItemStack> drops = this.getDrops(state, builder);
                drops.forEach((itemStack) -> {
                    if (!itemStack.isEmpty()) {
                        if (entity instanceof Player player) {
                            ItemHandlerHelper.giveItemToPlayer(player, itemStack);
                        } else {
                            ItemEntity itemEntity = new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), itemStack);
                            level.addFreshEntity(itemEntity);
                        }
                    }
                });
            }
        }

        FoodEffect foodEffect = FoodEffect.get(stack);
        if(foodEffect != null) {
            FoodProperties foodproperties = stack.getFoodProperties(entity);
            if(foodproperties != null) {
                FoodProperties foodProperties = Utils.foodPropertiesRemoveEffect(foodproperties);
                Utils.applyFoodEffect(foodEffect, entity);
                cir.setReturnValue(entity.eat(level, stack, foodProperties));
                cir.cancel();
                return;
            }
        }

        cir.setReturnValue(super.finishUsingItem(stack, level, entity));
        cir.cancel();
    }
}
