package io.github.tropheusj.stonecutter_recipe_tags.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.item.Item;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

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

	@Shadow
	private ItemStack inputStack;

	@Shadow
	@Final
	private World world;

	@ModifyExpressionValue(
			method = "updateInput",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/recipe/RecipeManager;getAllMatches(Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/world/World;)Ljava/util/List;"
			)
	)
	private List<StonecuttingRecipe> stonecutterRecipeTags$addFakeRecipes(List<StonecuttingRecipe> recipes) {
		DynamicRegistryManager drm = this.world.getRegistryManager();
		recipes = new ArrayList<>(recipes);
		List<Item> outputs = recipes.stream().map(r -> r.getOutput(drm).getItem()).toList();
		for (FakeStonecuttingRecipe recipe : StonecutterRecipeTagManager.makeFakeRecipes(inputStack)) {
			if (!outputs.contains(recipe.getOutput(drm).getItem()))
				recipes.add(recipe);
		}
		return recipes;
	}

	@WrapOperation(
			method = "onContentChanged",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"
			)
	)
	private boolean stonecutterRecipeTags$checkProvidedFullBlocks(ItemStack newInput, Item oldItem, Operation<Boolean> original) {
		boolean differentItem = !original.call(newInput, oldItem);
		ItemStack oldInput = inputStack; // oldItem is from inputStack.getItem()
		boolean couldCraftBefore = StonecutterRecipeTagManager.fullBlocksProvidedBy(oldInput) > 0;
		boolean canCraftNow = StonecutterRecipeTagManager.fullBlocksProvidedBy(newInput) > 0;
		boolean changeInCraftability = couldCraftBefore != canCraftNow;
		return !(differentItem || changeInCraftability); // invert: original call is inverted, invert here too to simplify logic
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
	private Optional<StonecuttingRecipe> stonecutterRecipeTags$fakeRecipesAllowShiftClick(
			RecipeManager manager, RecipeType<StonecuttingRecipe> type, Inventory inventory, World world,
			Operation<Optional<StonecuttingRecipe>> original) {
		Optional<StonecuttingRecipe> recipe = original.call(manager, type, inventory, world);
		if (recipe.isPresent())
			return recipe;
		ItemStack input = inventory.getStack(0); // inv is a SimpleInventory containing the input to test with
		List<FakeStonecuttingRecipe> recipes = StonecutterRecipeTagManager.makeFakeRecipes(input);
		return recipes.isEmpty() ? Optional.empty() : Optional.of(recipes.get(0));
	}
}
