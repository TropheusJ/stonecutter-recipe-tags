package io.github.tropheusj.stonecutter_recipe_tags.fabric;

import io.github.tropheusj.stonecutter_recipe_tags.StonecutterRecipeTagManager;
import io.github.tropheusj.stonecutter_recipe_tags.Utils;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.profiler.Profiler;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class ReloadListener extends SinglePreparationResourceReloader<Unit> implements IdentifiableResourceReloadListener {
	public static final ReloadListener INSTANCE = new ReloadListener();

	public static final Identifier ID = Utils.asId("server_data_reload_listener");
	public static final Set<Identifier> DEPENDENCIES = Collections.singleton(ResourceReloadListenerKeys.TAGS);

	@Override
	protected Unit prepare(ResourceManager manager, Profiler profiler) {
		return Unit.INSTANCE;
	}

	@Override
	protected void apply(Unit prepared, ResourceManager manager, Profiler profiler) {
		StonecutterRecipeTagManager.clearTags();
		for (Identifier id : manager.findResources("tags/items/stonecutter_recipes", path -> path.endsWith(".json"))) {
			String tagPath = id.getPath();
			tagPath = tagPath.substring(11, tagPath.length() - 5);
			Identifier tagId = new Identifier(id.getNamespace(), tagPath);
			StonecutterRecipeTagManager.registerOrGet(tagId);
		}
	}

	@Override
	public Identifier getFabricId() {
		return ID;
	}

	@Override
	public Collection<Identifier> getFabricDependencies() {
		return DEPENDENCIES;
	}
}
