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
public interface WorldRenderPostTranslucentCallback {
	void worldRenderAfterTranslucent(WorldRenderContext context);

	/**
	 * Called after entity, terrain, and particle translucent layers have been
	 * drawn to the framebuffer but before translucency combine has happened
	 * in fabulous mode.
	 *
	 * <p>Use for drawing overlays or other effects on top of those targets
	 * (or the main target when fabulous isn't active) before clouds and weather
	 * are drawn.  However, note that {@code WorldRenderPostEntityCallback} will
	 * offer better results in most use cases.
	 */
	Event<WorldRenderPostTranslucentCallback> EVENT = EventFactory.createArrayBacked(WorldRenderPostTranslucentCallback.class, callbacks -> context -> {
		for (final WorldRenderPostTranslucentCallback callback : callbacks) {
			callback.worldRenderAfterTranslucent(context);
		}
	});
}
