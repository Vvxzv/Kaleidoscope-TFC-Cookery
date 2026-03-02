package net.vvxzv.ktfcc.mixin.blockentity;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.ShawarmaSpitBlockEntity;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ShawarmaSpitBlockEntity.class)
public class ShawarmaSpitBlockEntityMixin extends BaseBlockEntity {
    @Unique
    private final RecipeManager.CachedCheck<ItemStackInventory, HeatingRecipe> tfcHeatingCheck = RecipeManager.createCheck(TFCRecipeTypes.HEATING.get());

    @Shadow(remap = false)
    public ItemStack cookingItem;

    @Shadow(remap = false)
    public ItemStack cookedItem;

    @Shadow(remap = false)
    public int cookTime;

    public ShawarmaSpitBlockEntityMixin(BlockEntityType<?> entityType, BlockPos pos, BlockState state) {
        super(entityType, pos, state);
    }

    /**
     * @author Vvxzv
     * @reason 替换原生营火配方为 TFC 加热配方
     */
    @Overwrite(remap = false)
    public boolean onPutCookingItem(Level level, ItemStack itemStack) {
        if (!this.cookingItem.isEmpty() || !this.cookedItem.isEmpty()) {
            return false;
        }

        IFood iFood = FoodCapability.get(itemStack);
        if(iFood != null && iFood.isRotten()){
            return false;
        }

        ItemStackInventory inventory = new ItemStackInventory(itemStack);
        return tfcHeatingCheck.getRecipeFor(inventory, level).map(recipe -> {
            if(recipe.getTemperature() > 200) return false;
            this.cookingItem = itemStack.split(8);
            this.cookedItem = recipe.assemble(inventory, level.registryAccess());
            this.cookedItem.setCount(this.cookingItem.getCount());
            this.cookTime = 500;

            this.refresh();

            if (level instanceof ServerLevel) {
                BlockPos pos = this.worldPosition;
                level.playSound(
                        null,
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        SoundEvents.ITEM_FRAME_ADD_ITEM,
                        SoundSource.BLOCKS,
                        0.5F + level.random.nextFloat(),
                        level.random.nextFloat() * 0.7F + 0.6F
                );
            }
            return true;
        }).orElse(false);
    }
}
