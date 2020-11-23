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

public final class WorldRenderEvents {
	private WorldRenderEvents() { }

	/**
	 * Called before world rendering executes. Input parameters are available but frustum is not.
	 * Use this event instead of injecting to the HEAD of {@link WorldRenderer#render} to avoid
	 * compatibility problems with 3rd-party renderer implementations.
	 *
	 * <p>Use for setup of state that is needed during the world render call that
	 * does not depend on the view frustum.
	 */
	public static final Event<Start> START = EventFactory.createArrayBacked(Start.class, callbacks -> context -> {
		for (final Start callback : callbacks) {
			callback.onStart(context);
		}
	});

	/**
	 * Called after view Frustum is computed and all render chunks to be rendered are
	 * identified and rebuilt but before chunks are uploaded to GPU.
	 *
	 * <p>Use for setup of state that depends on view frustum.
	 */
	public static final Event<AfterSetup> AFTER_SETUP = EventFactory.createArrayBacked(AfterSetup.class, callbacks -> context -> {
		for (final AfterSetup callback : callbacks) {
			callback.afterSetup(context);
		}
	});

	/**
	 * Called after the Solid, Cutout and Cutout Mipped terrain layers have been output to the framebuffer.
	 *
	 * <p>Use to render non-translucent terrain to the framebuffer.
	 *
	 * <p>Note that 3rd-party renderers may combine these passes or otherwise alter the
	 * rendering pipeline for sake of performance or features. This can break direct writes to the
	 * framebuffer.  Use this event for cases that cannot be satisfied by FabricBakedModel,
	 * BlockEntityRenderer or other existing abstraction. If at all possible, use an existing terrain
	 * RenderLayer instead of outputting to the framebuffer directly with GL calls.
	 *
	 * <p>The consumer is responsible for setup and tear down of GL state appropriate for the intended output.
	 *
	 * <p>Because solid and cutout quads are depth-tested, order of output does not matter except to improve
	 * culling performance, which should not be significant after primary terrain rendering. This means
	 * mods that currently hook calls to individual render layers can simply execute them all at once when
	 * the event is called.
	 *
	 * <p>This event fires before entities and block entities are rendered and may be useful to prepare them.
	 */
	public static final Event<BeforeEntities> BEFORE_ENTITIES = EventFactory.createArrayBacked(BeforeEntities.class, callbacks -> context -> {
		for (final BeforeEntities callback : callbacks) {
			callback.beforeEntities(context);
		}
	});

	/**
	 * Called after entities are rendered and solid entity layers
	 * have been drawn to the main frame buffer target, before
	 * block entity rendering begins.
	 *
	 * <p>Use for global block entity render setup, or
	 * to append block-related quads to the entity consumers using the
	 * {@VertexConsumerProvider} from the provided context. This
	 * will generally give better (if not perfect) results
	 * for non-terrain translucency vs. drawing directly later on.
	 */
	public static final Event<AfterEntities> AFTER_ENTITIES = EventFactory.createArrayBacked(AfterEntities.class, callbacks -> context -> {
		for (final AfterEntities callback : callbacks) {
			callback.afterEntities(context);
		}
	});

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
	public static final Event<DebugRender> BEFORE_DEBUG_RENDER = EventFactory.createArrayBacked(DebugRender.class, callbacks -> context -> {
		for (final DebugRender callback : callbacks) {
			callback.beforeDebugRender(context);
		}
	});

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
	public static final Event<AfterTranslucent> AFTER_TRANSLUCENT = EventFactory.createArrayBacked(AfterTranslucent.class, callbacks -> context -> {
		for (final AfterTranslucent callback : callbacks) {
			callback.afterTranslucent(context);
		}
	});

	/**
	 * Called after all framebuffer writes are complete but before all world
	 * rendering is torn down.
	 *
	 * <p>Unlike most other events, renders in this event are expected to be drawn
	 * directly and immediately to the framebuffer. The OpenGL render state view
	 * matrix will be transformed to match the camera view before the event is called.
	 *
	 * <p>Use to draw content that should appear on top of the world before hand and GUI rendering occur.
	 */
	public static final Event<Last> LAST = EventFactory.createArrayBacked(Last.class, callbacks -> context -> {
		for (final Last callback : callbacks) {
			callback.onLast(context);
		}
	});

	/**
	 * Called after all world rendering is complete and changes to GL state are unwound.
	 *
	 * <p>Use to draw overlays that handle GL state management independently or to tear
	 * down transient state in event handlers or as a hook that precedes hand/held item
	 * and GUI rendering.
	 */
	public static final Event<End> END = EventFactory.createArrayBacked(End.class, callbacks -> context -> {
		for (final End callback : callbacks) {
			callback.onEnd(context);
		}
	});

	@FunctionalInterface
	public interface Start {
		void onStart(WorldRenderStartContext context);
	}

	@FunctionalInterface
	public interface AfterSetup {
		void afterSetup(WorldRenderContext context);
	}

	@FunctionalInterface
	public interface BeforeEntities {
		void beforeEntities(WorldRenderContext context);
	}

	@FunctionalInterface
	public interface AfterEntities {
		void afterEntities(WorldRenderContext context);
	}

	@FunctionalInterface
	public interface DebugRender {
		void beforeDebugRender(WorldRenderContext context);
	}

	@FunctionalInterface
	public interface AfterTranslucent {
		void afterTranslucent(WorldRenderContext context);
	}

	@FunctionalInterface
	public interface Last {
		void onLast(WorldRenderContext context);
	}

	@FunctionalInterface
	public interface End {
		void onEnd(WorldRenderContext context);
	}
}
