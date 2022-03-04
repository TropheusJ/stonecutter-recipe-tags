package io.github.tropheusj.stonecutter_recipe_tags.forge.mixin;

import io.github.tropheusj.stonecutter_recipe_tags.StonecutterRecipeTagManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
	@Shadow
	@Final
	private MinecraftClient client;

	@Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
    private void onCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo ci) {
		if (packet.getChannel().equals(StonecutterRecipeTagManager.SYNC_STONECUTTER_RECIPE_TAGS_PACKET_ID)) {
			NetworkThreadUtils.forceMainThread(packet, (ClientPlayNetworkHandler) (Object) this, client);
			StonecutterRecipeTagManager.fromPacketBuf(packet.getData());
			ci.cancel();
		}
	}
}
