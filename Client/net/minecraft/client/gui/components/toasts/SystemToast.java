/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.components.toasts;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class SystemToast
implements Toast {
    private static final int MAX_LINE_SIZE = 200;
    private static final int LINE_SPACING = 12;
    private static final int MARGIN = 10;
    private final SystemToastIds id;
    private Component title;
    private List<FormattedCharSequence> messageLines;
    private long lastChanged;
    private boolean changed;
    private final int width;

    public SystemToast(SystemToastIds $$0, Component $$1, @Nullable Component $$2) {
        this($$0, $$1, (List<FormattedCharSequence>)SystemToast.nullToEmpty($$2), Math.max((int)160, (int)(30 + Math.max((int)Minecraft.getInstance().font.width($$1), (int)($$2 == null ? 0 : Minecraft.getInstance().font.width($$2))))));
    }

    public static SystemToast multiline(Minecraft $$0, SystemToastIds $$1, Component $$2, Component $$3) {
        Font $$4 = $$0.font;
        List<FormattedCharSequence> $$5 = $$4.split($$3, 200);
        int $$6 = Math.max((int)200, (int)$$5.stream().mapToInt($$4::width).max().orElse(200));
        return new SystemToast($$1, $$2, $$5, $$6 + 30);
    }

    private SystemToast(SystemToastIds $$0, Component $$1, List<FormattedCharSequence> $$2, int $$3) {
        this.id = $$0;
        this.title = $$1;
        this.messageLines = $$2;
        this.width = $$3;
    }

    private static ImmutableList<FormattedCharSequence> nullToEmpty(@Nullable Component $$0) {
        return $$0 == null ? ImmutableList.of() : ImmutableList.of((Object)$$0.getVisualOrderText());
    }

    @Override
    public int width() {
        return this.width;
    }

    @Override
    public int height() {
        return 20 + Math.max((int)this.messageLines.size(), (int)1) * 12;
    }

    @Override
    public Toast.Visibility render(PoseStack $$0, ToastComponent $$1, long $$2) {
        if (this.changed) {
            this.lastChanged = $$2;
            this.changed = false;
        }
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        int $$3 = this.width();
        if ($$3 == 160 && this.messageLines.size() <= 1) {
            $$1.blit($$0, 0, 0, 0, 64, $$3, this.height());
        } else {
            int $$4 = this.height();
            int $$5 = 28;
            int $$6 = Math.min((int)4, (int)($$4 - 28));
            this.renderBackgroundRow($$0, $$1, $$3, 0, 0, 28);
            for (int $$7 = 28; $$7 < $$4 - $$6; $$7 += 10) {
                this.renderBackgroundRow($$0, $$1, $$3, 16, $$7, Math.min((int)16, (int)($$4 - $$7 - $$6)));
            }
            this.renderBackgroundRow($$0, $$1, $$3, 32 - $$6, $$4 - $$6, $$6);
        }
        if (this.messageLines == null) {
            $$1.getMinecraft().font.draw($$0, this.title, 18.0f, 12.0f, -256);
        } else {
            $$1.getMinecraft().font.draw($$0, this.title, 18.0f, 7.0f, -256);
            for (int $$8 = 0; $$8 < this.messageLines.size(); ++$$8) {
                $$1.getMinecraft().font.draw($$0, (FormattedCharSequence)this.messageLines.get($$8), 18.0f, (float)(18 + $$8 * 12), -1);
            }
        }
        return $$2 - this.lastChanged < this.id.displayTime ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
    }

    private void renderBackgroundRow(PoseStack $$0, ToastComponent $$1, int $$2, int $$3, int $$4, int $$5) {
        int $$6 = $$3 == 0 ? 20 : 5;
        int $$7 = Math.min((int)60, (int)($$2 - $$6));
        $$1.blit($$0, 0, $$4, 0, 64 + $$3, $$6, $$5);
        for (int $$8 = $$6; $$8 < $$2 - $$7; $$8 += 64) {
            $$1.blit($$0, $$8, $$4, 32, 64 + $$3, Math.min((int)64, (int)($$2 - $$8 - $$7)), $$5);
        }
        $$1.blit($$0, $$2 - $$7, $$4, 160 - $$7, 64 + $$3, $$7, $$5);
    }

    public void reset(Component $$0, @Nullable Component $$1) {
        this.title = $$0;
        this.messageLines = SystemToast.nullToEmpty($$1);
        this.changed = true;
    }

    public SystemToastIds getToken() {
        return this.id;
    }

    public static void add(ToastComponent $$0, SystemToastIds $$1, Component $$2, @Nullable Component $$3) {
        $$0.addToast(new SystemToast($$1, $$2, $$3));
    }

    public static void addOrUpdate(ToastComponent $$0, SystemToastIds $$1, Component $$2, @Nullable Component $$3) {
        SystemToast $$4 = $$0.getToast(SystemToast.class, (Object)$$1);
        if ($$4 == null) {
            SystemToast.add($$0, $$1, $$2, $$3);
        } else {
            $$4.reset($$2, $$3);
        }
    }

    public static void onWorldAccessFailure(Minecraft $$0, String $$1) {
        SystemToast.add($$0.getToasts(), SystemToastIds.WORLD_ACCESS_FAILURE, Component.translatable("selectWorld.access_failure"), Component.literal($$1));
    }

    public static void onWorldDeleteFailure(Minecraft $$0, String $$1) {
        SystemToast.add($$0.getToasts(), SystemToastIds.WORLD_ACCESS_FAILURE, Component.translatable("selectWorld.delete_failure"), Component.literal($$1));
    }

    public static void onPackCopyFailure(Minecraft $$0, String $$1) {
        SystemToast.add($$0.getToasts(), SystemToastIds.PACK_COPY_FAILURE, Component.translatable("pack.copyFailure"), Component.literal($$1));
    }

    public static enum SystemToastIds {
        TUTORIAL_HINT,
        NARRATOR_TOGGLE,
        WORLD_BACKUP,
        WORLD_GEN_SETTINGS_TRANSFER,
        PACK_LOAD_FAILURE,
        WORLD_ACCESS_FAILURE,
        PACK_COPY_FAILURE,
        PERIODIC_NOTIFICATION,
        UNSECURE_SERVER_WARNING(10000L);

        final long displayTime;

        private SystemToastIds(long $$0) {
            this.displayTime = $$0;
        }

        private SystemToastIds() {
            this(5000L);
        }
    }
}