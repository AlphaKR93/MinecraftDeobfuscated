/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  java.util.Optional
 *  java.util.function.Supplier
 */
package net.minecraft.client.gui.screens.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;

public class GameModeSwitcherScreen
extends Screen {
    static final ResourceLocation GAMEMODE_SWITCHER_LOCATION = new ResourceLocation("textures/gui/container/gamemode_switcher.png");
    private static final int SPRITE_SHEET_WIDTH = 128;
    private static final int SPRITE_SHEET_HEIGHT = 128;
    private static final int SLOT_AREA = 26;
    private static final int SLOT_PADDING = 5;
    private static final int SLOT_AREA_PADDED = 31;
    private static final int HELP_TIPS_OFFSET_Y = 5;
    private static final int ALL_SLOTS_WIDTH = GameModeIcon.values().length * 31 - 5;
    private static final Component SELECT_KEY = Component.translatable("debug.gamemodes.select_next", Component.translatable("debug.gamemodes.press_f4").withStyle(ChatFormatting.AQUA));
    private final Optional<GameModeIcon> previousHovered;
    private Optional<GameModeIcon> currentlyHovered = Optional.empty();
    private int firstMouseX;
    private int firstMouseY;
    private boolean setFirstMousePos;
    private final List<GameModeSlot> slots = Lists.newArrayList();

    public GameModeSwitcherScreen() {
        super(GameNarrator.NO_TITLE);
        this.previousHovered = GameModeIcon.getFromGameType(this.getDefaultSelected());
    }

    private GameType getDefaultSelected() {
        MultiPlayerGameMode $$0 = Minecraft.getInstance().gameMode;
        GameType $$1 = $$0.getPreviousPlayerMode();
        if ($$1 != null) {
            return $$1;
        }
        return $$0.getPlayerMode() == GameType.CREATIVE ? GameType.SURVIVAL : GameType.CREATIVE;
    }

    @Override
    protected void init() {
        super.init();
        this.currentlyHovered = this.previousHovered.isPresent() ? this.previousHovered : GameModeIcon.getFromGameType(this.minecraft.gameMode.getPlayerMode());
        for (int $$0 = 0; $$0 < GameModeIcon.VALUES.length; ++$$0) {
            GameModeIcon $$1 = GameModeIcon.VALUES[$$0];
            this.slots.add((Object)new GameModeSlot($$1, this.width / 2 - ALL_SLOTS_WIDTH / 2 + $$0 * 31, this.height / 2 - 31));
        }
    }

    @Override
    public void render(PoseStack $$0, int $$12, int $$2, float $$3) {
        if (this.checkToClose()) {
            return;
        }
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        $$0.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, GAMEMODE_SWITCHER_LOCATION);
        int $$4 = this.width / 2 - 62;
        int $$5 = this.height / 2 - 31 - 27;
        GameModeSwitcherScreen.blit($$0, $$4, $$5, 0.0f, 0.0f, 125, 75, 128, 128);
        $$0.popPose();
        super.render($$0, $$12, $$2, $$3);
        this.currentlyHovered.ifPresent($$1 -> GameModeSwitcherScreen.drawCenteredString($$0, this.font, $$1.getName(), this.width / 2, this.height / 2 - 31 - 20, -1));
        GameModeSwitcherScreen.drawCenteredString($$0, this.font, SELECT_KEY, this.width / 2, this.height / 2 + 5, 0xFFFFFF);
        if (!this.setFirstMousePos) {
            this.firstMouseX = $$12;
            this.firstMouseY = $$2;
            this.setFirstMousePos = true;
        }
        boolean $$6 = this.firstMouseX == $$12 && this.firstMouseY == $$2;
        for (GameModeSlot $$7 : this.slots) {
            $$7.render($$0, $$12, $$2, $$3);
            this.currentlyHovered.ifPresent($$1 -> $$7.setSelected($$1 == $$0.icon));
            if ($$6 || !$$7.isHoveredOrFocused()) continue;
            this.currentlyHovered = Optional.of((Object)((Object)$$7.icon));
        }
    }

    private void switchToHoveredGameMode() {
        GameModeSwitcherScreen.switchToHoveredGameMode(this.minecraft, this.currentlyHovered);
    }

    private static void switchToHoveredGameMode(Minecraft $$0, Optional<GameModeIcon> $$1) {
        if ($$0.gameMode == null || $$0.player == null || !$$1.isPresent()) {
            return;
        }
        Optional<GameModeIcon> $$2 = GameModeIcon.getFromGameType($$0.gameMode.getPlayerMode());
        GameModeIcon $$3 = (GameModeIcon)((Object)$$1.get());
        if ($$2.isPresent() && $$0.player.hasPermissions(2) && $$3 != $$2.get()) {
            $$0.player.connection.sendUnsignedCommand($$3.getCommand());
        }
    }

    private boolean checkToClose() {
        if (!InputConstants.isKeyDown(this.minecraft.getWindow().getWindow(), 292)) {
            this.switchToHoveredGameMode();
            this.minecraft.setScreen(null);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if ($$0 == 293 && this.currentlyHovered.isPresent()) {
            this.setFirstMousePos = false;
            this.currentlyHovered = ((GameModeIcon)((Object)this.currentlyHovered.get())).getNext();
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    static enum GameModeIcon {
        CREATIVE(Component.translatable("gameMode.creative"), "gamemode creative", new ItemStack(Blocks.GRASS_BLOCK)),
        SURVIVAL(Component.translatable("gameMode.survival"), "gamemode survival", new ItemStack(Items.IRON_SWORD)),
        ADVENTURE(Component.translatable("gameMode.adventure"), "gamemode adventure", new ItemStack(Items.MAP)),
        SPECTATOR(Component.translatable("gameMode.spectator"), "gamemode spectator", new ItemStack(Items.ENDER_EYE));

        protected static final GameModeIcon[] VALUES;
        private static final int ICON_AREA = 16;
        protected static final int ICON_TOP_LEFT = 5;
        final Component name;
        final String command;
        final ItemStack renderStack;

        private GameModeIcon(Component $$0, String $$1, ItemStack $$2) {
            this.name = $$0;
            this.command = $$1;
            this.renderStack = $$2;
        }

        void drawIcon(ItemRenderer $$0, int $$1, int $$2) {
            $$0.renderAndDecorateItem(this.renderStack, $$1, $$2);
        }

        Component getName() {
            return this.name;
        }

        String getCommand() {
            return this.command;
        }

        Optional<GameModeIcon> getNext() {
            switch (this) {
                case CREATIVE: {
                    return Optional.of((Object)((Object)SURVIVAL));
                }
                case SURVIVAL: {
                    return Optional.of((Object)((Object)ADVENTURE));
                }
                case ADVENTURE: {
                    return Optional.of((Object)((Object)SPECTATOR));
                }
            }
            return Optional.of((Object)((Object)CREATIVE));
        }

        static Optional<GameModeIcon> getFromGameType(GameType $$0) {
            switch ($$0) {
                case SPECTATOR: {
                    return Optional.of((Object)((Object)SPECTATOR));
                }
                case SURVIVAL: {
                    return Optional.of((Object)((Object)SURVIVAL));
                }
                case CREATIVE: {
                    return Optional.of((Object)((Object)CREATIVE));
                }
                case ADVENTURE: {
                    return Optional.of((Object)((Object)ADVENTURE));
                }
            }
            return Optional.empty();
        }

        static {
            VALUES = GameModeIcon.values();
        }
    }

    public class GameModeSlot
    extends AbstractWidget {
        final GameModeIcon icon;
        private boolean isSelected;

        public GameModeSlot(GameModeIcon $$1, int $$2, int $$3) {
            super($$2, $$3, 26, 26, $$1.getName());
            this.icon = $$1;
        }

        @Override
        public void renderWidget(PoseStack $$0, int $$1, int $$2, float $$3) {
            Minecraft $$4 = Minecraft.getInstance();
            this.drawSlot($$0, $$4.getTextureManager());
            this.icon.drawIcon(GameModeSwitcherScreen.this.itemRenderer, this.getX() + 5, this.getY() + 5);
            if (this.isSelected) {
                this.drawSelection($$0, $$4.getTextureManager());
            }
        }

        @Override
        public void updateWidgetNarration(NarrationElementOutput $$0) {
            this.defaultButtonNarrationText($$0);
        }

        @Override
        public boolean isHoveredOrFocused() {
            return super.isHoveredOrFocused() || this.isSelected;
        }

        public void setSelected(boolean $$0) {
            this.isSelected = $$0;
        }

        private void drawSlot(PoseStack $$0, TextureManager $$1) {
            RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
            RenderSystem.setShaderTexture(0, GAMEMODE_SWITCHER_LOCATION);
            $$0.pushPose();
            $$0.translate(this.getX(), this.getY(), 0.0f);
            GameModeSlot.blit($$0, 0, 0, 0.0f, 75.0f, 26, 26, 128, 128);
            $$0.popPose();
        }

        private void drawSelection(PoseStack $$0, TextureManager $$1) {
            RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
            RenderSystem.setShaderTexture(0, GAMEMODE_SWITCHER_LOCATION);
            $$0.pushPose();
            $$0.translate(this.getX(), this.getY(), 0.0f);
            GameModeSlot.blit($$0, 0, 0, 26.0f, 75.0f, 26, 26, 128, 128);
            $$0.popPose();
        }
    }
}