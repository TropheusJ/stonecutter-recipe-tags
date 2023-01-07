package io.github.tropheusj.stonecutter_recipe_tags.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

	private static Stream<FakeStonecuttingRecipe> generateFakeRecipes(ItemStack inputStack) {
		var inputItem = inputStack.getItem();
		var inputItemCraftCount = StonecutterRecipeTagManager.getItemCraftCount(inputItem);
		return StonecutterRecipeTagManager.getRecipeTags(inputStack)
				.stream()
				.flatMap((key) -> StreamSupport.stream(Registries.ITEM.iterateEntries(key).spliterator(), false))
				.map(RegistryEntry::value)
				.filter(item -> !item.equals(inputItem))
				.map(outputItem -> new FakeStonecuttingRecipe(inputItem, inputItemCraftCount, outputItem));
	}

	@Shadow
	abstract void populateResult();

	/**
	 * Appends {@link FakeStonecuttingRecipe}s to the list of real recipes.
	 * <p>
	 * {@link StonecutterScreenHandlerOutputSlotMixin} tests for these recipe types when crafting.
	 */
	@Redirect(method = "updateInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/recipe/RecipeManager;getAllMatches(Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/world/World;)Ljava/util/List;"))
	private List<StonecuttingRecipe> stonecutterRecipeTags$updateInput(RecipeManager recipeManager, RecipeType<StonecuttingRecipe> type, Inventory inventory, World world, Inventory input, ItemStack inputStack) {
		var toReturn = new ArrayList<>(recipeManager.getAllMatches(type, inventory, world));

		generateFakeRecipes(inputStack)
				.forEachOrdered(toReturn::add);

		return toReturn;
	}

	/**
	 * (Re-)populates the output slot if the input stack size changes.
	 * <p>
	 * This isn't needed in vanilla because all recipes take exactly 1 item so the stack size changing would never change the output.
	 */
	@Inject(method = "onContentChanged", at = @At(value = "TAIL"))
	private void stonecutterRecipeTags$onContentChanged(Inventory inventory, CallbackInfo ci) {
		this.populateResult();
	}

	/**
	 * Includes our fake recipes in the check for if any recipes exist.
	 * <p>
	 * This is used to test if an item should be shift-clicked into the input.
	 */
	@Redirect(method = "quickMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/recipe/RecipeManager;getFirstMatch(Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/world/World;)Ljava/util/Optional;"))
	public Optional<StonecuttingRecipe> stonecutterRecipeTags$transferSlot(RecipeManager recipeManager, RecipeType<StonecuttingRecipe> type, Inventory inventory, World world) {
		return recipeManager.getFirstMatch(type, inventory, world)
				.or(() -> generateFakeRecipes(inventory.getStack(0))
						.filter(recipe -> recipe.matches(inventory, world))
						.findFirst()
				);
	}
}
