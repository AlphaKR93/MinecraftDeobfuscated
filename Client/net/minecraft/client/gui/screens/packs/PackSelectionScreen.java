/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.hash.Hashing
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
 *  java.nio.file.DirectoryStream
 *  java.nio.file.FileVisitOption
 *  java.nio.file.Files
 *  java.nio.file.LinkOption
 *  java.nio.file.Path
 *  java.nio.file.StandardWatchEventKinds
 *  java.nio.file.WatchEvent
 *  java.nio.file.WatchEvent$Kind
 *  java.nio.file.WatchKey
 *  java.nio.file.WatchService
 *  java.util.List
 *  java.util.Map
 *  java.util.function.Consumer
 *  java.util.function.Function
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screens.packs;

import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.client.gui.screens.packs.TransferableSelectionList;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.IoSupplier;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;

public class PackSelectionScreen
extends Screen {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final int LIST_WIDTH = 200;
    private static final Component DRAG_AND_DROP = Component.translatable("pack.dropInfo").withStyle(ChatFormatting.GRAY);
    private static final Component DIRECTORY_BUTTON_TOOLTIP = Component.translatable("pack.folderInfo");
    private static final int RELOAD_COOLDOWN = 20;
    private static final ResourceLocation DEFAULT_ICON = new ResourceLocation("textures/misc/unknown_pack.png");
    private final PackSelectionModel model;
    private final Screen lastScreen;
    @Nullable
    private Watcher watcher;
    private long ticksToReload;
    private TransferableSelectionList availablePackList;
    private TransferableSelectionList selectedPackList;
    private final Path packDir;
    private Button doneButton;
    private final Map<String, ResourceLocation> packIcons = Maps.newHashMap();

    public PackSelectionScreen(Screen $$0, PackRepository $$1, Consumer<PackRepository> $$2, Path $$3, Component $$4) {
        super($$4);
        this.lastScreen = $$0;
        this.model = new PackSelectionModel(this::populateLists, (Function<Pack, ResourceLocation>)((Function)this::getPackIcon), $$1, $$2);
        this.packDir = $$3;
        this.watcher = Watcher.create($$3);
    }

    @Override
    public void onClose() {
        this.model.commit();
        this.minecraft.setScreen(this.lastScreen);
        this.closeWatcher();
    }

    private void closeWatcher() {
        if (this.watcher != null) {
            try {
                this.watcher.close();
                this.watcher = null;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    @Override
    protected void init() {
        this.availablePackList = new TransferableSelectionList(this.minecraft, this, 200, this.height, Component.translatable("pack.available.title"));
        this.availablePackList.setLeftPos(this.width / 2 - 4 - 200);
        this.addWidget(this.availablePackList);
        this.selectedPackList = new TransferableSelectionList(this.minecraft, this, 200, this.height, Component.translatable("pack.selected.title"));
        this.selectedPackList.setLeftPos(this.width / 2 + 4);
        this.addWidget(this.selectedPackList);
        this.addRenderableWidget(Button.builder(Component.translatable("pack.openFolder"), $$0 -> Util.getPlatform().openUri(this.packDir.toUri())).bounds(this.width / 2 - 154, this.height - 48, 150, 20).tooltip(Tooltip.create(DIRECTORY_BUTTON_TOOLTIP)).build());
        this.doneButton = this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, $$0 -> this.onClose()).bounds(this.width / 2 + 4, this.height - 48, 150, 20).build());
        this.reload();
    }

    @Override
    public void tick() {
        if (this.watcher != null) {
            try {
                if (this.watcher.pollForChanges()) {
                    this.ticksToReload = 20L;
                }
            }
            catch (IOException $$0) {
                LOGGER.warn("Failed to poll for directory {} changes, stopping", (Object)this.packDir);
                this.closeWatcher();
            }
        }
        if (this.ticksToReload > 0L && --this.ticksToReload == 0L) {
            this.reload();
        }
    }

    private void populateLists() {
        this.updateList(this.selectedPackList, this.model.getSelected());
        this.updateList(this.availablePackList, this.model.getUnselected());
        this.doneButton.active = !this.selectedPackList.children().isEmpty();
    }

    private void updateList(TransferableSelectionList $$0, Stream<PackSelectionModel.Entry> $$1) {
        $$0.children().clear();
        TransferableSelectionList.PackEntry $$22 = (TransferableSelectionList.PackEntry)$$0.getSelected();
        String $$3 = $$22 == null ? "" : $$22.getPackId();
        $$0.setSelected(null);
        $$1.forEach($$2 -> {
            TransferableSelectionList.PackEntry $$3 = new TransferableSelectionList.PackEntry(this.minecraft, $$0, (PackSelectionModel.Entry)$$2);
            $$0.children().add((Object)$$3);
            if ($$2.getId().equals((Object)$$3)) {
                $$0.setSelected($$3);
            }
        });
    }

    public void updateFocus(PackSelectionModel.Entry $$0, TransferableSelectionList $$1) {
        TransferableSelectionList $$2 = this.selectedPackList == $$1 ? this.availablePackList : this.selectedPackList;
        this.changeFocus(ComponentPath.path($$2.getFirstElement(), new ContainerEventHandler[]{$$2, this}));
    }

    public void clearSelected() {
        this.selectedPackList.setSelected(null);
        this.availablePackList.setSelected(null);
    }

    private void reload() {
        this.model.findNewPacks();
        this.populateLists();
        this.ticksToReload = 0L;
        this.packIcons.clear();
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderDirtBackground($$0);
        this.availablePackList.render($$0, $$1, $$2, $$3);
        this.selectedPackList.render($$0, $$1, $$2, $$3);
        PackSelectionScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 8, 0xFFFFFF);
        PackSelectionScreen.drawCenteredString($$0, this.font, DRAG_AND_DROP, this.width / 2, 20, 0xFFFFFF);
        super.render($$0, $$1, $$2, $$3);
    }

    protected static void copyPacks(Minecraft $$0, List<Path> $$1, Path $$22) {
        MutableBoolean $$3 = new MutableBoolean();
        $$1.forEach($$2 -> {
            try (Stream $$32 = Files.walk((Path)$$2, (FileVisitOption[])new FileVisitOption[0]);){
                $$32.forEach($$3 -> {
                    try {
                        Util.copyBetweenDirs($$2.getParent(), $$22, $$3);
                    }
                    catch (IOException $$4) {
                        LOGGER.warn("Failed to copy datapack file  from {} to {}", new Object[]{$$3, $$22, $$4});
                        $$3.setTrue();
                    }
                });
            }
            catch (IOException $$4) {
                LOGGER.warn("Failed to copy datapack file from {} to {}", $$2, (Object)$$22);
                $$3.setTrue();
            }
        });
        if ($$3.isTrue()) {
            SystemToast.onPackCopyFailure($$0, $$22.toString());
        }
    }

    @Override
    public void onFilesDrop(List<Path> $$0) {
        String $$12 = (String)$$0.stream().map(Path::getFileName).map(Path::toString).collect(Collectors.joining((CharSequence)", "));
        this.minecraft.setScreen(new ConfirmScreen($$1 -> {
            if ($$1) {
                PackSelectionScreen.copyPacks(this.minecraft, $$0, this.packDir);
                this.reload();
            }
            this.minecraft.setScreen(this);
        }, Component.translatable("pack.dropConfirm"), Component.literal($$12)));
    }

    /*
     * Enabled aggressive exception aggregation
     */
    private ResourceLocation loadPackIcon(TextureManager $$0, Pack $$1) {
        try (PackResources $$2 = $$1.open();){
            ResourceLocation resourceLocation;
            block16: {
                IoSupplier<InputStream> $$3 = $$2.getRootResource("pack.png");
                if ($$3 == null) {
                    ResourceLocation resourceLocation2 = DEFAULT_ICON;
                    return resourceLocation2;
                }
                String $$4 = $$1.getId();
                ResourceLocation $$5 = new ResourceLocation("minecraft", "pack/" + Util.sanitizeName($$4, ResourceLocation::validPathChar) + "/" + Hashing.sha1().hashUnencodedChars((CharSequence)$$4) + "/icon");
                InputStream $$6 = $$3.get();
                try {
                    NativeImage $$7 = NativeImage.read($$6);
                    $$0.register($$5, (AbstractTexture)new DynamicTexture($$7));
                    resourceLocation = $$5;
                    if ($$6 == null) break block16;
                }
                catch (Throwable throwable) {
                    if ($$6 != null) {
                        try {
                            $$6.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                $$6.close();
            }
            return resourceLocation;
        }
        catch (Exception $$8) {
            LOGGER.warn("Failed to load icon from pack {}", (Object)$$1.getId(), (Object)$$8);
            return DEFAULT_ICON;
        }
    }

    private ResourceLocation getPackIcon(Pack $$0) {
        return (ResourceLocation)this.packIcons.computeIfAbsent((Object)$$0.getId(), $$1 -> this.loadPackIcon(this.minecraft.getTextureManager(), $$0));
    }

    static class Watcher
    implements AutoCloseable {
        private final WatchService watcher;
        private final Path packPath;

        public Watcher(Path $$0) throws IOException {
            this.packPath = $$0;
            this.watcher = $$0.getFileSystem().newWatchService();
            try {
                this.watchDir($$0);
                try (DirectoryStream $$1 = Files.newDirectoryStream((Path)$$0);){
                    for (Path $$2 : $$1) {
                        if (!Files.isDirectory((Path)$$2, (LinkOption[])new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) continue;
                        this.watchDir($$2);
                    }
                }
            }
            catch (Exception $$3) {
                this.watcher.close();
                throw $$3;
            }
        }

        @Nullable
        public static Watcher create(Path $$0) {
            try {
                return new Watcher($$0);
            }
            catch (IOException $$1) {
                LOGGER.warn("Failed to initialize pack directory {} monitoring", (Object)$$0, (Object)$$1);
                return null;
            }
        }

        private void watchDir(Path $$0) throws IOException {
            $$0.register(this.watcher, new WatchEvent.Kind[]{StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY});
        }

        public boolean pollForChanges() throws IOException {
            WatchKey $$1;
            boolean $$0 = false;
            while (($$1 = this.watcher.poll()) != null) {
                List $$2 = $$1.pollEvents();
                for (WatchEvent $$3 : $$2) {
                    Path $$4;
                    $$0 = true;
                    if ($$1.watchable() != this.packPath || $$3.kind() != StandardWatchEventKinds.ENTRY_CREATE || !Files.isDirectory((Path)($$4 = this.packPath.resolve((Path)$$3.context())), (LinkOption[])new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) continue;
                    this.watchDir($$4);
                }
                $$1.reset();
            }
            return $$0;
        }

        public void close() throws IOException {
            this.watcher.close();
        }
    }
}