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

package grondag.frex.impl.event;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.profiler.Profiler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grondag.frex.api.event.WorldRenderContext;

@Environment(EnvType.CLIENT)
public final class WorldRenderContextImpl implements WorldRenderContext.BlockOutlineContext, WorldRenderContext {
	private static net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext wrappedContext;
	private static net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext.BlockOutlineContext wrappedBlockContext;
	private static final WorldRenderContextImpl INSTANCE = new WorldRenderContextImpl();

	public static WorldRenderContext wrap(net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext ctx) {
		wrappedContext = ctx;
		return INSTANCE;
	}

	public static WorldRenderContextImpl wrap(net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext ctx, net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext.BlockOutlineContext blockCtx) {
		wrappedContext = ctx;
		wrappedBlockContext = blockCtx;
		return INSTANCE;
	}

	@Override
	public WorldRenderer worldRenderer() {
		return wrappedContext.worldRenderer();
	}

	@Override
	public MatrixStack matrixStack() {
		return wrappedContext.matrixStack();
	}

	@Override
	public float tickDelta() {
		return wrappedContext.tickDelta();
	}

	@Override
	public long limitTime() {
		return wrappedContext.limitTime();
	}

	@Override
	public boolean blockOutlines() {
		return wrappedContext.blockOutlines();
	}

	@Override
	public Camera camera() {
		return wrappedContext.camera();
	}

	@Override
	public Matrix4f projectionMatrix() {
		return wrappedContext.projectionMatrix();
	}

	@Override
	public ClientWorld world() {
		return wrappedContext.world();
	}

	@Override
	public Frustum frustum() {
		return wrappedContext.frustum();
	}

	@Override
	public VertexConsumerProvider consumers() {
		return wrappedContext.consumers();
	}

	@Override
	public GameRenderer gameRenderer() {
		return wrappedContext.gameRenderer();
	}

	@Override
	public LightmapTextureManager lightmapTextureManager() {
		return wrappedContext.lightmapTextureManager();
	}

	@Override
	public Profiler profiler() {
		return wrappedContext.profiler();
	}

	@Override
	public boolean advancedTranslucency() {
		return wrappedContext.advancedTranslucency();
	}

	@Override
	public VertexConsumer vertexConsumer() {
		return wrappedBlockContext.vertexConsumer();
	}

	@Override
	public Entity entity() {
		return wrappedBlockContext.entity();
	}

	@Override
	public double cameraX() {
		return wrappedBlockContext.cameraX();
	}

	@Override
	public double cameraY() {
		return wrappedBlockContext.cameraY();
	}

	@Override
	public double cameraZ() {
		return wrappedBlockContext.cameraZ();
	}

	@Override
	public BlockPos blockPos() {
		return wrappedBlockContext.blockPos();
	}

	@Override
	public BlockState blockState() {
		return wrappedBlockContext.blockState();
	}
}
