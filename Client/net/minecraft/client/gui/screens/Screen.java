/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.mojang.logging.LogUtils
 *  java.io.File
 *  java.lang.Long
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Record
 *  java.lang.Runnable
 *  java.lang.String
 *  java.lang.Throwable
 *  java.net.URI
 *  java.net.URISyntaxException
 *  java.nio.file.Path
 *  java.util.Arrays
 *  java.util.List
 *  java.util.Locale
 *  java.util.Optional
 *  java.util.Set
 *  java.util.concurrent.TimeUnit
 *  java.util.function.Consumer
 *  java.util.function.Supplier
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Vector2ic
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screens;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.ScreenNarrationCollector;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.joml.Matrix4f;
import org.joml.Vector2ic;
import org.slf4j.Logger;

public abstract class Screen
extends AbstractContainerEventHandler
implements Renderable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Set<String> ALLOWED_PROTOCOLS = Sets.newHashSet((Object[])new String[]{"http", "https"});
    private static final int EXTRA_SPACE_AFTER_FIRST_TOOLTIP_LINE = 2;
    private static final Component USAGE_NARRATION = Component.translatable("narrator.screen.usage");
    protected final Component title;
    private final List<GuiEventListener> children = Lists.newArrayList();
    private final List<NarratableEntry> narratables = Lists.newArrayList();
    @Nullable
    protected Minecraft minecraft;
    protected ItemRenderer itemRenderer;
    public int width;
    public int height;
    private final List<Renderable> renderables = Lists.newArrayList();
    public boolean passEvents;
    protected Font font;
    @Nullable
    private URI clickedLink;
    private static final long NARRATE_SUPPRESS_AFTER_INIT_TIME;
    private static final long NARRATE_DELAY_NARRATOR_ENABLED;
    private static final long NARRATE_DELAY_MOUSE_MOVE = 750L;
    private static final long NARRATE_DELAY_MOUSE_ACTION = 200L;
    private static final long NARRATE_DELAY_KEYBOARD_ACTION = 200L;
    private final ScreenNarrationCollector narrationState = new ScreenNarrationCollector();
    private long narrationSuppressTime = Long.MIN_VALUE;
    private long nextNarrationTime = Long.MAX_VALUE;
    @Nullable
    private NarratableEntry lastNarratable;
    @Nullable
    private DeferredTooltipRendering deferredTooltipRendering;

    protected Screen(Component $$0) {
        this.title = $$0;
    }

    public Component getTitle() {
        return this.title;
    }

    public Component getNarrationMessage() {
        return this.getTitle();
    }

    public final void renderWithTooltip(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.render($$0, $$1, $$2, $$3);
        if (this.deferredTooltipRendering != null) {
            this.renderTooltip($$0, this.deferredTooltipRendering, $$1, $$2);
            this.deferredTooltipRendering = null;
        }
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        for (Renderable $$4 : this.renderables) {
            $$4.render($$0, $$1, $$2, $$3);
        }
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        FocusNavigationEvent.TabNavigation $$3;
        if ($$0 == 256 && this.shouldCloseOnEsc()) {
            this.onClose();
            return true;
        }
        if (super.keyPressed($$0, $$1, $$2)) {
            return true;
        }
        switch ($$0) {
            case 263: {
                Record record = this.createArrowEvent(ScreenDirection.LEFT);
                break;
            }
            case 262: {
                Record record = this.createArrowEvent(ScreenDirection.RIGHT);
                break;
            }
            case 265: {
                Record record = this.createArrowEvent(ScreenDirection.UP);
                break;
            }
            case 264: {
                Record record = this.createArrowEvent(ScreenDirection.DOWN);
                break;
            }
            case 258: {
                Record record = this.createTabEvent();
                break;
            }
            default: {
                Record record = $$3 = null;
            }
        }
        if ($$3 != null) {
            ComponentPath $$4 = super.nextFocusPath((FocusNavigationEvent)$$3);
            if ($$4 == null && $$3 instanceof FocusNavigationEvent.TabNavigation) {
                this.clearFocus();
                $$4 = super.nextFocusPath((FocusNavigationEvent)$$3);
            }
            if ($$4 != null) {
                this.changeFocus($$4);
            }
        }
        return false;
    }

    private FocusNavigationEvent.TabNavigation createTabEvent() {
        boolean $$0 = !Screen.hasShiftDown();
        return new FocusNavigationEvent.TabNavigation($$0);
    }

    private FocusNavigationEvent.ArrowNavigation createArrowEvent(ScreenDirection $$0) {
        return new FocusNavigationEvent.ArrowNavigation($$0);
    }

    protected void setInitialFocus(GuiEventListener $$0) {
        ComponentPath $$1 = ComponentPath.path(this, $$0.nextFocusPath(new FocusNavigationEvent.InitialFocus()));
        if ($$1 != null) {
            this.changeFocus($$1);
        }
    }

    private void clearFocus() {
        ComponentPath $$0 = this.getCurrentFocusPath();
        if ($$0 != null) {
            $$0.applyFocus(false);
        }
    }

    @VisibleForTesting
    protected void changeFocus(ComponentPath $$0) {
        this.clearFocus();
        $$0.applyFocus(true);
    }

    public boolean shouldCloseOnEsc() {
        return true;
    }

    public void onClose() {
        this.minecraft.setScreen(null);
    }

    protected <T extends GuiEventListener & Renderable> T addRenderableWidget(T $$0) {
        this.renderables.add((Object)$$0);
        return this.addWidget($$0);
    }

    protected <T extends Renderable> T addRenderableOnly(T $$0) {
        this.renderables.add($$0);
        return $$0;
    }

    protected <T extends GuiEventListener & NarratableEntry> T addWidget(T $$0) {
        this.children.add($$0);
        this.narratables.add((Object)$$0);
        return $$0;
    }

    protected void removeWidget(GuiEventListener $$0) {
        if ($$0 instanceof Renderable) {
            this.renderables.remove((Object)((Renderable)((Object)$$0)));
        }
        if ($$0 instanceof NarratableEntry) {
            this.narratables.remove((Object)((NarratableEntry)((Object)$$0)));
        }
        this.children.remove((Object)$$0);
    }

    protected void clearWidgets() {
        this.renderables.clear();
        this.children.clear();
        this.narratables.clear();
    }

    protected void renderTooltip(PoseStack $$0, ItemStack $$1, int $$2, int $$3) {
        this.renderTooltip($$0, this.getTooltipFromItem($$1), $$1.getTooltipImage(), $$2, $$3);
    }

    public void renderTooltip(PoseStack $$0, List<Component> $$12, Optional<TooltipComponent> $$2, int $$3, int $$4) {
        List $$5 = (List)$$12.stream().map(Component::getVisualOrderText).map(ClientTooltipComponent::create).collect(Collectors.toList());
        $$2.ifPresent($$1 -> $$5.add(1, (Object)ClientTooltipComponent.create($$1)));
        this.renderTooltipInternal($$0, (List<ClientTooltipComponent>)$$5, $$3, $$4, DefaultTooltipPositioner.INSTANCE);
    }

    public List<Component> getTooltipFromItem(ItemStack $$0) {
        return $$0.getTooltipLines(this.minecraft.player, this.minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
    }

    public void renderTooltip(PoseStack $$0, Component $$1, int $$2, int $$3) {
        this.renderTooltip($$0, (List<? extends FormattedCharSequence>)Arrays.asList((Object[])new FormattedCharSequence[]{$$1.getVisualOrderText()}), $$2, $$3);
    }

    public void renderComponentTooltip(PoseStack $$0, List<Component> $$1, int $$2, int $$3) {
        this.renderTooltip($$0, (List<? extends FormattedCharSequence>)Lists.transform($$1, Component::getVisualOrderText), $$2, $$3);
    }

    public void renderTooltip(PoseStack $$0, List<? extends FormattedCharSequence> $$1, int $$2, int $$3) {
        this.renderTooltipInternal($$0, (List<ClientTooltipComponent>)((List)$$1.stream().map(ClientTooltipComponent::create).collect(Collectors.toList())), $$2, $$3, DefaultTooltipPositioner.INSTANCE);
    }

    private void renderTooltip(PoseStack $$0, DeferredTooltipRendering $$1, int $$2, int $$3) {
        this.renderTooltipInternal($$0, (List<ClientTooltipComponent>)((List)$$1.tooltip().stream().map(ClientTooltipComponent::create).collect(Collectors.toList())), $$2, $$3, $$1.positioner());
    }

    private void renderTooltipInternal(PoseStack $$02, List<ClientTooltipComponent> $$12, int $$22, int $$32, ClientTooltipPositioner $$42) {
        if ($$12.isEmpty()) {
            return;
        }
        int $$52 = 0;
        int $$62 = $$12.size() == 1 ? -2 : 0;
        for (ClientTooltipComponent $$72 : $$12) {
            int $$82 = $$72.getWidth(this.font);
            if ($$82 > $$52) {
                $$52 = $$82;
            }
            $$62 += $$72.getHeight();
        }
        int $$9 = $$22 + 12;
        int $$10 = $$32 - 12;
        int $$11 = $$52;
        int $$122 = $$62;
        Vector2ic $$13 = $$42.positionTooltip(this, $$9, $$10, $$11, $$122);
        $$9 = $$13.x();
        $$10 = $$13.y();
        $$02.pushPose();
        int $$14 = 400;
        float $$15 = this.itemRenderer.blitOffset;
        this.itemRenderer.blitOffset = 400.0f;
        Tesselator $$16 = Tesselator.getInstance();
        BufferBuilder $$17 = $$16.getBuilder();
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionColorShader));
        $$17.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f $$18 = $$02.last().pose();
        TooltipRenderUtil.renderTooltipBackground(($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8) -> GuiComponent.fillGradient($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8), $$18, $$17, $$9, $$10, $$11, $$122, 400);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        BufferUploader.drawWithShader($$17.end());
        RenderSystem.disableBlend();
        MultiBufferSource.BufferSource $$19 = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        $$02.translate(0.0f, 0.0f, 400.0f);
        int $$20 = $$10;
        for (int $$21 = 0; $$21 < $$12.size(); ++$$21) {
            ClientTooltipComponent $$222 = (ClientTooltipComponent)$$12.get($$21);
            $$222.renderText(this.font, $$9, $$20, $$18, $$19);
            $$20 += $$222.getHeight() + ($$21 == 0 ? 2 : 0);
        }
        $$19.endBatch();
        $$02.popPose();
        $$20 = $$10;
        for (int $$23 = 0; $$23 < $$12.size(); ++$$23) {
            ClientTooltipComponent $$24 = (ClientTooltipComponent)$$12.get($$23);
            $$24.renderImage(this.font, $$9, $$20, $$02, this.itemRenderer, 400);
            $$20 += $$24.getHeight() + ($$23 == 0 ? 2 : 0);
        }
        this.itemRenderer.blitOffset = $$15;
    }

    protected void renderComponentHoverEffect(PoseStack $$0, @Nullable Style $$1, int $$2, int $$3) {
        if ($$1 == null || $$1.getHoverEvent() == null) {
            return;
        }
        HoverEvent $$4 = $$1.getHoverEvent();
        HoverEvent.ItemStackInfo $$5 = $$4.getValue(HoverEvent.Action.SHOW_ITEM);
        if ($$5 != null) {
            this.renderTooltip($$0, $$5.getItemStack(), $$2, $$3);
        } else {
            HoverEvent.EntityTooltipInfo $$6 = $$4.getValue(HoverEvent.Action.SHOW_ENTITY);
            if ($$6 != null) {
                if (this.minecraft.options.advancedItemTooltips) {
                    this.renderComponentTooltip($$0, $$6.getTooltipLines(), $$2, $$3);
                }
            } else {
                Component $$7 = $$4.getValue(HoverEvent.Action.SHOW_TEXT);
                if ($$7 != null) {
                    this.renderTooltip($$0, this.minecraft.font.split($$7, Math.max((int)(this.width / 2), (int)200)), $$2, $$3);
                }
            }
        }
    }

    protected void insertText(String $$0, boolean $$1) {
    }

    public boolean handleComponentClicked(@Nullable Style $$0) {
        if ($$0 == null) {
            return false;
        }
        ClickEvent $$1 = $$0.getClickEvent();
        if (Screen.hasShiftDown()) {
            if ($$0.getInsertion() != null) {
                this.insertText($$0.getInsertion(), false);
            }
        } else if ($$1 != null) {
            block24: {
                if ($$1.getAction() == ClickEvent.Action.OPEN_URL) {
                    if (!this.minecraft.options.chatLinks().get().booleanValue()) {
                        return false;
                    }
                    try {
                        URI $$2 = new URI($$1.getValue());
                        String $$3 = $$2.getScheme();
                        if ($$3 == null) {
                            throw new URISyntaxException($$1.getValue(), "Missing protocol");
                        }
                        if (!ALLOWED_PROTOCOLS.contains((Object)$$3.toLowerCase(Locale.ROOT))) {
                            throw new URISyntaxException($$1.getValue(), "Unsupported protocol: " + $$3.toLowerCase(Locale.ROOT));
                        }
                        if (this.minecraft.options.chatLinksPrompt().get().booleanValue()) {
                            this.clickedLink = $$2;
                            this.minecraft.setScreen(new ConfirmLinkScreen(this::confirmLink, $$1.getValue(), false));
                            break block24;
                        }
                        this.openLink($$2);
                    }
                    catch (URISyntaxException $$4) {
                        LOGGER.error("Can't open url for {}", (Object)$$1, (Object)$$4);
                    }
                } else if ($$1.getAction() == ClickEvent.Action.OPEN_FILE) {
                    URI $$5 = new File($$1.getValue()).toURI();
                    this.openLink($$5);
                } else if ($$1.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
                    this.insertText(SharedConstants.filterText($$1.getValue()), true);
                } else if ($$1.getAction() == ClickEvent.Action.RUN_COMMAND) {
                    String $$6 = SharedConstants.filterText($$1.getValue());
                    if ($$6.startsWith("/")) {
                        if (!this.minecraft.player.connection.sendUnsignedCommand($$6.substring(1))) {
                            LOGGER.error("Not allowed to run command with signed argument from click event: '{}'", (Object)$$6);
                        }
                    } else {
                        LOGGER.error("Failed to run command without '/' prefix from click event: '{}'", (Object)$$6);
                    }
                } else if ($$1.getAction() == ClickEvent.Action.COPY_TO_CLIPBOARD) {
                    this.minecraft.keyboardHandler.setClipboard($$1.getValue());
                } else {
                    LOGGER.error("Don't know how to handle {}", (Object)$$1);
                }
            }
            return true;
        }
        return false;
    }

    public final void init(Minecraft $$0, int $$1, int $$2) {
        this.minecraft = $$0;
        this.itemRenderer = $$0.getItemRenderer();
        this.font = $$0.font;
        this.width = $$1;
        this.height = $$2;
        this.rebuildWidgets();
        this.triggerImmediateNarration(false);
        this.suppressNarration(NARRATE_SUPPRESS_AFTER_INIT_TIME);
    }

    protected void rebuildWidgets() {
        this.clearWidgets();
        this.clearFocus();
        this.init();
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return this.children;
    }

    protected void init() {
    }

    public void tick() {
    }

    public void removed() {
    }

    public void renderBackground(PoseStack $$0) {
        if (this.minecraft.level != null) {
            this.fillGradient($$0, 0, 0, this.width, this.height, -1072689136, -804253680);
        } else {
            this.renderDirtBackground($$0);
        }
    }

    public void renderDirtBackground(PoseStack $$0) {
        RenderSystem.setShaderTexture(0, BACKGROUND_LOCATION);
        RenderSystem.setShaderColor(0.25f, 0.25f, 0.25f, 1.0f);
        int $$1 = 32;
        Screen.blit($$0, 0, 0, 0, 0.0f, 0.0f, this.width, this.height, 32, 32);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public boolean isPauseScreen() {
        return true;
    }

    private void confirmLink(boolean $$0) {
        if ($$0) {
            this.openLink(this.clickedLink);
        }
        this.clickedLink = null;
        this.minecraft.setScreen(this);
    }

    private void openLink(URI $$0) {
        Util.getPlatform().openUri($$0);
    }

    public static boolean hasControlDown() {
        if (Minecraft.ON_OSX) {
            return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 343) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 347);
        }
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 341) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 345);
    }

    public static boolean hasShiftDown() {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344);
    }

    public static boolean hasAltDown() {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 342) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 346);
    }

    public static boolean isCut(int $$0) {
        return $$0 == 88 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    public static boolean isPaste(int $$0) {
        return $$0 == 86 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    public static boolean isCopy(int $$0) {
        return $$0 == 67 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    public static boolean isSelectAll(int $$0) {
        return $$0 == 65 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    public void resize(Minecraft $$0, int $$1, int $$2) {
        this.init($$0, $$1, $$2);
    }

    public static void wrapScreenError(Runnable $$0, String $$1, String $$2) {
        try {
            $$0.run();
        }
        catch (Throwable $$3) {
            CrashReport $$4 = CrashReport.forThrowable($$3, $$1);
            CrashReportCategory $$5 = $$4.addCategory("Affected screen");
            $$5.setDetail("Screen name", () -> $$2);
            throw new ReportedException($$4);
        }
    }

    protected boolean isValidCharacterForName(String $$0, char $$1, int $$2) {
        int $$3 = $$0.indexOf(58);
        int $$4 = $$0.indexOf(47);
        if ($$1 == ':') {
            return ($$4 == -1 || $$2 <= $$4) && $$3 == -1;
        }
        if ($$1 == '/') {
            return $$2 > $$3;
        }
        return $$1 == '_' || $$1 == '-' || $$1 >= 'a' && $$1 <= 'z' || $$1 >= '0' && $$1 <= '9' || $$1 == '.';
    }

    @Override
    public boolean isMouseOver(double $$0, double $$1) {
        return true;
    }

    public void onFilesDrop(List<Path> $$0) {
    }

    private void scheduleNarration(long $$0, boolean $$1) {
        this.nextNarrationTime = Util.getMillis() + $$0;
        if ($$1) {
            this.narrationSuppressTime = Long.MIN_VALUE;
        }
    }

    private void suppressNarration(long $$0) {
        this.narrationSuppressTime = Util.getMillis() + $$0;
    }

    public void afterMouseMove() {
        this.scheduleNarration(750L, false);
    }

    public void afterMouseAction() {
        this.scheduleNarration(200L, true);
    }

    public void afterKeyboardAction() {
        this.scheduleNarration(200L, true);
    }

    private boolean shouldRunNarration() {
        return this.minecraft.getNarrator().isActive();
    }

    public void handleDelayedNarration() {
        long $$0;
        if (this.shouldRunNarration() && ($$0 = Util.getMillis()) > this.nextNarrationTime && $$0 > this.narrationSuppressTime) {
            this.runNarration(true);
            this.nextNarrationTime = Long.MAX_VALUE;
        }
    }

    public void triggerImmediateNarration(boolean $$0) {
        if (this.shouldRunNarration()) {
            this.runNarration($$0);
        }
    }

    private void runNarration(boolean $$0) {
        this.narrationState.update((Consumer<NarrationElementOutput>)((Consumer)this::updateNarrationState));
        String $$1 = this.narrationState.collectNarrationText(!$$0);
        if (!$$1.isEmpty()) {
            this.minecraft.getNarrator().sayNow($$1);
        }
    }

    protected boolean shouldNarrateNavigation() {
        return true;
    }

    protected void updateNarrationState(NarrationElementOutput $$0) {
        $$0.add(NarratedElementType.TITLE, this.getNarrationMessage());
        if (this.shouldNarrateNavigation()) {
            $$0.add(NarratedElementType.USAGE, USAGE_NARRATION);
        }
        this.updateNarratedWidget($$0);
    }

    protected void updateNarratedWidget(NarrationElementOutput $$0) {
        ImmutableList $$1 = (ImmutableList)this.narratables.stream().filter(NarratableEntry::isActive).collect(ImmutableList.toImmutableList());
        NarratableSearchResult $$2 = Screen.findNarratableWidget((List<? extends NarratableEntry>)$$1, this.lastNarratable);
        if ($$2 != null) {
            if ($$2.priority.isTerminal()) {
                this.lastNarratable = $$2.entry;
            }
            if ($$1.size() > 1) {
                $$0.add(NarratedElementType.POSITION, (Component)Component.translatable("narrator.position.screen", $$2.index + 1, $$1.size()));
                if ($$2.priority == NarratableEntry.NarrationPriority.FOCUSED) {
                    $$0.add(NarratedElementType.USAGE, (Component)Component.translatable("narration.component_list.usage"));
                }
            }
            $$2.entry.updateNarration($$0.nest());
        }
    }

    @Nullable
    public static NarratableSearchResult findNarratableWidget(List<? extends NarratableEntry> $$0, @Nullable NarratableEntry $$1) {
        NarratableSearchResult $$2 = null;
        NarratableSearchResult $$3 = null;
        int $$5 = $$0.size();
        for (int $$4 = 0; $$4 < $$5; ++$$4) {
            NarratableEntry $$6 = (NarratableEntry)$$0.get($$4);
            NarratableEntry.NarrationPriority $$7 = $$6.narrationPriority();
            if ($$7.isTerminal()) {
                if ($$6 == $$1) {
                    $$3 = new NarratableSearchResult($$6, $$4, $$7);
                    continue;
                }
                return new NarratableSearchResult($$6, $$4, $$7);
            }
            if ($$7.compareTo($$2 != null ? $$2.priority : NarratableEntry.NarrationPriority.NONE) <= 0) continue;
            $$2 = new NarratableSearchResult($$6, $$4, $$7);
        }
        return $$2 != null ? $$2 : $$3;
    }

    public void narrationEnabled() {
        this.scheduleNarration(NARRATE_DELAY_NARRATOR_ENABLED, false);
    }

    public void setTooltipForNextRenderPass(List<FormattedCharSequence> $$0) {
        this.setTooltipForNextRenderPass($$0, DefaultTooltipPositioner.INSTANCE, true);
    }

    public void setTooltipForNextRenderPass(List<FormattedCharSequence> $$0, ClientTooltipPositioner $$1, boolean $$2) {
        if (this.deferredTooltipRendering == null || $$2) {
            this.deferredTooltipRendering = new DeferredTooltipRendering($$0, $$1);
        }
    }

    protected void setTooltipForNextRenderPass(Component $$0) {
        this.setTooltipForNextRenderPass(Tooltip.splitTooltip(this.minecraft, $$0));
    }

    public void setTooltipForNextRenderPass(Tooltip $$0, ClientTooltipPositioner $$1, boolean $$2) {
        this.setTooltipForNextRenderPass($$0.toCharSequence(this.minecraft), $$1, $$2);
    }

    protected static void hideWidgets(AbstractWidget ... $$0) {
        for (AbstractWidget $$1 : $$0) {
            $$1.visible = false;
        }
    }

    @Override
    public ScreenRectangle getRectangle() {
        return new ScreenRectangle(0, 0, this.width, this.height);
    }

    static {
        NARRATE_DELAY_NARRATOR_ENABLED = NARRATE_SUPPRESS_AFTER_INIT_TIME = TimeUnit.SECONDS.toMillis(2L);
    }

    record DeferredTooltipRendering(List<FormattedCharSequence> tooltip, ClientTooltipPositioner positioner) {
    }

    public static class NarratableSearchResult {
        public final NarratableEntry entry;
        public final int index;
        public final NarratableEntry.NarrationPriority priority;

        public NarratableSearchResult(NarratableEntry $$0, int $$1, NarratableEntry.NarrationPriority $$2) {
            this.entry = $$0;
            this.index = $$1;
            this.priority = $$2;
        }
    }
}