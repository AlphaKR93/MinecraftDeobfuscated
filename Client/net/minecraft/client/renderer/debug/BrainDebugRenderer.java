/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.logging.LogUtils
 *  java.lang.Float
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Collection
 *  java.util.HashMap
 *  java.util.List
 *  java.util.Locale
 *  java.util.Map
 *  java.util.Objects
 *  java.util.Set
 *  java.util.UUID
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.debug.PathfindingRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.pathfinder.Path;
import org.slf4j.Logger;

public class BrainDebugRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final boolean SHOW_NAME_FOR_ALL = true;
    private static final boolean SHOW_PROFESSION_FOR_ALL = false;
    private static final boolean SHOW_BEHAVIORS_FOR_ALL = false;
    private static final boolean SHOW_ACTIVITIES_FOR_ALL = false;
    private static final boolean SHOW_INVENTORY_FOR_ALL = false;
    private static final boolean SHOW_GOSSIPS_FOR_ALL = false;
    private static final boolean SHOW_PATH_FOR_ALL = false;
    private static final boolean SHOW_HEALTH_FOR_ALL = false;
    private static final boolean SHOW_WANTS_GOLEM_FOR_ALL = true;
    private static final boolean SHOW_ANGER_LEVEL_FOR_ALL = false;
    private static final boolean SHOW_NAME_FOR_SELECTED = true;
    private static final boolean SHOW_PROFESSION_FOR_SELECTED = true;
    private static final boolean SHOW_BEHAVIORS_FOR_SELECTED = true;
    private static final boolean SHOW_ACTIVITIES_FOR_SELECTED = true;
    private static final boolean SHOW_MEMORIES_FOR_SELECTED = true;
    private static final boolean SHOW_INVENTORY_FOR_SELECTED = true;
    private static final boolean SHOW_GOSSIPS_FOR_SELECTED = true;
    private static final boolean SHOW_PATH_FOR_SELECTED = true;
    private static final boolean SHOW_HEALTH_FOR_SELECTED = true;
    private static final boolean SHOW_WANTS_GOLEM_FOR_SELECTED = true;
    private static final boolean SHOW_ANGER_LEVEL_FOR_SELECTED = true;
    private static final boolean SHOW_POI_INFO = true;
    private static final int MAX_RENDER_DIST_FOR_BRAIN_INFO = 30;
    private static final int MAX_RENDER_DIST_FOR_POI_INFO = 30;
    private static final int MAX_TARGETING_DIST = 8;
    private static final float TEXT_SCALE = 0.02f;
    private static final int WHITE = -1;
    private static final int YELLOW = -256;
    private static final int CYAN = -16711681;
    private static final int GREEN = -16711936;
    private static final int GRAY = -3355444;
    private static final int PINK = -98404;
    private static final int RED = -65536;
    private static final int ORANGE = -23296;
    private final Minecraft minecraft;
    private final Map<BlockPos, PoiInfo> pois = Maps.newHashMap();
    private final Map<UUID, BrainDump> brainDumpsPerEntity = Maps.newHashMap();
    @Nullable
    private UUID lastLookedAtUuid;

    public BrainDebugRenderer(Minecraft $$0) {
        this.minecraft = $$0;
    }

    @Override
    public void clear() {
        this.pois.clear();
        this.brainDumpsPerEntity.clear();
        this.lastLookedAtUuid = null;
    }

    public void addPoi(PoiInfo $$0) {
        this.pois.put((Object)$$0.pos, (Object)$$0);
    }

    public void removePoi(BlockPos $$0) {
        this.pois.remove((Object)$$0);
    }

    public void setFreeTicketCount(BlockPos $$0, int $$1) {
        PoiInfo $$2 = (PoiInfo)this.pois.get((Object)$$0);
        if ($$2 == null) {
            LOGGER.warn("Strange, setFreeTicketCount was called for an unknown POI: {}", (Object)$$0);
            return;
        }
        $$2.freeTicketCount = $$1;
    }

    public void addOrUpdateBrainDump(BrainDump $$0) {
        this.brainDumpsPerEntity.put((Object)$$0.uuid, (Object)$$0);
    }

    public void removeBrainDump(int $$0) {
        this.brainDumpsPerEntity.values().removeIf($$1 -> $$1.id == $$0);
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, double $$2, double $$3, double $$4) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        this.clearRemovedEntities();
        this.doRender($$2, $$3, $$4);
        RenderSystem.disableBlend();
        if (!this.minecraft.player.isSpectator()) {
            this.updateLastLookedAtUuid();
        }
    }

    private void clearRemovedEntities() {
        this.brainDumpsPerEntity.entrySet().removeIf($$0 -> {
            Entity $$1 = this.minecraft.level.getEntity(((BrainDump)$$0.getValue()).id);
            return $$1 == null || $$1.isRemoved();
        });
    }

    private void doRender(double $$0, double $$12, double $$22) {
        BlockPos $$32 = new BlockPos($$0, $$12, $$22);
        this.brainDumpsPerEntity.values().forEach($$3 -> {
            if (this.isPlayerCloseEnoughToMob((BrainDump)$$3)) {
                this.renderBrainInfo((BrainDump)$$3, $$0, $$12, $$22);
            }
        });
        for (BlockPos $$4 : this.pois.keySet()) {
            if (!$$32.closerThan($$4, 30.0)) continue;
            BrainDebugRenderer.highlightPoi($$4);
        }
        this.pois.values().forEach($$1 -> {
            if ($$32.closerThan($$1.pos, 30.0)) {
                this.renderPoiInfo((PoiInfo)$$1);
            }
        });
        this.getGhostPois().forEach(($$1, $$2) -> {
            if ($$32.closerThan((Vec3i)$$1, 30.0)) {
                this.renderGhostPoi((BlockPos)$$1, (List<String>)$$2);
            }
        });
    }

    private static void highlightPoi(BlockPos $$0) {
        float $$1 = 0.05f;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        DebugRenderer.renderFilledBox($$0, 0.05f, 0.2f, 0.2f, 1.0f, 0.3f);
    }

    private void renderGhostPoi(BlockPos $$0, List<String> $$1) {
        float $$2 = 0.05f;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        DebugRenderer.renderFilledBox($$0, 0.05f, 0.2f, 0.2f, 1.0f, 0.3f);
        BrainDebugRenderer.renderTextOverPos("" + $$1, $$0, 0, -256);
        BrainDebugRenderer.renderTextOverPos("Ghost POI", $$0, 1, -65536);
    }

    private void renderPoiInfo(PoiInfo $$0) {
        int $$1 = 0;
        Set<String> $$2 = this.getTicketHolderNames($$0);
        if ($$2.size() < 4) {
            BrainDebugRenderer.renderTextOverPoi("Owners: " + $$2, $$0, $$1, -256);
        } else {
            BrainDebugRenderer.renderTextOverPoi($$2.size() + " ticket holders", $$0, $$1, -256);
        }
        ++$$1;
        Set<String> $$3 = this.getPotentialTicketHolderNames($$0);
        if ($$3.size() < 4) {
            BrainDebugRenderer.renderTextOverPoi("Candidates: " + $$3, $$0, $$1, -23296);
        } else {
            BrainDebugRenderer.renderTextOverPoi($$3.size() + " potential owners", $$0, $$1, -23296);
        }
        BrainDebugRenderer.renderTextOverPoi("Free tickets: " + $$0.freeTicketCount, $$0, ++$$1, -256);
        BrainDebugRenderer.renderTextOverPoi($$0.type, $$0, ++$$1, -1);
    }

    private void renderPath(BrainDump $$0, double $$1, double $$2, double $$3) {
        if ($$0.path != null) {
            PathfindingRenderer.renderPath($$0.path, 0.5f, false, false, $$1, $$2, $$3);
        }
    }

    private void renderBrainInfo(BrainDump $$0, double $$1, double $$2, double $$3) {
        boolean $$4 = this.isMobSelected($$0);
        int $$5 = 0;
        BrainDebugRenderer.renderTextOverMob($$0.pos, $$5, $$0.name, -1, 0.03f);
        ++$$5;
        if ($$4) {
            BrainDebugRenderer.renderTextOverMob($$0.pos, $$5, $$0.profession + " " + $$0.xp + " xp", -1, 0.02f);
            ++$$5;
        }
        if ($$4) {
            int $$6 = $$0.health < $$0.maxHealth ? -23296 : -1;
            BrainDebugRenderer.renderTextOverMob($$0.pos, $$5, "health: " + String.format((Locale)Locale.ROOT, (String)"%.1f", (Object[])new Object[]{Float.valueOf((float)$$0.health)}) + " / " + String.format((Locale)Locale.ROOT, (String)"%.1f", (Object[])new Object[]{Float.valueOf((float)$$0.maxHealth)}), $$6, 0.02f);
            ++$$5;
        }
        if ($$4 && !$$0.inventory.equals((Object)"")) {
            BrainDebugRenderer.renderTextOverMob($$0.pos, $$5, $$0.inventory, -98404, 0.02f);
            ++$$5;
        }
        if ($$4) {
            for (String $$7 : $$0.behaviors) {
                BrainDebugRenderer.renderTextOverMob($$0.pos, $$5, $$7, -16711681, 0.02f);
                ++$$5;
            }
        }
        if ($$4) {
            for (String $$8 : $$0.activities) {
                BrainDebugRenderer.renderTextOverMob($$0.pos, $$5, $$8, -16711936, 0.02f);
                ++$$5;
            }
        }
        if ($$0.wantsGolem) {
            BrainDebugRenderer.renderTextOverMob($$0.pos, $$5, "Wants Golem", -23296, 0.02f);
            ++$$5;
        }
        if ($$4 && $$0.angerLevel != -1) {
            BrainDebugRenderer.renderTextOverMob($$0.pos, $$5, "Anger Level: " + $$0.angerLevel, -98404, 0.02f);
            ++$$5;
        }
        if ($$4) {
            for (String $$9 : $$0.gossips) {
                if ($$9.startsWith($$0.name)) {
                    BrainDebugRenderer.renderTextOverMob($$0.pos, $$5, $$9, -1, 0.02f);
                } else {
                    BrainDebugRenderer.renderTextOverMob($$0.pos, $$5, $$9, -23296, 0.02f);
                }
                ++$$5;
            }
        }
        if ($$4) {
            for (String $$10 : Lists.reverse($$0.memories)) {
                BrainDebugRenderer.renderTextOverMob($$0.pos, $$5, $$10, -3355444, 0.02f);
                ++$$5;
            }
        }
        if ($$4) {
            this.renderPath($$0, $$1, $$2, $$3);
        }
    }

    private static void renderTextOverPoi(String $$0, PoiInfo $$1, int $$2, int $$3) {
        BlockPos $$4 = $$1.pos;
        BrainDebugRenderer.renderTextOverPos($$0, $$4, $$2, $$3);
    }

    private static void renderTextOverPos(String $$0, BlockPos $$1, int $$2, int $$3) {
        double $$4 = 1.3;
        double $$5 = 0.2;
        double $$6 = (double)$$1.getX() + 0.5;
        double $$7 = (double)$$1.getY() + 1.3 + (double)$$2 * 0.2;
        double $$8 = (double)$$1.getZ() + 0.5;
        DebugRenderer.renderFloatingText($$0, $$6, $$7, $$8, $$3, 0.02f, true, 0.0f, true);
    }

    private static void renderTextOverMob(Position $$0, int $$1, String $$2, int $$3, float $$4) {
        double $$5 = 2.4;
        double $$6 = 0.25;
        BlockPos $$7 = new BlockPos($$0);
        double $$8 = (double)$$7.getX() + 0.5;
        double $$9 = $$0.y() + 2.4 + (double)$$1 * 0.25;
        double $$10 = (double)$$7.getZ() + 0.5;
        float $$11 = 0.5f;
        DebugRenderer.renderFloatingText($$2, $$8, $$9, $$10, $$3, $$4, false, 0.5f, true);
    }

    private Set<String> getTicketHolderNames(PoiInfo $$0) {
        return (Set)this.getTicketHolders($$0.pos).stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet());
    }

    private Set<String> getPotentialTicketHolderNames(PoiInfo $$0) {
        return (Set)this.getPotentialTicketHolders($$0.pos).stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet());
    }

    private boolean isMobSelected(BrainDump $$0) {
        return Objects.equals((Object)this.lastLookedAtUuid, (Object)$$0.uuid);
    }

    private boolean isPlayerCloseEnoughToMob(BrainDump $$0) {
        LocalPlayer $$1 = this.minecraft.player;
        BlockPos $$2 = new BlockPos($$1.getX(), $$0.pos.y(), $$1.getZ());
        BlockPos $$3 = new BlockPos($$0.pos);
        return $$2.closerThan($$3, 30.0);
    }

    private Collection<UUID> getTicketHolders(BlockPos $$0) {
        return (Collection)this.brainDumpsPerEntity.values().stream().filter($$1 -> $$1.hasPoi($$0)).map(BrainDump::getUuid).collect(Collectors.toSet());
    }

    private Collection<UUID> getPotentialTicketHolders(BlockPos $$0) {
        return (Collection)this.brainDumpsPerEntity.values().stream().filter($$1 -> $$1.hasPotentialPoi($$0)).map(BrainDump::getUuid).collect(Collectors.toSet());
    }

    private Map<BlockPos, List<String>> getGhostPois() {
        HashMap $$02 = Maps.newHashMap();
        for (BrainDump $$1 : this.brainDumpsPerEntity.values()) {
            for (BlockPos $$2 : Iterables.concat($$1.pois, $$1.potentialPois)) {
                if (this.pois.containsKey((Object)$$2)) continue;
                ((List)$$02.computeIfAbsent((Object)$$2, $$0 -> Lists.newArrayList())).add((Object)$$1.name);
            }
        }
        return $$02;
    }

    private void updateLastLookedAtUuid() {
        DebugRenderer.getTargetedEntity(this.minecraft.getCameraEntity(), 8).ifPresent($$0 -> {
            this.lastLookedAtUuid = $$0.getUUID();
        });
    }

    public static class PoiInfo {
        public final BlockPos pos;
        public String type;
        public int freeTicketCount;

        public PoiInfo(BlockPos $$0, String $$1, int $$2) {
            this.pos = $$0;
            this.type = $$1;
            this.freeTicketCount = $$2;
        }
    }

    public static class BrainDump {
        public final UUID uuid;
        public final int id;
        public final String name;
        public final String profession;
        public final int xp;
        public final float health;
        public final float maxHealth;
        public final Position pos;
        public final String inventory;
        public final Path path;
        public final boolean wantsGolem;
        public final int angerLevel;
        public final List<String> activities = Lists.newArrayList();
        public final List<String> behaviors = Lists.newArrayList();
        public final List<String> memories = Lists.newArrayList();
        public final List<String> gossips = Lists.newArrayList();
        public final Set<BlockPos> pois = Sets.newHashSet();
        public final Set<BlockPos> potentialPois = Sets.newHashSet();

        public BrainDump(UUID $$0, int $$1, String $$2, String $$3, int $$4, float $$5, float $$6, Position $$7, String $$8, @Nullable Path $$9, boolean $$10, int $$11) {
            this.uuid = $$0;
            this.id = $$1;
            this.name = $$2;
            this.profession = $$3;
            this.xp = $$4;
            this.health = $$5;
            this.maxHealth = $$6;
            this.pos = $$7;
            this.inventory = $$8;
            this.path = $$9;
            this.wantsGolem = $$10;
            this.angerLevel = $$11;
        }

        boolean hasPoi(BlockPos $$0) {
            return this.pois.stream().anyMatch($$0::equals);
        }

        boolean hasPotentialPoi(BlockPos $$0) {
            return this.potentialPois.contains((Object)$$0);
        }

        public UUID getUuid() {
            return this.uuid;
        }
    }
}