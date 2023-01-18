/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.Optional
 *  java.util.function.Supplier
 *  org.joml.Vector3f
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import org.joml.Vector3f;

public class GameEventListenerRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    private static final int LISTENER_RENDER_DIST = 32;
    private static final float BOX_HEIGHT = 1.0f;
    private final List<TrackedGameEvent> trackedGameEvents = Lists.newArrayList();
    private final List<TrackedListener> trackedListeners = Lists.newArrayList();

    public GameEventListenerRenderer(Minecraft $$0) {
        this.minecraft = $$0;
    }

    @Override
    public void render(PoseStack $$02, MultiBufferSource $$1, double $$22, double $$3, double $$42) {
        ClientLevel $$5 = this.minecraft.level;
        if ($$5 == null) {
            this.trackedGameEvents.clear();
            this.trackedListeners.clear();
            return;
        }
        Vec3 $$62 = new Vec3($$22, 0.0, $$42);
        this.trackedGameEvents.removeIf(TrackedGameEvent::isExpired);
        this.trackedListeners.removeIf($$2 -> $$2.isExpired($$5, $$62));
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        VertexConsumer $$7 = $$1.getBuffer(RenderType.lines());
        for (TrackedListener $$8 : this.trackedListeners) {
            $$8.getPosition($$5).ifPresent($$6 -> {
                double $$7 = $$6.x() - (double)$$8.getListenerRadius();
                double $$8 = $$6.y() - (double)$$8.getListenerRadius();
                double $$9 = $$6.z() - (double)$$8.getListenerRadius();
                double $$10 = $$6.x() + (double)$$8.getListenerRadius();
                double $$11 = $$6.y() + (double)$$8.getListenerRadius();
                double $$12 = $$6.z() + (double)$$8.getListenerRadius();
                Vector3f $$13 = new Vector3f(1.0f, 1.0f, 0.0f);
                LevelRenderer.renderVoxelShape($$02, $$7, Shapes.create(new AABB($$7, $$8, $$9, $$10, $$11, $$12)), -$$22, -$$3, -$$42, $$13.x(), $$13.y(), $$13.z(), 0.35f);
            });
        }
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionColorShader));
        Tesselator $$9 = Tesselator.getInstance();
        BufferBuilder $$10 = $$9.getBuilder();
        $$10.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        for (TrackedListener $$11 : this.trackedListeners) {
            $$11.getPosition($$5).ifPresent($$4 -> {
                Vector3f $$5 = new Vector3f(1.0f, 1.0f, 0.0f);
                LevelRenderer.addChainedFilledBoxVertices($$10, $$4.x() - 0.25 - $$22, $$4.y() - $$3, $$4.z() - 0.25 - $$42, $$4.x() + 0.25 - $$22, $$4.y() - $$3 + 1.0, $$4.z() + 0.25 - $$42, $$5.x(), $$5.y(), $$5.z(), 0.35f);
            });
        }
        $$9.end();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(2.0f);
        RenderSystem.depthMask(false);
        for (TrackedListener $$12 : this.trackedListeners) {
            $$12.getPosition($$5).ifPresent($$0 -> {
                DebugRenderer.renderFloatingText("Listener Origin", $$0.x(), $$0.y() + (double)1.8f, $$0.z(), -1, 0.025f);
                DebugRenderer.renderFloatingText(new BlockPos((Vec3)$$0).toString(), $$0.x(), $$0.y() + 1.5, $$0.z(), -6959665, 0.025f);
            });
        }
        for (TrackedGameEvent $$13 : this.trackedGameEvents) {
            Vec3 $$14 = $$13.position;
            double $$15 = 0.2f;
            double $$16 = $$14.x - (double)0.2f;
            double $$17 = $$14.y - (double)0.2f;
            double $$18 = $$14.z - (double)0.2f;
            double $$19 = $$14.x + (double)0.2f;
            double $$20 = $$14.y + (double)0.2f + 0.5;
            double $$21 = $$14.z + (double)0.2f;
            GameEventListenerRenderer.renderTransparentFilledBox(new AABB($$16, $$17, $$18, $$19, $$20, $$21), 1.0f, 1.0f, 1.0f, 0.2f);
            DebugRenderer.renderFloatingText($$13.gameEvent.getName(), $$14.x, $$14.y + (double)0.85f, $$14.z, -7564911, 0.0075f);
        }
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }

    private static void renderTransparentFilledBox(AABB $$0, float $$1, float $$2, float $$3, float $$4) {
        Camera $$5 = Minecraft.getInstance().gameRenderer.getMainCamera();
        if (!$$5.isInitialized()) {
            return;
        }
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        Vec3 $$6 = $$5.getPosition().reverse();
        DebugRenderer.renderFilledBox($$0.move($$6), $$1, $$2, $$3, $$4);
    }

    public void trackGameEvent(GameEvent $$0, Vec3 $$1) {
        this.trackedGameEvents.add((Object)new TrackedGameEvent(Util.getMillis(), $$0, $$1));
    }

    public void trackListener(PositionSource $$0, int $$1) {
        this.trackedListeners.add((Object)new TrackedListener($$0, $$1));
    }

    static class TrackedListener
    implements GameEventListener {
        public final PositionSource listenerSource;
        public final int listenerRange;

        public TrackedListener(PositionSource $$0, int $$1) {
            this.listenerSource = $$0;
            this.listenerRange = $$1;
        }

        public boolean isExpired(Level $$0, Vec3 $$12) {
            return this.listenerSource.getPosition($$0).filter($$1 -> $$1.distanceToSqr($$12) <= 1024.0).isPresent();
        }

        public Optional<Vec3> getPosition(Level $$0) {
            return this.listenerSource.getPosition($$0);
        }

        @Override
        public PositionSource getListenerSource() {
            return this.listenerSource;
        }

        @Override
        public int getListenerRadius() {
            return this.listenerRange;
        }

        @Override
        public boolean handleGameEvent(ServerLevel $$0, GameEvent $$1, GameEvent.Context $$2, Vec3 $$3) {
            return false;
        }
    }

    record TrackedGameEvent(long timeStamp, GameEvent gameEvent, Vec3 position) {
        public boolean isExpired() {
            return Util.getMillis() - this.timeStamp > 3000L;
        }
    }
}