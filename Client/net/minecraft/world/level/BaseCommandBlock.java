/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ResultConsumer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.text.SimpleDateFormat
 *  java.util.Date
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level;

import com.mojang.brigadier.ResultConsumer;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class BaseCommandBlock
implements CommandSource {
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private static final Component DEFAULT_NAME = Component.literal("@");
    private long lastExecution = -1L;
    private boolean updateLastExecution = true;
    private int successCount;
    private boolean trackOutput = true;
    @Nullable
    private Component lastOutput;
    private String command = "";
    private Component name = DEFAULT_NAME;

    public int getSuccessCount() {
        return this.successCount;
    }

    public void setSuccessCount(int $$0) {
        this.successCount = $$0;
    }

    public Component getLastOutput() {
        return this.lastOutput == null ? CommonComponents.EMPTY : this.lastOutput;
    }

    public CompoundTag save(CompoundTag $$0) {
        $$0.putString("Command", this.command);
        $$0.putInt("SuccessCount", this.successCount);
        $$0.putString("CustomName", Component.Serializer.toJson(this.name));
        $$0.putBoolean("TrackOutput", this.trackOutput);
        if (this.lastOutput != null && this.trackOutput) {
            $$0.putString("LastOutput", Component.Serializer.toJson(this.lastOutput));
        }
        $$0.putBoolean("UpdateLastExecution", this.updateLastExecution);
        if (this.updateLastExecution && this.lastExecution > 0L) {
            $$0.putLong("LastExecution", this.lastExecution);
        }
        return $$0;
    }

    public void load(CompoundTag $$0) {
        this.command = $$0.getString("Command");
        this.successCount = $$0.getInt("SuccessCount");
        if ($$0.contains("CustomName", 8)) {
            this.setName(Component.Serializer.fromJson($$0.getString("CustomName")));
        }
        if ($$0.contains("TrackOutput", 1)) {
            this.trackOutput = $$0.getBoolean("TrackOutput");
        }
        if ($$0.contains("LastOutput", 8) && this.trackOutput) {
            try {
                this.lastOutput = Component.Serializer.fromJson($$0.getString("LastOutput"));
            }
            catch (Throwable $$1) {
                this.lastOutput = Component.literal($$1.getMessage());
            }
        } else {
            this.lastOutput = null;
        }
        if ($$0.contains("UpdateLastExecution")) {
            this.updateLastExecution = $$0.getBoolean("UpdateLastExecution");
        }
        this.lastExecution = this.updateLastExecution && $$0.contains("LastExecution") ? $$0.getLong("LastExecution") : -1L;
    }

    public void setCommand(String $$0) {
        this.command = $$0;
        this.successCount = 0;
    }

    public String getCommand() {
        return this.command;
    }

    public boolean performCommand(Level $$02) {
        if ($$02.isClientSide || $$02.getGameTime() == this.lastExecution) {
            return false;
        }
        if ("Searge".equalsIgnoreCase(this.command)) {
            this.lastOutput = Component.literal("#itzlipofutzli");
            this.successCount = 1;
            return true;
        }
        this.successCount = 0;
        MinecraftServer $$12 = this.getLevel().getServer();
        if ($$12.isCommandBlockEnabled() && !StringUtil.isNullOrEmpty(this.command)) {
            try {
                this.lastOutput = null;
                CommandSourceStack $$22 = this.createCommandSourceStack().withCallback((ResultConsumer<CommandSourceStack>)((ResultConsumer)($$0, $$1, $$2) -> {
                    if ($$1) {
                        ++this.successCount;
                    }
                }));
                $$12.getCommands().performPrefixedCommand($$22, this.command);
            }
            catch (Throwable $$3) {
                CrashReport $$4 = CrashReport.forThrowable($$3, "Executing command block");
                CrashReportCategory $$5 = $$4.addCategory("Command to be executed");
                $$5.setDetail("Command", this::getCommand);
                $$5.setDetail("Name", () -> this.getName().getString());
                throw new ReportedException($$4);
            }
        }
        this.lastExecution = this.updateLastExecution ? $$02.getGameTime() : -1L;
        return true;
    }

    public Component getName() {
        return this.name;
    }

    public void setName(@Nullable Component $$0) {
        this.name = $$0 != null ? $$0 : DEFAULT_NAME;
    }

    @Override
    public void sendSystemMessage(Component $$0) {
        if (this.trackOutput) {
            this.lastOutput = Component.literal("[" + TIME_FORMAT.format(new Date()) + "] ").append($$0);
            this.onUpdated();
        }
    }

    public abstract ServerLevel getLevel();

    public abstract void onUpdated();

    public void setLastOutput(@Nullable Component $$0) {
        this.lastOutput = $$0;
    }

    public void setTrackOutput(boolean $$0) {
        this.trackOutput = $$0;
    }

    public boolean isTrackOutput() {
        return this.trackOutput;
    }

    public InteractionResult usedBy(Player $$0) {
        if (!$$0.canUseGameMasterBlocks()) {
            return InteractionResult.PASS;
        }
        if ($$0.getCommandSenderWorld().isClientSide) {
            $$0.openMinecartCommandBlock(this);
        }
        return InteractionResult.sidedSuccess($$0.level.isClientSide);
    }

    public abstract Vec3 getPosition();

    public abstract CommandSourceStack createCommandSourceStack();

    @Override
    public boolean acceptsSuccess() {
        return this.getLevel().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK) && this.trackOutput;
    }

    @Override
    public boolean acceptsFailure() {
        return this.trackOutput;
    }

    @Override
    public boolean shouldInformAdmins() {
        return this.getLevel().getGameRules().getBoolean(GameRules.RULE_COMMANDBLOCKOUTPUT);
    }
}