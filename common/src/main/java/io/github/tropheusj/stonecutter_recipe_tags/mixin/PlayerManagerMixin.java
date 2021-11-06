package io.github.tropheusj.stonecutter_recipe_tags.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.tropheusj.stonecutter_recipe_tags.StonecutterRecipeTagManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
	@Shadow
	public void sendToAll(Packet<?> packet) {
	}

	@Inject(method = "onPlayerConnect(Lnet/minecraft/network/ClientConnection;Lnet/minecraft/server/network/ServerPlayerEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/SynchronizeTagsS2CPacket;<init>(Ljava/util/Map;)V", shift = At.Shift.BY, by = 2))
	private void stonecutterRecipeTags$afterSyncTagsOnPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
		connection.send(StonecutterRecipeTagManager.toSyncPacket());
	}

	@Inject(method = "onDataPacksReloaded()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/SynchronizeTagsS2CPacket;<init>(Ljava/util/Map;)V", shift = At.Shift.BY, by = 2))
	private void stonecutterRecipeTags$afterSyncTagsOnDataPacksReloaded(CallbackInfo ci) {
		sendToAll(StonecutterRecipeTagManager.toSyncPacket());
	}
}
