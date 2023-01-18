/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.List
 *  java.util.Optional
 */
package net.minecraft.client.gui.spectator.categories;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.SpectatorMenuCategory;
import net.minecraft.client.gui.spectator.SpectatorMenuItem;
import net.minecraft.client.gui.spectator.categories.TeleportToPlayerMenuCategory;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public class TeleportToTeamMenuCategory
implements SpectatorMenuCategory,
SpectatorMenuItem {
    private static final Component TELEPORT_TEXT = Component.translatable("spectatorMenu.team_teleport");
    private static final Component TELEPORT_PROMPT = Component.translatable("spectatorMenu.team_teleport.prompt");
    private final List<SpectatorMenuItem> items;

    public TeleportToTeamMenuCategory() {
        Minecraft $$0 = Minecraft.getInstance();
        this.items = TeleportToTeamMenuCategory.createTeamEntries($$0, $$0.level.getScoreboard());
    }

    private static List<SpectatorMenuItem> createTeamEntries(Minecraft $$0, Scoreboard $$12) {
        return $$12.getPlayerTeams().stream().flatMap($$1 -> TeamSelectionItem.create($$0, $$1).stream()).toList();
    }

    @Override
    public List<SpectatorMenuItem> getItems() {
        return this.items;
    }

    @Override
    public Component getPrompt() {
        return TELEPORT_PROMPT;
    }

    @Override
    public void selectItem(SpectatorMenu $$0) {
        $$0.selectCategory(this);
    }

    @Override
    public Component getName() {
        return TELEPORT_TEXT;
    }

    @Override
    public void renderIcon(PoseStack $$0, float $$1, int $$2) {
        RenderSystem.setShaderTexture(0, SpectatorGui.SPECTATOR_LOCATION);
        GuiComponent.blit($$0, 0, 0, 16.0f, 0.0f, 16, 16, 256, 256);
    }

    @Override
    public boolean isEnabled() {
        return !this.items.isEmpty();
    }

    static class TeamSelectionItem
    implements SpectatorMenuItem {
        private final PlayerTeam team;
        private final ResourceLocation iconSkin;
        private final List<PlayerInfo> players;

        private TeamSelectionItem(PlayerTeam $$0, List<PlayerInfo> $$1, ResourceLocation $$2) {
            this.team = $$0;
            this.players = $$1;
            this.iconSkin = $$2;
        }

        public static Optional<SpectatorMenuItem> create(Minecraft $$0, PlayerTeam $$1) {
            ArrayList $$2 = new ArrayList();
            for (String $$3 : $$1.getPlayers()) {
                PlayerInfo $$4 = $$0.getConnection().getPlayerInfo($$3);
                if ($$4 == null || $$4.getGameMode() == GameType.SPECTATOR) continue;
                $$2.add((Object)$$4);
            }
            if ($$2.isEmpty()) {
                return Optional.empty();
            }
            GameProfile $$5 = ((PlayerInfo)$$2.get(RandomSource.create().nextInt($$2.size()))).getProfile();
            ResourceLocation $$6 = $$0.getSkinManager().getInsecureSkinLocation($$5);
            return Optional.of((Object)new TeamSelectionItem($$1, (List<PlayerInfo>)$$2, $$6));
        }

        @Override
        public void selectItem(SpectatorMenu $$0) {
            $$0.selectCategory(new TeleportToPlayerMenuCategory((Collection<PlayerInfo>)this.players));
        }

        @Override
        public Component getName() {
            return this.team.getDisplayName();
        }

        @Override
        public void renderIcon(PoseStack $$0, float $$1, int $$2) {
            Integer $$3 = this.team.getColor().getColor();
            if ($$3 != null) {
                float $$4 = (float)($$3 >> 16 & 0xFF) / 255.0f;
                float $$5 = (float)($$3 >> 8 & 0xFF) / 255.0f;
                float $$6 = (float)($$3 & 0xFF) / 255.0f;
                GuiComponent.fill($$0, 1, 1, 15, 15, Mth.color($$4 * $$1, $$5 * $$1, $$6 * $$1) | $$2 << 24);
            }
            RenderSystem.setShaderTexture(0, this.iconSkin);
            RenderSystem.setShaderColor($$1, $$1, $$1, (float)$$2 / 255.0f);
            PlayerFaceRenderer.draw($$0, 2, 2, 12);
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}