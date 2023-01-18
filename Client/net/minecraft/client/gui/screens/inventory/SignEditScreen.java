/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Function
 *  javax.annotation.Nullable
 *  org.joml.Vector3f
 */
package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

public class SignEditScreen
extends AbstractSignEditScreen {
    public static final float MAGIC_SCALE_NUMBER = 62.500004f;
    public static final float MAGIC_TEXT_SCALE = 0.9765628f;
    private static final Vector3f TEXT_SCALE = new Vector3f(0.9765628f, 0.9765628f, 0.9765628f);
    @Nullable
    private SignRenderer.SignModel signModel;

    public SignEditScreen(SignBlockEntity $$0, boolean $$1) {
        super($$0, $$1);
    }

    @Override
    protected void init() {
        super.init();
        this.signModel = SignRenderer.createSignModel(this.minecraft.getEntityModels(), this.woodType);
    }

    @Override
    protected void offsetSign(PoseStack $$0, BlockState $$1) {
        super.offsetSign($$0, $$1);
        boolean $$2 = $$1.getBlock() instanceof StandingSignBlock;
        if (!$$2) {
            $$0.translate(0.0f, 35.0f, 0.0f);
        }
    }

    @Override
    protected void renderSignBackground(PoseStack $$0, MultiBufferSource.BufferSource $$1, BlockState $$2) {
        if (this.signModel == null) {
            return;
        }
        boolean $$3 = $$2.getBlock() instanceof StandingSignBlock;
        $$0.translate(0.0f, 31.0f, 0.0f);
        $$0.scale(62.500004f, 62.500004f, -62.500004f);
        Material $$4 = Sheets.getSignMaterial(this.woodType);
        VertexConsumer $$5 = $$4.buffer($$1, (Function<ResourceLocation, RenderType>)((Function)this.signModel::renderType));
        this.signModel.stick.visible = $$3;
        this.signModel.root.render($$0, $$5, 0xF000F0, OverlayTexture.NO_OVERLAY);
    }

    @Override
    protected Vector3f getSignTextScale() {
        return TEXT_SCALE;
    }
}