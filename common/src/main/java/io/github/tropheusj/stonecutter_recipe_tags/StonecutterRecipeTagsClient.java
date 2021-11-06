package io.github.tropheusj.stonecutter_recipe_tags;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class StonecutterRecipeTagsClient {
	public static void init() {
		StonecutterRecipeTagManager.initClientsideSync();
	}
}
