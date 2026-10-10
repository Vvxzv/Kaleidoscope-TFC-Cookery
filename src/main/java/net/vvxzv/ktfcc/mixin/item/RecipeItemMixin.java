package net.vvxzv.ktfcc.mixin.item;

import com.github.ysbbbbbb.kaleidoscopecookery.api.event.RecipeItemEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.PotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.SimpleInput;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.StockpotInput;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.FlexPotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.FlexStockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.PotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModDataComponents;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopecookery.item.RecipeItem;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.IFood;
import net.dries007.tfc.common.items.TFCItems;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.PlayerMainInvWrapper;
import net.vvxzv.ktfcc.Config;
import net.vvxzv.ktfcc.common.registry.Items;
import net.vvxzv.ktfcc.common.utils.AllTags;
import net.vvxzv.ktfcc.common.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(RecipeItem.class)
public class RecipeItemMixin {

    @Unique
    private static Constructor<?> RECIPE_RESULT_CONSTRUCTOR;

    static {
        try {
            Class<?> recipeResultClass = Class.forName("com.github.ysbbbbbb.kaleidoscopecookery.item.RecipeItem$RecipeResult");
            RECIPE_RESULT_CONSTRUCTOR = recipeResultClass.getDeclaredConstructor(ItemStack.class, boolean.class);
            RECIPE_RESULT_CONSTRUCTOR.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Unique
    private Object createRecipeResult(ItemStack output) {
        try {
            return RECIPE_RESULT_CONSTRUCTOR.newInstance(output, false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Unique
    private float getMaxDynamicNutrient() {
        return (float) Config.maxDynamicNutrient;
    }

    @Inject(method = "getPotRecipeResult", at = @At("HEAD"), cancellable = true)
    private void getPotRecipeResult(Level level, RecipeManager recipeManager, PotBlockEntity pot, List<ItemStack> inputs, ItemStack recordStack, CallbackInfoReturnable<Object> cir) {
        SimpleInput container = pot.getContainer();
        Optional<RecipeHolder<PotRecipe>> potRecipe = recipeManager.getRecipeFor(ModRecipes.POT_RECIPE, container, level);
        Optional<RecipeHolder<FlexPotRecipe>> flexPotRecipe = recipeManager.getRecipeFor(ModRecipes.FLEX_POT_RECIPE, container, level);

        if(potRecipe.isEmpty() && flexPotRecipe.isEmpty() && Utils.isValidItems(inputs, AllTags.Items.POT_INGREDIENT)) {
            float[] nutrients = Utils.getFinalNutrients(inputs, 0.8f, this.getMaxDynamicNutrient());
            ItemStack stack = new ItemStack(Items.DISHES.get(Utils.matchMainNutrient(nutrients)).get());
            cir.setReturnValue(this.createRecipeResult(stack));
            cir.cancel();
        }

    }

    @Inject(method = "getStockpotRecipeResult", at = @At("HEAD"), cancellable = true)
    private void getStockpotRecipeResult(Level level, RecipeManager recipeManager, StockpotBlockEntity stockpot, List<ItemStack> inputs, ItemStack recordStack, CallbackInfoReturnable<Object> cir) {
        StockpotInput container = stockpot.getInput();
        Optional<RecipeHolder<StockpotRecipe>> stockpotRecipe = recipeManager.getRecipeFor(ModRecipes.STOCKPOT_RECIPE, container, level);
        Optional<RecipeHolder<FlexStockpotRecipe>> flexStockpotRecipe = recipeManager.getRecipeFor(ModRecipes.FLEX_STOCKPOT_RECIPE, container, level);

        if(stockpotRecipe.isEmpty() && flexStockpotRecipe.isEmpty() && Utils.isValidItems(inputs, AllTags.Items.STOCKPOT_INGREDIENT)) {
            float[] nutrients = Utils.getFinalNutrients(inputs, 0.85f, this.getMaxDynamicNutrient());
            ItemStack stack = new ItemStack(TFCItems.SOUPS.get(Utils.matchMainNutrient(nutrients)).get());
            cir.setReturnValue(this.createRecipeResult(stack));
            cir.cancel();
        }
    }

    @Inject(method = "useOn", at = @At("HEAD"))
    private void useOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemInHand = context.getItemInHand();
        Player player = context.getPlayer();
        if(RecipeItem.hasRecipe(itemInHand) && player != null) {
            RecipeItem.RecipeRecord record = RecipeItem.getRecipe(itemInHand);
            if(record != null) {
                this.refreshRecord(record, itemInHand);
            }
        }
    }

    @Unique
    private void refreshRecord(@NotNull RecipeItem.RecipeRecord record, @NotNull ItemStack recordItem) {
        List<ItemStack> list = new ArrayList<>();
        for (ItemStack s : record.input()) {
            if (!s.isEmpty()) {
                list.add(new ItemStack(s.getItem()));
            }
        }
        ItemStack output = new ItemStack(record.output().getItem());
        RecipeItem.RecipeRecord refreshedRecord = new RecipeItem.RecipeRecord(list, output, record.type(), record.flexRecipe());
        recordItem.set(ModDataComponents.RECIPE_RECORD, refreshedRecord);
    }

    @Inject(method = "handlePutRecipe", at = @At("HEAD"), cancellable = true)
    private void handlePutRecipe(Player player, RecipeItem.RecipeRecord _record, Runnable success, CallbackInfoReturnable<InteractionResult> cir) {
        Reference2IntMap<Item> need = new Reference2IntOpenHashMap<>();

        for(ItemStack s : _record.input()) {
            if (!s.isEmpty()) {
                Item item = s.getItem();
                need.put(item, need.getInt(item) + 1);
            }
        }

        IItemHandler inventory = new PlayerMainInvWrapper(player.getInventory());
        Reference2IntMap<Item> supply = new Reference2IntOpenHashMap<>();

        for(int slot = 0; slot < inventory.getSlots(); ++slot) {
            ItemStack s = inventory.getStackInSlot(slot);
            IFood food = FoodCapability.get(s);
            if(food != null && food.isRotten()) {
                continue;
            }
            if (!s.isEmpty()) {
                RecipeItemEvent.CheckItem event = new RecipeItemEvent.CheckItem(s, supply);
                NeoForge.EVENT_BUS.post(event);
                Item item = s.getItem();
                supply.put(item, supply.getInt(item) + s.getCount());
            }
        }

        Reference2IntMap<Item> missing = new Reference2IntOpenHashMap<>();

        for (Item item : need.keySet()) {
            if (supply.getInt(item) < need.getInt(item)) {
                missing.put(item, need.getInt(item) - supply.getInt(item));
            }
        }

        if (!missing.isEmpty()) {
            MutableComponent text = Component.translatable("tooltip.kaleidoscope_cookery.recipe_item.missing");
            int i = 0;

            for(ObjectIterator<Item> var26 = missing.keySet().iterator(); var26.hasNext(); ++i) {
                Item s = var26.next();
                Component hoverName = s.getDefaultInstance().getHoverName();
                MutableComponent count = Component.literal("×%d".formatted(missing.getInt(s)));
                if (i != 0) {
                    text = text.append(CommonComponents.SPACE);
                }

                text.append(CommonComponents.SPACE).append(hoverName).append(count);
            }

            if (!player.level().isClientSide()) {
                player.sendSystemMessage(text);
            }

            cir.setReturnValue(InteractionResult.FAIL);
            cir.cancel();
        } else {
            for (Item item : need.keySet()) {
                int needCount = need.getInt(item);

                for (int i = 0; i < inventory.getSlots(); ++i) {
                    ItemStack inSlot = inventory.getStackInSlot(i);
                    if (!inSlot.isEmpty()) {
                        RecipeItemEvent.DeductItem event = new RecipeItemEvent.DeductItem(inSlot, item, new int[]{needCount});
                        NeoForge.EVENT_BUS.post(event);
                        needCount = event.getNeedCount();
                        if (needCount <= 0) {
                            break;
                        }

                        if (inSlot.is(item)) {
                            int extracted = Math.min(needCount, inSlot.getCount());
                            inventory.extractItem(i, extracted, false);
                            needCount -= extracted;
                            if (needCount <= 0) {
                                break;
                            }
                        }
                    }
                }
            }

            success.run();
            cir.setReturnValue(InteractionResult.SUCCESS);
            cir.cancel();
        }
    }
}
