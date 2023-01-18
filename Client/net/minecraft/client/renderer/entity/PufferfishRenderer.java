/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PufferfishBigModel;
import net.minecraft.client.model.PufferfishMidModel;
import net.minecraft.client.model.PufferfishSmallModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Pufferfish;

public class PufferfishRenderer
extends MobRenderer<Pufferfish, EntityModel<Pufferfish>> {
    private static final ResourceLocation PUFFER_LOCATION = new ResourceLocation("textures/entity/fish/pufferfish.png");
    private int puffStateO = 3;
    private final EntityModel<Pufferfish> small;
    private final EntityModel<Pufferfish> mid;
    private final EntityModel<Pufferfish> big = this.getModel();

    public PufferfishRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new PufferfishBigModel($$0.bakeLayer(ModelLayers.PUFFERFISH_BIG)), 0.2f);
        this.mid = new PufferfishMidModel<Pufferfish>($$0.bakeLayer(ModelLayers.PUFFERFISH_MEDIUM));
        this.small = new PufferfishSmallModel<Pufferfish>($$0.bakeLayer(ModelLayers.PUFFERFISH_SMALL));
    }

    @Override
    public ResourceLocation getTextureLocation(Pufferfish $$0) {
        return PUFFER_LOCATION;
    }

    @Override
    public void render(Pufferfish $$0, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        int $$6 = $$0.getPuffState();
        if ($$6 != this.puffStateO) {
            this.model = $$6 == 0 ? this.small : ($$6 == 1 ? this.mid : this.big);
        }
        this.puffStateO = $$6;
        this.shadowRadius = 0.1f + 0.1f * (float)$$6;
        super.render($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    protected void setupRotations(Pufferfish $$0, PoseStack $$1, float $$2, float $$3, float $$4) {
        $$1.translate(0.0f, Mth.cos($$2 * 0.05f) * 0.08f, 0.0f);
        super.setupRotations($$0, $$1, $$2, $$3, $$4);
    }
}