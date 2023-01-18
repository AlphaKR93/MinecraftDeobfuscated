/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Collection
 *  java.util.HashMap
 *  java.util.List
 *  java.util.Map
 *  java.util.Objects
 *  java.util.Set
 *  java.util.UUID
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.debug.PathfindingRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.world.level.pathfinder.Path;

public class BeeDebugRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private static final boolean SHOW_GOAL_FOR_ALL_BEES = true;
    private static final boolean SHOW_NAME_FOR_ALL_BEES = true;
    private static final boolean SHOW_HIVE_FOR_ALL_BEES = true;
    private static final boolean SHOW_FLOWER_POS_FOR_ALL_BEES = true;
    private static final boolean SHOW_TRAVEL_TICKS_FOR_ALL_BEES = true;
    private static final boolean SHOW_PATH_FOR_ALL_BEES = false;
    private static final boolean SHOW_GOAL_FOR_SELECTED_BEE = true;
    private static final boolean SHOW_NAME_FOR_SELECTED_BEE = true;
    private static final boolean SHOW_HIVE_FOR_SELECTED_BEE = true;
    private static final boolean SHOW_FLOWER_POS_FOR_SELECTED_BEE = true;
    private static final boolean SHOW_TRAVEL_TICKS_FOR_SELECTED_BEE = true;
    private static final boolean SHOW_PATH_FOR_SELECTED_BEE = true;
    private static final boolean SHOW_HIVE_MEMBERS = true;
    private static final boolean SHOW_BLACKLISTS = true;
    private static final int MAX_RENDER_DIST_FOR_HIVE_OVERLAY = 30;
    private static final int MAX_RENDER_DIST_FOR_BEE_OVERLAY = 30;
    private static final int MAX_TARGETING_DIST = 8;
    private static final int HIVE_TIMEOUT = 20;
    private static final float TEXT_SCALE = 0.02f;
    private static final int WHITE = -1;
    private static final int YELLOW = -256;
    private static final int ORANGE = -23296;
    private static final int GREEN = -16711936;
    private static final int GRAY = -3355444;
    private static final int PINK = -98404;
    private static final int RED = -65536;
    private final Minecraft minecraft;
    private final Map<BlockPos, HiveInfo> hives = Maps.newHashMap();
    private final Map<UUID, BeeInfo> beeInfosPerEntity = Maps.newHashMap();
    private UUID lastLookedAtUuid;

    public BeeDebugRenderer(Minecraft $$0) {
        this.minecraft = $$0;
    }

    @Override
    public void clear() {
        this.hives.clear();
        this.beeInfosPerEntity.clear();
        this.lastLookedAtUuid = null;
    }

    public void addOrUpdateHiveInfo(HiveInfo $$0) {
        this.hives.put((Object)$$0.pos, (Object)$$0);
    }

    public void addOrUpdateBeeInfo(BeeInfo $$0) {
        this.beeInfosPerEntity.put((Object)$$0.uuid, (Object)$$0);
    }

    public void removeBeeInfo(int $$0) {
        this.beeInfosPerEntity.values().removeIf($$1 -> $$1.id == $$0);
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, double $$2, double $$3, double $$4) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        this.clearRemovedHives();
        this.clearRemovedBees();
        this.doRender();
        RenderSystem.disableBlend();
        if (!this.minecraft.player.isSpectator()) {
            this.updateLastLookedAtUuid();
        }
    }

    private void clearRemovedBees() {
        this.beeInfosPerEntity.entrySet().removeIf($$0 -> this.minecraft.level.getEntity(((BeeInfo)$$0.getValue()).id) == null);
    }

    private void clearRemovedHives() {
        long $$0 = this.minecraft.level.getGameTime() - 20L;
        this.hives.entrySet().removeIf($$1 -> ((HiveInfo)$$1.getValue()).lastSeen < $$0);
    }

    private void doRender() {
        BlockPos $$02 = this.getCamera().getBlockPosition();
        this.beeInfosPerEntity.values().forEach($$0 -> {
            if (this.isPlayerCloseEnoughToMob((BeeInfo)$$0)) {
                this.renderBeeInfo((BeeInfo)$$0);
            }
        });
        this.renderFlowerInfos();
        for (BlockPos $$12 : this.hives.keySet()) {
            if (!$$02.closerThan($$12, 30.0)) continue;
            BeeDebugRenderer.highlightHive($$12);
        }
        Map<BlockPos, Set<UUID>> $$22 = this.createHiveBlacklistMap();
        this.hives.values().forEach($$2 -> {
            if ($$02.closerThan($$2.pos, 30.0)) {
                Set $$3 = (Set)$$22.get((Object)$$2.pos);
                this.renderHiveInfo((HiveInfo)$$2, (Collection<UUID>)($$3 == null ? Sets.newHashSet() : $$3));
            }
        });
        this.getGhostHives().forEach(($$1, $$2) -> {
            if ($$02.closerThan((Vec3i)$$1, 30.0)) {
                this.renderGhostHive((BlockPos)$$1, (List<String>)$$2);
            }
        });
    }

    private Map<BlockPos, Set<UUID>> createHiveBlacklistMap() {
        HashMap $$0 = Maps.newHashMap();
        this.beeInfosPerEntity.values().forEach(arg_0 -> BeeDebugRenderer.lambda$createHiveBlacklistMap$8((Map)$$0, arg_0));
        return $$0;
    }

    private void renderFlowerInfos() {
        HashMap $$02 = Maps.newHashMap();
        this.beeInfosPerEntity.values().stream().filter(BeeInfo::hasFlower).forEach(arg_0 -> BeeDebugRenderer.lambda$renderFlowerInfos$10((Map)$$02, arg_0));
        $$02.entrySet().forEach($$0 -> {
            BlockPos $$1 = (BlockPos)$$0.getKey();
            Set $$2 = (Set)$$0.getValue();
            Set $$3 = (Set)$$2.stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet());
            int $$4 = 1;
            BeeDebugRenderer.renderTextOverPos($$3.toString(), $$1, $$4++, -256);
            BeeDebugRenderer.renderTextOverPos("Flower", $$1, $$4++, -1);
            float $$5 = 0.05f;
            BeeDebugRenderer.renderTransparentFilledBox($$1, 0.05f, 0.8f, 0.8f, 0.0f, 0.3f);
        });
    }

    private static String getBeeUuidsAsString(Collection<UUID> $$0) {
        if ($$0.isEmpty()) {
            return "-";
        }
        if ($$0.size() > 3) {
            return $$0.size() + " bees";
        }
        return ((Set)$$0.stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet())).toString();
    }

    private static void highlightHive(BlockPos $$0) {
        float $$1 = 0.05f;
        BeeDebugRenderer.renderTransparentFilledBox($$0, 0.05f, 0.2f, 0.2f, 1.0f, 0.3f);
    }

    private void renderGhostHive(BlockPos $$0, List<String> $$1) {
        float $$2 = 0.05f;
        BeeDebugRenderer.renderTransparentFilledBox($$0, 0.05f, 0.2f, 0.2f, 1.0f, 0.3f);
        BeeDebugRenderer.renderTextOverPos("" + $$1, $$0, 0, -256);
        BeeDebugRenderer.renderTextOverPos("Ghost Hive", $$0, 1, -65536);
    }

    private static void renderTransparentFilledBox(BlockPos $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        DebugRenderer.renderFilledBox($$0, $$1, $$2, $$3, $$4, $$5);
    }

    private void renderHiveInfo(HiveInfo $$0, Collection<UUID> $$1) {
        int $$2 = 0;
        if (!$$1.isEmpty()) {
            BeeDebugRenderer.renderTextOverHive("Blacklisted by " + BeeDebugRenderer.getBeeUuidsAsString($$1), $$0, $$2++, -65536);
        }
        BeeDebugRenderer.renderTextOverHive("Out: " + BeeDebugRenderer.getBeeUuidsAsString(this.getHiveMembers($$0.pos)), $$0, $$2++, -3355444);
        if ($$0.occupantCount == 0) {
            BeeDebugRenderer.renderTextOverHive("In: -", $$0, $$2++, -256);
        } else if ($$0.occupantCount == 1) {
            BeeDebugRenderer.renderTextOverHive("In: 1 bee", $$0, $$2++, -256);
        } else {
            BeeDebugRenderer.renderTextOverHive("In: " + $$0.occupantCount + " bees", $$0, $$2++, -256);
        }
        BeeDebugRenderer.renderTextOverHive("Honey: " + $$0.honeyLevel, $$0, $$2++, -23296);
        BeeDebugRenderer.renderTextOverHive($$0.hiveType + ($$0.sedated ? " (sedated)" : ""), $$0, $$2++, -1);
    }

    private void renderPath(BeeInfo $$0) {
        if ($$0.path != null) {
            PathfindingRenderer.renderPath($$0.path, 0.5f, false, false, this.getCamera().getPosition().x(), this.getCamera().getPosition().y(), this.getCamera().getPosition().z());
        }
    }

    private void renderBeeInfo(BeeInfo $$0) {
        boolean $$1 = this.isBeeSelected($$0);
        int $$2 = 0;
        BeeDebugRenderer.renderTextOverMob($$0.pos, $$2++, $$0.toString(), -1, 0.03f);
        if ($$0.hivePos == null) {
            BeeDebugRenderer.renderTextOverMob($$0.pos, $$2++, "No hive", -98404, 0.02f);
        } else {
            BeeDebugRenderer.renderTextOverMob($$0.pos, $$2++, "Hive: " + this.getPosDescription($$0, $$0.hivePos), -256, 0.02f);
        }
        if ($$0.flowerPos == null) {
            BeeDebugRenderer.renderTextOverMob($$0.pos, $$2++, "No flower", -98404, 0.02f);
        } else {
            BeeDebugRenderer.renderTextOverMob($$0.pos, $$2++, "Flower: " + this.getPosDescription($$0, $$0.flowerPos), -256, 0.02f);
        }
        for (String $$3 : $$0.goals) {
            BeeDebugRenderer.renderTextOverMob($$0.pos, $$2++, $$3, -16711936, 0.02f);
        }
        if ($$1) {
            this.renderPath($$0);
        }
        if ($$0.travelTicks > 0) {
            int $$4 = $$0.travelTicks < 600 ? -3355444 : -23296;
            BeeDebugRenderer.renderTextOverMob($$0.pos, $$2++, "Travelling: " + $$0.travelTicks + " ticks", $$4, 0.02f);
        }
    }

    private static void renderTextOverHive(String $$0, HiveInfo $$1, int $$2, int $$3) {
        BlockPos $$4 = $$1.pos;
        BeeDebugRenderer.renderTextOverPos($$0, $$4, $$2, $$3);
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

    private Camera getCamera() {
        return this.minecraft.gameRenderer.getMainCamera();
    }

    private Set<String> getHiveMemberNames(HiveInfo $$0) {
        return (Set)this.getHiveMembers($$0.pos).stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet());
    }

    private String getPosDescription(BeeInfo $$0, BlockPos $$1) {
        double $$2 = Math.sqrt((double)$$1.distToCenterSqr($$0.pos));
        double $$3 = (double)Math.round((double)($$2 * 10.0)) / 10.0;
        return $$1.toShortString() + " (dist " + $$3 + ")";
    }

    private boolean isBeeSelected(BeeInfo $$0) {
        return Objects.equals((Object)this.lastLookedAtUuid, (Object)$$0.uuid);
    }

    private boolean isPlayerCloseEnoughToMob(BeeInfo $$0) {
        LocalPlayer $$1 = this.minecraft.player;
        BlockPos $$2 = new BlockPos($$1.getX(), $$0.pos.y(), $$1.getZ());
        BlockPos $$3 = new BlockPos($$0.pos);
        return $$2.closerThan($$3, 30.0);
    }

    private Collection<UUID> getHiveMembers(BlockPos $$0) {
        return (Collection)this.beeInfosPerEntity.values().stream().filter($$1 -> $$1.hasHive($$0)).map(BeeInfo::getUuid).collect(Collectors.toSet());
    }

    private Map<BlockPos, List<String>> getGhostHives() {
        HashMap $$02 = Maps.newHashMap();
        for (BeeInfo $$1 : this.beeInfosPerEntity.values()) {
            if ($$1.hivePos == null || this.hives.containsKey((Object)$$1.hivePos)) continue;
            ((List)$$02.computeIfAbsent((Object)$$1.hivePos, $$0 -> Lists.newArrayList())).add((Object)$$1.getName());
        }
        return $$02;
    }

    private void updateLastLookedAtUuid() {
        DebugRenderer.getTargetedEntity(this.minecraft.getCameraEntity(), 8).ifPresent($$0 -> {
            this.lastLookedAtUuid = $$0.getUUID();
        });
    }

    private static /* synthetic */ void lambda$renderFlowerInfos$10(Map $$02, BeeInfo $$1) {
        ((Set)$$02.computeIfAbsent((Object)$$1.flowerPos, $$0 -> Sets.newHashSet())).add((Object)$$1.getUuid());
    }

    private static /* synthetic */ void lambda$createHiveBlacklistMap$8(Map $$0, BeeInfo $$1) {
        $$1.blacklistedHives.forEach($$2 -> ((Set)$$0.computeIfAbsent($$2, $$0 -> Sets.newHashSet())).add((Object)$$1.getUuid()));
    }

    public static class HiveInfo {
        public final BlockPos pos;
        public final String hiveType;
        public final int occupantCount;
        public final int honeyLevel;
        public final boolean sedated;
        public final long lastSeen;

        public HiveInfo(BlockPos $$0, String $$1, int $$2, int $$3, boolean $$4, long $$5) {
            this.pos = $$0;
            this.hiveType = $$1;
            this.occupantCount = $$2;
            this.honeyLevel = $$3;
            this.sedated = $$4;
            this.lastSeen = $$5;
        }
    }

    public static class BeeInfo {
        public final UUID uuid;
        public final int id;
        public final Position pos;
        @Nullable
        public final Path path;
        @Nullable
        public final BlockPos hivePos;
        @Nullable
        public final BlockPos flowerPos;
        public final int travelTicks;
        public final List<String> goals = Lists.newArrayList();
        public final Set<BlockPos> blacklistedHives = Sets.newHashSet();

        public BeeInfo(UUID $$0, int $$1, Position $$2, @Nullable Path $$3, @Nullable BlockPos $$4, @Nullable BlockPos $$5, int $$6) {
            this.uuid = $$0;
            this.id = $$1;
            this.pos = $$2;
            this.path = $$3;
            this.hivePos = $$4;
            this.flowerPos = $$5;
            this.travelTicks = $$6;
        }

        public boolean hasHive(BlockPos $$0) {
            return this.hivePos != null && this.hivePos.equals($$0);
        }

        public UUID getUuid() {
            return this.uuid;
        }

        public String getName() {
            return DebugEntityNameGenerator.getEntityName(this.uuid);
        }

        public String toString() {
            return this.getName();
        }

        public boolean hasFlower() {
            return this.flowerPos != null;
        }
    }
}