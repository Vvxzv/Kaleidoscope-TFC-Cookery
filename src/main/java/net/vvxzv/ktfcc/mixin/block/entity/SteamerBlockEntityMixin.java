package net.vvxzv.ktfcc.mixin.block.entity;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.SteamerBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.SteamerRecipe;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.IFood;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.vvxzv.ktfcc.common.utils.AllTags;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SteamerBlockEntity.class)
public class SteamerBlockEntityMixin extends BaseBlockEntity {

    @Final
    @Shadow
    private RecipeManager.CachedCheck<SingleRecipeInput, SteamerRecipe> quickCheck;

    public SteamerBlockEntityMixin(BlockEntityType<?> entityType, BlockPos pos, BlockState state) {
        super(entityType, pos, state);
    }

    @Inject(method = "placeFood", at = @At("HEAD"), cancellable = true)
    private void placeFood(Level level, LivingEntity user, ItemStack food, CallbackInfoReturnable<Boolean> cir) {
        IFood iFood = FoodCapability.get(food);
        if(iFood != null && iFood.isRotten()){
            cir.setReturnValue(false);
        }
    }

    @Redirect(method = "cookingTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/NonNullList;set(ILjava/lang/Object;)Ljava/lang/Object;"))
    private Object cookingTick(NonNullList<ItemStack> instance, int index, Object value, Level level) {
        ItemStack stack = instance.get(index);
        SingleRecipeInput container = new SingleRecipeInput(stack);
        ItemStack resultStack = quickCheck.getRecipeFor(container, level).map((r) -> r.value().assemble(container, level.registryAccess())).orElse(stack);
        return instance.set(index, FoodCapability.updateFoodFromPrevious(stack, resultStack));
    }

    @Inject(method = "hasHeatSource", at = @At("RETURN"), cancellable = true)
    private void heatSource(Level level, CallbackInfoReturnable<Boolean> cir){
        BlockState belowState = level.getBlockState(this.worldPosition.below());
        if(belowState.hasProperty(BlockStateProperties.LIT) && belowState.getValue(BlockStateProperties.LIT)){
            cir.setReturnValue(true);
            return;
        }
        cir.setReturnValue(belowState.is(AllTags.Blocks.HEAT_SOURCE));
    }
}
