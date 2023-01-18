/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.text2speech.Narrator
 *  java.lang.Object
 *  java.lang.String
 *  org.slf4j.Logger
 */
package net.minecraft.client;

import com.mojang.logging.LogUtils;
import com.mojang.text2speech.Narrator;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.NarratorStatus;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

public class GameNarrator {
    public static final Component NO_TITLE = CommonComponents.EMPTY;
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Minecraft minecraft;
    private final Narrator narrator = Narrator.getNarrator();

    public GameNarrator(Minecraft $$0) {
        this.minecraft = $$0;
    }

    public void sayChat(Component $$0) {
        if (this.getStatus().shouldNarrateChat()) {
            String $$1 = $$0.getString();
            this.logNarratedMessage($$1);
            this.narrator.say($$1, false);
        }
    }

    public void say(Component $$0) {
        String $$1 = $$0.getString();
        if (this.getStatus().shouldNarrateSystem() && !$$1.isEmpty()) {
            this.logNarratedMessage($$1);
            this.narrator.say($$1, true);
        }
    }

    public void sayNow(Component $$0) {
        this.sayNow($$0.getString());
    }

    public void sayNow(String $$0) {
        if (this.getStatus().shouldNarrateSystem() && !$$0.isEmpty()) {
            this.logNarratedMessage($$0);
            if (this.narrator.active()) {
                this.narrator.clear();
                this.narrator.say($$0, true);
            }
        }
    }

    private NarratorStatus getStatus() {
        return this.minecraft.options.narrator().get();
    }

    private void logNarratedMessage(String $$0) {
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            LOGGER.debug("Narrating: {}", (Object)$$0.replaceAll("\n", "\\\\n"));
        }
    }

    public void updateNarratorStatus(NarratorStatus $$0) {
        this.clear();
        this.narrator.say(Component.translatable("options.narrator").append(" : ").append($$0.getName()).getString(), true);
        ToastComponent $$1 = Minecraft.getInstance().getToasts();
        if (this.narrator.active()) {
            if ($$0 == NarratorStatus.OFF) {
                SystemToast.addOrUpdate($$1, SystemToast.SystemToastIds.NARRATOR_TOGGLE, Component.translatable("narrator.toast.disabled"), null);
            } else {
                SystemToast.addOrUpdate($$1, SystemToast.SystemToastIds.NARRATOR_TOGGLE, Component.translatable("narrator.toast.enabled"), $$0.getName());
            }
        } else {
            SystemToast.addOrUpdate($$1, SystemToast.SystemToastIds.NARRATOR_TOGGLE, Component.translatable("narrator.toast.disabled"), Component.translatable("options.narrator.notavailable"));
        }
    }

    public boolean isActive() {
        return this.narrator.active();
    }

    public void clear() {
        if (this.getStatus() == NarratorStatus.OFF || !this.narrator.active()) {
            return;
        }
        this.narrator.clear();
    }

    public void destroy() {
        this.narrator.destroy();
    }
}