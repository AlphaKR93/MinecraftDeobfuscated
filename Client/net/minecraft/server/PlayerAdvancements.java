/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
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
 *  java.io.BufferedWriter
 *  java.io.IOException
 *  java.io.Reader
 *  java.lang.Appendable
 *  java.lang.Integer
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.String
 *  java.nio.charset.Charset
 *  java.nio.charset.StandardCharsets
 *  java.nio.file.Files
 *  java.nio.file.LinkOption
 *  java.nio.file.OpenOption
 *  java.nio.file.Path
 *  java.util.Collection
 *  java.util.HashMap
 *  java.util.HashSet
 *  java.util.LinkedHashMap
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Set
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.server;

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
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
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
import net.minecraft.server.advancements.AdvancementVisibilityEvaluator;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.GameRules;
import org.slf4j.Logger;

public class PlayerAdvancements {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(AdvancementProgress.class, (Object)new AdvancementProgress.Serializer()).registerTypeAdapter(ResourceLocation.class, (Object)new ResourceLocation.Serializer()).setPrettyPrinting().create();
    private static final TypeToken<Map<ResourceLocation, AdvancementProgress>> TYPE_TOKEN = new TypeToken<Map<ResourceLocation, AdvancementProgress>>(){};
    private final DataFixer dataFixer;
    private final PlayerList playerList;
    private final Path playerSavePath;
    private final Map<Advancement, AdvancementProgress> progress = new LinkedHashMap();
    private final Set<Advancement> visible = new HashSet();
    private final Set<Advancement> progressChanged = new HashSet();
    private final Set<Advancement> rootsToUpdate = new HashSet();
    private ServerPlayer player;
    @Nullable
    private Advancement lastSelectedTab;
    private boolean isFirstPacket = true;

    public PlayerAdvancements(DataFixer $$0, PlayerList $$1, ServerAdvancementManager $$2, Path $$3, ServerPlayer $$4) {
        this.dataFixer = $$0;
        this.playerList = $$1;
        this.playerSavePath = $$3;
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
        this.progress.clear();
        this.visible.clear();
        this.rootsToUpdate.clear();
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

    private void checkForAutomaticTriggers(ServerAdvancementManager $$0) {
        for (Advancement $$1 : $$0.getAllAdvancements()) {
            if (!$$1.getCriteria().isEmpty()) continue;
            this.award($$1, "");
            $$1.getRewards().grant(this.player);
        }
    }

    private void load(ServerAdvancementManager $$0) {
        if (Files.isRegularFile((Path)this.playerSavePath, (LinkOption[])new LinkOption[0])) {
            try (JsonReader $$12 = new JsonReader((Reader)Files.newBufferedReader((Path)this.playerSavePath, (Charset)StandardCharsets.UTF_8));){
                $$12.setLenient(false);
                Dynamic $$2 = new Dynamic((DynamicOps)JsonOps.INSTANCE, (Object)Streams.parse((JsonReader)$$12));
                int $$3 = $$2.get("DataVersion").asInt(1343);
                $$2 = $$2.remove("DataVersion");
                $$2 = DataFixTypes.ADVANCEMENTS.updateToCurrentVersion(this.dataFixer, $$2, $$3);
                Map $$4 = (Map)GSON.getAdapter(TYPE_TOKEN).fromJsonTree((JsonElement)$$2.getValue());
                if ($$4 == null) {
                    throw new JsonParseException("Found null for advancements");
                }
                $$4.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach($$1 -> {
                    Advancement $$2 = $$0.getAdvancement((ResourceLocation)$$1.getKey());
                    if ($$2 == null) {
                        LOGGER.warn("Ignored advancement '{}' in progress file {} - it doesn't exist anymore?", $$1.getKey(), (Object)this.playerSavePath);
                        return;
                    }
                    this.startProgress($$2, (AdvancementProgress)$$1.getValue());
                    this.progressChanged.add((Object)$$2);
                    this.markForVisibilityUpdate($$2);
                });
            }
            catch (JsonParseException $$5) {
                LOGGER.error("Couldn't parse player advancements in {}", (Object)this.playerSavePath, (Object)$$5);
            }
            catch (IOException $$6) {
                LOGGER.error("Couldn't access player advancements in {}", (Object)this.playerSavePath, (Object)$$6);
            }
        }
        this.checkForAutomaticTriggers($$0);
        this.registerListeners($$0);
    }

    public void save() {
        LinkedHashMap $$0 = new LinkedHashMap();
        for (Map.Entry $$1 : this.progress.entrySet()) {
            AdvancementProgress $$2 = (AdvancementProgress)$$1.getValue();
            if (!$$2.hasProgress()) continue;
            $$0.put((Object)((Advancement)$$1.getKey()).getId(), (Object)$$2);
        }
        JsonElement $$3 = GSON.toJsonTree((Object)$$0);
        $$3.getAsJsonObject().addProperty("DataVersion", (Number)Integer.valueOf((int)SharedConstants.getCurrentVersion().getDataVersion().getVersion()));
        try {
            FileUtil.createDirectoriesSafe(this.playerSavePath.getParent());
            try (BufferedWriter $$4 = Files.newBufferedWriter((Path)this.playerSavePath, (Charset)StandardCharsets.UTF_8, (OpenOption[])new OpenOption[0]);){
                GSON.toJson($$3, (Appendable)$$4);
            }
        }
        catch (IOException $$5) {
            LOGGER.error("Couldn't save player advancements to {}", (Object)this.playerSavePath, (Object)$$5);
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
        if (!$$4 && $$3.isDone()) {
            this.markForVisibilityUpdate($$0);
        }
        return $$2;
    }

    public boolean revoke(Advancement $$0, String $$1) {
        boolean $$2 = false;
        AdvancementProgress $$3 = this.getOrStartProgress($$0);
        boolean $$4 = $$3.isDone();
        if ($$3.revokeProgress($$1)) {
            this.registerListeners($$0);
            this.progressChanged.add((Object)$$0);
            $$2 = true;
        }
        if ($$4 && !$$3.isDone()) {
            this.markForVisibilityUpdate($$0);
        }
        return $$2;
    }

    private void markForVisibilityUpdate(Advancement $$0) {
        this.rootsToUpdate.add((Object)$$0.getRoot());
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
        if (this.isFirstPacket || !this.rootsToUpdate.isEmpty() || !this.progressChanged.isEmpty()) {
            HashMap $$1 = new HashMap();
            HashSet $$2 = new HashSet();
            HashSet $$3 = new HashSet();
            for (Advancement $$4 : this.rootsToUpdate) {
                this.updateTreeVisibility($$4, (Set<Advancement>)$$2, (Set<ResourceLocation>)$$3);
            }
            this.rootsToUpdate.clear();
            for (Advancement $$5 : this.progressChanged) {
                if (!this.visible.contains((Object)$$5)) continue;
                $$1.put((Object)$$5.getId(), (Object)((AdvancementProgress)this.progress.get((Object)$$5)));
            }
            this.progressChanged.clear();
            if (!($$1.isEmpty() && $$2.isEmpty() && $$3.isEmpty())) {
                $$0.connection.send(new ClientboundUpdateAdvancementsPacket(this.isFirstPacket, (Collection<Advancement>)$$2, (Set<ResourceLocation>)$$3, (Map<ResourceLocation, AdvancementProgress>)$$1));
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
        AdvancementProgress $$1 = (AdvancementProgress)this.progress.get((Object)$$0);
        if ($$1 == null) {
            $$1 = new AdvancementProgress();
            this.startProgress($$0, $$1);
        }
        return $$1;
    }

    private void startProgress(Advancement $$0, AdvancementProgress $$1) {
        $$1.update($$0.getCriteria(), $$0.getRequirements());
        this.progress.put((Object)$$0, (Object)$$1);
    }

    private void updateTreeVisibility(Advancement $$02, Set<Advancement> $$1, Set<ResourceLocation> $$22) {
        AdvancementVisibilityEvaluator.evaluateVisibility($$02, (Predicate<Advancement>)((Predicate)$$0 -> this.getOrStartProgress((Advancement)$$0).isDone()), ($$2, $$3) -> {
            if ($$3) {
                if (this.visible.add((Object)$$2)) {
                    $$1.add((Object)$$2);
                    if (this.progress.containsKey((Object)$$2)) {
                        this.progressChanged.add((Object)$$2);
                    }
                }
            } else if (this.visible.remove((Object)$$2)) {
                $$22.add((Object)$$2.getId());
            }
        });
    }
}