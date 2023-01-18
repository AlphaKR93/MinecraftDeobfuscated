/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IncompatibleClassChangeError
 *  java.lang.Object
 *  java.lang.String
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 */
package net.minecraft.client.telemetry.events;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.telemetry.TelemetryEventSender;
import net.minecraft.client.telemetry.TelemetryEventType;
import net.minecraft.client.telemetry.TelemetryProperty;
import net.minecraft.client.telemetry.TelemetryPropertyMap;
import net.minecraft.world.level.GameType;

public class WorldLoadEvent {
    private boolean eventSent;
    @Nullable
    private TelemetryProperty.GameMode gameMode = null;
    @Nullable
    private String serverBrand;

    public void addProperties(TelemetryPropertyMap.Builder $$0) {
        if (this.serverBrand != null) {
            $$0.put(TelemetryProperty.SERVER_MODDED, !this.serverBrand.equals((Object)"vanilla"));
        }
        $$0.put(TelemetryProperty.SERVER_TYPE, this.getServerType());
    }

    private TelemetryProperty.ServerType getServerType() {
        if (Minecraft.getInstance().isConnectedToRealms()) {
            return TelemetryProperty.ServerType.REALM;
        }
        if (Minecraft.getInstance().hasSingleplayerServer()) {
            return TelemetryProperty.ServerType.LOCAL;
        }
        return TelemetryProperty.ServerType.OTHER;
    }

    public boolean send(TelemetryEventSender $$02) {
        if (this.eventSent || this.gameMode == null || this.serverBrand == null) {
            return false;
        }
        this.eventSent = true;
        $$02.send(TelemetryEventType.WORLD_LOADED, (Consumer<TelemetryPropertyMap.Builder>)((Consumer)$$0 -> $$0.put(TelemetryProperty.GAME_MODE, this.gameMode)));
        return true;
    }

    public void setGameMode(GameType $$0, boolean $$1) {
        this.gameMode = switch ($$0) {
            default -> throw new IncompatibleClassChangeError();
            case GameType.SURVIVAL -> {
                if ($$1) {
                    yield TelemetryProperty.GameMode.HARDCORE;
                }
                yield TelemetryProperty.GameMode.SURVIVAL;
            }
            case GameType.CREATIVE -> TelemetryProperty.GameMode.CREATIVE;
            case GameType.ADVENTURE -> TelemetryProperty.GameMode.ADVENTURE;
            case GameType.SPECTATOR -> TelemetryProperty.GameMode.SPECTATOR;
        };
    }

    public void setServerBrand(String $$0) {
        this.serverBrand = $$0;
    }
}