package io.github.tropheusj.stonecutter_recipe_tags;

import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Utils {
	public static final String ID = "stonecutter_recipe_tags";

	public static TagKey<Item> getItemTag(Identifier id) {
		return TagKey.of(Registry.ITEM_KEY, id);
	}

	public static Identifier asId(String path) {
		return new Identifier(ID, path);
	}
}
