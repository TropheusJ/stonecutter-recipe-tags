package io.github.tropheusj.stonecutter_recipe_tags;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.tag.TagManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

import java.util.Map;

public class StonecutterRecipeTags implements ModInitializer {
	public static final Identifier FORCE_RELOAD_PACKET = new Identifier("stonecutter_recipe_tags", "force_reload");
	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTED.register(server -> handleDataPackReload(server.getResourceManager()));
		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, manager, success) -> handleDataPackReload(manager.getResourceManager()));
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			PacketByteBuf idBuf = PacketByteBufs.create();
			StonecutterRecipeTagHandler.ALL_STONECUTTER_TAG_IDS.forEach(idBuf::writeIdentifier);
			sender.sendPacket(FORCE_RELOAD_PACKET, idBuf);
		});
	}

	public static void handleDataPackReload(ResourceManager resourceManager) {
		for (Identifier id : resourceManager.findResources("tags/items/stonecutter_recipes", path -> path.endsWith(".json"))) {
			if (id.getNamespace().equals("stonecutter_recipe_tags")) continue; // comment this to debug
			StonecutterRecipeTagHandler.VALID = false;
			StonecutterRecipeTagHandler.TAGS_TO_ADD.add(id);
		}
	}
}
