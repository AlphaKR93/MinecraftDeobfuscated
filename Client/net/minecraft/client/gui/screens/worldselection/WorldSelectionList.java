/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.hash.Hashing
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  java.io.IOException
 *  java.io.InputStream
 *  java.lang.AutoCloseable
 *  java.lang.CharSequence
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.nio.file.Files
 *  java.nio.file.LinkOption
 *  java.nio.file.OpenOption
 *  java.nio.file.Path
 *  java.text.DateFormat
 *  java.text.SimpleDateFormat
 *  java.util.Date
 *  java.util.List
 *  java.util.Locale
 *  java.util.Objects
 *  java.util.Optional
 *  java.util.concurrent.CancellationException
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.CompletionException
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.Validate
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screens.worldselection;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.ErrorScreen;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.LoadingDotsText;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.EditWorldScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

public class WorldSelectionList
extends ObjectSelectionList<Entry> {
    static final Logger LOGGER = LogUtils.getLogger();
    static final DateFormat DATE_FORMAT = new SimpleDateFormat();
    static final ResourceLocation ICON_MISSING = new ResourceLocation("textures/misc/unknown_server.png");
    static final ResourceLocation ICON_OVERLAY_LOCATION = new ResourceLocation("textures/gui/world_selection.png");
    static final Component FROM_NEWER_TOOLTIP_1 = Component.translatable("selectWorld.tooltip.fromNewerVersion1").withStyle(ChatFormatting.RED);
    static final Component FROM_NEWER_TOOLTIP_2 = Component.translatable("selectWorld.tooltip.fromNewerVersion2").withStyle(ChatFormatting.RED);
    static final Component SNAPSHOT_TOOLTIP_1 = Component.translatable("selectWorld.tooltip.snapshot1").withStyle(ChatFormatting.GOLD);
    static final Component SNAPSHOT_TOOLTIP_2 = Component.translatable("selectWorld.tooltip.snapshot2").withStyle(ChatFormatting.GOLD);
    static final Component WORLD_LOCKED_TOOLTIP = Component.translatable("selectWorld.locked").withStyle(ChatFormatting.RED);
    static final Component WORLD_REQUIRES_CONVERSION = Component.translatable("selectWorld.conversion.tooltip").withStyle(ChatFormatting.RED);
    private final SelectWorldScreen screen;
    private CompletableFuture<List<LevelSummary>> pendingLevels;
    @Nullable
    private List<LevelSummary> currentlyDisplayedLevels;
    private String filter;
    private final LoadingHeader loadingHeader;

    public WorldSelectionList(SelectWorldScreen $$0, Minecraft $$1, int $$2, int $$3, int $$4, int $$5, int $$6, String $$7, @Nullable WorldSelectionList $$8) {
        super($$1, $$2, $$3, $$4, $$5, $$6);
        this.screen = $$0;
        this.loadingHeader = new LoadingHeader($$1);
        this.filter = $$7;
        this.pendingLevels = $$8 != null ? $$8.pendingLevels : this.loadLevels();
        this.handleNewLevels(this.pollLevelsIgnoreErrors());
    }

    @Nullable
    private List<LevelSummary> pollLevelsIgnoreErrors() {
        try {
            return (List)this.pendingLevels.getNow(null);
        }
        catch (CancellationException | CompletionException $$0) {
            return null;
        }
    }

    void reloadWorldList() {
        this.pendingLevels = this.loadLevels();
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        List<LevelSummary> $$4 = this.pollLevelsIgnoreErrors();
        if ($$4 != this.currentlyDisplayedLevels) {
            this.handleNewLevels($$4);
        }
        super.render($$0, $$1, $$2, $$3);
    }

    private void handleNewLevels(@Nullable List<LevelSummary> $$0) {
        if ($$0 == null) {
            this.fillLoadingLevels();
        } else {
            this.fillLevels(this.filter, $$0);
        }
        this.currentlyDisplayedLevels = $$0;
    }

    public void updateFilter(String $$0) {
        if (this.currentlyDisplayedLevels != null && !$$0.equals((Object)this.filter)) {
            this.fillLevels($$0, this.currentlyDisplayedLevels);
        }
        this.filter = $$0;
    }

    /*
     * WARNING - void declaration
     */
    private CompletableFuture<List<LevelSummary>> loadLevels() {
        void $$2;
        try {
            LevelStorageSource.LevelCandidates $$02 = this.minecraft.getLevelSource().findLevelCandidates();
        }
        catch (LevelStorageException $$1) {
            LOGGER.error("Couldn't load level list", (Throwable)((Object)$$1));
            this.handleLevelLoadFailure($$1.getMessageComponent());
            return CompletableFuture.completedFuture((Object)List.of());
        }
        if ($$2.isEmpty()) {
            CreateWorldScreen.openFresh(this.minecraft, null);
            return CompletableFuture.completedFuture((Object)List.of());
        }
        return this.minecraft.getLevelSource().loadLevelSummaries((LevelStorageSource.LevelCandidates)$$2).exceptionally($$0 -> {
            this.minecraft.delayCrash(CrashReport.forThrowable($$0, "Couldn't load level list"));
            return List.of();
        });
    }

    private void fillLevels(String $$0, List<LevelSummary> $$1) {
        this.clearEntries();
        $$0 = $$0.toLowerCase(Locale.ROOT);
        for (LevelSummary $$2 : $$1) {
            if (!this.filterAccepts($$0, $$2)) continue;
            this.addEntry(new WorldListEntry(this, $$2));
        }
        this.notifyListUpdated();
    }

    private boolean filterAccepts(String $$0, LevelSummary $$1) {
        return $$1.getLevelName().toLowerCase(Locale.ROOT).contains((CharSequence)$$0) || $$1.getLevelId().toLowerCase(Locale.ROOT).contains((CharSequence)$$0);
    }

    private void fillLoadingLevels() {
        this.clearEntries();
        this.addEntry(this.loadingHeader);
        this.notifyListUpdated();
    }

    private void notifyListUpdated() {
        this.screen.triggerImmediateNarration(true);
    }

    private void handleLevelLoadFailure(Component $$0) {
        this.minecraft.setScreen(new ErrorScreen(Component.translatable("selectWorld.unable_to_load"), $$0));
    }

    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 20;
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 50;
    }

    @Override
    public void setSelected(@Nullable Entry $$0) {
        super.setSelected($$0);
        this.screen.updateButtonStatus($$0 != null && $$0.isSelectable());
    }

    public Optional<WorldListEntry> getSelectedOpt() {
        Entry $$0 = (Entry)this.getSelected();
        if ($$0 instanceof WorldListEntry) {
            WorldListEntry $$1 = (WorldListEntry)$$0;
            return Optional.of((Object)$$1);
        }
        return Optional.empty();
    }

    public SelectWorldScreen getScreen() {
        return this.screen;
    }

    @Override
    public void updateNarration(NarrationElementOutput $$0) {
        if (this.children().contains((Object)this.loadingHeader)) {
            this.loadingHeader.updateNarration($$0);
            return;
        }
        super.updateNarration($$0);
    }

    public static class LoadingHeader
    extends Entry {
        private static final Component LOADING_LABEL = Component.translatable("selectWorld.loading_list");
        private final Minecraft minecraft;

        public LoadingHeader(Minecraft $$0) {
            this.minecraft = $$0;
        }

        @Override
        public void render(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            int $$10 = (this.minecraft.screen.width - this.minecraft.font.width(LOADING_LABEL)) / 2;
            Objects.requireNonNull((Object)this.minecraft.font);
            int $$11 = $$2 + ($$5 - 9) / 2;
            this.minecraft.font.draw($$0, LOADING_LABEL, (float)$$10, (float)$$11, 0xFFFFFF);
            String $$12 = LoadingDotsText.get(Util.getMillis());
            int $$13 = (this.minecraft.screen.width - this.minecraft.font.width($$12)) / 2;
            Objects.requireNonNull((Object)this.minecraft.font);
            int $$14 = $$11 + 9;
            this.minecraft.font.draw($$0, $$12, (float)$$13, (float)$$14, 0x808080);
        }

        @Override
        public Component getNarration() {
            return LOADING_LABEL;
        }

        @Override
        public boolean isSelectable() {
            return false;
        }
    }

    public final class WorldListEntry
    extends Entry
    implements AutoCloseable {
        private static final int ICON_WIDTH = 32;
        private static final int ICON_HEIGHT = 32;
        private static final int ICON_OVERLAY_X_JOIN = 0;
        private static final int ICON_OVERLAY_X_JOIN_WITH_NOTIFY = 32;
        private static final int ICON_OVERLAY_X_WARNING = 64;
        private static final int ICON_OVERLAY_X_ERROR = 96;
        private static final int ICON_OVERLAY_Y_UNSELECTED = 0;
        private static final int ICON_OVERLAY_Y_SELECTED = 32;
        private final Minecraft minecraft;
        private final SelectWorldScreen screen;
        private final LevelSummary summary;
        private final ResourceLocation iconLocation;
        @Nullable
        private Path iconFile;
        @Nullable
        private final DynamicTexture icon;
        private long lastClickTime;

        public WorldListEntry(WorldSelectionList $$1, LevelSummary $$2) {
            this.minecraft = $$1.minecraft;
            this.screen = $$1.getScreen();
            this.summary = $$2;
            String $$3 = $$2.getLevelId();
            this.iconLocation = new ResourceLocation("minecraft", "worlds/" + Util.sanitizeName($$3, ResourceLocation::validPathChar) + "/" + Hashing.sha1().hashUnencodedChars((CharSequence)$$3) + "/icon");
            this.iconFile = $$2.getIcon();
            if (!Files.isRegularFile((Path)this.iconFile, (LinkOption[])new LinkOption[0])) {
                this.iconFile = null;
            }
            this.icon = this.loadServerIcon();
        }

        @Override
        public Component getNarration() {
            MutableComponent $$2;
            MutableComponent $$0 = Component.translatable("narrator.select.world", this.summary.getLevelName(), new Date(this.summary.getLastPlayed()), this.summary.isHardcore() ? Component.translatable("gameMode.hardcore") : Component.translatable("gameMode." + this.summary.getGameMode().getName()), this.summary.hasCheats() ? Component.translatable("selectWorld.cheats") : CommonComponents.EMPTY, this.summary.getWorldVersionName());
            if (this.summary.isLocked()) {
                MutableComponent $$1 = CommonComponents.joinForNarration($$0, WORLD_LOCKED_TOOLTIP);
            } else {
                $$2 = $$0;
            }
            return Component.translatable("narrator.select", $$2);
        }

        @Override
        public void render(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            String $$10 = this.summary.getLevelName();
            String $$11 = this.summary.getLevelId() + " (" + DATE_FORMAT.format(new Date(this.summary.getLastPlayed())) + ")";
            if (StringUtils.isEmpty((CharSequence)$$10)) {
                $$10 = I18n.get("selectWorld.world", new Object[0]) + " " + ($$1 + 1);
            }
            Component $$12 = this.summary.getInfo();
            this.minecraft.font.draw($$0, $$10, (float)($$3 + 32 + 3), (float)($$2 + 1), 0xFFFFFF);
            Font font = this.minecraft.font;
            float f = $$3 + 32 + 3;
            Objects.requireNonNull((Object)this.minecraft.font);
            font.draw($$0, $$11, f, (float)($$2 + 9 + 3), 0x808080);
            Font font2 = this.minecraft.font;
            float f2 = $$3 + 32 + 3;
            Objects.requireNonNull((Object)this.minecraft.font);
            Objects.requireNonNull((Object)this.minecraft.font);
            font2.draw($$0, $$12, f2, (float)($$2 + 9 + 9 + 3), 0x808080);
            RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
            RenderSystem.setShaderTexture(0, this.icon != null ? this.iconLocation : ICON_MISSING);
            RenderSystem.enableBlend();
            GuiComponent.blit($$0, $$3, $$2, 0.0f, 0.0f, 32, 32, 32, 32);
            RenderSystem.disableBlend();
            if (this.minecraft.options.touchscreen().get().booleanValue() || $$8) {
                int $$15;
                RenderSystem.setShaderTexture(0, ICON_OVERLAY_LOCATION);
                GuiComponent.fill($$0, $$3, $$2, $$3 + 32, $$2 + 32, -1601138544);
                RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
                int $$13 = $$6 - $$3;
                boolean $$14 = $$13 < 32;
                int n = $$15 = $$14 ? 32 : 0;
                if (this.summary.isLocked()) {
                    GuiComponent.blit($$0, $$3, $$2, 96.0f, $$15, 32, 32, 256, 256);
                    if ($$14) {
                        this.screen.setTooltipForNextRenderPass(this.minecraft.font.split(WORLD_LOCKED_TOOLTIP, 175));
                    }
                } else if (this.summary.requiresManualConversion()) {
                    GuiComponent.blit($$0, $$3, $$2, 96.0f, $$15, 32, 32, 256, 256);
                    if ($$14) {
                        this.screen.setTooltipForNextRenderPass(this.minecraft.font.split(WORLD_REQUIRES_CONVERSION, 175));
                    }
                } else if (this.summary.markVersionInList()) {
                    GuiComponent.blit($$0, $$3, $$2, 32.0f, $$15, 32, 32, 256, 256);
                    if (this.summary.askToOpenWorld()) {
                        GuiComponent.blit($$0, $$3, $$2, 96.0f, $$15, 32, 32, 256, 256);
                        if ($$14) {
                            this.screen.setTooltipForNextRenderPass((List<FormattedCharSequence>)ImmutableList.of((Object)FROM_NEWER_TOOLTIP_1.getVisualOrderText(), (Object)FROM_NEWER_TOOLTIP_2.getVisualOrderText()));
                        }
                    } else if (!SharedConstants.getCurrentVersion().isStable()) {
                        GuiComponent.blit($$0, $$3, $$2, 64.0f, $$15, 32, 32, 256, 256);
                        if ($$14) {
                            this.screen.setTooltipForNextRenderPass((List<FormattedCharSequence>)ImmutableList.of((Object)SNAPSHOT_TOOLTIP_1.getVisualOrderText(), (Object)SNAPSHOT_TOOLTIP_2.getVisualOrderText()));
                        }
                    }
                } else {
                    GuiComponent.blit($$0, $$3, $$2, 0.0f, $$15, 32, 32, 256, 256);
                }
            }
        }

        @Override
        public boolean mouseClicked(double $$0, double $$1, int $$2) {
            if (this.summary.isDisabled()) {
                return true;
            }
            WorldSelectionList.this.setSelected(this);
            this.screen.updateButtonStatus(WorldSelectionList.this.getSelectedOpt().isPresent());
            if ($$0 - (double)WorldSelectionList.this.getRowLeft() <= 32.0) {
                this.joinWorld();
                return true;
            }
            if (Util.getMillis() - this.lastClickTime < 250L) {
                this.joinWorld();
                return true;
            }
            this.lastClickTime = Util.getMillis();
            return true;
        }

        public void joinWorld() {
            if (this.summary.isDisabled()) {
                return;
            }
            LevelSummary.BackupStatus $$02 = this.summary.backupStatus();
            if ($$02.shouldBackup()) {
                String $$12 = "selectWorld.backupQuestion." + $$02.getTranslationKey();
                String $$2 = "selectWorld.backupWarning." + $$02.getTranslationKey();
                MutableComponent $$3 = Component.translatable($$12);
                if ($$02.isSevere()) {
                    $$3.withStyle(ChatFormatting.BOLD, ChatFormatting.RED);
                }
                MutableComponent $$4 = Component.translatable($$2, this.summary.getWorldVersionName(), SharedConstants.getCurrentVersion().getName());
                this.minecraft.setScreen(new BackupConfirmScreen(this.screen, ($$0, $$1) -> {
                    if ($$0) {
                        String $$2 = this.summary.getLevelId();
                        try (LevelStorageSource.LevelStorageAccess $$3 = this.minecraft.getLevelSource().createAccess($$2);){
                            EditWorldScreen.makeBackupAndShowToast($$3);
                        }
                        catch (IOException $$4) {
                            SystemToast.onWorldAccessFailure(this.minecraft, $$2);
                            LOGGER.error("Failed to backup level {}", (Object)$$2, (Object)$$4);
                        }
                    }
                    this.loadWorld();
                }, $$3, $$4, false));
            } else if (this.summary.askToOpenWorld()) {
                this.minecraft.setScreen(new ConfirmScreen($$0 -> {
                    if ($$0) {
                        try {
                            this.loadWorld();
                        }
                        catch (Exception $$1) {
                            LOGGER.error("Failure to open 'future world'", (Throwable)$$1);
                            this.minecraft.setScreen(new AlertScreen(() -> this.minecraft.setScreen(this.screen), Component.translatable("selectWorld.futureworld.error.title"), Component.translatable("selectWorld.futureworld.error.text")));
                        }
                    } else {
                        this.minecraft.setScreen(this.screen);
                    }
                }, Component.translatable("selectWorld.versionQuestion"), Component.translatable("selectWorld.versionWarning", this.summary.getWorldVersionName()), Component.translatable("selectWorld.versionJoinButton"), CommonComponents.GUI_CANCEL));
            } else {
                this.loadWorld();
            }
        }

        public void deleteWorld() {
            this.minecraft.setScreen(new ConfirmScreen($$0 -> {
                if ($$0) {
                    this.minecraft.setScreen(new ProgressScreen(true));
                    this.doDeleteWorld();
                }
                this.minecraft.setScreen(this.screen);
            }, Component.translatable("selectWorld.deleteQuestion"), Component.translatable("selectWorld.deleteWarning", this.summary.getLevelName()), Component.translatable("selectWorld.deleteButton"), CommonComponents.GUI_CANCEL));
        }

        public void doDeleteWorld() {
            LevelStorageSource $$0 = this.minecraft.getLevelSource();
            String $$1 = this.summary.getLevelId();
            try (LevelStorageSource.LevelStorageAccess $$2 = $$0.createAccess($$1);){
                $$2.deleteLevel();
            }
            catch (IOException $$3) {
                SystemToast.onWorldDeleteFailure(this.minecraft, $$1);
                LOGGER.error("Failed to delete world {}", (Object)$$1, (Object)$$3);
            }
            WorldSelectionList.this.reloadWorldList();
        }

        public void editWorld() {
            this.queueLoadScreen();
            String $$0 = this.summary.getLevelId();
            try {
                LevelStorageSource.LevelStorageAccess $$1 = this.minecraft.getLevelSource().createAccess($$0);
                this.minecraft.setScreen(new EditWorldScreen($$2 -> {
                    try {
                        $$1.close();
                    }
                    catch (IOException $$3) {
                        LOGGER.error("Failed to unlock level {}", (Object)$$0, (Object)$$3);
                    }
                    if ($$2) {
                        WorldSelectionList.this.reloadWorldList();
                    }
                    this.minecraft.setScreen(this.screen);
                }, $$1));
            }
            catch (IOException $$22) {
                SystemToast.onWorldAccessFailure(this.minecraft, $$0);
                LOGGER.error("Failed to access level {}", (Object)$$0, (Object)$$22);
                WorldSelectionList.this.reloadWorldList();
            }
        }

        public void recreateWorld() {
            this.queueLoadScreen();
            try (LevelStorageSource.LevelStorageAccess $$0 = this.minecraft.getLevelSource().createAccess(this.summary.getLevelId());){
                Pair<LevelSettings, WorldCreationContext> $$1 = this.minecraft.createWorldOpenFlows().recreateWorldData($$0);
                LevelSettings $$2 = (LevelSettings)$$1.getFirst();
                WorldCreationContext $$32 = (WorldCreationContext)((Object)$$1.getSecond());
                Path $$4 = CreateWorldScreen.createTempDataPackDirFromExistingWorld($$0.getLevelPath(LevelResource.DATAPACK_DIR), this.minecraft);
                if ($$32.options().isOldCustomizedWorld()) {
                    this.minecraft.setScreen(new ConfirmScreen($$3 -> this.minecraft.setScreen($$3 ? CreateWorldScreen.createFromExisting(this.screen, $$2, $$32, $$4) : this.screen), Component.translatable("selectWorld.recreate.customized.title"), Component.translatable("selectWorld.recreate.customized.text"), CommonComponents.GUI_PROCEED, CommonComponents.GUI_CANCEL));
                } else {
                    this.minecraft.setScreen(CreateWorldScreen.createFromExisting(this.screen, $$2, $$32, $$4));
                }
            }
            catch (Exception $$5) {
                LOGGER.error("Unable to recreate world", (Throwable)$$5);
                this.minecraft.setScreen(new AlertScreen(() -> this.minecraft.setScreen(this.screen), Component.translatable("selectWorld.recreate.error.title"), Component.translatable("selectWorld.recreate.error.text")));
            }
        }

        private void loadWorld() {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            if (this.minecraft.getLevelSource().levelExists(this.summary.getLevelId())) {
                this.queueLoadScreen();
                this.minecraft.createWorldOpenFlows().loadLevel(this.screen, this.summary.getLevelId());
            }
        }

        private void queueLoadScreen() {
            this.minecraft.forceSetScreen(new GenericDirtMessageScreen(Component.translatable("selectWorld.data_read")));
        }

        @Nullable
        private DynamicTexture loadServerIcon() {
            boolean $$0;
            boolean bl = $$0 = this.iconFile != null && Files.isRegularFile((Path)this.iconFile, (LinkOption[])new LinkOption[0]);
            if ($$0) {
                DynamicTexture dynamicTexture;
                block9: {
                    InputStream $$1 = Files.newInputStream((Path)this.iconFile, (OpenOption[])new OpenOption[0]);
                    try {
                        NativeImage $$2 = NativeImage.read($$1);
                        Validate.validState(($$2.getWidth() == 64 ? 1 : 0) != 0, (String)"Must be 64 pixels wide", (Object[])new Object[0]);
                        Validate.validState(($$2.getHeight() == 64 ? 1 : 0) != 0, (String)"Must be 64 pixels high", (Object[])new Object[0]);
                        DynamicTexture $$3 = new DynamicTexture($$2);
                        this.minecraft.getTextureManager().register(this.iconLocation, (AbstractTexture)$$3);
                        dynamicTexture = $$3;
                        if ($$1 == null) break block9;
                    }
                    catch (Throwable throwable) {
                        try {
                            if ($$1 != null) {
                                try {
                                    $$1.close();
                                }
                                catch (Throwable throwable2) {
                                    throwable.addSuppressed(throwable2);
                                }
                            }
                            throw throwable;
                        }
                        catch (Throwable $$4) {
                            LOGGER.error("Invalid icon for world {}", (Object)this.summary.getLevelId(), (Object)$$4);
                            this.iconFile = null;
                            return null;
                        }
                    }
                    $$1.close();
                }
                return dynamicTexture;
            }
            this.minecraft.getTextureManager().release(this.iconLocation);
            return null;
        }

        @Override
        public void close() {
            if (this.icon != null) {
                this.icon.close();
            }
        }

        public String getLevelName() {
            return this.summary.getLevelName();
        }

        @Override
        public boolean isSelectable() {
            return !this.summary.isDisabled();
        }
    }

    public static abstract class Entry
    extends ObjectSelectionList.Entry<Entry>
    implements AutoCloseable {
        public abstract boolean isSelectable();

        public void close() {
        }
    }
}