package com.tropheus_jay.stonecutter_recipe_tags;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class StonecutterRecipeTags implements ModInitializer {
	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTED.register(server -> handleDataPackReload(server.getResourceManager()));
		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, manager, success) -> handleDataPackReload(manager.getResourceManager()));
	}

	public void handleDataPackReload(ResourceManager resourceManager) {
		StonecutterTagRecipeHandler.ALL_STONECUTTER_TAGS.clear();
		for (Identifier id : resourceManager.findResources("tags/items/stonecutter_recipes", path -> path.endsWith(".json"))) {
			StonecutterTagRecipeHandler.VALID = false;
			StonecutterTagRecipeHandler.TAGS_TO_ADD.add(id);
		}
	}
}
