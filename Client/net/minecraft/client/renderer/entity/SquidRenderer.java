/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.SquidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Squid;

public class SquidRenderer<T extends Squid>
extends MobRenderer<T, SquidModel<T>> {
    private static final ResourceLocation SQUID_LOCATION = new ResourceLocation("textures/entity/squid/squid.png");

    public SquidRenderer(EntityRendererProvider.Context $$0, SquidModel<T> $$1) {
        super($$0, $$1, 0.7f);
    }

    @Override
    public ResourceLocation getTextureLocation(T $$0) {
        return SQUID_LOCATION;
    }

    @Override
    protected void setupRotations(T $$0, PoseStack $$1, float $$2, float $$3, float $$4) {
        float $$5 = Mth.lerp($$4, ((Squid)$$0).xBodyRotO, ((Squid)$$0).xBodyRot);
        float $$6 = Mth.lerp($$4, ((Squid)$$0).zBodyRotO, ((Squid)$$0).zBodyRot);
        $$1.translate(0.0f, 0.5f, 0.0f);
        $$1.mulPose(Axis.YP.rotationDegrees(180.0f - $$3));
        $$1.mulPose(Axis.XP.rotationDegrees($$5));
        $$1.mulPose(Axis.YP.rotationDegrees($$6));
        $$1.translate(0.0f, -1.2f, 0.0f);
    }

    @Override
    protected float getBob(T $$0, float $$1) {
        return Mth.lerp($$1, ((Squid)$$0).oldTentacleAngle, ((Squid)$$0).tentacleAngle);
    }
}