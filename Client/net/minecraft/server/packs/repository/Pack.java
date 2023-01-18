/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.logging.LogUtils
 *  java.lang.Exception
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.List
 *  java.util.function.Function
 *  java.util.function.UnaryOperator
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.server.packs.repository;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.packs.FeatureFlagsMetadataSection;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.flag.FeatureFlagSet;
import org.slf4j.Logger;

public class Pack {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final String id;
    private final ResourcesSupplier resources;
    private final Component title;
    private final Component description;
    private final PackCompatibility compatibility;
    private final FeatureFlagSet requestedFeatures;
    private final Position defaultPosition;
    private final boolean required;
    private final boolean fixedPosition;
    private final PackSource packSource;

    @Nullable
    public static Pack readMetaAndCreate(String $$0, Component $$1, boolean $$2, ResourcesSupplier $$3, PackType $$4, Position $$5, PackSource $$6) {
        Info $$7 = Pack.readPackInfo($$0, $$3);
        return $$7 != null ? Pack.create($$0, $$1, $$2, $$3, $$7, $$4, $$5, false, $$6) : null;
    }

    public static Pack create(String $$0, Component $$1, boolean $$2, ResourcesSupplier $$3, Info $$4, PackType $$5, Position $$6, boolean $$7, PackSource $$8) {
        return new Pack($$0, $$2, $$3, $$1, $$4, $$4.compatibility($$5), $$6, $$7, $$8);
    }

    private Pack(String $$0, boolean $$1, ResourcesSupplier $$2, Component $$3, Info $$4, PackCompatibility $$5, Position $$6, boolean $$7, PackSource $$8) {
        this.id = $$0;
        this.resources = $$2;
        this.title = $$3;
        this.description = $$4.description();
        this.compatibility = $$5;
        this.requestedFeatures = $$4.requestedFeatures();
        this.required = $$1;
        this.defaultPosition = $$6;
        this.fixedPosition = $$7;
        this.packSource = $$8;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    public static Info readPackInfo(String $$0, ResourcesSupplier $$1) {
        try (PackResources $$2 = $$1.open($$0);){
            PackMetadataSection $$3 = $$2.getMetadataSection(PackMetadataSection.TYPE);
            if ($$3 == null) {
                LOGGER.warn("Missing metadata in pack {}", (Object)$$0);
                Info info2 = null;
                return info2;
            }
            FeatureFlagsMetadataSection $$4 = $$2.getMetadataSection(FeatureFlagsMetadataSection.TYPE);
            FeatureFlagSet $$5 = $$4 != null ? $$4.flags() : FeatureFlagSet.of();
            Info info = new Info($$3.getDescription(), $$3.getPackFormat(), $$5);
            return info;
        }
        catch (Exception $$6) {
            LOGGER.warn("Failed to read pack metadata", (Throwable)$$6);
            return null;
        }
    }

    public Component getTitle() {
        return this.title;
    }

    public Component getDescription() {
        return this.description;
    }

    public Component getChatLink(boolean $$0) {
        return ComponentUtils.wrapInSquareBrackets(this.packSource.decorate(Component.literal(this.id))).withStyle((UnaryOperator<Style>)((UnaryOperator)$$1 -> $$1.withColor($$0 ? ChatFormatting.GREEN : ChatFormatting.RED).withInsertion(StringArgumentType.escapeIfRequired((String)this.id)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.empty().append(this.title).append("\n").append(this.description)))));
    }

    public PackCompatibility getCompatibility() {
        return this.compatibility;
    }

    public FeatureFlagSet getRequestedFeatures() {
        return this.requestedFeatures;
    }

    public PackResources open() {
        return this.resources.open(this.id);
    }

    public String getId() {
        return this.id;
    }

    public boolean isRequired() {
        return this.required;
    }

    public boolean isFixedPosition() {
        return this.fixedPosition;
    }

    public Position getDefaultPosition() {
        return this.defaultPosition;
    }

    public PackSource getPackSource() {
        return this.packSource;
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if (!($$0 instanceof Pack)) {
            return false;
        }
        Pack $$1 = (Pack)$$0;
        return this.id.equals((Object)$$1.id);
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    @FunctionalInterface
    public static interface ResourcesSupplier {
        public PackResources open(String var1);
    }

    public record Info(Component description, int format, FeatureFlagSet requestedFeatures) {
        public PackCompatibility compatibility(PackType $$0) {
            return PackCompatibility.forFormat(this.format, $$0);
        }
    }

    public static enum Position {
        TOP,
        BOTTOM;


        public <T> int insert(List<T> $$0, T $$1, Function<T, Pack> $$2, boolean $$3) {
            Pack $$8;
            int $$7;
            Position $$4;
            Position position = $$4 = $$3 ? this.opposite() : this;
            if ($$4 == BOTTOM) {
                Pack $$6;
                int $$5;
                for ($$5 = 0; $$5 < $$0.size() && ($$6 = (Pack)$$2.apply($$0.get($$5))).isFixedPosition() && $$6.getDefaultPosition() == this; ++$$5) {
                }
                $$0.add($$5, $$1);
                return $$5;
            }
            for ($$7 = $$0.size() - 1; $$7 >= 0 && ($$8 = (Pack)$$2.apply($$0.get($$7))).isFixedPosition() && $$8.getDefaultPosition() == this; --$$7) {
            }
            $$0.add($$7 + 1, $$1);
            return $$7 + 1;
        }

        public Position opposite() {
            return this == TOP ? BOTTOM : TOP;
        }
    }
}