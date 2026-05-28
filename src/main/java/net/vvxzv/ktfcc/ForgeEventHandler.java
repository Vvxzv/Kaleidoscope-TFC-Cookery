package net.vvxzv.ktfcc;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.util.events.StartFireEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.vvxzv.ktfcc.common.block.entity.StoveBlockEntity;
import net.vvxzv.ktfcc.common.data.FoodEffect;
import net.vvxzv.ktfcc.common.data.TeaEffect;
import net.vvxzv.ktfcc.compat.firmalife.FLEventHandler;

public class ForgeEventHandler {

    public static void init() {
        IEventBus bus = MinecraftForge.EVENT_BUS;
        bus.addListener(ForgeEventHandler::addReloadListeners);
        bus.addListener(ForgeEventHandler::onFireStart);
        bus.addListener(ForgeEventHandler::addFuelToStove);
        bus.addListener(ForgeEventHandler::cancelPlaceRottenBlockItem);

        if(ModList.get().isLoaded("firmalife")) {
            FLEventHandler.init(bus);
        }
    }

    public static void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener(TeaEffect.MANAGER);
        event.addListener(FoodEffect.MANAGER);
    }

    public static void onFireStart(StartFireEvent event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        BlockState state = event.getState();
        if(blockEntity instanceof StoveBlockEntity stove) {
            if(stove.lit(level, pos, state)) {
                event.setCanceled(true);
            }
        }
    }

    public static void addFuelToStove(PlayerInteractEvent.RightClickBlock event) {
        if(event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        Level level = event.getLevel();
        BlockPos clickPos = event.getPos();
        BlockEntity blockEntity = level.getBlockEntity(clickPos);
        if(blockEntity instanceof StoveBlockEntity stove) {
            Player player = event.getEntity();
            ItemStack stack = player.getMainHandItem();
            if(stack.isEmpty()) {
                float p = stove.getFuelFillPercentage();
                player.displayClientMessage(
                        Component.translatable("ktfcc.stove.fuel")
                                .append(Component.literal(p*100 + "%").withStyle(ChatFormatting.GRAY)),
                        true
                );
            }
            if(stove.addFuel(stack)) {
                if(!player.isCreative()) {
                    stack.shrink(1);
                }
                event.setCanceled(true);
            }
        }
    }

    public static void cancelPlaceRottenBlockItem(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        Player player = event.getEntity();

        Direction face = event.getFace();
        if (face == null) {
            return;
        }

        ItemStack stack = player.getItemInHand(event.getHand());
        if(stack.getItem() instanceof BlockItem blockItem) {
            BlockState state = blockItem.getBlock().defaultBlockState();
            BlockPos clickPos = event.getPos();
            BlockState clickedState = level.getBlockState(clickPos);
            BlockPos placePos = clickedState.canBeReplaced() ? clickPos : clickPos.relative(face);
            if (!level.isUnobstructed(state, placePos, CollisionContext.of(player))) {
                return;
            }

            if(stack.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath(KaleidoscopeTFCCookery.MODID, "cant_place_when_rotten")))) {
                IFood food = FoodCapability.get(stack);
                if (food != null && food.isRotten()) {
                    event.setCanceled(true);
                }
            }
        }
    }

}
