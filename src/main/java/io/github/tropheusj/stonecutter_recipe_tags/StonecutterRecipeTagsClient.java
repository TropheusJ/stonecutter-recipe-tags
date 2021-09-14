package io.github.tropheusj.stonecutter_recipe_tags;

import net.fabricmc.api.ClientModInitializer;

public class StonecutterRecipeTagsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		StonecutterRecipeTagManager.initClientsideSync();
	}
}
