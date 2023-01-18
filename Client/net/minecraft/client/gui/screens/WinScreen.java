/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  java.io.BufferedReader
 *  java.io.IOException
 *  java.io.Reader
 *  java.lang.Exception
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runnable
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.List
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screens;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import org.slf4j.Logger;

public class WinScreen
extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation VIGNETTE_LOCATION = new ResourceLocation("textures/misc/vignette.png");
    private static final Component SECTION_HEADING = Component.literal("============").withStyle(ChatFormatting.WHITE);
    private static final String NAME_PREFIX = "           ";
    private static final String OBFUSCATE_TOKEN = "" + ChatFormatting.WHITE + ChatFormatting.OBFUSCATED + ChatFormatting.GREEN + ChatFormatting.AQUA;
    private static final float SPEEDUP_FACTOR = 5.0f;
    private static final float SPEEDUP_FACTOR_FAST = 15.0f;
    private final boolean poem;
    private final Runnable onFinished;
    private float scroll;
    private List<FormattedCharSequence> lines;
    private IntSet centeredLines;
    private int totalScrollLength;
    private boolean speedupActive;
    private final IntSet speedupModifiers = new IntOpenHashSet();
    private float scrollSpeed;
    private final float unmodifiedScrollSpeed;
    private final LogoRenderer logoRenderer;

    public WinScreen(boolean $$0, LogoRenderer $$1, Runnable $$2) {
        super(GameNarrator.NO_TITLE);
        this.poem = $$0;
        this.logoRenderer = $$1;
        this.onFinished = $$2;
        this.unmodifiedScrollSpeed = !$$0 ? 0.75f : 0.5f;
        this.scrollSpeed = this.unmodifiedScrollSpeed;
    }

    private float calculateScrollSpeed() {
        if (this.speedupActive) {
            return this.unmodifiedScrollSpeed * (5.0f + (float)this.speedupModifiers.size() * 15.0f);
        }
        return this.unmodifiedScrollSpeed;
    }

    @Override
    public void tick() {
        this.minecraft.getMusicManager().tick();
        this.minecraft.getSoundManager().tick(false);
        float $$0 = this.totalScrollLength + this.height + this.height + 24;
        if (this.scroll > $$0) {
            this.respawn();
        }
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if ($$0 == 341 || $$0 == 345) {
            this.speedupModifiers.add($$0);
        } else if ($$0 == 32) {
            this.speedupActive = true;
        }
        this.scrollSpeed = this.calculateScrollSpeed();
        return super.keyPressed($$0, $$1, $$2);
    }

    @Override
    public boolean keyReleased(int $$0, int $$1, int $$2) {
        if ($$0 == 32) {
            this.speedupActive = false;
        } else if ($$0 == 341 || $$0 == 345) {
            this.speedupModifiers.remove($$0);
        }
        this.scrollSpeed = this.calculateScrollSpeed();
        return super.keyReleased($$0, $$1, $$2);
    }

    @Override
    public void onClose() {
        this.respawn();
    }

    private void respawn() {
        this.onFinished.run();
        this.minecraft.setScreen(null);
    }

    @Override
    protected void init() {
        if (this.lines != null) {
            return;
        }
        this.lines = Lists.newArrayList();
        this.centeredLines = new IntOpenHashSet();
        if (this.poem) {
            this.wrapCreditsIO("texts/end.txt", this::addPoemFile);
        }
        this.wrapCreditsIO("texts/credits.json", this::addCreditsFile);
        if (this.poem) {
            this.wrapCreditsIO("texts/postcredits.txt", this::addPoemFile);
        }
        this.totalScrollLength = this.lines.size() * 12;
    }

    private void wrapCreditsIO(String $$0, CreditsReader $$1) {
        try (BufferedReader $$2 = this.minecraft.getResourceManager().openAsReader(new ResourceLocation($$0));){
            $$1.read((Reader)$$2);
        }
        catch (Exception $$3) {
            LOGGER.error("Couldn't load credits", (Throwable)$$3);
        }
    }

    private void addPoemFile(Reader $$0) throws IOException {
        String $$3;
        BufferedReader $$1 = new BufferedReader($$0);
        RandomSource $$2 = RandomSource.create(8124371L);
        while (($$3 = $$1.readLine()) != null) {
            int $$4;
            $$3 = $$3.replaceAll("PLAYERNAME", this.minecraft.getUser().getName());
            while (($$4 = $$3.indexOf(OBFUSCATE_TOKEN)) != -1) {
                String $$5 = $$3.substring(0, $$4);
                String $$6 = $$3.substring($$4 + OBFUSCATE_TOKEN.length());
                $$3 = $$5 + ChatFormatting.WHITE + ChatFormatting.OBFUSCATED + "XXXXXXXX".substring(0, $$2.nextInt(4) + 3) + $$6;
            }
            this.addPoemLines($$3);
            this.addEmptyLine();
        }
        for (int $$7 = 0; $$7 < 8; ++$$7) {
            this.addEmptyLine();
        }
    }

    private void addCreditsFile(Reader $$0) {
        JsonArray $$1 = GsonHelper.parseArray($$0);
        for (JsonElement $$2 : $$1) {
            JsonObject $$3 = $$2.getAsJsonObject();
            String $$4 = $$3.get("section").getAsString();
            this.addCreditsLine(SECTION_HEADING, true);
            this.addCreditsLine(Component.literal($$4).withStyle(ChatFormatting.YELLOW), true);
            this.addCreditsLine(SECTION_HEADING, true);
            this.addEmptyLine();
            this.addEmptyLine();
            JsonArray $$5 = $$3.getAsJsonArray("titles");
            for (JsonElement $$6 : $$5) {
                JsonObject $$7 = $$6.getAsJsonObject();
                String $$8 = $$7.get("title").getAsString();
                JsonArray $$9 = $$7.getAsJsonArray("names");
                this.addCreditsLine(Component.literal($$8).withStyle(ChatFormatting.GRAY), false);
                for (JsonElement $$10 : $$9) {
                    String $$11 = $$10.getAsString();
                    this.addCreditsLine(Component.literal(NAME_PREFIX).append($$11).withStyle(ChatFormatting.WHITE), false);
                }
                this.addEmptyLine();
                this.addEmptyLine();
            }
        }
    }

    private void addEmptyLine() {
        this.lines.add((Object)FormattedCharSequence.EMPTY);
    }

    private void addPoemLines(String $$0) {
        this.lines.addAll(this.minecraft.font.split(Component.literal($$0), 274));
    }

    private void addCreditsLine(Component $$0, boolean $$1) {
        if ($$1) {
            this.centeredLines.add(this.lines.size());
        }
        this.lines.add((Object)$$0.getVisualOrderText());
    }

    private void renderBg(PoseStack $$0) {
        RenderSystem.setShaderTexture(0, GuiComponent.BACKGROUND_LOCATION);
        int $$1 = this.width;
        float $$2 = this.scroll * 0.5f;
        int $$3 = 64;
        float $$4 = this.scroll / this.unmodifiedScrollSpeed;
        float $$5 = $$4 * 0.02f;
        float $$6 = (float)(this.totalScrollLength + this.height + this.height + 24) / this.unmodifiedScrollSpeed;
        float $$7 = ($$6 - 20.0f - $$4) * 0.005f;
        if ($$7 < $$5) {
            $$5 = $$7;
        }
        if ($$5 > 1.0f) {
            $$5 = 1.0f;
        }
        $$5 *= $$5;
        $$5 = $$5 * 96.0f / 255.0f;
        RenderSystem.setShaderColor($$5, $$5, $$5, 1.0f);
        WinScreen.blit($$0, 0, 0, this.getBlitOffset(), 0.0f, $$2, $$1, this.height, 64, 64);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.scroll += $$3 * this.scrollSpeed;
        this.renderBg($$0);
        int $$4 = this.width / 2 - 137;
        int $$5 = this.height + 50;
        float $$6 = -this.scroll;
        $$0.pushPose();
        $$0.translate(0.0f, $$6, 0.0f);
        this.logoRenderer.renderLogo($$0, this.width, $$3, $$5);
        int $$7 = $$5 + 100;
        for (int $$8 = 0; $$8 < this.lines.size(); ++$$8) {
            float $$9;
            if ($$8 == this.lines.size() - 1 && ($$9 = (float)$$7 + $$6 - (float)(this.height / 2 - 6)) < 0.0f) {
                $$0.translate(0.0f, -$$9, 0.0f);
            }
            if ((float)$$7 + $$6 + 12.0f + 8.0f > 0.0f && (float)$$7 + $$6 < (float)this.height) {
                FormattedCharSequence $$10 = (FormattedCharSequence)this.lines.get($$8);
                if (this.centeredLines.contains($$8)) {
                    this.font.drawShadow($$0, $$10, (float)($$4 + (274 - this.font.width($$10)) / 2), (float)$$7, 0xFFFFFF);
                } else {
                    this.font.drawShadow($$0, $$10, (float)$$4, (float)$$7, 0xFFFFFF);
                }
            }
            $$7 += 12;
        }
        $$0.popPose();
        RenderSystem.setShaderTexture(0, VIGNETTE_LOCATION);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR);
        WinScreen.blit($$0, 0, 0, this.getBlitOffset(), 0.0f, 0.0f, this.width, this.height, this.width, this.height);
        RenderSystem.disableBlend();
        super.render($$0, $$1, $$2, $$3);
    }

    @FunctionalInterface
    static interface CreditsReader {
        public void read(Reader var1) throws IOException;
    }
}