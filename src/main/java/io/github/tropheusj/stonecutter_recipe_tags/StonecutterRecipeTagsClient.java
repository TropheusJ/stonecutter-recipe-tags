package io.github.tropheusj.stonecutter_recipe_tags;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import static io.github.tropheusj.stonecutter_recipe_tags.StonecutterRecipeTags.FORCE_RELOAD_PACKET;

public class StonecutterRecipeTagsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(FORCE_RELOAD_PACKET, (client, handler, buf, responseSender) -> handleBuf(buf));
	}

	public static void handleBuf(PacketByteBuf buf) {
		while (buf.isReadable()) {
			Identifier id = buf.readIdentifier();
			StonecutterRecipeTagHandler.register(id);
		}
	}
}
