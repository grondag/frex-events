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
public interface WorldRenderEndCallback {
	void worldRenderEnd(WorldRenderContext context);

	/**
	 * Called after all world rendering is complete and changes to GL state are unwound.
	 *
	 * <p>Satin: PostWorldRenderCallbackV2.onWorldRendered
	 * BBOR: ClientInterop.render
	 * VoxelMap: MixinWorldRenderer.postRender
	 */
	Event<WorldRenderEndCallback> EVENT = EventFactory.createArrayBacked(WorldRenderEndCallback.class, callbacks -> context -> {
		for (final WorldRenderEndCallback callback : callbacks) {
			callback.worldRenderEnd(context);
		}
	});
}
