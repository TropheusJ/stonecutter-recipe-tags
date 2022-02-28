package io.github.tropheusj.stonecutter_recipe_tags.fabric;

import io.github.tropheusj.stonecutter_recipe_tags.StonecutterRecipeTagManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class StonecutterRecipeTagsClientFabric implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(
				StonecutterRecipeTagManager.SYNC_STONECUTTER_RECIPE_TAGS_PACKET_ID,
				(client, handler, buf, sender) -> StonecutterRecipeTagManager.fromPacketBuf(buf)
		);
	}
}
