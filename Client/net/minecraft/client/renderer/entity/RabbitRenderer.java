/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IncompatibleClassChangeError
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.ChatFormatting;
import net.minecraft.client.model.RabbitModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Rabbit;

public class RabbitRenderer
extends MobRenderer<Rabbit, RabbitModel<Rabbit>> {
    private static final ResourceLocation RABBIT_BROWN_LOCATION = new ResourceLocation("textures/entity/rabbit/brown.png");
    private static final ResourceLocation RABBIT_WHITE_LOCATION = new ResourceLocation("textures/entity/rabbit/white.png");
    private static final ResourceLocation RABBIT_BLACK_LOCATION = new ResourceLocation("textures/entity/rabbit/black.png");
    private static final ResourceLocation RABBIT_GOLD_LOCATION = new ResourceLocation("textures/entity/rabbit/gold.png");
    private static final ResourceLocation RABBIT_SALT_LOCATION = new ResourceLocation("textures/entity/rabbit/salt.png");
    private static final ResourceLocation RABBIT_WHITE_SPLOTCHED_LOCATION = new ResourceLocation("textures/entity/rabbit/white_splotched.png");
    private static final ResourceLocation RABBIT_TOAST_LOCATION = new ResourceLocation("textures/entity/rabbit/toast.png");
    private static final ResourceLocation RABBIT_EVIL_LOCATION = new ResourceLocation("textures/entity/rabbit/caerbannog.png");

    public RabbitRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new RabbitModel($$0.bakeLayer(ModelLayers.RABBIT)), 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(Rabbit $$0) {
        String $$1 = ChatFormatting.stripFormatting($$0.getName().getString());
        if ("Toast".equals((Object)$$1)) {
            return RABBIT_TOAST_LOCATION;
        }
        return switch ($$0.getVariant()) {
            default -> throw new IncompatibleClassChangeError();
            case Rabbit.Variant.BROWN -> RABBIT_BROWN_LOCATION;
            case Rabbit.Variant.WHITE -> RABBIT_WHITE_LOCATION;
            case Rabbit.Variant.BLACK -> RABBIT_BLACK_LOCATION;
            case Rabbit.Variant.GOLD -> RABBIT_GOLD_LOCATION;
            case Rabbit.Variant.SALT -> RABBIT_SALT_LOCATION;
            case Rabbit.Variant.WHITE_SPLOTCHED -> RABBIT_WHITE_SPLOTCHED_LOCATION;
            case Rabbit.Variant.EVIL -> RABBIT_EVIL_LOCATION;
        };
    }
}