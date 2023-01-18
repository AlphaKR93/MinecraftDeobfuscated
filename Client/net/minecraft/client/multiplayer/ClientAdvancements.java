/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.logging.LogUtils
 *  java.lang.Object
 *  java.util.Map
 *  java.util.Map$Entry
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.multiplayer;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.AdvancementToast;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class ClientAdvancements {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Minecraft minecraft;
    private final AdvancementList advancements = new AdvancementList();
    private final Map<Advancement, AdvancementProgress> progress = Maps.newHashMap();
    @Nullable
    private Listener listener;
    @Nullable
    private Advancement selectedTab;

    public ClientAdvancements(Minecraft $$0) {
        this.minecraft = $$0;
    }

    public void update(ClientboundUpdateAdvancementsPacket $$0) {
        if ($$0.shouldReset()) {
            this.advancements.clear();
            this.progress.clear();
        }
        this.advancements.remove($$0.getRemoved());
        this.advancements.add($$0.getAdded());
        for (Map.Entry $$1 : $$0.getProgress().entrySet()) {
            Advancement $$2 = this.advancements.get((ResourceLocation)$$1.getKey());
            if ($$2 != null) {
                AdvancementProgress $$3 = (AdvancementProgress)$$1.getValue();
                $$3.update($$2.getCriteria(), $$2.getRequirements());
                this.progress.put((Object)$$2, (Object)$$3);
                if (this.listener != null) {
                    this.listener.onUpdateAdvancementProgress($$2, $$3);
                }
                if ($$0.shouldReset() || !$$3.isDone() || $$2.getDisplay() == null || !$$2.getDisplay().shouldShowToast()) continue;
                this.minecraft.getToasts().addToast(new AdvancementToast($$2));
                continue;
            }
            LOGGER.warn("Server informed client about progress for unknown advancement {}", $$1.getKey());
        }
    }

    public AdvancementList getAdvancements() {
        return this.advancements;
    }

    public void setSelectedTab(@Nullable Advancement $$0, boolean $$1) {
        ClientPacketListener $$2 = this.minecraft.getConnection();
        if ($$2 != null && $$0 != null && $$1) {
            $$2.send(ServerboundSeenAdvancementsPacket.openedTab($$0));
        }
        if (this.selectedTab != $$0) {
            this.selectedTab = $$0;
            if (this.listener != null) {
                this.listener.onSelectedTabChanged($$0);
            }
        }
    }

    public void setListener(@Nullable Listener $$0) {
        this.listener = $$0;
        this.advancements.setListener($$0);
        if ($$0 != null) {
            for (Map.Entry $$1 : this.progress.entrySet()) {
                $$0.onUpdateAdvancementProgress((Advancement)$$1.getKey(), (AdvancementProgress)$$1.getValue());
            }
            $$0.onSelectedTabChanged(this.selectedTab);
        }
    }

    public static interface Listener
    extends AdvancementList.Listener {
        public void onUpdateAdvancementProgress(Advancement var1, AdvancementProgress var2);

        public void onSelectedTabChanged(@Nullable Advancement var1);
    }
}