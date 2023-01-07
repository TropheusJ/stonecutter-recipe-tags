package io.github.tropheusj.stonecutter_recipe_tags.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.github.tropheusj.stonecutter_recipe_tags.FakeStonecuttingRecipe;
import io.github.tropheusj.stonecutter_recipe_tags.StonecutterRecipeTagManager;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.StonecutterScreenHandler;
import net.minecraft.world.World;

@Mixin(StonecutterScreenHandler.class)
public abstract class StonecutterScreenHandlerMixin extends ScreenHandler {
	protected StonecutterScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId) {
		super(type, syncId);
	}

	private static Stream<FakeStonecuttingRecipe> stonecutterRecipeTags$generateFakeRecipes(ItemStack inputStack) {
		Item inputItem = inputStack.getItem();
		int inputItemCraftCount = StonecutterRecipeTagManager.getItemCraftCount(inputItem);
		return StonecutterRecipeTagManager.getRecipeTags(inputStack)
				.stream()
				.flatMap((key) -> StreamSupport.stream(Registries.ITEM.iterateEntries(key).spliterator(), false))
				.map(RegistryEntry::value)
				.filter(item -> !item.equals(inputItem))
				.map(outputItem -> new FakeStonecuttingRecipe(inputItem, inputItemCraftCount, outputItem));
	}

	@Shadow
	private ItemStack inputStack;

	@Shadow
	@Final
	private World world;

	/**
	 * Appends {@link FakeStonecuttingRecipe}s to the list of real recipes.
	 * <p>
	 * {@link StonecutterScreenHandlerOutputSlotMixin} tests for these recipe types when crafting.
	 */
	@ModifyExpressionValue(
			method = "updateInput",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/recipe/RecipeManager;getAllMatches(Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/world/World;)Ljava/util/List;"
			)
	)
	private List<StonecuttingRecipe> stonecutterRecipeTags$updateInput(List<StonecuttingRecipe> recipes) {
		recipes = new ArrayList<>(recipes);

		stonecutterRecipeTags$generateFakeRecipes(this.inputStack)
				.forEachOrdered(recipes::add);

		int available = this.inputStack.getCount();
		recipes.removeIf(r -> r instanceof FakeStonecuttingRecipe fake && available < fake.inputItemCraftCount);

		return recipes;
	}

	/**
	 * Always reset no matter how the item changes, because amounts are important now
	 */
	@ModifyExpressionValue(
			method = "onContentChanged",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"
			)
	)
	private boolean stonecutterRecipeTags$onContentChanged(boolean sameItem) {
		return false; // it's inverted after
	}

	/**
	 * Includes our fake recipes in the check for if any recipes exist.
	 * <p>
	 * This is used to test if an item should be shift-clicked into the input.
	 */
	@WrapOperation(
			method = "quickMove",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/recipe/RecipeManager;getFirstMatch(Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/world/World;)Ljava/util/Optional;"
			)
	)
	private Optional<StonecuttingRecipe> stonecutterRecipeTags$transferSlot(
			RecipeManager manager, RecipeType<StonecuttingRecipe> type, Inventory inventory, World world,
			Operation<Optional<StonecuttingRecipe>> original) {
		Optional<StonecuttingRecipe> recipe = original.call(manager, type, inventory, world);
		return recipe.or(() -> stonecutterRecipeTags$generateFakeRecipes(inventory.getStack(0))
						.filter(r -> r.matches(inventory, world))
						.findFirst()
		);
	}
}
