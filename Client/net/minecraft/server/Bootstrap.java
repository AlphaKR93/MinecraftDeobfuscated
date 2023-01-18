/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.io.OutputStream
 *  java.io.PrintStream
 *  java.lang.Exception
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.System
 *  java.lang.Throwable
 *  java.util.Set
 *  java.util.TreeSet
 *  java.util.function.Function
 *  java.util.function.Supplier
 *  org.slf4j.Logger
 */
package net.minecraft.server;

import com.mojang.logging.LogUtils;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.selector.options.EntitySelectorOptions;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.server.DebugLoggedPrintStream;
import net.minecraft.server.LoggedPrintStream;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.FireBlock;
import org.slf4j.Logger;

public class Bootstrap {
    public static final PrintStream STDOUT = System.out;
    private static volatile boolean isBootstrapped;
    private static final Logger LOGGER;

    public static void bootStrap() {
        if (isBootstrapped) {
            return;
        }
        isBootstrapped = true;
        if (BuiltInRegistries.REGISTRY.keySet().isEmpty()) {
            throw new IllegalStateException("Unable to load registries");
        }
        FireBlock.bootStrap();
        ComposterBlock.bootStrap();
        if (EntityType.getKey(EntityType.PLAYER) == null) {
            throw new IllegalStateException("Failed loading EntityTypes");
        }
        PotionBrewing.bootStrap();
        EntitySelectorOptions.bootStrap();
        DispenseItemBehavior.bootStrap();
        CauldronInteraction.bootStrap();
        BuiltInRegistries.bootStrap();
        Bootstrap.wrapStreams();
    }

    private static <T> void checkTranslations(Iterable<T> $$0, Function<T, String> $$1, Set<String> $$2) {
        Language $$32 = Language.getInstance();
        $$0.forEach($$3 -> {
            String $$4 = (String)$$1.apply($$3);
            if (!$$32.has($$4)) {
                $$2.add((Object)$$4);
            }
        });
    }

    private static void checkGameruleTranslations(final Set<String> $$0) {
        final Language $$1 = Language.getInstance();
        GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor(){

            @Override
            public <T extends GameRules.Value<T>> void visit(GameRules.Key<T> $$02, GameRules.Type<T> $$12) {
                if (!$$1.has($$02.getDescriptionId())) {
                    $$0.add((Object)$$02.getId());
                }
            }
        });
    }

    public static Set<String> getMissingTranslations() {
        TreeSet $$02 = new TreeSet();
        Bootstrap.checkTranslations(BuiltInRegistries.ATTRIBUTE, Attribute::getDescriptionId, (Set<String>)$$02);
        Bootstrap.checkTranslations(BuiltInRegistries.ENTITY_TYPE, EntityType::getDescriptionId, (Set<String>)$$02);
        Bootstrap.checkTranslations(BuiltInRegistries.MOB_EFFECT, MobEffect::getDescriptionId, (Set<String>)$$02);
        Bootstrap.checkTranslations(BuiltInRegistries.ITEM, Item::getDescriptionId, (Set<String>)$$02);
        Bootstrap.checkTranslations(BuiltInRegistries.ENCHANTMENT, Enchantment::getDescriptionId, (Set<String>)$$02);
        Bootstrap.checkTranslations(BuiltInRegistries.BLOCK, Block::getDescriptionId, (Set<String>)$$02);
        Bootstrap.checkTranslations(BuiltInRegistries.CUSTOM_STAT, $$0 -> "stat." + $$0.toString().replace(':', '.'), (Set<String>)$$02);
        Bootstrap.checkGameruleTranslations((Set<String>)$$02);
        return $$02;
    }

    public static void checkBootstrapCalled(Supplier<String> $$0) {
        if (!isBootstrapped) {
            throw Bootstrap.createBootstrapException($$0);
        }
    }

    private static RuntimeException createBootstrapException(Supplier<String> $$0) {
        try {
            String $$1 = (String)$$0.get();
            return new IllegalArgumentException("Not bootstrapped (called from " + $$1 + ")");
        }
        catch (Exception $$2) {
            IllegalArgumentException $$3 = new IllegalArgumentException("Not bootstrapped (failed to resolve location)");
            $$3.addSuppressed((Throwable)$$2);
            return $$3;
        }
    }

    public static void validate() {
        Bootstrap.checkBootstrapCalled((Supplier<String>)((Supplier)() -> "validate"));
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            Bootstrap.getMissingTranslations().forEach($$0 -> LOGGER.error("Missing translations: {}", $$0));
            Commands.validate();
        }
        DefaultAttributes.validate();
    }

    private static void wrapStreams() {
        if (LOGGER.isDebugEnabled()) {
            System.setErr((PrintStream)new DebugLoggedPrintStream("STDERR", (OutputStream)System.err));
            System.setOut((PrintStream)new DebugLoggedPrintStream("STDOUT", (OutputStream)STDOUT));
        } else {
            System.setErr((PrintStream)new LoggedPrintStream("STDERR", (OutputStream)System.err));
            System.setOut((PrintStream)new LoggedPrintStream("STDOUT", (OutputStream)STDOUT));
        }
    }

    public static void realStdoutPrintln(String $$0) {
        STDOUT.println($$0);
    }

    static {
        LOGGER = LogUtils.getLogger();
    }
}