package com.tropheus_jay.stonecutter_recipe_tags;

import java.util.List;

import net.minecraft.item.ItemStack;

public interface StonecutterScreenHandlerExtensions {
	boolean tagRecipeMode();

	List<ItemStack> getRecipeStacks();
}
