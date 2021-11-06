package io.github.tropheusj.stonecutter_recipe_tags.fabric;

import io.github.tropheusj.stonecutter_recipe_tags.StonecutterRecipeTags;
import net.fabricmc.api.ModInitializer;

public class StonecutterRecipeTagsFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		StonecutterRecipeTags.init();
	}
}
