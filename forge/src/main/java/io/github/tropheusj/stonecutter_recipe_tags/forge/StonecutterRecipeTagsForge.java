package io.github.tropheusj.stonecutter_recipe_tags.forge;

import io.github.tropheusj.stonecutter_recipe_tags.StonecutterRecipeTagManager;
import io.github.tropheusj.stonecutter_recipe_tags.Utils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = StonecutterRecipeTagManager.ID)
@Mod(StonecutterRecipeTagManager.ID)
public class StonecutterRecipeTagsForge {
	public StonecutterRecipeTagsForge() {
		MinecraftForge.EVENT_BUS.register(this);
    }

	@SubscribeEvent
	public static void addReloadListeners(AddReloadListenerEvent event) {
		event.addListener(Utils.getListener());
	}
}
