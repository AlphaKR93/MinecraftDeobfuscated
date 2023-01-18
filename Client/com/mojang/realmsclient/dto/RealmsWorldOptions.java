/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  java.lang.Boolean
 *  java.lang.Integer
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Objects
 *  javax.annotation.Nullable
 */
package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.resources.language.I18n;

public class RealmsWorldOptions
extends ValueObject {
    public final boolean pvp;
    public final boolean spawnAnimals;
    public final boolean spawnMonsters;
    public final boolean spawnNPCs;
    public final int spawnProtection;
    public final boolean commandBlocks;
    public final boolean forceGameMode;
    public final int difficulty;
    public final int gameMode;
    @Nullable
    private final String slotName;
    public long templateId;
    @Nullable
    public String templateImage;
    public boolean empty;
    private static final boolean DEFAULT_FORCE_GAME_MODE = false;
    private static final boolean DEFAULT_PVP = true;
    private static final boolean DEFAULT_SPAWN_ANIMALS = true;
    private static final boolean DEFAULT_SPAWN_MONSTERS = true;
    private static final boolean DEFAULT_SPAWN_NPCS = true;
    private static final int DEFAULT_SPAWN_PROTECTION = 0;
    private static final boolean DEFAULT_COMMAND_BLOCKS = false;
    private static final int DEFAULT_DIFFICULTY = 2;
    private static final int DEFAULT_GAME_MODE = 0;
    private static final String DEFAULT_SLOT_NAME = "";
    private static final long DEFAULT_TEMPLATE_ID = -1L;
    private static final String DEFAULT_TEMPLATE_IMAGE = null;

    public RealmsWorldOptions(boolean $$0, boolean $$1, boolean $$2, boolean $$3, int $$4, boolean $$5, int $$6, int $$7, boolean $$8, @Nullable String $$9) {
        this.pvp = $$0;
        this.spawnAnimals = $$1;
        this.spawnMonsters = $$2;
        this.spawnNPCs = $$3;
        this.spawnProtection = $$4;
        this.commandBlocks = $$5;
        this.difficulty = $$6;
        this.gameMode = $$7;
        this.forceGameMode = $$8;
        this.slotName = $$9;
    }

    public static RealmsWorldOptions createDefaults() {
        return new RealmsWorldOptions(true, true, true, true, 0, false, 2, 0, false, DEFAULT_SLOT_NAME);
    }

    public static RealmsWorldOptions createEmptyDefaults() {
        RealmsWorldOptions $$0 = RealmsWorldOptions.createDefaults();
        $$0.setEmpty(true);
        return $$0;
    }

    public void setEmpty(boolean $$0) {
        this.empty = $$0;
    }

    public static RealmsWorldOptions parse(JsonObject $$0) {
        RealmsWorldOptions $$1 = new RealmsWorldOptions(JsonUtils.getBooleanOr("pvp", $$0, true), JsonUtils.getBooleanOr("spawnAnimals", $$0, true), JsonUtils.getBooleanOr("spawnMonsters", $$0, true), JsonUtils.getBooleanOr("spawnNPCs", $$0, true), JsonUtils.getIntOr("spawnProtection", $$0, 0), JsonUtils.getBooleanOr("commandBlocks", $$0, false), JsonUtils.getIntOr("difficulty", $$0, 2), JsonUtils.getIntOr("gameMode", $$0, 0), JsonUtils.getBooleanOr("forceGameMode", $$0, false), JsonUtils.getStringOr("slotName", $$0, DEFAULT_SLOT_NAME));
        $$1.templateId = JsonUtils.getLongOr("worldTemplateId", $$0, -1L);
        $$1.templateImage = JsonUtils.getStringOr("worldTemplateImage", $$0, DEFAULT_TEMPLATE_IMAGE);
        return $$1;
    }

    public String getSlotName(int $$0) {
        if (this.slotName == null || this.slotName.isEmpty()) {
            if (this.empty) {
                return I18n.get("mco.configure.world.slot.empty", new Object[0]);
            }
            return this.getDefaultSlotName($$0);
        }
        return this.slotName;
    }

    public String getDefaultSlotName(int $$0) {
        return I18n.get("mco.configure.world.slot", $$0);
    }

    public String toJson() {
        JsonObject $$0 = new JsonObject();
        if (!this.pvp) {
            $$0.addProperty("pvp", Boolean.valueOf((boolean)this.pvp));
        }
        if (!this.spawnAnimals) {
            $$0.addProperty("spawnAnimals", Boolean.valueOf((boolean)this.spawnAnimals));
        }
        if (!this.spawnMonsters) {
            $$0.addProperty("spawnMonsters", Boolean.valueOf((boolean)this.spawnMonsters));
        }
        if (!this.spawnNPCs) {
            $$0.addProperty("spawnNPCs", Boolean.valueOf((boolean)this.spawnNPCs));
        }
        if (this.spawnProtection != 0) {
            $$0.addProperty("spawnProtection", (Number)Integer.valueOf((int)this.spawnProtection));
        }
        if (this.commandBlocks) {
            $$0.addProperty("commandBlocks", Boolean.valueOf((boolean)this.commandBlocks));
        }
        if (this.difficulty != 2) {
            $$0.addProperty("difficulty", (Number)Integer.valueOf((int)this.difficulty));
        }
        if (this.gameMode != 0) {
            $$0.addProperty("gameMode", (Number)Integer.valueOf((int)this.gameMode));
        }
        if (this.forceGameMode) {
            $$0.addProperty("forceGameMode", Boolean.valueOf((boolean)this.forceGameMode));
        }
        if (!Objects.equals((Object)this.slotName, (Object)DEFAULT_SLOT_NAME)) {
            $$0.addProperty("slotName", this.slotName);
        }
        return $$0.toString();
    }

    public RealmsWorldOptions clone() {
        return new RealmsWorldOptions(this.pvp, this.spawnAnimals, this.spawnMonsters, this.spawnNPCs, this.spawnProtection, this.commandBlocks, this.difficulty, this.gameMode, this.forceGameMode, this.slotName);
    }
}