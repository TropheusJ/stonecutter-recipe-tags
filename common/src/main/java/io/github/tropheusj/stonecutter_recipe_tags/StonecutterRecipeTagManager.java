package io.github.tropheusj.stonecutter_recipe_tags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.shedaniel.architectury.networking.NetworkChannel;
import me.shedaniel.architectury.networking.NetworkManager;
import me.shedaniel.architectury.networking.simple.SimpleNetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class StonecutterRecipeTagManager {
	public static final Identifier SYNC_STONECUTTER_RECIPE_TAGS_PACKET_ID = Utils.asId("sync_stonecutter_recipe_tags");

	private static final Map<Identifier, Tag.Identified<Item>> STONECUTTER_TAG_MAP = new HashMap<>();
	private static final Map<Item, Integer> ITEM_COUNT_MAP = new HashMap<>();

	static {
		registerItemCraftCount(Items.SNOW, 8);
	}

	/**
	 * Registers or gets an already registered tag for the provided id.<br>
	 * Registered tags will be used as stonecutter recipes.<br>
	 * Tags registered manually will not persist through resource reloads.
	 * @param id The id of the tag
	 * @return The registered tag
	 */
	public static Tag.Identified<Item> registerOrGet(Identifier id) {
		Tag.Identified<Item> tag = getRegisteredTag(id);
		if (tag == null) {
			tag = Utils.getItemTag(id);
			register(tag);
		}
		return tag;
	}

	/**
	 * Registers an already existing tag if a tag with the same id has not already been registered.<br>
	 * Registered tags will be used as stonecutter recipes.<br>
	 * Tags registered manually will not persist through resource reloads.
	 * @param tag The tag to register
	 */
	public static void register(Tag.Identified<Item> tag) {
		STONECUTTER_TAG_MAP.putIfAbsent(tag.getId(), tag);
	}

	/**
	 * Gets a tag from an id that was previously used for registration.
	 * @param id The id of the registered tag
	 * @return The registered tag, or null if no tag for the specified id exists
	 */
	@Nullable
	public static Tag.Identified<Item> getRegisteredTag(Identifier id) {
		return STONECUTTER_TAG_MAP.get(id);
	}

	/**
	 * Gets all stonecutter recipe tags the given item is in.
	 * @param item The item to check
	 * @return All stonecutter recipe tags the item is in
	 */
	public static List<Tag.Identified<Item>> getRecipeTags(Item item) {
		List<Tag.Identified<Item>> tags = new ArrayList<>();
		for (Tag.Identified<Item> tag : STONECUTTER_TAG_MAP.values()) {
			if (tag.contains(item)) {
				tags.add(tag);
			}
		}
		return tags;
	}

	/**
	 * @see StonecutterRecipeTagManager#getItemCraftCount(Item)
	 */
	public static List<Tag.Identified<Item>> getRecipeTags(ItemStack stack) {
		return getRecipeTags(stack.getItem());
	}

	/**
	 * Register a craft count for an item.<br>
	 * This should be used when an item requires a custom craft count.
	 * By default, all items have a count of one, except slabs, which have a count of two.<br>
	 * Example use case: quarter slabs
	 * @param item Item to register a count for
	 * @param count The count for this item
	 */
	public static void registerItemCraftCount(Item item, int count) {
		ITEM_COUNT_MAP.put(item, count);
	}

	/**
	 * Get the required amount of items needed to craft this item.<br>
	 * Amount is retrieved from {@link StonecutterRecipeTagManager#ITEM_COUNT_MAP}.
	 * If no value is found, slabs return 2, while all other items return 1.
	 * @param item The item to check
	 * @return The amount needed
	 * @see StonecutterRecipeTagManager#registerItemCraftCount(Item, int)
	 */
	public static int getItemCraftCount(Item item) {
		Integer count = ITEM_COUNT_MAP.get(item);
		if (count != null) return count;
		if (item instanceof BlockItem) {
			BlockItem blockItem = (BlockItem)item;
			Block block = blockItem.getBlock();
			if (block instanceof SlabBlock || BlockTags.SLABS.contains(block)) {
				return 2;
			}
		}
		return 1;
	}

	/**
	 * @see StonecutterRecipeTagManager#getItemCraftCount(Item)
	 */
	public static int getItemCraftCount(ItemStack stack) {
		return getItemCraftCount(stack.getItem());
	}

	public static void clearTags() {
		STONECUTTER_TAG_MAP.clear();
	}

	public static void toPacketBuf(PacketByteBuf buf) {
		buf.writeVarInt(STONECUTTER_TAG_MAP.keySet().size());
		for (Identifier id : STONECUTTER_TAG_MAP.keySet()) {
			buf.writeIdentifier(id);
		}
	}

	public static void fromPacketBuf(PacketByteBuf buf) {
		clearTags();
		System.out.println("Cleared Tags");
		for (int i = 0; i < buf.readVarInt(); i++) {
			Identifier id = buf.readIdentifier();
			registerOrGet(id);
		}
		System.out.println("Registered and got");
	}

	/**
	 * Creates an S2C packet that can be sent to clients to sync their stonecutter recipe tags.
	 * @return The sync packet
	 */
	public static Packet<?> toSyncPacket() {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		toPacketBuf(buf);
		return NetworkManager.toPacket(NetworkManager.s2c(), SYNC_STONECUTTER_RECIPE_TAGS_PACKET_ID, buf);
	}

	@Environment(EnvType.CLIENT)
	static void initClientsideSync() {
		// this only works on fabric, forge handles it via mixin
		NetworkManager.registerReceiver(NetworkManager.s2c(), SYNC_STONECUTTER_RECIPE_TAGS_PACKET_ID, (buf, ctx) -> fromPacketBuf(buf));
	}
}
