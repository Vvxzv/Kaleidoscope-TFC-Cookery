package net.vvxzv.ktfcc.common.registry;

import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.vvxzv.ktfcc.KaleidoscopeTFCCookery;
import net.vvxzv.ktfcc.common.block.Tea;
import net.vvxzv.ktfcc.common.block.entity.DecayingFoodBlockEntity;
import net.vvxzv.ktfcc.common.block.entity.StoveBlockEntity;
import net.vvxzv.ktfcc.common.block.entity.TeaTreeBlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class BlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, KaleidoscopeTFCCookery.MODID);

    public static final RegistryObject<BlockEntityType<StoveBlockEntity>> STOVE = BLOCK_ENTITIES.register(
            "stove",
            () -> BlockEntityType.Builder.of(
                    StoveBlockEntity::new,
                    ModBlocks.STOVE.get()
            ).build(null)
    );

    public static final RegistryObject<BlockEntityType<DecayingFoodBlockEntity>> DECAYING = BLOCK_ENTITIES.register(
            "decaying",
            () -> BlockEntityType.Builder.of(
                    DecayingFoodBlockEntity::new,
                    BuiltInRegistries.BLOCK.stream()
                            .filter(block -> block instanceof FoodBlock)
                            .toArray(Block[]::new)
            ).build(null)
    );

    public static final RegistryObject<BlockEntityType<TeaTreeBlockEntity>> TEA_TREE  = BLOCK_ENTITIES.register(
            "tea_tree",
            () -> BlockEntityType.Builder.of(
                    TeaTreeBlockEntity::new,
                    Blocks.TEA_TREES.values().stream().map(Supplier::get).toArray(Block[]::new)
            ).build(null)
    );
}
