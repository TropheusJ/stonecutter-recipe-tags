package io.github.tropheusj.stonecutter_recipe_tags.forge;

import io.github.tropheusj.stonecutter_recipe_tags.Utils;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@Mod(Utils.ID)
@EventBusSubscriber
public class StonecutterRecipeTagsForge {
	public StonecutterRecipeTagsForge() {
    }

	@SubscribeEvent
	public static void reload(AddReloadListenerEvent event) {
		event.addListener(ReloadListener.INSTANCE);
	}
}
