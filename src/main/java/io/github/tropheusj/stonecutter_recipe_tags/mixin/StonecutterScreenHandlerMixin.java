package io.github.tropheusj.stonecutter_recipe_tags.mixin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.github.tropheusj.stonecutter_recipe_tags.StonecutterScreenHandlerExtensions;

import io.github.tropheusj.stonecutter_recipe_tags.StonecutterRecipeTagHandler;

import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import net.minecraft.recipe.RecipeType;

import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.StonecutterScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.tag.Tag;

@Mixin(StonecutterScreenHandler.class)
public abstract class StonecutterScreenHandlerMixin extends ScreenHandler implements StonecutterScreenHandlerExtensions {
	/**
	 * Boolean value - true if the stack is a non-tag recipe
	 */
	@Unique
	private final Map<ItemStack, Boolean> stacksToDisplay = new HashMap<>();
	@Unique
	private List<ItemStack> displayStackList = new ArrayList<>();
	@Shadow
	@Final
	Slot outputSlot;
	@Shadow
	@Final
	Slot inputSlot;
	@Shadow
	@Final
	private Property selectedRecipe;
	@Shadow
	private List<StonecuttingRecipe> availableRecipes;
	@Shadow
	private ItemStack inputStack;

	protected StonecutterScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId) {
		super(type, syncId);
	}

	@Shadow
	protected abstract boolean isInBounds(int id);

	@Shadow
	abstract void populateResult();

	@Shadow
	@Final
	private World world;

	@Inject(at = @At("HEAD"), method = "updateInput", cancellable = true)
	private void stonecutterRecipeTags$updateInput(Inventory input, ItemStack stack, CallbackInfo ci) {
		stacksToDisplay.clear();
		displayStackList = new ArrayList<>();
		if (!stack.isEmpty()) {
			// regular recipes
			availableRecipes = world.getRecipeManager().getAllMatches(RecipeType.STONECUTTING, input, world);
			availableRecipes.forEach(recipe -> {
				ItemStack toAdd = recipe.getOutput();
				if (stacksToDisplay.keySet().stream().noneMatch(displayedStack -> displayedStack.isItemEqual(toAdd))) {
					stacksToDisplay.put(toAdd, true);
				}
			});
			// tag recipes
			List<Tag<Item>> tags = StonecutterRecipeTagHandler.getRecipeTags(stack);
			if (!tags.isEmpty() && StonecutterRecipeTagHandler.getItemCraftCount(stack) <= stack.getCount()) {
				List<Item> items = new ArrayList<>();
				for (Tag<Item> tag : tags) {
					for (Item item : tag.values()) {
						if (!stack.isOf(item) && !items.contains(item)) {
							items.add(item);
						}
					}
				}

				items.stream().map(ItemStack::new).forEach(regularRecipeStack -> stacksToDisplay.put(regularRecipeStack, false));
			}

			displayStackList = stacksToDisplay.keySet().stream().toList();
			selectedRecipe.set(-1);
			outputSlot.setStack(ItemStack.EMPTY);
			ci.cancel();
		}
	}

	@Inject(at = @At("HEAD"), method = "isInBounds", cancellable = true)
	private void stonecutterRecipeTags$isInBounds(int id, CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(id >= 0 && id < stacksToDisplay.size());
	}

	@Inject(at = @At("HEAD"), method = "onButtonClick", cancellable = true)
	private void stonecutterRecipeTags$onButtonClick(PlayerEntity player, int id, CallbackInfoReturnable<Boolean> cir) {
		ItemStack selected = displayStackList.get(id);
		if (stackFromTagRecipe(selected)) {
			int required = StonecutterRecipeTagHandler.getItemCraftCount(inputSlot.getStack());
			if (inputSlot.getStack().getCount() >= required) {
				if (isInBounds(id)) {
					this.selectedRecipe.set(id);
					populateResult();
				}
			}
			cir.setReturnValue(true);
		}
	}

	@Redirect(method = "onContentChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
	private boolean stonecutterRecipeTags$onContentsChanged(ItemStack itemStack, Item item) {
		// inputStack is old item
		// itemStack is new item
		boolean differentItem = !itemStack.isOf(inputStack.getItem());
		int lastCount = inputStack.getCount();
		int lastRequiredCount = StonecutterRecipeTagHandler.getItemCraftCount(inputSlot.getStack());
		int newCount = itemStack.getCount();
		int newRequiredCount = StonecutterRecipeTagHandler.getItemCraftCount(itemStack);
		boolean nowMeetsCountRequirement = lastCount < lastRequiredCount && newCount >= newRequiredCount;
		boolean noLongerMeetsCountRequirement = newCount < newRequiredCount && lastCount >= lastRequiredCount;
		return !(differentItem || nowMeetsCountRequirement || noLongerMeetsCountRequirement); // redirected method is inverted, invert here to make it Good:tm:
	}

	@Inject(at = @At("HEAD"), method = "populateResult", cancellable = true)
	private void stonecutterRecipeTags$populateResult(CallbackInfo ci) {
		ItemStack selected = displayStackList.get(selectedRecipe.get() - 1);
		if (stackFromTagRecipe(selected)) {
			int neededCount = StonecutterRecipeTagHandler.getItemCraftCount(inputSlot.getStack());
			if (!this.stacksToDisplay.isEmpty() && inputSlot.getStack().getCount() >= neededCount) {
				ItemStack stack = selected.copy();
				stack.setCount(StonecutterRecipeTagHandler.getItemCraftCount(stack.getItem()));
				outputSlot.setStack(stack);
			} else {
				stacksToDisplay.clear();
				outputSlot.setStack(ItemStack.EMPTY);
			}

			sendContentUpdates();
			ci.cancel();
		}
	}

	@Inject(at = @At("HEAD"), method = "canCraft", cancellable = true)
	private void stonecutterRecipeTags$canCraft(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(inputSlot.hasStack() &&
				StonecutterRecipeTagHandler.getItemCraftCount(inputSlot.getStack()) <= inputSlot.getStack().getCount() &&
				!stacksToDisplay.isEmpty());
	}

	@Redirect(at = @At(value = "INVOKE", target = "Ljava/util/Optional;isPresent()Z"), method = "transferSlot")
	public boolean stonecutterRecipeTags$transferSlot(Optional optional) {
		return true;
	}

	@Override
	public List<ItemStack> getStacksToDisplay() {
		return displayStackList;
	}

	@Override
	public boolean stackFromTagRecipe(ItemStack stack) {
		return stacksToDisplay.get(stack);
	}
}
