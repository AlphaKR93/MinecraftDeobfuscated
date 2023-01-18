/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Unit
 *  com.mojang.serialization.Codec
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.ArrayList
 *  java.util.List
 *  javax.annotation.Nullable
 *  org.apache.commons.compress.utils.Lists
 */
package net.minecraft.client.gui.screens;

import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.SimpleOptionsSubScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Difficulty;
import org.apache.commons.compress.utils.Lists;

public class OnlineOptionsScreen
extends SimpleOptionsSubScreen {
    @Nullable
    private final OptionInstance<Unit> difficultyDisplay;

    public static OnlineOptionsScreen createOnlineOptionsScreen(Minecraft $$0, Screen $$1, Options $$2) {
        ArrayList $$3 = Lists.newArrayList();
        $$3.add($$2.realmsNotifications());
        $$3.add($$2.allowServerListing());
        OptionInstance $$4 = (OptionInstance)Util.mapNullable($$0.level, $$02 -> {
            Difficulty $$12 = $$02.getDifficulty();
            return new OptionInstance<Unit>("options.difficulty.online", OptionInstance.noTooltip(), ($$1, $$2) -> $$12.getDisplayName(), new OptionInstance.Enum(List.of((Object)Unit.INSTANCE), Codec.EMPTY.codec()), Unit.INSTANCE, $$0 -> {});
        });
        if ($$4 != null) {
            $$3.add((Object)$$4);
        }
        return new OnlineOptionsScreen($$1, $$2, (OptionInstance[])$$3.toArray((Object[])new OptionInstance[0]), $$4);
    }

    private OnlineOptionsScreen(Screen $$0, Options $$1, OptionInstance<?>[] $$2, @Nullable OptionInstance<Unit> $$3) {
        super($$0, $$1, Component.translatable("options.online.title"), $$2);
        this.difficultyDisplay = $$3;
    }

    @Override
    protected void init() {
        AbstractWidget $$1;
        AbstractWidget $$0;
        super.init();
        if (this.difficultyDisplay != null && ($$0 = this.list.findOption(this.difficultyDisplay)) != null) {
            $$0.active = false;
        }
        if (($$1 = this.list.findOption(this.options.telemetryOptInExtra())) != null) {
            $$1.active = this.minecraft.extraTelemetryAvailable();
        }
    }
}