package io.github.tropheusj.stonecutter_recipe_tags;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
public class Utils {
	@ExpectPlatform
	public static Tag.Identified<Item> getItemTag(Identifier id) {
		throw new RuntimeException("Architectury failed");
	}

	@ExpectPlatform
	public static ResourceReloader getListener() {
		throw new RuntimeException("Architectury failed");
	}

	@ExpectPlatform
	public static Packet<?> createPacket(Identifier id, PacketByteBuf buf) {
		throw new RuntimeException("Architectury failed");
	}

	public static Identifier asId(String path) {
		return new Identifier(StonecutterRecipeTagManager.ID, path);
	}
}
