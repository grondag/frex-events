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
public interface WorldRenderPostSetupCallback {
	void afterWorldRenderSetup(WorldRenderContext context);

	/**
	 * Called after view Frustum is computed and all render chunks to be rendered are
	 * identified and rebuilt but before chunks are uploaded to GPU.
	 *
	 * <p>Typical usage is for setup of state that depends on view frustum.
	 */
	Event<WorldRenderPostSetupCallback> EVENT = EventFactory.createArrayBacked(WorldRenderPostSetupCallback.class, callbacks -> context -> {
		for (final WorldRenderPostSetupCallback callback : callbacks) {
			callback.afterWorldRenderSetup(context);
		}
	});
}
