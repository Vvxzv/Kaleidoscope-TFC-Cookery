package net.vvxzv.ktfcc.compat.jei;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import net.dries007.tfc.compat.jei.JEIIntegration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.vvxzv.ktfcc.KaleidoscopeTFCCookery;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(KaleidoscopeTFCCookery.MODID, "jei_plugin");
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.MILLSTONE.get()), JEIIntegration.QUERN);
    }
}
