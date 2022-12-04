package io.github.tropheusj.stonecutter_recipe_tags.mixin;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
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
	@ModifyConstant(method = "onTakeItem(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)V", constant = @Constant(intValue = 1))
	private int stonecutterRecipeTags$redirectIntToShrinkInput(int original) {
		var recipe = field_17639.output.getLastRecipe();
		if (recipe instanceof FakeStonecuttingRecipe fakeStonecuttingRecipe) {
			return fakeStonecuttingRecipe.inputItemCraftCount;
		} else {
			return original;
		}
	}
}
