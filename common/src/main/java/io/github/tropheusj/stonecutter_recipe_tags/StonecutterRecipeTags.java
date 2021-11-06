package io.github.tropheusj.stonecutter_recipe_tags;

import dev.architectury.registry.ReloadListenerRegistry;
import net.minecraft.resource.ResourceType;

public class StonecutterRecipeTags {
	public static final String ID = "stonecutter_recipe_tags";

	public static void init() {
		ReloadListenerRegistry.register(ResourceType.SERVER_DATA, Utils.getListener());
	}
}
