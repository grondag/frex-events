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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Matrix4f;

import grondag.frex.api.event.WorldRenderDebugRenderCallback;
import grondag.frex.api.event.WorldRenderEndCallback;
import grondag.frex.api.event.WorldRenderLastCallback;
import grondag.frex.api.event.WorldRenderPostEntityCallback;
import grondag.frex.api.event.WorldRenderPostSetupCallback;
import grondag.frex.api.event.WorldRenderPostTranslucentCallback;
import grondag.frex.api.event.WorldRenderPreEntityCallback;
import grondag.frex.api.event.WorldRenderStartCallback;
import grondag.frex.impl.event.WorldRenderContextImpl;

@Internal
@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {
	@Shadow private BufferBuilderStorage bufferBuilders;
	@Shadow private ClientWorld world;
	private final WorldRenderContextImpl context = new WorldRenderContextImpl();
	private boolean didRenderParticles;

	@Inject(method = "render", at = @At("HEAD"))
	private void beforeRender(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {
		context.prepare((WorldRenderer) (Object) this, matrices, tickDelta, limitTime, renderBlockOutline, camera, matrix4f, bufferBuilders.getEntityVertexConsumers(), world.getProfiler());
		WorldRenderStartCallback.EVENT.invoker().onWorldRenderStart(context);
		didRenderParticles = false;
	}

	@Inject(method = "setupTerrain", at = @At("RETURN"))
	private void afterTerrainSetup(Camera camera, Frustum frustum, boolean hasForcedFrustum, int frame, boolean spectator, CallbackInfo ci) {
		context.setFrustum(frustum);
		WorldRenderPostSetupCallback.EVENT.invoker().afterWorldRenderSetup(context);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderLayer(Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/util/math/MatrixStack;DDD)V", ordinal = 2, shift = Shift.AFTER))
	private void afterTerrainSolid(CallbackInfo ci) {
		WorldRenderPreEntityCallback.EVENT.invoker().beforeWorldRenderEntities(context);
	}

	@Inject(method = "render", at = @At(value = "CONSTANT", args = "stringValue=blockentities", ordinal = 0))
	private void afterEntities(CallbackInfo ci) {
		WorldRenderPostEntityCallback.EVENT.invoker().afterWorldRenderEntities(context);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/debug/DebugRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;DDD)V", ordinal = 0))
	private void beforeDebugRender(CallbackInfo ci) {
		WorldRenderDebugRenderCallback.EVENT.invoker().onWorldRenderDebugRender(context);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleManager;renderParticles(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/render/LightmapTextureManager;Lnet/minecraft/client/render/Camera;F)V"))
	private void onRenderParticles(CallbackInfo ci) {
		// set a flag so we know the next pushMatrix call is after particles
		didRenderParticles = true;
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;pushMatrix()V"))
	private void beforeClouds(CallbackInfo ci) {
		if (didRenderParticles) {
			didRenderParticles = false;
			WorldRenderPostTranslucentCallback.EVENT.invoker().worldRenderAfterTranslucent(context);
		}
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderChunkDebugInfo(Lnet/minecraft/client/render/Camera;)V"))
	private void onChunkDebugRender(CallbackInfo ci) {
		WorldRenderLastCallback.EVENT.invoker().worldRenderLast(context);
	}

	@Inject(method = "render", at = @At("RETURN"))
	private void afternRender(CallbackInfo ci) {
		WorldRenderEndCallback.EVENT.invoker().worldRenderEnd(context);
	}
}

