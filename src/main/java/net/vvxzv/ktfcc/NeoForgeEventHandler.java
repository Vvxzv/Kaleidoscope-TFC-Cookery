package net.vvxzv.ktfcc;

import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.PlateBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.item.FruitBasketItem;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.plant.fruit.FruitTreeLeavesBlock;
import net.dries007.tfc.common.blocks.plant.fruit.Lifecycle;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.IFood;
import net.dries007.tfc.util.events.StartFireEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.vvxzv.ktfcc.common.block.entity.StoveBlockEntity;
import net.vvxzv.ktfcc.common.data.DataManagers;
import net.vvxzv.ktfcc.common.data.Plate;
import net.vvxzv.ktfcc.common.utils.Decaying;
import net.vvxzv.ktfcc.compat.firmalife.FLEventHandler;

public class NeoForgeEventHandler {

    public static void init() {
        IEventBus bus = NeoForge.EVENT_BUS;
        bus.addListener(NeoForgeEventHandler::addReloadListeners);
        bus.addListener(NeoForgeEventHandler::onFireStart);
        bus.addListener(NeoForgeEventHandler::addFuelToStove);
        bus.addListener(NeoForgeEventHandler::cancelPlaceRottenBlockItem);
        bus.addListener(NeoForgeEventHandler::setPlate);
        bus.addListener(NeoForgeEventHandler::plateTooltip);
        bus.addListener(NeoForgeEventHandler::pickFruits);

        if(ModList.get().isLoaded("firmalife")) {
            FLEventHandler.init(bus);
        }
    }

    public static void addReloadListeners(AddReloadListenerEvent event) {
        DataManagers.REGISTRY.forEach(event::addListener);
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

    public static void addFuelToStove(UseItemOnBlockEvent event) {
        if(event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        Level level = event.getLevel();
        BlockPos clickPos = event.getPos();
        BlockEntity blockEntity = level.getBlockEntity(clickPos);
        if(blockEntity instanceof StoveBlockEntity stove) {
            Player player = event.getPlayer();
            if(player != null) {
                float p = stove.getFuelFillPercentage();
                player.displayClientMessage(
                        Component.translatable("ktfcc.stove.fuel")
                                .append(Component.literal(p*100 + "%").withStyle(ChatFormatting.GRAY)),
                        true
                );

                ItemStack stack = player.getMainHandItem();
                if(stove.addFuel(stack)) {
                    if(!player.isCreative()) {
                        stack.shrink(1);
                    }
                    event.setCancellationResult(ItemInteractionResult.SUCCESS);
                    event.setCanceled(true);
                }
            }
        }
    }

    public static void cancelPlaceRottenBlockItem(UseItemOnBlockEvent event) {
        Level level = event.getLevel();
        Player player = event.getPlayer();
        if(player == null) return;

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

    public static void setPlate(UseItemOnBlockEvent event) {
        if(event.getHand() != InteractionHand.MAIN_HAND) return;

        Player player = event.getPlayer();
        if(player == null) return;

        Level level = event.getLevel();

        BlockState state = level.getBlockState(event.getPos());
        if(state.is(TFCBlocks.WOODEN_BOWL.get())) {
            ItemStack stack = player.getMainHandItem();
            ItemStack plateItem = Plate.getPlateItem(stack);
            if(plateItem != null && plateItem.getItem() instanceof BlockItem blockItem) {
                if(blockItem.getBlock() instanceof PlateBlock plateBlock) {
                    BlockState newState = plateBlock.defaultBlockState();
                    level.setBlockAndUpdate(event.getPos(), newState.setValue(plateBlock.getServingsProperty(), 1));
                    BlockEntity blockEntity = level.getBlockEntity(event.getPos());
                    if(blockEntity instanceof Decaying decaying) {
                        decaying.setStack(stack);
                    }
                    stack.shrink(1);
                    event.setCancellationResult(ItemInteractionResult.SUCCESS);
                    event.setCanceled(true);
                }
            }
        }
    }

    public static void plateTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) return;

        ItemStack plateItem = Plate.getPlateItem(stack);
        if(plateItem != null) {
            event.getToolTip().add(Component.literal(" "));
            event.getToolTip().add(Component.translatable("ktfcc.tooltip.can_place_on_wooden_bowl").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            event.getToolTip().add(Component.literal(" "));
        }

        if(stack.is(ModItems.FRUIT_BASKET.get())) {
            event.getToolTip().add(Component.translatable("ktfcc.tooltip.pick_fruit").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
    }

    public static void pickFruits(UseItemOnBlockEvent event) {
        if (event.getHand() != InteractionHand.MAIN_HAND) return;

        Player player = event.getPlayer();
        if (player == null) return;

        ItemStack handStack = event.getItemStack();
        if (!handStack.is(ModItems.FRUIT_BASKET.get())) return;

        Level level = event.getLevel();
        BlockPos clickPos = event.getPos();
        BlockState clickState = level.getBlockState(clickPos);

        if (!clickState.is(TFCTags.Blocks.FRUIT_TREE_LEAVES) && !clickState.is(TFCTags.Blocks.FRUIT_TREE_BRANCH)) {
            return;
        }

        ItemStackHandler basketInv = FruitBasketItem.getItems(handStack);

        BlockPos min = clickPos.offset(-3, -3, -3);
        BlockPos max = clickPos.offset(3, 3, 3);

        boolean hasPick = false;
        RandomSource random = RandomSource.create();

        for (BlockPos blockPos : BlockPos.betweenClosed(min, max)) {
            BlockState blockState = level.getBlockState(blockPos);
            if (
                    blockState.is(TFCTags.Blocks.FRUIT_TREE_LEAVES)
                            && blockState.getValue(TFCBlockStateProperties.LIFECYCLE) != Lifecycle.FRUITING
            ) continue;
            if (!(blockState.getBlock() instanceof FruitTreeLeavesBlock leavesBlock)) continue;

            ItemStack fruitStack = leavesBlock.getProductItem(random);
            if (fruitStack.isEmpty()) continue;

            boolean insertSuccess = false;
            for (int i = 0; i < basketInv.getSlots(); i++) {
                if (basketInv.insertItem(i, fruitStack.copy(), false).isEmpty()) {
                    insertSuccess = true;
                    break;
                }
            }
            if (!insertSuccess) continue;

            hasPick = true;
            level.setBlockAndUpdate(blockPos, blockState.setValue(TFCBlockStateProperties.LIFECYCLE, Lifecycle.HEALTHY));
        }

        if (hasPick) {
            FruitBasketItem.saveItems(handStack, basketInv);
        }
        event.setCancellationResult(ItemInteractionResult.SUCCESS);
        event.setCanceled(true);
    }
}
