/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.UnaryOperator
 */
package net.minecraft.world.damagesource;

import java.util.function.UnaryOperator;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.damagesource.PointDamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class BadRespawnPointDamage
extends PointDamageSource {
    protected BadRespawnPointDamage(Vec3 $$0) {
        super("badRespawnPoint", $$0);
        this.setScalesWithDifficulty();
        this.setExplosion();
    }

    @Override
    public Component getLocalizedDeathMessage(LivingEntity $$02) {
        MutableComponent $$1 = ComponentUtils.wrapInSquareBrackets(Component.translatable("death.attack.badRespawnPoint.link")).withStyle((UnaryOperator<Style>)((UnaryOperator)$$0 -> $$0.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://bugs.mojang.com/browse/MCPE-28723")).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("MCPE-28723")))));
        return Component.translatable("death.attack.badRespawnPoint.message", $$02.getDisplayName(), $$1);
    }
}