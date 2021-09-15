package io.github.tropheusj.stonecutter_recipe_tags;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.profiler.Profiler;

public class StonecutterRecipeTags implements ModInitializer {
	public static final String ID = "stonecutter_recipe_tags";

	@Override
	public void onInitialize() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(ReloadListener.INSTANCE);
	}

	public static Identifier asId(String path) {
		return new Identifier(ID, path);
	}

	public static class ReloadListener extends SinglePreparationResourceReloader<Unit> implements IdentifiableResourceReloadListener {
		private static final ReloadListener INSTANCE = new ReloadListener();

		public static final Identifier ID = asId("server_data_reload_listener");
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
}
