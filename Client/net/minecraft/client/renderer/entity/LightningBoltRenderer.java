/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  org.joml.Matrix4f
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LightningBolt;
import org.joml.Matrix4f;

public class LightningBoltRenderer
extends EntityRenderer<LightningBolt> {
    public LightningBoltRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
    }

    @Override
    public void render(LightningBolt $$0, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        float[] $$6 = new float[8];
        float[] $$7 = new float[8];
        float $$8 = 0.0f;
        float $$9 = 0.0f;
        RandomSource $$10 = RandomSource.create($$0.seed);
        for (int $$11 = 7; $$11 >= 0; --$$11) {
            $$6[$$11] = $$8;
            $$7[$$11] = $$9;
            $$8 += (float)($$10.nextInt(11) - 5);
            $$9 += (float)($$10.nextInt(11) - 5);
        }
        VertexConsumer $$12 = $$4.getBuffer(RenderType.lightning());
        Matrix4f $$13 = $$3.last().pose();
        for (int $$14 = 0; $$14 < 4; ++$$14) {
            RandomSource $$15 = RandomSource.create($$0.seed);
            for (int $$16 = 0; $$16 < 3; ++$$16) {
                int $$17 = 7;
                int $$18 = 0;
                if ($$16 > 0) {
                    $$17 = 7 - $$16;
                }
                if ($$16 > 0) {
                    $$18 = $$17 - 2;
                }
                float $$19 = $$6[$$17] - $$8;
                float $$20 = $$7[$$17] - $$9;
                for (int $$21 = $$17; $$21 >= $$18; --$$21) {
                    float $$22 = $$19;
                    float $$23 = $$20;
                    if ($$16 == 0) {
                        $$19 += (float)($$15.nextInt(11) - 5);
                        $$20 += (float)($$15.nextInt(11) - 5);
                    } else {
                        $$19 += (float)($$15.nextInt(31) - 15);
                        $$20 += (float)($$15.nextInt(31) - 15);
                    }
                    float $$24 = 0.5f;
                    float $$25 = 0.45f;
                    float $$26 = 0.45f;
                    float $$27 = 0.5f;
                    float $$28 = 0.1f + (float)$$14 * 0.2f;
                    if ($$16 == 0) {
                        $$28 *= (float)$$21 * 0.1f + 1.0f;
                    }
                    float $$29 = 0.1f + (float)$$14 * 0.2f;
                    if ($$16 == 0) {
                        $$29 *= ((float)$$21 - 1.0f) * 0.1f + 1.0f;
                    }
                    LightningBoltRenderer.quad($$13, $$12, $$19, $$20, $$21, $$22, $$23, 0.45f, 0.45f, 0.5f, $$28, $$29, false, false, true, false);
                    LightningBoltRenderer.quad($$13, $$12, $$19, $$20, $$21, $$22, $$23, 0.45f, 0.45f, 0.5f, $$28, $$29, true, false, true, true);
                    LightningBoltRenderer.quad($$13, $$12, $$19, $$20, $$21, $$22, $$23, 0.45f, 0.45f, 0.5f, $$28, $$29, true, true, false, true);
                    LightningBoltRenderer.quad($$13, $$12, $$19, $$20, $$21, $$22, $$23, 0.45f, 0.45f, 0.5f, $$28, $$29, false, true, false, false);
                }
            }
        }
    }

    private static void quad(Matrix4f $$0, VertexConsumer $$1, float $$2, float $$3, int $$4, float $$5, float $$6, float $$7, float $$8, float $$9, float $$10, float $$11, boolean $$12, boolean $$13, boolean $$14, boolean $$15) {
        $$1.vertex($$0, $$2 + ($$12 ? $$11 : -$$11), $$4 * 16, $$3 + ($$13 ? $$11 : -$$11)).color($$7, $$8, $$9, 0.3f).endVertex();
        $$1.vertex($$0, $$5 + ($$12 ? $$10 : -$$10), ($$4 + 1) * 16, $$6 + ($$13 ? $$10 : -$$10)).color($$7, $$8, $$9, 0.3f).endVertex();
        $$1.vertex($$0, $$5 + ($$14 ? $$10 : -$$10), ($$4 + 1) * 16, $$6 + ($$15 ? $$10 : -$$10)).color($$7, $$8, $$9, 0.3f).endVertex();
        $$1.vertex($$0, $$2 + ($$14 ? $$11 : -$$11), $$4 * 16, $$3 + ($$15 ? $$11 : -$$11)).color($$7, $$8, $$9, 0.3f).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(LightningBolt $$0) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}