/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.gui.spectator;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.SpectatorMenuItem;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;
import net.minecraft.resources.ResourceLocation;

public class PlayerMenuItem
implements SpectatorMenuItem {
    private final GameProfile profile;
    private final ResourceLocation location;
    private final Component name;

    public PlayerMenuItem(GameProfile $$0) {
        this.profile = $$0;
        Minecraft $$1 = Minecraft.getInstance();
        this.location = $$1.getSkinManager().getInsecureSkinLocation($$0);
        this.name = Component.literal($$0.getName());
    }

    @Override
    public void selectItem(SpectatorMenu $$0) {
        Minecraft.getInstance().getConnection().send(new ServerboundTeleportToEntityPacket(this.profile.getId()));
    }

    @Override
    public Component getName() {
        return this.name;
    }

    @Override
    public void renderIcon(PoseStack $$0, float $$1, int $$2) {
        RenderSystem.setShaderTexture(0, this.location);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, (float)$$2 / 255.0f);
        PlayerFaceRenderer.draw($$0, 2, 2, 12);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}