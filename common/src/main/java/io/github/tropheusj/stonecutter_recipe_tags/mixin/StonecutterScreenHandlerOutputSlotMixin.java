package io.github.tropheusj.stonecutter_recipe_tags.mixin;

import net.minecraft.recipe.Recipe;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import io.github.tropheusj.stonecutter_recipe_tags.FakeStonecuttingRecipe;
import net.minecraft.screen.StonecutterScreenHandler;

@Mixin(targets = "net.minecraft.screen.StonecutterScreenHandler$2")
public abstract class StonecutterScreenHandlerOutputSlotMixin {
	/**
	 * Reference from inner class to outer {@code this}.
	 */
	@Final
	@Dynamic
	@Shadow
	StonecutterScreenHandler field_17639;

	/**
	 * Modifies the number of items removed after a craft operation by checking for a {@link FakeStonecuttingRecipe}.
	 */
	@ModifyArg(
			method = "onTakeItem(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/screen/slot/Slot;takeStack(I)Lnet/minecraft/item/ItemStack;"
			)
	)
	private int stonecutterRecipeTags$removeCorrectAmount(int amount) {
		Recipe<?> recipe = field_17639.output.getLastRecipe();
		if (recipe instanceof FakeStonecuttingRecipe fake) {
			return fake.inputItemCraftCount;
		} else {
			return amount;
		}
	}
}
