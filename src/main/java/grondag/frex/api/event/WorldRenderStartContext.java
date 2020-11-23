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

package grondag.frex.api.event;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.profiler.Profiler;

/**
 * Except as noted below, the properties exposed here match the parameters passed to
 * {@link WorldRenderer#render(MatrixStack, float, long, boolean, Camera, GameRenderer, LightmapTextureManager, Matrix4f)}.
 */
public interface WorldRenderStartContext {
	/**
	 * The world renderer instance doing the rendering and invoking the event.
	 *
	 * @return WorldRenderer instance invoking the event
	 */
	WorldRenderer worldRenderer();

	MatrixStack matrixStack();

	float tickDelta();

	long limitTime();

	boolean blockOutlines();

	Camera camera();

	GameRenderer gameRenderer();

	LightmapTextureManager lightmapTextureManager();

	Matrix4f projectionMatrix();

	/**
	 * Convenient access to game performance profiler.
	 *
	 * @return the active profiler
	 */
	Profiler profiler();

	/**
	 * The {@code VertexConsumerProvider} instance being used by the world renderer for most non-terrain renders.
	 * Generally this will be better for any direct renders because quads for the same layer can be cached
	 * and then drawn all at once by the world renderer.
	 *
	 * <p>IMPORTANT - all vertex coordinates sent to consumers should be relative to the camera to
	 * be consistent with other quads emitted by the world renderer and other mods.  If this isn't
	 * possible, caller should use a separate "immediate" instance.
	 *
	 * <p>If the renderer implementation supports {@code FrexVertexConsumerProvider} this instance can be
	 * cast to that type, and caller can use the extended features available in FREX materials.
	 */
	VertexConsumerProvider consumers();

	/**
	 * Test to know if "fabulous" graphics mode is enabled.
	 *
	 * <p>Use this for renders that need to render on top of all translucency to activate or deactivate different
	 * event handlers to get optimal depth testing results. When fabulous is off, it may be better to render
	 * during {@code WorldRenderLastCallback} after clouds and weather are drawn. Conversely, when fabulous mode is on,
	 * it may be better to draw during {@code WorldRenderPostTranslucentCallback}, before the fabulous mode composite
	 * shader runs, depending on which translucent buffer is being targeted.
	 *
	 * @return {@code true} when "fabulous" graphics mode is enabled.
	 */
	boolean advancedTranslucency();
}
