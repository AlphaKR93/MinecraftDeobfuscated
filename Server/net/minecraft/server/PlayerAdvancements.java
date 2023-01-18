/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.common.io.Files
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  com.google.gson.internal.Streams
 *  com.google.gson.reflect.TypeToken
 *  com.google.gson.stream.JsonReader
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  java.io.File
 *  java.io.FileOutputStream
 *  java.io.IOException
 *  java.io.OutputStream
 *  java.io.OutputStreamWriter
 *  java.io.Reader
 *  java.io.StringReader
 *  java.lang.Appendable
 *  java.lang.Integer
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.String
 *  java.nio.charset.Charset
 *  java.nio.charset.StandardCharsets
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.Comparator
 *  java.util.HashMap
 *  java.util.LinkedHashSet
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Set
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.server;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSelectAdvancementsTabPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.GameRules;
import org.slf4j.Logger;

public class PlayerAdvancements {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int VISIBILITY_DEPTH = 2;
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(AdvancementProgress.class, (Object)new AdvancementProgress.Serializer()).registerTypeAdapter(ResourceLocation.class, (Object)new ResourceLocation.Serializer()).setPrettyPrinting().create();
    private static final TypeToken<Map<ResourceLocation, AdvancementProgress>> TYPE_TOKEN = new TypeToken<Map<ResourceLocation, AdvancementProgress>>(){};
    private final DataFixer dataFixer;
    private final PlayerList playerList;
    private final File file;
    private final Map<Advancement, AdvancementProgress> advancements = Maps.newLinkedHashMap();
    private final Set<Advancement> visible = Sets.newLinkedHashSet();
    private final Set<Advancement> visibilityChanged = Sets.newLinkedHashSet();
    private final Set<Advancement> progressChanged = Sets.newLinkedHashSet();
    private ServerPlayer player;
    @Nullable
    private Advancement lastSelectedTab;
    private boolean isFirstPacket = true;

    public PlayerAdvancements(DataFixer $$0, PlayerList $$1, ServerAdvancementManager $$2, File $$3, ServerPlayer $$4) {
        this.dataFixer = $$0;
        this.playerList = $$1;
        this.file = $$3;
        this.player = $$4;
        this.load($$2);
    }

    public void setPlayer(ServerPlayer $$0) {
        this.player = $$0;
    }

    public void stopListening() {
        for (CriterionTrigger $$0 : CriteriaTriggers.all()) {
            $$0.removePlayerListeners(this);
        }
    }

    public void reload(ServerAdvancementManager $$0) {
        this.stopListening();
        this.advancements.clear();
        this.visible.clear();
        this.visibilityChanged.clear();
        this.progressChanged.clear();
        this.isFirstPacket = true;
        this.lastSelectedTab = null;
        this.load($$0);
    }

    private void registerListeners(ServerAdvancementManager $$0) {
        for (Advancement $$1 : $$0.getAllAdvancements()) {
            this.registerListeners($$1);
        }
    }

    private void ensureAllVisible() {
        ArrayList $$0 = Lists.newArrayList();
        for (Map.Entry $$1 : this.advancements.entrySet()) {
            if (!((AdvancementProgress)$$1.getValue()).isDone()) continue;
            $$0.add((Object)((Advancement)$$1.getKey()));
            this.progressChanged.add((Object)((Advancement)$$1.getKey()));
        }
        for (Advancement $$2 : $$0) {
            this.ensureVisibility($$2);
        }
    }

    private void checkForAutomaticTriggers(ServerAdvancementManager $$0) {
        for (Advancement $$1 : $$0.getAllAdvancements()) {
            if (!$$1.getCriteria().isEmpty()) continue;
            this.award($$1, "");
            $$1.getRewards().grant(this.player);
        }
    }

    private void load(ServerAdvancementManager $$0) {
        if (this.file.isFile()) {
            try (JsonReader $$1 = new JsonReader((Reader)new StringReader(Files.toString((File)this.file, (Charset)StandardCharsets.UTF_8)));){
                $$1.setLenient(false);
                Dynamic $$2 = new Dynamic((DynamicOps)JsonOps.INSTANCE, (Object)Streams.parse((JsonReader)$$1));
                if (!$$2.get("DataVersion").asNumber().result().isPresent()) {
                    $$2 = $$2.set("DataVersion", $$2.createInt(1343));
                }
                $$2 = this.dataFixer.update(DataFixTypes.ADVANCEMENTS.getType(), $$2, $$2.get("DataVersion").asInt(0), SharedConstants.getCurrentVersion().getWorldVersion());
                $$2 = $$2.remove("DataVersion");
                Map $$3 = (Map)GSON.getAdapter(TYPE_TOKEN).fromJsonTree((JsonElement)$$2.getValue());
                if ($$3 == null) {
                    throw new JsonParseException("Found null for advancements");
                }
                Stream $$4 = $$3.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getValue));
                for (Map.Entry $$5 : (List)$$4.collect(Collectors.toList())) {
                    Advancement $$6 = $$0.getAdvancement((ResourceLocation)$$5.getKey());
                    if ($$6 == null) {
                        LOGGER.warn("Ignored advancement '{}' in progress file {} - it doesn't exist anymore?", $$5.getKey(), (Object)this.file);
                        continue;
                    }
                    this.startProgress($$6, (AdvancementProgress)$$5.getValue());
                }
            }
            catch (JsonParseException $$7) {
                LOGGER.error("Couldn't parse player advancements in {}", (Object)this.file, (Object)$$7);
            }
            catch (IOException $$8) {
                LOGGER.error("Couldn't access player advancements in {}", (Object)this.file, (Object)$$8);
            }
        }
        this.checkForAutomaticTriggers($$0);
        this.ensureAllVisible();
        this.registerListeners($$0);
    }

    public void save() {
        HashMap $$0 = Maps.newHashMap();
        for (Map.Entry $$1 : this.advancements.entrySet()) {
            AdvancementProgress $$2 = (AdvancementProgress)$$1.getValue();
            if (!$$2.hasProgress()) continue;
            $$0.put((Object)((Advancement)$$1.getKey()).getId(), (Object)$$2);
        }
        if (this.file.getParentFile() != null) {
            this.file.getParentFile().mkdirs();
        }
        JsonElement $$3 = GSON.toJsonTree((Object)$$0);
        $$3.getAsJsonObject().addProperty("DataVersion", (Number)Integer.valueOf((int)SharedConstants.getCurrentVersion().getWorldVersion()));
        try (FileOutputStream $$4 = new FileOutputStream(this.file);
             OutputStreamWriter $$5 = new OutputStreamWriter((OutputStream)$$4, Charsets.UTF_8.newEncoder());){
            GSON.toJson($$3, (Appendable)$$5);
        }
        catch (IOException $$6) {
            LOGGER.error("Couldn't save player advancements to {}", (Object)this.file, (Object)$$6);
        }
    }

    public boolean award(Advancement $$0, String $$1) {
        boolean $$2 = false;
        AdvancementProgress $$3 = this.getOrStartProgress($$0);
        boolean $$4 = $$3.isDone();
        if ($$3.grantProgress($$1)) {
            this.unregisterListeners($$0);
            this.progressChanged.add((Object)$$0);
            $$2 = true;
            if (!$$4 && $$3.isDone()) {
                $$0.getRewards().grant(this.player);
                if ($$0.getDisplay() != null && $$0.getDisplay().shouldAnnounceChat() && this.player.level.getGameRules().getBoolean(GameRules.RULE_ANNOUNCE_ADVANCEMENTS)) {
                    this.playerList.broadcastSystemMessage(Component.translatable("chat.type.advancement." + $$0.getDisplay().getFrame().getName(), this.player.getDisplayName(), $$0.getChatComponent()), false);
                }
            }
        }
        if ($$3.isDone()) {
            this.ensureVisibility($$0);
        }
        return $$2;
    }

    public boolean revoke(Advancement $$0, String $$1) {
        boolean $$2 = false;
        AdvancementProgress $$3 = this.getOrStartProgress($$0);
        if ($$3.revokeProgress($$1)) {
            this.registerListeners($$0);
            this.progressChanged.add((Object)$$0);
            $$2 = true;
        }
        if (!$$3.hasProgress()) {
            this.ensureVisibility($$0);
        }
        return $$2;
    }

    private void registerListeners(Advancement $$0) {
        AdvancementProgress $$1 = this.getOrStartProgress($$0);
        if ($$1.isDone()) {
            return;
        }
        for (Map.Entry $$2 : $$0.getCriteria().entrySet()) {
            CriterionTrigger<CriterionTriggerInstance> $$5;
            CriterionTriggerInstance $$4;
            CriterionProgress $$3 = $$1.getCriterion((String)$$2.getKey());
            if ($$3 == null || $$3.isDone() || ($$4 = ((Criterion)$$2.getValue()).getTrigger()) == null || ($$5 = CriteriaTriggers.getCriterion($$4.getCriterion())) == null) continue;
            $$5.addPlayerListener(this, new CriterionTrigger.Listener<CriterionTriggerInstance>($$4, $$0, (String)$$2.getKey()));
        }
    }

    private void unregisterListeners(Advancement $$0) {
        AdvancementProgress $$1 = this.getOrStartProgress($$0);
        for (Map.Entry $$2 : $$0.getCriteria().entrySet()) {
            CriterionTrigger<CriterionTriggerInstance> $$5;
            CriterionTriggerInstance $$4;
            CriterionProgress $$3 = $$1.getCriterion((String)$$2.getKey());
            if ($$3 == null || !$$3.isDone() && !$$1.isDone() || ($$4 = ((Criterion)$$2.getValue()).getTrigger()) == null || ($$5 = CriteriaTriggers.getCriterion($$4.getCriterion())) == null) continue;
            $$5.removePlayerListener(this, new CriterionTrigger.Listener<CriterionTriggerInstance>($$4, $$0, (String)$$2.getKey()));
        }
    }

    public void flushDirty(ServerPlayer $$0) {
        if (this.isFirstPacket || !this.visibilityChanged.isEmpty() || !this.progressChanged.isEmpty()) {
            HashMap $$1 = Maps.newHashMap();
            LinkedHashSet $$2 = Sets.newLinkedHashSet();
            LinkedHashSet $$3 = Sets.newLinkedHashSet();
            for (Advancement $$4 : this.progressChanged) {
                if (!this.visible.contains((Object)$$4)) continue;
                $$1.put((Object)$$4.getId(), (Object)((AdvancementProgress)this.advancements.get((Object)$$4)));
            }
            for (Advancement $$5 : this.visibilityChanged) {
                if (this.visible.contains((Object)$$5)) {
                    $$2.add((Object)$$5);
                    continue;
                }
                $$3.add((Object)$$5.getId());
            }
            if (this.isFirstPacket || !$$1.isEmpty() || !$$2.isEmpty() || !$$3.isEmpty()) {
                $$0.connection.send(new ClientboundUpdateAdvancementsPacket(this.isFirstPacket, (Collection<Advancement>)$$2, (Set<ResourceLocation>)$$3, (Map<ResourceLocation, AdvancementProgress>)$$1));
                this.visibilityChanged.clear();
                this.progressChanged.clear();
            }
        }
        this.isFirstPacket = false;
    }

    public void setSelectedTab(@Nullable Advancement $$0) {
        Advancement $$1 = this.lastSelectedTab;
        this.lastSelectedTab = $$0 != null && $$0.getParent() == null && $$0.getDisplay() != null ? $$0 : null;
        if ($$1 != this.lastSelectedTab) {
            this.player.connection.send(new ClientboundSelectAdvancementsTabPacket(this.lastSelectedTab == null ? null : this.lastSelectedTab.getId()));
        }
    }

    public AdvancementProgress getOrStartProgress(Advancement $$0) {
        AdvancementProgress $$1 = (AdvancementProgress)this.advancements.get((Object)$$0);
        if ($$1 == null) {
            $$1 = new AdvancementProgress();
            this.startProgress($$0, $$1);
        }
        return $$1;
    }

    private void startProgress(Advancement $$0, AdvancementProgress $$1) {
        $$1.update($$0.getCriteria(), $$0.getRequirements());
        this.advancements.put((Object)$$0, (Object)$$1);
    }

    private void ensureVisibility(Advancement $$0) {
        boolean $$1 = this.shouldBeVisible($$0);
        boolean $$2 = this.visible.contains((Object)$$0);
        if ($$1 && !$$2) {
            this.visible.add((Object)$$0);
            this.visibilityChanged.add((Object)$$0);
            if (this.advancements.containsKey((Object)$$0)) {
                this.progressChanged.add((Object)$$0);
            }
        } else if (!$$1 && $$2) {
            this.visible.remove((Object)$$0);
            this.visibilityChanged.add((Object)$$0);
        }
        if ($$1 != $$2 && $$0.getParent() != null) {
            this.ensureVisibility($$0.getParent());
        }
        for (Advancement $$3 : $$0.getChildren()) {
            this.ensureVisibility($$3);
        }
    }

    private boolean shouldBeVisible(Advancement $$0) {
        for (int $$1 = 0; $$0 != null && $$1 <= 2; $$0 = $$0.getParent(), ++$$1) {
            if ($$1 == 0 && this.hasCompletedChildrenOrSelf($$0)) {
                return true;
            }
            if ($$0.getDisplay() == null) {
                return false;
            }
            AdvancementProgress $$2 = this.getOrStartProgress($$0);
            if ($$2.isDone()) {
                return true;
            }
            if (!$$0.getDisplay().isHidden()) continue;
            return false;
        }
        return false;
    }

    private boolean hasCompletedChildrenOrSelf(Advancement $$0) {
        AdvancementProgress $$1 = this.getOrStartProgress($$0);
        if ($$1.isDone()) {
            return true;
        }
        for (Advancement $$2 : $$0.getChildren()) {
            if (!this.hasCompletedChildrenOrSelf($$2)) continue;
            return true;
        }
        return false;
    }
}