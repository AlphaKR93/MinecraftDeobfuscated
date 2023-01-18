/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.function.Function
 */
package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Function;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class BookModel
extends Model {
    private static final String LEFT_PAGES = "left_pages";
    private static final String RIGHT_PAGES = "right_pages";
    private static final String FLIP_PAGE_1 = "flip_page1";
    private static final String FLIP_PAGE_2 = "flip_page2";
    private final ModelPart root;
    private final ModelPart leftLid;
    private final ModelPart rightLid;
    private final ModelPart leftPages;
    private final ModelPart rightPages;
    private final ModelPart flipPage1;
    private final ModelPart flipPage2;

    public BookModel(ModelPart $$0) {
        super((Function<ResourceLocation, RenderType>)((Function)RenderType::entitySolid));
        this.root = $$0;
        this.leftLid = $$0.getChild("left_lid");
        this.rightLid = $$0.getChild("right_lid");
        this.leftPages = $$0.getChild(LEFT_PAGES);
        this.rightPages = $$0.getChild(RIGHT_PAGES);
        this.flipPage1 = $$0.getChild(FLIP_PAGE_1);
        this.flipPage2 = $$0.getChild(FLIP_PAGE_2);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild("left_lid", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0f, -5.0f, -0.005f, 6.0f, 10.0f, 0.005f), PartPose.offset(0.0f, 0.0f, -1.0f));
        $$1.addOrReplaceChild("right_lid", CubeListBuilder.create().texOffs(16, 0).addBox(0.0f, -5.0f, -0.005f, 6.0f, 10.0f, 0.005f), PartPose.offset(0.0f, 0.0f, 1.0f));
        $$1.addOrReplaceChild("seam", CubeListBuilder.create().texOffs(12, 0).addBox(-1.0f, -5.0f, 0.0f, 2.0f, 10.0f, 0.005f), PartPose.rotation(0.0f, 1.5707964f, 0.0f));
        $$1.addOrReplaceChild(LEFT_PAGES, CubeListBuilder.create().texOffs(0, 10).addBox(0.0f, -4.0f, -0.99f, 5.0f, 8.0f, 1.0f), PartPose.ZERO);
        $$1.addOrReplaceChild(RIGHT_PAGES, CubeListBuilder.create().texOffs(12, 10).addBox(0.0f, -4.0f, -0.01f, 5.0f, 8.0f, 1.0f), PartPose.ZERO);
        CubeListBuilder $$2 = CubeListBuilder.create().texOffs(24, 10).addBox(0.0f, -4.0f, 0.0f, 5.0f, 8.0f, 0.005f);
        $$1.addOrReplaceChild(FLIP_PAGE_1, $$2, PartPose.ZERO);
        $$1.addOrReplaceChild(FLIP_PAGE_2, $$2, PartPose.ZERO);
        return LayerDefinition.create($$0, 64, 32);
    }

    @Override
    public void renderToBuffer(PoseStack $$0, VertexConsumer $$1, int $$2, int $$3, float $$4, float $$5, float $$6, float $$7) {
        this.render($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    public void render(PoseStack $$0, VertexConsumer $$1, int $$2, int $$3, float $$4, float $$5, float $$6, float $$7) {
        this.root.render($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    public void setupAnim(float $$0, float $$1, float $$2, float $$3) {
        float $$4 = (Mth.sin($$0 * 0.02f) * 0.1f + 1.25f) * $$3;
        this.leftLid.yRot = (float)Math.PI + $$4;
        this.rightLid.yRot = -$$4;
        this.leftPages.yRot = $$4;
        this.rightPages.yRot = -$$4;
        this.flipPage1.yRot = $$4 - $$4 * 2.0f * $$1;
        this.flipPage2.yRot = $$4 - $$4 * 2.0f * $$2;
        this.leftPages.x = Mth.sin($$4);
        this.rightPages.x = Mth.sin($$4);
        this.flipPage1.x = Mth.sin($$4);
        this.flipPage2.x = Mth.sin($$4);
    }
}