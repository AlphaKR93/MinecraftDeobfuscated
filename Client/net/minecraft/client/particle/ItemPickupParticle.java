/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;

public class ItemPickupParticle
extends Particle {
    private static final int LIFE_TIME = 3;
    private final RenderBuffers renderBuffers;
    private final Entity itemEntity;
    private final Entity target;
    private int life;
    private final EntityRenderDispatcher entityRenderDispatcher;

    public ItemPickupParticle(EntityRenderDispatcher $$0, RenderBuffers $$1, ClientLevel $$2, Entity $$3, Entity $$4) {
        this($$0, $$1, $$2, $$3, $$4, $$3.getDeltaMovement());
    }

    private ItemPickupParticle(EntityRenderDispatcher $$0, RenderBuffers $$1, ClientLevel $$2, Entity $$3, Entity $$4, Vec3 $$5) {
        super($$2, $$3.getX(), $$3.getY(), $$3.getZ(), $$5.x, $$5.y, $$5.z);
        this.renderBuffers = $$1;
        this.itemEntity = this.getSafeCopy($$3);
        this.target = $$4;
        this.entityRenderDispatcher = $$0;
    }

    private Entity getSafeCopy(Entity $$0) {
        if (!($$0 instanceof ItemEntity)) {
            return $$0;
        }
        return ((ItemEntity)$$0).copy();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @Override
    public void render(VertexConsumer $$0, Camera $$1, float $$2) {
        float $$3 = ((float)this.life + $$2) / 3.0f;
        $$3 *= $$3;
        double $$4 = Mth.lerp((double)$$2, this.target.xOld, this.target.getX());
        double $$5 = Mth.lerp((double)$$2, this.target.yOld, (this.target.getY() + this.target.getEyeY()) / 2.0);
        double $$6 = Mth.lerp((double)$$2, this.target.zOld, this.target.getZ());
        double $$7 = Mth.lerp((double)$$3, this.itemEntity.getX(), $$4);
        double $$8 = Mth.lerp((double)$$3, this.itemEntity.getY(), $$5);
        double $$9 = Mth.lerp((double)$$3, this.itemEntity.getZ(), $$6);
        MultiBufferSource.BufferSource $$10 = this.renderBuffers.bufferSource();
        Vec3 $$11 = $$1.getPosition();
        this.entityRenderDispatcher.render(this.itemEntity, $$7 - $$11.x(), $$8 - $$11.y(), $$9 - $$11.z(), this.itemEntity.getYRot(), $$2, new PoseStack(), $$10, this.entityRenderDispatcher.getPackedLightCoords(this.itemEntity, $$2));
        $$10.endBatch();
    }

    @Override
    public void tick() {
        ++this.life;
        if (this.life == 3) {
            this.remove();
        }
    }
}