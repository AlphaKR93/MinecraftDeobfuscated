/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  org.joml.Vector3f
 */
package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

public class HangingSignEditScreen
extends AbstractSignEditScreen {
    public static final float MAGIC_BACKGROUND_SCALE = 4.0f;
    private static final Vector3f TEXT_SCALE = new Vector3f(1.0f, 1.0f, 1.0f);
    private static final int TEXTURE_WIDTH = 16;
    private static final int TEXTURE_HEIGHT = 16;
    private final ResourceLocation texture;

    public HangingSignEditScreen(SignBlockEntity $$0, boolean $$1) {
        super($$0, $$1, Component.translatable("hanging_sign.edit"));
        this.texture = new ResourceLocation("textures/gui/hanging_signs/" + this.woodType.name() + ".png");
    }

    @Override
    protected void offsetSign(PoseStack $$0, BlockState $$1) {
        $$0.translate((float)this.width / 2.0f, 125.0f, 50.0f);
    }

    @Override
    protected void renderSignBackground(PoseStack $$0, MultiBufferSource.BufferSource $$1, BlockState $$2) {
        $$0.translate(0.0f, -13.0f, 0.0f);
        RenderSystem.setShaderTexture(0, this.texture);
        $$0.scale(4.0f, 4.0f, 1.0f);
        HangingSignEditScreen.blit($$0, -8, -8, 0.0f, 0.0f, 16, 16, 16, 16);
    }

    @Override
    protected Vector3f getSignTextScale() {
        return TEXT_SCALE;
    }
}