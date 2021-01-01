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

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext.BlockOutlineContext;

import grondag.frex.impl.event.WorldRenderContextImpl;

public class FrexEventInit implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.AFTER_ENTITIES.register(ctx ->
			WorldRenderEvents.AFTER_ENTITIES.invoker().afterEntities(WorldRenderContextImpl.wrap(ctx))
		);

		net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.AFTER_SETUP.register(ctx ->
			WorldRenderEvents.AFTER_SETUP.invoker().afterSetup(WorldRenderContextImpl.wrap(ctx))
		);

		net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.AFTER_TRANSLUCENT.register(ctx ->
			WorldRenderEvents.AFTER_TRANSLUCENT.invoker().afterTranslucent(WorldRenderContextImpl.wrap(ctx))
		);

		net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((c, b) ->
			WorldRenderEvents.BEFORE_BLOCK_OUTLINE.invoker().beforeBlockOutline(WorldRenderContextImpl.wrap(c), b)
		);

		net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.BEFORE_DEBUG_RENDER.register(ctx ->
			WorldRenderEvents.BEFORE_DEBUG_RENDER.invoker().beforeDebugRender(WorldRenderContextImpl.wrap(ctx))
		);

		net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.BEFORE_ENTITIES.register(ctx ->
			WorldRenderEvents.BEFORE_ENTITIES.invoker().beforeEntities(WorldRenderContextImpl.wrap(ctx))
		);

		net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.BLOCK_OUTLINE.register(new net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.BlockOutline() {
			@Override
			public boolean onBlockOutline(WorldRenderContext worldRenderContext, BlockOutlineContext blockOutlieContext) {
				final WorldRenderContextImpl ctx = WorldRenderContextImpl.wrap(worldRenderContext, blockOutlieContext);
				return WorldRenderEvents.BLOCK_OUTLINE.invoker().onBlockOutline(ctx, ctx);
			}
		});

		net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.END.register(ctx ->
			WorldRenderEvents.END.invoker().onEnd(WorldRenderContextImpl.wrap(ctx))
		);

		net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.LAST.register(ctx ->
			WorldRenderEvents.LAST.invoker().onLast(WorldRenderContextImpl.wrap(ctx))
		);

		net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.START.register(ctx ->
			WorldRenderEvents.START.invoker().onStart(WorldRenderContextImpl.wrap(ctx))
		);
	}
}
