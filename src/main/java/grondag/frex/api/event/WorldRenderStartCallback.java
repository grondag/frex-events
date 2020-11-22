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

import net.minecraft.client.render.WorldRenderer;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@FunctionalInterface
public interface WorldRenderStartCallback {
	/**
	 * Called before world rendering executes. Input parameters are available but frustum is not.
	 * Use this event instead of injecting to the HEAD of {@link WorldRenderer#render} to avoid
	 * compatibility problems with 3rd-party renderer implementations.
	 *
	 * <p>Typical usage is for setup of state that is needed during the world render call that
	 * does not depend on the view frustum.
	 */
	void onWorldRenderStart(WorldRenderStartContext context);

	Event<WorldRenderStartCallback> EVENT = EventFactory.createArrayBacked(WorldRenderStartCallback.class, callbacks -> context -> {
		for (final WorldRenderStartCallback callback : callbacks) {
			callback.onWorldRenderStart(context);
		}
	});
}
