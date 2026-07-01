package net.vvxzv.ktfcc.compat.jade;

import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.PlateBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.StackableFoodBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.StoveBlock;
import net.vvxzv.ktfcc.common.block.MugwortBlock;
import net.vvxzv.ktfcc.common.block.TeaTreeBlock;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadeIntegration implements IWailaPlugin {

    @Override
    public void registerClient(IWailaClientRegistration reg) {
        reg.registerBlockComponent(DecayingBlockComponentProvider.INSTANCE, FoodBiteBlock.class);
        reg.registerBlockComponent(DecayingBlockComponentProvider.INSTANCE, PlateBlock.class);
        reg.registerBlockComponent(DecayingBlockComponentProvider.INSTANCE, StackableFoodBlock.class);
        reg.registerBlockComponent(StoveComponentProvider.INSTANCE, StoveBlock.class);
        reg.registerBlockComponent(TeaTreeBlockComponentProvider.INSTANCE, TeaTreeBlock.class);
        reg.registerBlockComponent(MugwortBlockComponentProvider.INSTANCE, MugwortBlock.class);
    }
}
