package io.github.tropheusj.stonecutter_recipe_tags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class StonecutterRecipeTagHandler {
	public static final Set<Tag<Item>> ALL_STONECUTTER_TAGS = new HashSet<>();
	public static final Set<Identifier> ALL_STONECUTTER_TAG_IDS = new HashSet<>();
	private static final Map<Item, Integer> COUNT_CACHE = new HashMap<>();
	static boolean VALID = true;
	static Set<Identifier> TAGS_TO_ADD = new HashSet<>();

	static {
		registerItemCraftCount(Items.SNOW, 8);
	}

	/**
	 * Tags fed into this method will be used as recipes.<br>
	 * Tags registered manually will need to be re-registered on every resource reload.
	 * @param id The tag to register
	 * @return The registered tag
	 */
	public static Tag<Item> register(Identifier id) {
		Identifier newID = new Identifier(id.getNamespace(), id.getPath().replace(".json", "").replace("tags/items/", ""));
		Tag<Item> tag = TagFactory.ITEM.create(newID);
//		ServerTagManagerHolder.getTagManager().getTag(Registry.ITEM_KEY, newID, identifier -> new RuntimeException("Identifier not a recognized tag: " + identifier));
		ALL_STONECUTTER_TAGS.add(tag);
		ALL_STONECUTTER_TAG_IDS.add(id);
		return tag;
	}

	/**
	 * Gets all stonecutter recipe tags the given item is in.
	 * @param item The item to check
	 * @return All stonecutter recipe tags the item is in
	 */
	public static List<Tag<Item>> getRecipeTags(Item item) {
		List<Tag<Item>> tags = new ArrayList<>();
		if (!VALID) registerTags();
		for (Tag<Item> tag : ALL_STONECUTTER_TAGS) {
			if (tag.contains(item)) {
				tags.add(tag);
			}
		}
		return tags;
	}

	/**
	 * @see StonecutterRecipeTagHandler#getItemCraftCount(Item)
	 */
	public static List<Tag<Item>> getRecipeTags(ItemStack stack) {
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
		COUNT_CACHE.put(item, count);
	}

	/**
	 * Get the required amount of items needed to craft this item.<br>
	 * Amount is retrieved from {@link StonecutterRecipeTagHandler#COUNT_CACHE}.
	 * If no value is found, slabs return 2, while all other items return 1.
	 * @param item The item to check
	 * @return The amount needed
	 * @see StonecutterRecipeTagHandler#registerItemCraftCount(Item, int)
	 */
	public static int getItemCraftCount(Item item) {
		Integer count = COUNT_CACHE.get(item);
		if (count != null) return count;
		if (item instanceof BlockItem blockItem) {
			Block block = blockItem.getBlock();
			if (block instanceof SlabBlock || BlockTags.SLABS.contains(block)) {
				return 2;
			}
		}
		return 1;
	}

	/**
	 * @see StonecutterRecipeTagHandler#getItemCraftCount(Item)
	 */
	public static int getItemCraftCount(ItemStack stack) {
		return getItemCraftCount(stack.getItem());
	}

	/**
	 * Registers tags after a resource reload.
	 */
	private static void registerTags() {
		for (Identifier id : TAGS_TO_ADD) {
			register(id);
		}
		TAGS_TO_ADD.clear();
		VALID = true;
	}


	public static void test() {

	}
}
