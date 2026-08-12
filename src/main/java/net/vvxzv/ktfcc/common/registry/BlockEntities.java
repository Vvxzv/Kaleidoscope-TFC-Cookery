package net.vvxzv.ktfcc.common.registry;

import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.PlateBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.vvxzv.ktfcc.KaleidoscopeTFCCookery;
import net.vvxzv.ktfcc.common.block.AbstractBushBlock;
import net.vvxzv.ktfcc.common.block.entity.BushBlockEntity;
import net.vvxzv.ktfcc.common.block.entity.DecayingFoodBlockEntity;
import net.vvxzv.ktfcc.common.block.entity.OilPotBlockEntity;
import net.vvxzv.ktfcc.common.block.entity.StoveBlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class BlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, KaleidoscopeTFCCookery.MODID);

    public static final Supplier<BlockEntityType<StoveBlockEntity>> STOVE = BLOCK_ENTITIES.register(
            "stove",
            () -> BlockEntityType.Builder.of(
                    StoveBlockEntity::new,
                    ModBlocks.STOVE.get()
            ).build(null)
    );

    private static Block[] getDecayingFoodBlocks() {
        Stream<Block> foodBlocks = BuiltInRegistries.BLOCK.stream().filter(block -> block instanceof FoodBlock);
        Stream<Block> plateBlocks = BuiltInRegistries.BLOCK.stream().filter(block -> block instanceof PlateBlock);
        List<Block> blocks = new ArrayList<>(Stream.concat(foodBlocks, plateBlocks).toList());
        blocks.add(ModBlocks.BAMBOO_TUBE_RICE.get());
        return blocks.toArray(Block[]::new);
    }

    public static final Supplier<BlockEntityType<DecayingFoodBlockEntity>> DECAYING = BLOCK_ENTITIES.register(
            "decaying",
            () -> BlockEntityType.Builder.of(
                    DecayingFoodBlockEntity::new,
                    getDecayingFoodBlocks()
            ).build(null)
    );

    public static final Supplier<BlockEntityType<BushBlockEntity>> BUSH  = BLOCK_ENTITIES.register(
            "bush",
            () -> BlockEntityType.Builder.of(
                    BushBlockEntity::new,
                    BuiltInRegistries.BLOCK.stream().filter(block -> block instanceof AbstractBushBlock).toArray(Block[]::new)
            ).build(null)
    );

    public static final Supplier<BlockEntityType<OilPotBlockEntity>> OIL_POT = BLOCK_ENTITIES.register(
            "oil_pot",
            () -> BlockEntityType.Builder.of(
                    OilPotBlockEntity::new,
                    Blocks.OIL_POT.get()
            ).build(null)
    );
}
