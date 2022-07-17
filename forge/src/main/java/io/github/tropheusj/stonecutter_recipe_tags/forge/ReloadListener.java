package io.github.tropheusj.stonecutter_recipe_tags.forge;

import io.github.tropheusj.stonecutter_recipe_tags.StonecutterRecipeTagManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.profiler.Profiler;

public class ReloadListener extends SinglePreparationResourceReloader<Unit> {
	public static final ReloadListener INSTANCE = new ReloadListener();

	@Override
	protected Unit prepare(ResourceManager manager, Profiler profiler) {
		return Unit.INSTANCE;
	}

	@Override
	protected void apply(Unit prepared, ResourceManager manager, Profiler profiler) {
		StonecutterRecipeTagManager.clearTags();
		manager.findResources("tags/items/stonecutter_recipes", id -> id.getPath().endsWith(".json")).forEach((id, unused) -> {
			String tagPath = id.getPath();
			tagPath = tagPath.substring(11, tagPath.length() - 5);
			Identifier tagId = new Identifier(id.getNamespace(), tagPath);
			StonecutterRecipeTagManager.registerOrGet(tagId);
		});
	}
}
