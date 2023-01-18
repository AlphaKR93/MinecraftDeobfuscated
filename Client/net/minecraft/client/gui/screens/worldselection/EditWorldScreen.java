/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonIOException
 *  com.google.gson.stream.JsonWriter
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DataResult$PartialResult
 *  com.mojang.serialization.JsonOps
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  java.io.File
 *  java.io.IOException
 *  java.io.Writer
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.Throwable
 *  java.nio.charset.Charset
 *  java.nio.charset.StandardCharsets
 *  java.nio.file.Files
 *  java.nio.file.LinkOption
 *  java.nio.file.OpenOption
 *  java.nio.file.Path
 *  java.util.function.Consumer
 *  java.util.function.Function
 *  org.apache.commons.io.FileUtils
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screens.worldselection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonWriter;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.FileUtil;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.OptimizeWorldScreen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.WorldStem;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

public class EditWorldScreen
extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson WORLD_GEN_SETTINGS_GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create();
    private static final Component NAME_LABEL = Component.translatable("selectWorld.enterName");
    private Button renameButton;
    private final BooleanConsumer callback;
    private EditBox nameEdit;
    private final LevelStorageSource.LevelStorageAccess levelAccess;

    public EditWorldScreen(BooleanConsumer $$0, LevelStorageSource.LevelStorageAccess $$1) {
        super(Component.translatable("selectWorld.edit.title"));
        this.callback = $$0;
        this.levelAccess = $$1;
    }

    @Override
    public void tick() {
        this.nameEdit.tick();
    }

    @Override
    protected void init() {
        Button $$03 = this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.edit.resetIcon"), $$02 -> {
            this.levelAccess.getIconFile().ifPresent($$0 -> FileUtils.deleteQuietly((File)$$0.toFile()));
            $$02.active = false;
        }).bounds(this.width / 2 - 100, this.height / 4 + 0 + 5, 200, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.edit.openFolder"), $$0 -> Util.getPlatform().openFile(this.levelAccess.getLevelPath(LevelResource.ROOT).toFile())).bounds(this.width / 2 - 100, this.height / 4 + 24 + 5, 200, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.edit.backup"), $$0 -> {
            boolean $$1 = EditWorldScreen.makeBackupAndShowToast(this.levelAccess);
            this.callback.accept(!$$1);
        }).bounds(this.width / 2 - 100, this.height / 4 + 48 + 5, 200, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.edit.backupFolder"), $$0 -> {
            LevelStorageSource $$1 = this.minecraft.getLevelSource();
            Path $$2 = $$1.getBackupPath();
            try {
                FileUtil.createDirectoriesSafe($$2);
            }
            catch (IOException $$3) {
                throw new RuntimeException((Throwable)$$3);
            }
            Util.getPlatform().openFile($$2.toFile());
        }).bounds(this.width / 2 - 100, this.height / 4 + 72 + 5, 200, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.edit.optimize"), $$02 -> this.minecraft.setScreen(new BackupConfirmScreen(this, ($$0, $$1) -> {
            if ($$0) {
                EditWorldScreen.makeBackupAndShowToast(this.levelAccess);
            }
            this.minecraft.setScreen(OptimizeWorldScreen.create(this.minecraft, this.callback, this.minecraft.getFixerUpper(), this.levelAccess, $$1));
        }, Component.translatable("optimizeWorld.confirm.title"), Component.translatable("optimizeWorld.confirm.description"), true))).bounds(this.width / 2 - 100, this.height / 4 + 96 + 5, 200, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.edit.export_worldgen_settings"), $$02 -> {
            DataResult $$8;
            try (WorldStem $$1 = this.minecraft.createWorldOpenFlows().loadWorldStem(this.levelAccess, false);){
                RegistryAccess.Frozen $$2 = $$1.registries().compositeAccess();
                RegistryOps $$3 = RegistryOps.create(JsonOps.INSTANCE, $$2);
                DataResult $$4 = WorldGenSettings.encode($$3, $$1.worldData().worldGenOptions(), $$2);
                DataResult $$5 = $$4.flatMap($$0 -> {
                    Path $$1 = this.levelAccess.getLevelPath(LevelResource.ROOT).resolve("worldgen_settings_export.json");
                    try (JsonWriter $$2 = WORLD_GEN_SETTINGS_GSON.newJsonWriter((Writer)Files.newBufferedWriter((Path)$$1, (Charset)StandardCharsets.UTF_8, (OpenOption[])new OpenOption[0]));){
                        WORLD_GEN_SETTINGS_GSON.toJson($$0, $$2);
                    }
                    catch (JsonIOException | IOException $$3) {
                        return DataResult.error((String)("Error writing file: " + $$3.getMessage()));
                    }
                    return DataResult.success((Object)$$1.toString());
                });
            }
            catch (Exception $$7) {
                LOGGER.warn("Could not parse level data", (Throwable)$$7);
                $$8 = DataResult.error((String)("Could not parse level data: " + $$7.getMessage()));
            }
            MutableComponent $$9 = Component.literal((String)$$8.get().map(Function.identity(), DataResult.PartialResult::message));
            MutableComponent $$10 = Component.translatable($$8.result().isPresent() ? "selectWorld.edit.export_worldgen_settings.success" : "selectWorld.edit.export_worldgen_settings.failure");
            $$8.error().ifPresent($$0 -> LOGGER.error("Error exporting world settings: {}", $$0));
            this.minecraft.getToasts().addToast(SystemToast.multiline(this.minecraft, SystemToast.SystemToastIds.WORLD_GEN_SETTINGS_TRANSFER, $$10, $$9));
        }).bounds(this.width / 2 - 100, this.height / 4 + 120 + 5, 200, 20).build());
        this.renameButton = this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.edit.save"), $$0 -> this.onRename()).bounds(this.width / 2 - 100, this.height / 4 + 144 + 5, 98, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> this.callback.accept(false)).bounds(this.width / 2 + 2, this.height / 4 + 144 + 5, 98, 20).build());
        $$03.active = this.levelAccess.getIconFile().filter($$0 -> Files.isRegularFile((Path)$$0, (LinkOption[])new LinkOption[0])).isPresent();
        LevelSummary $$1 = this.levelAccess.getSummary();
        String $$2 = $$1 == null ? "" : $$1.getLevelName();
        this.nameEdit = new EditBox(this.font, this.width / 2 - 100, 38, 200, 20, Component.translatable("selectWorld.enterName"));
        this.nameEdit.setValue($$2);
        this.nameEdit.setResponder((Consumer<String>)((Consumer)$$0 -> {
            this.renameButton.active = !$$0.trim().isEmpty();
        }));
        this.addWidget(this.nameEdit);
        this.setInitialFocus(this.nameEdit);
    }

    @Override
    public void resize(Minecraft $$0, int $$1, int $$2) {
        String $$3 = this.nameEdit.getValue();
        this.init($$0, $$1, $$2);
        this.nameEdit.setValue($$3);
    }

    @Override
    public void onClose() {
        this.callback.accept(false);
    }

    private void onRename() {
        try {
            this.levelAccess.renameLevel(this.nameEdit.getValue().trim());
            this.callback.accept(true);
        }
        catch (IOException $$0) {
            LOGGER.error("Failed to access world '{}'", (Object)this.levelAccess.getLevelId(), (Object)$$0);
            SystemToast.onWorldAccessFailure(this.minecraft, this.levelAccess.getLevelId());
            this.callback.accept(true);
        }
    }

    public static void makeBackupAndShowToast(LevelStorageSource $$0, String $$1) {
        boolean $$2 = false;
        try (LevelStorageSource.LevelStorageAccess $$3 = $$0.createAccess($$1);){
            $$2 = true;
            EditWorldScreen.makeBackupAndShowToast($$3);
        }
        catch (IOException $$4) {
            if (!$$2) {
                SystemToast.onWorldAccessFailure(Minecraft.getInstance(), $$1);
            }
            LOGGER.warn("Failed to create backup of level {}", (Object)$$1, (Object)$$4);
        }
    }

    public static boolean makeBackupAndShowToast(LevelStorageSource.LevelStorageAccess $$0) {
        long $$1 = 0L;
        IOException $$2 = null;
        try {
            $$1 = $$0.makeWorldBackup();
        }
        catch (IOException $$3) {
            $$2 = $$3;
        }
        if ($$2 != null) {
            MutableComponent $$4 = Component.translatable("selectWorld.edit.backupFailed");
            MutableComponent $$5 = Component.literal($$2.getMessage());
            Minecraft.getInstance().getToasts().addToast(new SystemToast(SystemToast.SystemToastIds.WORLD_BACKUP, $$4, $$5));
            return false;
        }
        MutableComponent $$6 = Component.translatable("selectWorld.edit.backupCreated", $$0.getLevelId());
        MutableComponent $$7 = Component.translatable("selectWorld.edit.backupSize", Mth.ceil((double)$$1 / 1048576.0));
        Minecraft.getInstance().getToasts().addToast(new SystemToast(SystemToast.SystemToastIds.WORLD_BACKUP, $$6, $$7));
        return true;
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        EditWorldScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 15, 0xFFFFFF);
        EditWorldScreen.drawString($$0, this.font, NAME_LABEL, this.width / 2 - 100, 24, 0xA0A0A0);
        this.nameEdit.render($$0, $$1, $$2, $$3);
        super.render($$0, $$1, $$2, $$3);
    }
}