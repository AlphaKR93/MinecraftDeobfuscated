/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 *  java.lang.Object
 *  java.lang.String
 */
package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import com.mojang.realmsclient.dto.ReflectionBasedSerialization;
import com.mojang.realmsclient.dto.ValueObject;

public class PlayerInfo
extends ValueObject
implements ReflectionBasedSerialization {
    @SerializedName(value="name")
    private String name;
    @SerializedName(value="uuid")
    private String uuid;
    @SerializedName(value="operator")
    private boolean operator;
    @SerializedName(value="accepted")
    private boolean accepted;
    @SerializedName(value="online")
    private boolean online;

    public String getName() {
        return this.name;
    }

    public void setName(String $$0) {
        this.name = $$0;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String $$0) {
        this.uuid = $$0;
    }

    public boolean isOperator() {
        return this.operator;
    }

    public void setOperator(boolean $$0) {
        this.operator = $$0;
    }

    public boolean getAccepted() {
        return this.accepted;
    }

    public void setAccepted(boolean $$0) {
        this.accepted = $$0;
    }

    public boolean getOnline() {
        return this.online;
    }

    public void setOnline(boolean $$0) {
        this.online = $$0;
    }
}