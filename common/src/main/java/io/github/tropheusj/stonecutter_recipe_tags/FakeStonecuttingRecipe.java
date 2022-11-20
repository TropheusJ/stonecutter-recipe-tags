package io.github.tropheusj.stonecutter_recipe_tags;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.screen.StonecutterScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

/**
 * A fake {@link StonecuttingRecipe} that checks the stack count.
 */
public class FakeStonecuttingRecipe extends StonecuttingRecipe {
	private static final String FAKE_RECIPE_GROUP = Utils.ID + "_fake_recipes";
	public final int inputItemCraftCount;
	private final Item inputItem;

	public FakeStonecuttingRecipe(Item inputItem, int inputItemCraftCount, Item outputItem) {
		super(
				new Identifier(Utils.ID,
						"fake_recipe_" + Registry.ITEM.getId(inputItem).toUnderscoreSeparatedString()
								+ "_to_" + Registry.ITEM.getId(outputItem).toUnderscoreSeparatedString()),
				FAKE_RECIPE_GROUP,
				Ingredient.ofItems(inputItem),
				new ItemStack(outputItem, StonecutterRecipeTagManager.getItemCraftCount(outputItem))
		);
		this.inputItem = inputItem;
		this.inputItemCraftCount = inputItemCraftCount;
	}

	/**
	 * Checks for {@link ItemStack} size, not just {@link Item}.
	 */
	@Override
	public boolean matches(Inventory inventory, World world) {
		var inputStack = inventory.getStack(0);
		return inputStack.getItem().equals(inputItem) && inputStack.getCount() >= inputItemCraftCount;
	}

	/**
	 * Recheck to prevent any item dupe shenanigans if {@link StonecutterScreenHandler#populateResult()} isn't called when the stack size changes.
	 */
	@Override
	public ItemStack craft(Inventory inventory) {
		if (this.matches(inventory, null))
			return super.craft(inventory);
		else return ItemStack.EMPTY;
	}

	/**
	 * Prevents recipes from being saved in the recipe book since their IDs are bogus.
	 */
	@Override
	public boolean isIgnoredInRecipeBook() {
		return true;
	}
}
