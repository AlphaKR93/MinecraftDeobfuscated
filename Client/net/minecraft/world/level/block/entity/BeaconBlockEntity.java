/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Arrays
 *  java.util.List
 *  java.util.Set
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.LockCode;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

public class BeaconBlockEntity
extends BlockEntity
implements MenuProvider,
Nameable {
    private static final int MAX_LEVELS = 4;
    public static final MobEffect[][] BEACON_EFFECTS = new MobEffect[][]{{MobEffects.MOVEMENT_SPEED, MobEffects.DIG_SPEED}, {MobEffects.DAMAGE_RESISTANCE, MobEffects.JUMP}, {MobEffects.DAMAGE_BOOST}, {MobEffects.REGENERATION}};
    private static final Set<MobEffect> VALID_EFFECTS = (Set)Arrays.stream((Object[])BEACON_EFFECTS).flatMap(Arrays::stream).collect(Collectors.toSet());
    public static final int DATA_LEVELS = 0;
    public static final int DATA_PRIMARY = 1;
    public static final int DATA_SECONDARY = 2;
    public static final int NUM_DATA_VALUES = 3;
    private static final int BLOCKS_CHECK_PER_TICK = 10;
    private static final Component DEFAULT_NAME = Component.translatable("container.beacon");
    List<BeaconBeamSection> beamSections = Lists.newArrayList();
    private List<BeaconBeamSection> checkingBeamSections = Lists.newArrayList();
    int levels;
    private int lastCheckY;
    @Nullable
    MobEffect primaryPower;
    @Nullable
    MobEffect secondaryPower;
    @Nullable
    private Component name;
    private LockCode lockKey = LockCode.NO_LOCK;
    private final ContainerData dataAccess = new ContainerData(){

        @Override
        public int get(int $$0) {
            return switch ($$0) {
                case 0 -> BeaconBlockEntity.this.levels;
                case 1 -> MobEffect.getIdFromNullable(BeaconBlockEntity.this.primaryPower);
                case 2 -> MobEffect.getIdFromNullable(BeaconBlockEntity.this.secondaryPower);
                default -> 0;
            };
        }

        @Override
        public void set(int $$0, int $$1) {
            switch ($$0) {
                case 0: {
                    BeaconBlockEntity.this.levels = $$1;
                    break;
                }
                case 1: {
                    if (!BeaconBlockEntity.this.level.isClientSide && !BeaconBlockEntity.this.beamSections.isEmpty()) {
                        BeaconBlockEntity.playSound(BeaconBlockEntity.this.level, BeaconBlockEntity.this.worldPosition, SoundEvents.BEACON_POWER_SELECT);
                    }
                    BeaconBlockEntity.this.primaryPower = BeaconBlockEntity.getValidEffectById($$1);
                    break;
                }
                case 2: {
                    BeaconBlockEntity.this.secondaryPower = BeaconBlockEntity.getValidEffectById($$1);
                }
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    };

    public BeaconBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.BEACON, $$0, $$1);
    }

    public static void tick(Level $$0, BlockPos $$1, BlockState $$2, BeaconBlockEntity $$3) {
        Vec3i $$8;
        int $$4 = $$1.getX();
        int $$5 = $$1.getY();
        int $$6 = $$1.getZ();
        if ($$3.lastCheckY < $$5) {
            BlockPos $$7 = $$1;
            $$3.checkingBeamSections = Lists.newArrayList();
            $$3.lastCheckY = $$7.getY() - 1;
        } else {
            $$8 = new BlockPos($$4, $$3.lastCheckY + 1, $$6);
        }
        BeaconBeamSection $$9 = $$3.checkingBeamSections.isEmpty() ? null : (BeaconBeamSection)$$3.checkingBeamSections.get($$3.checkingBeamSections.size() - 1);
        int $$10 = $$0.getHeight(Heightmap.Types.WORLD_SURFACE, $$4, $$6);
        for (int $$11 = 0; $$11 < 10 && $$8.getY() <= $$10; ++$$11) {
            block18: {
                BlockState $$12;
                block16: {
                    float[] $$14;
                    block17: {
                        $$12 = $$0.getBlockState((BlockPos)$$8);
                        Block $$13 = $$12.getBlock();
                        if (!($$13 instanceof BeaconBeamBlock)) break block16;
                        $$14 = ((BeaconBeamBlock)((Object)$$13)).getColor().getTextureDiffuseColors();
                        if ($$3.checkingBeamSections.size() > 1) break block17;
                        $$9 = new BeaconBeamSection($$14);
                        $$3.checkingBeamSections.add((Object)$$9);
                        break block18;
                    }
                    if ($$9 == null) break block18;
                    if (Arrays.equals((float[])$$14, (float[])$$9.color)) {
                        $$9.increaseHeight();
                    } else {
                        $$9 = new BeaconBeamSection(new float[]{($$9.color[0] + $$14[0]) / 2.0f, ($$9.color[1] + $$14[1]) / 2.0f, ($$9.color[2] + $$14[2]) / 2.0f});
                        $$3.checkingBeamSections.add((Object)$$9);
                    }
                    break block18;
                }
                if ($$9 != null && ($$12.getLightBlock($$0, (BlockPos)$$8) < 15 || $$12.is(Blocks.BEDROCK))) {
                    $$9.increaseHeight();
                } else {
                    $$3.checkingBeamSections.clear();
                    $$3.lastCheckY = $$10;
                    break;
                }
            }
            $$8 = $$8.above();
            ++$$3.lastCheckY;
        }
        int $$15 = $$3.levels;
        if ($$0.getGameTime() % 80L == 0L) {
            if (!$$3.beamSections.isEmpty()) {
                $$3.levels = BeaconBlockEntity.updateBase($$0, $$4, $$5, $$6);
            }
            if ($$3.levels > 0 && !$$3.beamSections.isEmpty()) {
                BeaconBlockEntity.applyEffects($$0, $$1, $$3.levels, $$3.primaryPower, $$3.secondaryPower);
                BeaconBlockEntity.playSound($$0, $$1, SoundEvents.BEACON_AMBIENT);
            }
        }
        if ($$3.lastCheckY >= $$10) {
            $$3.lastCheckY = $$0.getMinBuildHeight() - 1;
            boolean $$16 = $$15 > 0;
            $$3.beamSections = $$3.checkingBeamSections;
            if (!$$0.isClientSide) {
                boolean $$17;
                boolean bl = $$17 = $$3.levels > 0;
                if (!$$16 && $$17) {
                    BeaconBlockEntity.playSound($$0, $$1, SoundEvents.BEACON_ACTIVATE);
                    for (ServerPlayer $$18 : $$0.getEntitiesOfClass(ServerPlayer.class, new AABB($$4, $$5, $$6, $$4, $$5 - 4, $$6).inflate(10.0, 5.0, 10.0))) {
                        CriteriaTriggers.CONSTRUCT_BEACON.trigger($$18, $$3.levels);
                    }
                } else if ($$16 && !$$17) {
                    BeaconBlockEntity.playSound($$0, $$1, SoundEvents.BEACON_DEACTIVATE);
                }
            }
        }
    }

    private static int updateBase(Level $$0, int $$1, int $$2, int $$3) {
        int $$6;
        int $$4 = 0;
        int $$5 = 1;
        while ($$5 <= 4 && ($$6 = $$2 - $$5) >= $$0.getMinBuildHeight()) {
            boolean $$7 = true;
            block1: for (int $$8 = $$1 - $$5; $$8 <= $$1 + $$5 && $$7; ++$$8) {
                for (int $$9 = $$3 - $$5; $$9 <= $$3 + $$5; ++$$9) {
                    if ($$0.getBlockState(new BlockPos($$8, $$6, $$9)).is(BlockTags.BEACON_BASE_BLOCKS)) continue;
                    $$7 = false;
                    continue block1;
                }
            }
            if (!$$7) break;
            $$4 = $$5++;
        }
        return $$4;
    }

    @Override
    public void setRemoved() {
        BeaconBlockEntity.playSound(this.level, this.worldPosition, SoundEvents.BEACON_DEACTIVATE);
        super.setRemoved();
    }

    private static void applyEffects(Level $$0, BlockPos $$1, int $$2, @Nullable MobEffect $$3, @Nullable MobEffect $$4) {
        if ($$0.isClientSide || $$3 == null) {
            return;
        }
        double $$5 = $$2 * 10 + 10;
        int $$6 = 0;
        if ($$2 >= 4 && $$3 == $$4) {
            $$6 = 1;
        }
        int $$7 = (9 + $$2 * 2) * 20;
        AABB $$8 = new AABB($$1).inflate($$5).expandTowards(0.0, $$0.getHeight(), 0.0);
        List $$9 = $$0.getEntitiesOfClass(Player.class, $$8);
        for (Player $$10 : $$9) {
            $$10.addEffect(new MobEffectInstance($$3, $$7, $$6, true, true));
        }
        if ($$2 >= 4 && $$3 != $$4 && $$4 != null) {
            for (Player $$11 : $$9) {
                $$11.addEffect(new MobEffectInstance($$4, $$7, 0, true, true));
            }
        }
    }

    public static void playSound(Level $$0, BlockPos $$1, SoundEvent $$2) {
        $$0.playSound(null, $$1, $$2, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    public List<BeaconBeamSection> getBeamSections() {
        return this.levels == 0 ? ImmutableList.of() : this.beamSections;
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Nullable
    static MobEffect getValidEffectById(int $$0) {
        MobEffect $$1 = MobEffect.byId($$0);
        return VALID_EFFECTS.contains((Object)$$1) ? $$1 : null;
    }

    @Override
    public void load(CompoundTag $$0) {
        super.load($$0);
        this.primaryPower = BeaconBlockEntity.getValidEffectById($$0.getInt("Primary"));
        this.secondaryPower = BeaconBlockEntity.getValidEffectById($$0.getInt("Secondary"));
        if ($$0.contains("CustomName", 8)) {
            this.name = Component.Serializer.fromJson($$0.getString("CustomName"));
        }
        this.lockKey = LockCode.fromTag($$0);
    }

    @Override
    protected void saveAdditional(CompoundTag $$0) {
        super.saveAdditional($$0);
        $$0.putInt("Primary", MobEffect.getIdFromNullable(this.primaryPower));
        $$0.putInt("Secondary", MobEffect.getIdFromNullable(this.secondaryPower));
        $$0.putInt("Levels", this.levels);
        if (this.name != null) {
            $$0.putString("CustomName", Component.Serializer.toJson(this.name));
        }
        this.lockKey.addToTag($$0);
    }

    public void setCustomName(@Nullable Component $$0) {
        this.name = $$0;
    }

    @Override
    @Nullable
    public Component getCustomName() {
        return this.name;
    }

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int $$0, Inventory $$1, Player $$2) {
        if (BaseContainerBlockEntity.canUnlock($$2, this.lockKey, this.getDisplayName())) {
            return new BeaconMenu($$0, $$1, this.dataAccess, ContainerLevelAccess.create(this.level, this.getBlockPos()));
        }
        return null;
    }

    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    @Override
    public Component getName() {
        if (this.name != null) {
            return this.name;
        }
        return DEFAULT_NAME;
    }

    @Override
    public void setLevel(Level $$0) {
        super.setLevel($$0);
        this.lastCheckY = $$0.getMinBuildHeight() - 1;
    }

    public static class BeaconBeamSection {
        final float[] color;
        private int height;

        public BeaconBeamSection(float[] $$0) {
            this.color = $$0;
            this.height = 1;
        }

        protected void increaseHeight() {
            ++this.height;
        }

        public float[] getColor() {
            return this.color;
        }

        public int getHeight() {
            return this.height;
        }
    }
}