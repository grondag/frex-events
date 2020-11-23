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

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@FunctionalInterface
public interface WorldRenderDebugRenderCallback {
	void onWorldRenderDebugRender(WorldRenderContext context);

	/**
	 * Called before vanilla debug renderers are output to the framebuffer.
	 * This happens very soon after entities, block breaking and most other
	 * non-translucent renders but before translucency is drawn.
	 *
	 * <p>Unlike most other events, renders in this event are expected to be drawn
	 * directly and immediately to the framebuffer. The OpenGL render state view
	 * matrix will be transformed to match the camera view before the event is called.
	 *
	 * <p>Use to drawn lines, overlays and other content similar to vanilla
	 * debug renders.
	 */
	Event<WorldRenderDebugRenderCallback> EVENT = EventFactory.createArrayBacked(WorldRenderDebugRenderCallback.class, callbacks -> context -> {
		for (final WorldRenderDebugRenderCallback callback : callbacks) {
			callback.onWorldRenderDebugRender(context);
		}
	});
}
