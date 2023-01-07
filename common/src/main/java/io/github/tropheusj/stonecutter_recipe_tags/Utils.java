package io.github.tropheusj.stonecutter_recipe_tags;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class Utils {
	public static final String ID = "stonecutter_recipe_tags";

	public static TagKey<Item> getItemTag(Identifier id) {
		return TagKey.of(RegistryKeys.ITEM, id);
	}

	public static Identifier asId(String path) {
		return new Identifier(ID, path);
	}
}
