package io.github.tropheusj.stonecutter_recipe_tags.forge;

import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import io.github.tropheusj.stonecutter_recipe_tags.StonecutterRecipeTags;
import io.github.tropheusj.stonecutter_recipe_tags.StonecutterRecipeTagsClient;
import net.minecraftforge.fml.common.Mod;

@Mod(StonecutterRecipeTags.ID)
public class StonecutterRecipeTagsForge {
	public StonecutterRecipeTagsForge() {
		StonecutterRecipeTags.init();
		EnvExecutor.runInEnv(Env.CLIENT, () -> StonecutterRecipeTagsClient::init);
    }
}
