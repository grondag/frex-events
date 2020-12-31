/*
 *  Copyright 2019, 2020 grondag
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License.  You may obtain a copy
 *  of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
 */

package grondag.frex.mixin;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;

import net.fabricmc.fabric.api.client.rendering.v1.InvalidateRenderStateCallback;

import grondag.frex.api.event.WorldRenderEvents;
import grondag.frex.impl.event.WorldRenderContextImpl;

@Internal
@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {
	@Shadow private BufferBuilderStorage bufferBuilders;
	@Shadow private ClientWorld world;
	@Shadow private ShaderEffect transparencyShader;
	@Shadow private MinecraftClient client;
	@Unique private final WorldRenderContextImpl frexContext = new WorldRenderContextImpl();
	@Unique private boolean didRenderParticles;

	@Inject(method = "render", at = @At("HEAD"))
	private void beforeRender(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {
		frexContext.prepare((WorldRenderer) (Object) this, matrices, tickDelta, limitTime, renderBlockOutline, camera, gameRenderer, lightmapTextureManager, matrix4f, bufferBuilders.getEntityVertexConsumers(), world.getProfiler(), transparencyShader != null, world);
		WorldRenderEvents.START.invoker().onStart(frexContext);
		didRenderParticles = false;
	}

	@Inject(method = "setupTerrain", at = @At("RETURN"))
	private void afterTerrainSetup(Camera camera, Frustum frustum, boolean hasForcedFrustum, int frame, boolean spectator, CallbackInfo ci) {
		frexContext.setFrustum(frustum);
		WorldRenderEvents.AFTER_SETUP.invoker().afterSetup(frexContext);
	}

	@Inject(
			method = "render",
			at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/client/render/WorldRenderer;renderLayer(Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/util/math/MatrixStack;DDD)V",
				ordinal = 2,
				shift = Shift.AFTER
			)
	)
	private void afterTerrainSolid(CallbackInfo ci) {
		WorldRenderEvents.BEFORE_ENTITIES.invoker().beforeEntities(frexContext);
	}

	@Inject(method = "render", at = @At(value = "CONSTANT", args = "stringValue=blockentities", ordinal = 0))
	private void afterEntities(CallbackInfo ci) {
		WorldRenderEvents.AFTER_ENTITIES.invoker().afterEntities(frexContext);
	}

	@Inject(
			method = "render",
			at = @At(
				value = "FIELD",
				target = "Lnet/minecraft/client/MinecraftClient;crosshairTarget:Lnet/minecraft/util/hit/HitResult;",
				shift = At.Shift.AFTER,
				ordinal = 1
			)
	)
	private void beforeRenderOutline(CallbackInfo ci) {
		frexContext.renderBlockOutline = WorldRenderEvents.BEFORE_BLOCK_OUTLINE.invoker().beforeBlockOutline(frexContext, client.crosshairTarget);
	}

	@Inject(method = "drawBlockOutline", at = @At("HEAD"), cancellable = true)
	private void onDrawBlockOutline(MatrixStack matrixStack, VertexConsumer vertexConsumer, Entity entity, double cameraX, double cameraY, double cameraZ, BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
		if (!frexContext.renderBlockOutline) {
			// Was cancelled before we got here, so do not
			// fire the BLOCK_OUTLINE event per contract of the API.
			ci.cancel();
		} else {
			frexContext.prepareBlockOutline(vertexConsumer, entity, cameraX, cameraY, cameraZ, blockPos, blockState);

			if (!WorldRenderEvents.BLOCK_OUTLINE.invoker().onBlockOutline(frexContext, frexContext)) {
				ci.cancel();
			}
		}
	}

	@Inject(
			method = "render",
			at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/client/render/debug/DebugRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;DDD)V",
				ordinal = 0
			)
	)
	private void beforeDebugRender(CallbackInfo ci) {
		WorldRenderEvents.BEFORE_DEBUG_RENDER.invoker().beforeDebugRender(frexContext);
	}

	@Inject(
			method = "render",
			at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/client/particle/ParticleManager;renderParticles(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/render/LightmapTextureManager;Lnet/minecraft/client/render/Camera;F)V"
			)
	)
	private void onRenderParticles(CallbackInfo ci) {
		// set a flag so we know the next pushMatrix call is after particles
		didRenderParticles = true;
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;pushMatrix()V"))
	private void beforeClouds(CallbackInfo ci) {
		if (didRenderParticles) {
			didRenderParticles = false;
			WorldRenderEvents.AFTER_TRANSLUCENT.invoker().afterTranslucent(frexContext);
		}
	}

	@Inject(
			method = "render",
			at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/client/render/WorldRenderer;renderChunkDebugInfo(Lnet/minecraft/client/render/Camera;)V"
			)
	)
	private void onChunkDebugRender(CallbackInfo ci) {
		WorldRenderEvents.LAST.invoker().onLast(frexContext);
	}

	@Inject(method = "render", at = @At("RETURN"))
	private void afterRender(CallbackInfo ci) {
		WorldRenderEvents.END.invoker().onEnd(frexContext);
	}

	@Inject(method = "reload", at = @At("HEAD"))
	private void onReload(CallbackInfo ci) {
		InvalidateRenderStateCallback.EVENT.invoker().onInvalidate();
	}
}
