package io.github.tropheusj.stonecutter_recipe_tags.fabric;

import io.github.tropheusj.stonecutter_recipe_tags.StonecutterRecipeTagsClient;
import net.fabricmc.api.ClientModInitializer;

public class StonecutterRecipeTagsClientFabric implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		StonecutterRecipeTagsClient.init();
	}
}
