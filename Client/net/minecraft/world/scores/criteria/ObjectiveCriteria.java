/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Collection
 *  java.util.Map
 *  java.util.Optional
 *  java.util.Set
 *  java.util.function.Supplier
 */
package net.minecraft.world.scores.criteria;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatType;
import net.minecraft.util.StringRepresentable;

public class ObjectiveCriteria {
    private static final Map<String, ObjectiveCriteria> CUSTOM_CRITERIA = Maps.newHashMap();
    private static final Map<String, ObjectiveCriteria> CRITERIA_CACHE = Maps.newHashMap();
    public static final ObjectiveCriteria DUMMY = ObjectiveCriteria.registerCustom("dummy");
    public static final ObjectiveCriteria TRIGGER = ObjectiveCriteria.registerCustom("trigger");
    public static final ObjectiveCriteria DEATH_COUNT = ObjectiveCriteria.registerCustom("deathCount");
    public static final ObjectiveCriteria KILL_COUNT_PLAYERS = ObjectiveCriteria.registerCustom("playerKillCount");
    public static final ObjectiveCriteria KILL_COUNT_ALL = ObjectiveCriteria.registerCustom("totalKillCount");
    public static final ObjectiveCriteria HEALTH = ObjectiveCriteria.registerCustom("health", true, RenderType.HEARTS);
    public static final ObjectiveCriteria FOOD = ObjectiveCriteria.registerCustom("food", true, RenderType.INTEGER);
    public static final ObjectiveCriteria AIR = ObjectiveCriteria.registerCustom("air", true, RenderType.INTEGER);
    public static final ObjectiveCriteria ARMOR = ObjectiveCriteria.registerCustom("armor", true, RenderType.INTEGER);
    public static final ObjectiveCriteria EXPERIENCE = ObjectiveCriteria.registerCustom("xp", true, RenderType.INTEGER);
    public static final ObjectiveCriteria LEVEL = ObjectiveCriteria.registerCustom("level", true, RenderType.INTEGER);
    public static final ObjectiveCriteria[] TEAM_KILL = new ObjectiveCriteria[]{ObjectiveCriteria.registerCustom("teamkill." + ChatFormatting.BLACK.getName()), ObjectiveCriteria.registerCustom("teamkill." + ChatFormatting.DARK_BLUE.getName()), ObjectiveCriteria.registerCustom("teamkill." + ChatFormatting.DARK_GREEN.getName()), ObjectiveCriteria.registerCustom("teamkill." + ChatFormatting.DARK_AQUA.getName()), ObjectiveCriteria.registerCustom("teamkill." + ChatFormatting.DARK_RED.getName()), ObjectiveCriteria.registerCustom("teamkill." + ChatFormatting.DARK_PURPLE.getName()), ObjectiveCriteria.registerCustom("teamkill." + ChatFormatting.GOLD.getName()), ObjectiveCriteria.registerCustom("teamkill." + ChatFormatting.GRAY.getName()), ObjectiveCriteria.registerCustom("teamkill." + ChatFormatting.DARK_GRAY.getName()), ObjectiveCriteria.registerCustom("teamkill." + ChatFormatting.BLUE.getName()), ObjectiveCriteria.registerCustom("teamkill." + ChatFormatting.GREEN.getName()), ObjectiveCriteria.registerCustom("teamkill." + ChatFormatting.AQUA.getName()), ObjectiveCriteria.registerCustom("teamkill." + ChatFormatting.RED.getName()), ObjectiveCriteria.registerCustom("teamkill." + ChatFormatting.LIGHT_PURPLE.getName()), ObjectiveCriteria.registerCustom("teamkill." + ChatFormatting.YELLOW.getName()), ObjectiveCriteria.registerCustom("teamkill." + ChatFormatting.WHITE.getName())};
    public static final ObjectiveCriteria[] KILLED_BY_TEAM = new ObjectiveCriteria[]{ObjectiveCriteria.registerCustom("killedByTeam." + ChatFormatting.BLACK.getName()), ObjectiveCriteria.registerCustom("killedByTeam." + ChatFormatting.DARK_BLUE.getName()), ObjectiveCriteria.registerCustom("killedByTeam." + ChatFormatting.DARK_GREEN.getName()), ObjectiveCriteria.registerCustom("killedByTeam." + ChatFormatting.DARK_AQUA.getName()), ObjectiveCriteria.registerCustom("killedByTeam." + ChatFormatting.DARK_RED.getName()), ObjectiveCriteria.registerCustom("killedByTeam." + ChatFormatting.DARK_PURPLE.getName()), ObjectiveCriteria.registerCustom("killedByTeam." + ChatFormatting.GOLD.getName()), ObjectiveCriteria.registerCustom("killedByTeam." + ChatFormatting.GRAY.getName()), ObjectiveCriteria.registerCustom("killedByTeam." + ChatFormatting.DARK_GRAY.getName()), ObjectiveCriteria.registerCustom("killedByTeam." + ChatFormatting.BLUE.getName()), ObjectiveCriteria.registerCustom("killedByTeam." + ChatFormatting.GREEN.getName()), ObjectiveCriteria.registerCustom("killedByTeam." + ChatFormatting.AQUA.getName()), ObjectiveCriteria.registerCustom("killedByTeam." + ChatFormatting.RED.getName()), ObjectiveCriteria.registerCustom("killedByTeam." + ChatFormatting.LIGHT_PURPLE.getName()), ObjectiveCriteria.registerCustom("killedByTeam." + ChatFormatting.YELLOW.getName()), ObjectiveCriteria.registerCustom("killedByTeam." + ChatFormatting.WHITE.getName())};
    private final String name;
    private final boolean readOnly;
    private final RenderType renderType;

    private static ObjectiveCriteria registerCustom(String $$0, boolean $$1, RenderType $$2) {
        ObjectiveCriteria $$3 = new ObjectiveCriteria($$0, $$1, $$2);
        CUSTOM_CRITERIA.put((Object)$$0, (Object)$$3);
        return $$3;
    }

    private static ObjectiveCriteria registerCustom(String $$0) {
        return ObjectiveCriteria.registerCustom($$0, false, RenderType.INTEGER);
    }

    protected ObjectiveCriteria(String $$0) {
        this($$0, false, RenderType.INTEGER);
    }

    protected ObjectiveCriteria(String $$0, boolean $$1, RenderType $$2) {
        this.name = $$0;
        this.readOnly = $$1;
        this.renderType = $$2;
        CRITERIA_CACHE.put((Object)$$0, (Object)this);
    }

    public static Set<String> getCustomCriteriaNames() {
        return ImmutableSet.copyOf((Collection)CUSTOM_CRITERIA.keySet());
    }

    public static Optional<ObjectiveCriteria> byName(String $$0) {
        ObjectiveCriteria $$1 = (ObjectiveCriteria)CRITERIA_CACHE.get((Object)$$0);
        if ($$1 != null) {
            return Optional.of((Object)$$1);
        }
        int $$22 = $$0.indexOf(58);
        if ($$22 < 0) {
            return Optional.empty();
        }
        return BuiltInRegistries.STAT_TYPE.getOptional(ResourceLocation.of($$0.substring(0, $$22), '.')).flatMap($$2 -> ObjectiveCriteria.getStat($$2, ResourceLocation.of($$0.substring($$22 + 1), '.')));
    }

    private static <T> Optional<ObjectiveCriteria> getStat(StatType<T> $$0, ResourceLocation $$1) {
        return $$0.getRegistry().getOptional($$1).map($$0::get);
    }

    public String getName() {
        return this.name;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public RenderType getDefaultRenderType() {
        return this.renderType;
    }

    public static enum RenderType implements StringRepresentable
    {
        INTEGER("integer"),
        HEARTS("hearts");

        private final String id;
        public static final StringRepresentable.EnumCodec<RenderType> CODEC;

        private RenderType(String $$0) {
            this.id = $$0;
        }

        public String getId() {
            return this.id;
        }

        @Override
        public String getSerializedName() {
            return this.id;
        }

        public static RenderType byId(String $$0) {
            return CODEC.byName($$0, INTEGER);
        }

        static {
            CODEC = StringRepresentable.fromEnum((Supplier<E[]>)((Supplier)RenderType::values));
        }
    }
}