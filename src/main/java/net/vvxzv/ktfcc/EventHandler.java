package net.vvxzv.ktfcc;

import net.dries007.tfc.util.events.StartFireEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.vvxzv.ktfcc.common.registry.KBlocks;

public class EventHandler {

    public static void init() {
        IEventBus bus = MinecraftForge.EVENT_BUS;
        bus.addListener(EventHandler::onFireStart);
    }

    public static void onFireStart(StartFireEvent event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = event.getState();
        Block block = state.getBlock();
        if (block == KBlocks.STOVE.get() && !state.getValue(BlockStateProperties.LIT)) {
            level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.LIT, true));
            event.setCanceled(true);
        }
    }
}
