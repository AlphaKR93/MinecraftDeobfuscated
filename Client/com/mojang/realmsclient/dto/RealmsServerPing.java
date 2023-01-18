/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 */
package com.mojang.realmsclient.dto;

import com.mojang.realmsclient.dto.ValueObject;

public class RealmsServerPing
extends ValueObject {
    public volatile String nrOfPlayers = "0";
    public volatile String playerList = "";
}