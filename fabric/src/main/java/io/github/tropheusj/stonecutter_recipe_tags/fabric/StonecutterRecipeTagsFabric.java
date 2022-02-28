package io.github.tropheusj.stonecutter_recipe_tags.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

public class StonecutterRecipeTagsFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(ReloadListener.INSTANCE);
	}
}
