/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CarriedBlockLayer;
import net.minecraft.client.renderer.entity.layers.EnderEyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class EndermanRenderer
extends MobRenderer<EnderMan, EndermanModel<EnderMan>> {
    private static final ResourceLocation ENDERMAN_LOCATION = new ResourceLocation("textures/entity/enderman/enderman.png");
    private final RandomSource random = RandomSource.create();

    public EndermanRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new EndermanModel($$0.bakeLayer(ModelLayers.ENDERMAN)), 0.5f);
        this.addLayer(new EnderEyesLayer<EnderMan>(this));
        this.addLayer(new CarriedBlockLayer(this, $$0.getBlockRenderDispatcher()));
    }

    @Override
    public void render(EnderMan $$0, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        BlockState $$6 = $$0.getCarriedBlock();
        EndermanModel $$7 = (EndermanModel)this.getModel();
        $$7.carrying = $$6 != null;
        $$7.creepy = $$0.isCreepy();
        super.render($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public Vec3 getRenderOffset(EnderMan $$0, float $$1) {
        if ($$0.isCreepy()) {
            double $$2 = 0.02;
            return new Vec3(this.random.nextGaussian() * 0.02, 0.0, this.random.nextGaussian() * 0.02);
        }
        return super.getRenderOffset($$0, $$1);
    }

    @Override
    public ResourceLocation getTextureLocation(EnderMan $$0) {
        return ENDERMAN_LOCATION;
    }
}