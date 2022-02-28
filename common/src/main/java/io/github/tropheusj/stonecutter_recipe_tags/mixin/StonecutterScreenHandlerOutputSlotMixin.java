package io.github.tropheusj.stonecutter_recipe_tags.mixin;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import io.github.tropheusj.stonecutter_recipe_tags.StonecutterRecipeTagManager;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.StonecutterScreenHandler;

@Mixin(targets = "net.minecraft.screen.StonecutterScreenHandler$2")
public abstract class StonecutterScreenHandlerOutputSlotMixin {
	@Final
	@Dynamic
	@Shadow
	StonecutterScreenHandler field_17639;

	@ModifyConstant(method = "onTakeItem(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)V", constant = @Constant(intValue = 1))
	private int stonecutterRecipeTags$redirectIntToShrinkInput(int original) {
		ItemStack inputStack = field_17639.input.getStack(0);
		int toTake = StonecutterRecipeTagManager.getItemCraftCount(inputStack);
		if (toTake > inputStack.getCount()) {
			return 0;
		}
		return StonecutterRecipeTagManager.getItemCraftCount(inputStack.getItem());
	}
}
