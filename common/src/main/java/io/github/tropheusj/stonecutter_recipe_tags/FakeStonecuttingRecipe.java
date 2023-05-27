package io.github.tropheusj.stonecutter_recipe_tags;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.screen.StonecutterScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

/**
 * A fake {@link StonecuttingRecipe} that checks the stack count.
 */
public class FakeStonecuttingRecipe extends StonecuttingRecipe {
	private static final String FAKE_RECIPE_GROUP = Utils.ID + "_fake_recipes";
	/**
	 * The size of this stack is the amount of input that will be consumed on crafting.
	 */
	public final ItemStack input;

	/**
	 * Creates fake recipe data so the recipe can be shown on screen and has reasonable defaults for mods that interact with it.
	 */
	public FakeStonecuttingRecipe(ItemStack input, ItemStack output) {
		super(makeId(input, output), FAKE_RECIPE_GROUP, Ingredient.ofStacks(input), output);
		this.input = input;
	}

	/**
	 * Checks for {@link ItemStack} size, not just {@link Item}.
	 */
	@Override
	public boolean matches(Inventory inventory, World world) {
		ItemStack actualInput = inventory.getStack(0);
		return actualInput.getItem() == input.getItem()
				&& actualInput.getCount() >= input.getCount();
	}

	/**
	 * Rechecks with {@link FakeStonecuttingRecipe#matches} to prevent any item dupe shenanigans
	 * if {@link StonecutterScreenHandler#populateResult()} isn't called when the stack size changes.
	 */
	@SuppressWarnings("JavadocReference")
	@Override
	public ItemStack craft(Inventory inventory, DynamicRegistryManager manager) {
		if (!this.matches(inventory, null))
			return ItemStack.EMPTY;
		return super.craft(inventory, manager);
	}

	/**
	 * Prevents recipes from being saved in the recipe book since their IDs are bogus.
	 */
	@Override
	public boolean isIgnoredInRecipeBook() {
		return true;
	}

	private static Identifier makeId(ItemStack input, ItemStack output) {
		String inId = Registries.ITEM.getId(input.getItem()).toUnderscoreSeparatedString();
		String outId = Registries.ITEM.getId(output.getItem()).toUnderscoreSeparatedString();
		return Utils.asId("fake_recipe_" + inId + "_to_" + outId);
	}
}
