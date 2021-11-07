package io.github.tropheusj.stonecutter_recipe_tags.forge;

import me.shedaniel.architectury.utils.Env;
import me.shedaniel.architectury.utils.EnvExecutor;
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
