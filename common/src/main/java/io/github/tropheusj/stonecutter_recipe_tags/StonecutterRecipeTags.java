package io.github.tropheusj.stonecutter_recipe_tags;

import me.shedaniel.architectury.registry.ReloadListeners;
import net.minecraft.resource.ResourceType;

public class StonecutterRecipeTags {
	public static final String ID = "stonecutter_recipe_tags";

	public static void init() {
		ReloadListeners.registerReloadListener(ResourceType.SERVER_DATA, Utils.getListener());
	}
}
