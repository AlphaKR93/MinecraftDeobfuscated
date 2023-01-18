/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.Objects
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screens.worldselection;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.WorldStem;
import net.minecraft.util.Mth;
import net.minecraft.util.worldupdate.WorldUpgrader;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import org.slf4j.Logger;

public class OptimizeWorldScreen
extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Object2IntMap<ResourceKey<Level>> DIMENSION_COLORS = (Object2IntMap)Util.make(new Object2IntOpenCustomHashMap(Util.identityStrategy()), $$0 -> {
        $$0.put(Level.OVERWORLD, -13408734);
        $$0.put(Level.NETHER, -10075085);
        $$0.put(Level.END, -8943531);
        $$0.defaultReturnValue(-2236963);
    });
    private final BooleanConsumer callback;
    private final WorldUpgrader upgrader;

    @Nullable
    public static OptimizeWorldScreen create(Minecraft $$0, BooleanConsumer $$1, DataFixer $$2, LevelStorageSource.LevelStorageAccess $$3, boolean $$4) {
        OptimizeWorldScreen optimizeWorldScreen;
        block8: {
            WorldStem $$5 = $$0.createWorldOpenFlows().loadWorldStem($$3, false);
            try {
                WorldData $$6 = $$5.worldData();
                RegistryAccess.Frozen $$7 = $$5.registries().compositeAccess();
                $$3.saveDataTag($$7, $$6);
                optimizeWorldScreen = new OptimizeWorldScreen($$1, $$2, $$3, $$6.getLevelSettings(), $$4, $$7.registryOrThrow(Registries.LEVEL_STEM));
                if ($$5 == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if ($$5 != null) {
                        try {
                            $$5.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (Exception $$8) {
                    LOGGER.warn("Failed to load datapacks, can't optimize world", (Throwable)$$8);
                    return null;
                }
            }
            $$5.close();
        }
        return optimizeWorldScreen;
    }

    private OptimizeWorldScreen(BooleanConsumer $$0, DataFixer $$1, LevelStorageSource.LevelStorageAccess $$2, LevelSettings $$3, boolean $$4, Registry<LevelStem> $$5) {
        super(Component.translatable("optimizeWorld.title", $$3.levelName()));
        this.callback = $$0;
        this.upgrader = new WorldUpgrader($$2, $$1, $$5, $$4);
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> {
            this.upgrader.cancel();
            this.callback.accept(false);
        }).bounds(this.width / 2 - 100, this.height / 4 + 150, 200, 20).build());
    }

    @Override
    public void tick() {
        if (this.upgrader.isFinished()) {
            this.callback.accept(true);
        }
    }

    @Override
    public void onClose() {
        this.callback.accept(false);
    }

    @Override
    public void removed() {
        this.upgrader.cancel();
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        OptimizeWorldScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        int $$4 = this.width / 2 - 150;
        int $$5 = this.width / 2 + 150;
        int $$6 = this.height / 4 + 100;
        int $$7 = $$6 + 10;
        Component component = this.upgrader.getStatus();
        int n = this.width / 2;
        Objects.requireNonNull((Object)this.font);
        OptimizeWorldScreen.drawCenteredString($$0, this.font, component, n, $$6 - 9 - 2, 0xA0A0A0);
        if (this.upgrader.getTotalChunks() > 0) {
            OptimizeWorldScreen.fill($$0, $$4 - 1, $$6 - 1, $$5 + 1, $$7 + 1, -16777216);
            OptimizeWorldScreen.drawString($$0, this.font, Component.translatable("optimizeWorld.info.converted", this.upgrader.getConverted()), $$4, 40, 0xA0A0A0);
            MutableComponent mutableComponent = Component.translatable("optimizeWorld.info.skipped", this.upgrader.getSkipped());
            Objects.requireNonNull((Object)this.font);
            OptimizeWorldScreen.drawString($$0, this.font, mutableComponent, $$4, 40 + 9 + 3, 0xA0A0A0);
            MutableComponent mutableComponent2 = Component.translatable("optimizeWorld.info.total", this.upgrader.getTotalChunks());
            Objects.requireNonNull((Object)this.font);
            OptimizeWorldScreen.drawString($$0, this.font, mutableComponent2, $$4, 40 + (9 + 3) * 2, 0xA0A0A0);
            int $$8 = 0;
            for (ResourceKey $$9 : this.upgrader.levels()) {
                int $$10 = Mth.floor(this.upgrader.dimensionProgress($$9) * (float)($$5 - $$4));
                OptimizeWorldScreen.fill($$0, $$4 + $$8, $$6, $$4 + $$8 + $$10, $$7, DIMENSION_COLORS.getInt((Object)$$9));
                $$8 += $$10;
            }
            int $$11 = this.upgrader.getConverted() + this.upgrader.getSkipped();
            String string = $$11 + " / " + this.upgrader.getTotalChunks();
            int n2 = this.width / 2;
            Objects.requireNonNull((Object)this.font);
            OptimizeWorldScreen.drawCenteredString($$0, this.font, string, n2, $$6 + 2 * 9 + 2, 0xA0A0A0);
            String string2 = Mth.floor(this.upgrader.getProgress() * 100.0f) + "%";
            int n3 = this.width / 2;
            int n4 = $$6 + ($$7 - $$6) / 2;
            Objects.requireNonNull((Object)this.font);
            OptimizeWorldScreen.drawCenteredString($$0, this.font, string2, n3, n4 - 9 / 2, 0xA0A0A0);
        }
        super.render($$0, $$1, $$2, $$3);
    }
}