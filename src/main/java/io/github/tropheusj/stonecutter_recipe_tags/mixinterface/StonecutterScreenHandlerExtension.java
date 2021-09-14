package io.github.tropheusj.stonecutter_recipe_tags.mixinterface;

import java.util.List;

import net.minecraft.item.ItemStack;

public interface StonecutterScreenHandlerExtension {
	List<ItemStack> getStacksToDisplay();
}
