/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Objects
 */
package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class SubtitleOverlay
extends GuiComponent
implements SoundEventListener {
    private static final long DISPLAY_TIME = 3000L;
    private final Minecraft minecraft;
    private final List<Subtitle> subtitles = Lists.newArrayList();
    private boolean isListening;

    public SubtitleOverlay(Minecraft $$0) {
        this.minecraft = $$0;
    }

    public void render(PoseStack $$0) {
        if (!this.isListening && this.minecraft.options.showSubtitles().get().booleanValue()) {
            this.minecraft.getSoundManager().addListener(this);
            this.isListening = true;
        } else if (this.isListening && !this.minecraft.options.showSubtitles().get().booleanValue()) {
            this.minecraft.getSoundManager().removeListener(this);
            this.isListening = false;
        }
        if (!this.isListening || this.subtitles.isEmpty()) {
            return;
        }
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        Vec3 $$1 = new Vec3(this.minecraft.player.getX(), this.minecraft.player.getEyeY(), this.minecraft.player.getZ());
        Vec3 $$2 = new Vec3(0.0, 0.0, -1.0).xRot(-this.minecraft.player.getXRot() * ((float)Math.PI / 180)).yRot(-this.minecraft.player.getYRot() * ((float)Math.PI / 180));
        Vec3 $$3 = new Vec3(0.0, 1.0, 0.0).xRot(-this.minecraft.player.getXRot() * ((float)Math.PI / 180)).yRot(-this.minecraft.player.getYRot() * ((float)Math.PI / 180));
        Vec3 $$4 = $$2.cross($$3);
        int $$5 = 0;
        int $$6 = 0;
        double $$7 = this.minecraft.options.notificationDisplayTime().get();
        Iterator $$8 = this.subtitles.iterator();
        while ($$8.hasNext()) {
            Subtitle $$9 = (Subtitle)$$8.next();
            if ((double)$$9.getTime() + 3000.0 * $$7 <= (double)Util.getMillis()) {
                $$8.remove();
                continue;
            }
            $$6 = Math.max((int)$$6, (int)this.minecraft.font.width($$9.getText()));
        }
        $$6 += this.minecraft.font.width("<") + this.minecraft.font.width(" ") + this.minecraft.font.width(">") + this.minecraft.font.width(" ");
        for (Subtitle $$10 : this.subtitles) {
            int $$11 = 255;
            Component $$12 = $$10.getText();
            Vec3 $$13 = $$10.getLocation().subtract($$1).normalize();
            double $$14 = -$$4.dot($$13);
            double $$15 = -$$2.dot($$13);
            boolean $$16 = $$15 > 0.5;
            int $$17 = $$6 / 2;
            Objects.requireNonNull((Object)this.minecraft.font);
            int $$18 = 9;
            int $$19 = $$18 / 2;
            float $$20 = 1.0f;
            int $$21 = this.minecraft.font.width($$12);
            int $$22 = Mth.floor(Mth.clampedLerp(255.0f, 75.0f, (float)(Util.getMillis() - $$10.getTime()) / (float)(3000.0 * $$7)));
            int $$23 = $$22 << 16 | $$22 << 8 | $$22;
            $$0.pushPose();
            $$0.translate((float)this.minecraft.getWindow().getGuiScaledWidth() - (float)$$17 * 1.0f - 2.0f, (float)(this.minecraft.getWindow().getGuiScaledHeight() - 35) - (float)($$5 * ($$18 + 1)) * 1.0f, 0.0f);
            $$0.scale(1.0f, 1.0f, 1.0f);
            SubtitleOverlay.fill($$0, -$$17 - 1, -$$19 - 1, $$17 + 1, $$19 + 1, this.minecraft.options.getBackgroundColor(0.8f));
            RenderSystem.enableBlend();
            int $$24 = $$23 + -16777216;
            if (!$$16) {
                if ($$14 > 0.0) {
                    SubtitleOverlay.drawString($$0, this.minecraft.font, ">", $$17 - this.minecraft.font.width(">"), -$$19, $$24);
                } else if ($$14 < 0.0) {
                    SubtitleOverlay.drawString($$0, this.minecraft.font, "<", -$$17, -$$19, $$24);
                }
            }
            SubtitleOverlay.drawString($$0, this.minecraft.font, $$12, -$$21 / 2, -$$19, $$24);
            $$0.popPose();
            ++$$5;
        }
        RenderSystem.disableBlend();
    }

    @Override
    public void onPlaySound(SoundInstance $$0, WeighedSoundEvents $$1) {
        if ($$1.getSubtitle() == null) {
            return;
        }
        Component $$2 = $$1.getSubtitle();
        if (!this.subtitles.isEmpty()) {
            for (Subtitle $$3 : this.subtitles) {
                if (!$$3.getText().equals($$2)) continue;
                $$3.refresh(new Vec3($$0.getX(), $$0.getY(), $$0.getZ()));
                return;
            }
        }
        this.subtitles.add((Object)new Subtitle($$2, new Vec3($$0.getX(), $$0.getY(), $$0.getZ())));
    }

    public static class Subtitle {
        private final Component text;
        private long time;
        private Vec3 location;

        public Subtitle(Component $$0, Vec3 $$1) {
            this.text = $$0;
            this.location = $$1;
            this.time = Util.getMillis();
        }

        public Component getText() {
            return this.text;
        }

        public long getTime() {
            return this.time;
        }

        public Vec3 getLocation() {
            return this.location;
        }

        public void refresh(Vec3 $$0) {
            this.location = $$0;
            this.time = Util.getMillis();
        }
    }
}