/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IncompatibleClassChangeError
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Parrot;

public class ParrotRenderer
extends MobRenderer<Parrot, ParrotModel> {
    private static final ResourceLocation RED_BLUE = new ResourceLocation("textures/entity/parrot/parrot_red_blue.png");
    private static final ResourceLocation BLUE = new ResourceLocation("textures/entity/parrot/parrot_blue.png");
    private static final ResourceLocation GREEN = new ResourceLocation("textures/entity/parrot/parrot_green.png");
    private static final ResourceLocation YELLOW_BLUE = new ResourceLocation("textures/entity/parrot/parrot_yellow_blue.png");
    private static final ResourceLocation GREY = new ResourceLocation("textures/entity/parrot/parrot_grey.png");

    public ParrotRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new ParrotModel($$0.bakeLayer(ModelLayers.PARROT)), 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(Parrot $$0) {
        return ParrotRenderer.getVariantTexture($$0.getVariant());
    }

    public static ResourceLocation getVariantTexture(Parrot.Variant $$0) {
        return switch ($$0) {
            default -> throw new IncompatibleClassChangeError();
            case Parrot.Variant.RED_BLUE -> RED_BLUE;
            case Parrot.Variant.BLUE -> BLUE;
            case Parrot.Variant.GREEN -> GREEN;
            case Parrot.Variant.YELLOW_BLUE -> YELLOW_BLUE;
            case Parrot.Variant.GRAY -> GREY;
        };
    }

    @Override
    public float getBob(Parrot $$0, float $$1) {
        float $$2 = Mth.lerp($$1, $$0.oFlap, $$0.flap);
        float $$3 = Mth.lerp($$1, $$0.oFlapSpeed, $$0.flapSpeed);
        return (Mth.sin($$2) + 1.0f) * $$3;
    }
}