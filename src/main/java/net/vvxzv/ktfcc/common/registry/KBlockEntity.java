package net.vvxzv.ktfcc.common.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.vvxzv.ktfcc.KTFCC;
import net.vvxzv.ktfcc.common.block.decay.DecayingFoodBiteBlock;
import net.vvxzv.ktfcc.common.block.decay.DecayingFoodBiteThreeByThreeBlock;
import net.vvxzv.ktfcc.common.blockentity.DecayingFoodBlockEntity;
import net.vvxzv.ktfcc.common.blockentity.DecayingThreeFoodBlockEntity;
import net.vvxzv.ktfcc.common.blockentity.StoveBlockEntity;

public class KBlockEntity {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, KTFCC.MODID);

    public static final RegistryObject<BlockEntityType<StoveBlockEntity>> STOVE = BLOCK_ENTITIES.register(
            "stove",
            () -> BlockEntityType.Builder.of(
                    StoveBlockEntity::new,
                    KBlocks.STOVE.get()
            ).build(null)
    );

    public static final RegistryObject<BlockEntityType<DecayingFoodBlockEntity>> DECAYING = BLOCK_ENTITIES.register(
            "decaying",
            () -> BlockEntityType.Builder.of(
                    DecayingFoodBlockEntity::new,
                    BuiltInRegistries.BLOCK.stream()
                            .filter(block ->
                                    block instanceof DecayingFoodBiteBlock &&
                                            !(block instanceof DecayingFoodBiteThreeByThreeBlock)
                            )
                            .toArray(Block[]::new)
            ).build(null)
    );

    public static final RegistryObject<BlockEntityType<DecayingThreeFoodBlockEntity>> THREE_DECAYING = BLOCK_ENTITIES.register(
            "three_decaying",
            () -> BlockEntityType.Builder.of(
                    DecayingThreeFoodBlockEntity::new,
                    BuiltInRegistries.BLOCK.stream()
                            .filter(block -> block instanceof DecayingFoodBiteThreeByThreeBlock)
                            .toArray(Block[]::new)
            ).build(null)
    );
}
