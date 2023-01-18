/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Arrays
 *  java.util.Iterator
 *  java.util.List
 *  java.util.function.Function
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class BeehiveBlockEntity
extends BlockEntity {
    public static final String TAG_FLOWER_POS = "FlowerPos";
    public static final String MIN_OCCUPATION_TICKS = "MinOccupationTicks";
    public static final String ENTITY_DATA = "EntityData";
    public static final String TICKS_IN_HIVE = "TicksInHive";
    public static final String HAS_NECTAR = "HasNectar";
    public static final String BEES = "Bees";
    private static final List<String> IGNORED_BEE_TAGS = Arrays.asList((Object[])new String[]{"Air", "ArmorDropChances", "ArmorItems", "Brain", "CanPickUpLoot", "DeathTime", "FallDistance", "FallFlying", "Fire", "HandDropChances", "HandItems", "HurtByTimestamp", "HurtTime", "LeftHanded", "Motion", "NoGravity", "OnGround", "PortalCooldown", "Pos", "Rotation", "CannotEnterHiveTicks", "TicksSincePollination", "CropsGrownSincePollination", "HivePos", "Passengers", "Leash", "UUID"});
    public static final int MAX_OCCUPANTS = 3;
    private static final int MIN_TICKS_BEFORE_REENTERING_HIVE = 400;
    private static final int MIN_OCCUPATION_TICKS_NECTAR = 2400;
    public static final int MIN_OCCUPATION_TICKS_NECTARLESS = 600;
    private final List<BeeData> stored = Lists.newArrayList();
    @Nullable
    private BlockPos savedFlowerPos;

    public BeehiveBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.BEEHIVE, $$0, $$1);
    }

    @Override
    public void setChanged() {
        if (this.isFireNearby()) {
            this.emptyAllLivingFromHive(null, this.level.getBlockState(this.getBlockPos()), BeeReleaseStatus.EMERGENCY);
        }
        super.setChanged();
    }

    public boolean isFireNearby() {
        if (this.level == null) {
            return false;
        }
        for (BlockPos $$0 : BlockPos.betweenClosed(this.worldPosition.offset(-1, -1, -1), this.worldPosition.offset(1, 1, 1))) {
            if (!(this.level.getBlockState($$0).getBlock() instanceof FireBlock)) continue;
            return true;
        }
        return false;
    }

    public boolean isEmpty() {
        return this.stored.isEmpty();
    }

    public boolean isFull() {
        return this.stored.size() == 3;
    }

    public void emptyAllLivingFromHive(@Nullable Player $$0, BlockState $$1, BeeReleaseStatus $$2) {
        List<Entity> $$3 = this.releaseAllOccupants($$1, $$2);
        if ($$0 != null) {
            for (Entity $$4 : $$3) {
                if (!($$4 instanceof Bee)) continue;
                Bee $$5 = (Bee)$$4;
                if (!($$0.position().distanceToSqr($$4.position()) <= 16.0)) continue;
                if (!this.isSedated()) {
                    $$5.setTarget($$0);
                    continue;
                }
                $$5.setStayOutOfHiveCountdown(400);
            }
        }
    }

    private List<Entity> releaseAllOccupants(BlockState $$0, BeeReleaseStatus $$1) {
        ArrayList $$2 = Lists.newArrayList();
        this.stored.removeIf(arg_0 -> this.lambda$releaseAllOccupants$0($$0, (List)$$2, $$1, arg_0));
        if (!$$2.isEmpty()) {
            super.setChanged();
        }
        return $$2;
    }

    public void addOccupant(Entity $$0, boolean $$1) {
        this.addOccupantWithPresetTicks($$0, $$1, 0);
    }

    @VisibleForDebug
    public int getOccupantCount() {
        return this.stored.size();
    }

    public static int getHoneyLevel(BlockState $$0) {
        return $$0.getValue(BeehiveBlock.HONEY_LEVEL);
    }

    @VisibleForDebug
    public boolean isSedated() {
        return CampfireBlock.isSmokeyPos(this.level, this.getBlockPos());
    }

    public void addOccupantWithPresetTicks(Entity $$0, boolean $$1, int $$2) {
        if (this.stored.size() >= 3) {
            return;
        }
        $$0.stopRiding();
        $$0.ejectPassengers();
        CompoundTag $$3 = new CompoundTag();
        $$0.save($$3);
        this.storeBee($$3, $$2, $$1);
        if (this.level != null) {
            Bee $$4;
            if ($$0 instanceof Bee && ($$4 = (Bee)$$0).hasSavedFlowerPos() && (!this.hasSavedFlowerPos() || this.level.random.nextBoolean())) {
                this.savedFlowerPos = $$4.getSavedFlowerPos();
            }
            BlockPos $$5 = this.getBlockPos();
            this.level.playSound(null, $$5.getX(), $$5.getY(), $$5.getZ(), SoundEvents.BEEHIVE_ENTER, SoundSource.BLOCKS, 1.0f, 1.0f);
            this.level.gameEvent(GameEvent.BLOCK_CHANGE, $$5, GameEvent.Context.of($$0, this.getBlockState()));
        }
        $$0.discard();
        super.setChanged();
    }

    public void storeBee(CompoundTag $$0, int $$1, boolean $$2) {
        this.stored.add((Object)new BeeData($$0, $$1, $$2 ? 2400 : 600));
    }

    private static boolean releaseOccupant(Level $$02, BlockPos $$1, BlockState $$2, BeeData $$3, @Nullable List<Entity> $$4, BeeReleaseStatus $$5, @Nullable BlockPos $$6) {
        boolean $$10;
        if (($$02.isNight() || $$02.isRaining()) && $$5 != BeeReleaseStatus.EMERGENCY) {
            return false;
        }
        CompoundTag $$7 = $$3.entityData.copy();
        BeehiveBlockEntity.removeIgnoredBeeTags($$7);
        $$7.put("HivePos", NbtUtils.writeBlockPos($$1));
        $$7.putBoolean("NoGravity", true);
        Direction $$8 = $$2.getValue(BeehiveBlock.FACING);
        Vec3i $$9 = $$1.relative($$8);
        boolean bl = $$10 = !$$02.getBlockState((BlockPos)$$9).getCollisionShape($$02, (BlockPos)$$9).isEmpty();
        if ($$10 && $$5 != BeeReleaseStatus.EMERGENCY) {
            return false;
        }
        Entity $$11 = EntityType.loadEntityRecursive($$7, $$02, (Function<Entity, Entity>)((Function)$$0 -> $$0));
        if ($$11 != null) {
            if (!$$11.getType().is(EntityTypeTags.BEEHIVE_INHABITORS)) {
                return false;
            }
            if ($$11 instanceof Bee) {
                Bee $$12 = (Bee)$$11;
                if ($$6 != null && !$$12.hasSavedFlowerPos() && $$02.random.nextFloat() < 0.9f) {
                    $$12.setSavedFlowerPos($$6);
                }
                if ($$5 == BeeReleaseStatus.HONEY_DELIVERED) {
                    int $$13;
                    $$12.dropOffNectar();
                    if ($$2.is(BlockTags.BEEHIVES, (Predicate<BlockBehaviour.BlockStateBase>)((Predicate)$$0 -> $$0.hasProperty(BeehiveBlock.HONEY_LEVEL))) && ($$13 = BeehiveBlockEntity.getHoneyLevel($$2)) < 5) {
                        int $$14;
                        int n = $$14 = $$02.random.nextInt(100) == 0 ? 2 : 1;
                        if ($$13 + $$14 > 5) {
                            --$$14;
                        }
                        $$02.setBlockAndUpdate($$1, (BlockState)$$2.setValue(BeehiveBlock.HONEY_LEVEL, $$13 + $$14));
                    }
                }
                BeehiveBlockEntity.setBeeReleaseData($$3.ticksInHive, $$12);
                if ($$4 != null) {
                    $$4.add((Object)$$12);
                }
                float $$15 = $$11.getBbWidth();
                double $$16 = $$10 ? 0.0 : 0.55 + (double)($$15 / 2.0f);
                double $$17 = (double)$$1.getX() + 0.5 + $$16 * (double)$$8.getStepX();
                double $$18 = (double)$$1.getY() + 0.5 - (double)($$11.getBbHeight() / 2.0f);
                double $$19 = (double)$$1.getZ() + 0.5 + $$16 * (double)$$8.getStepZ();
                $$11.moveTo($$17, $$18, $$19, $$11.getYRot(), $$11.getXRot());
            }
            $$02.playSound(null, $$1, SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0f, 1.0f);
            $$02.gameEvent(GameEvent.BLOCK_CHANGE, $$1, GameEvent.Context.of($$11, $$02.getBlockState($$1)));
            return $$02.addFreshEntity($$11);
        }
        return false;
    }

    static void removeIgnoredBeeTags(CompoundTag $$0) {
        for (String $$1 : IGNORED_BEE_TAGS) {
            $$0.remove($$1);
        }
    }

    private static void setBeeReleaseData(int $$0, Bee $$1) {
        int $$2 = $$1.getAge();
        if ($$2 < 0) {
            $$1.setAge(Math.min((int)0, (int)($$2 + $$0)));
        } else if ($$2 > 0) {
            $$1.setAge(Math.max((int)0, (int)($$2 - $$0)));
        }
        $$1.setInLoveTime(Math.max((int)0, (int)($$1.getInLoveTime() - $$0)));
    }

    private boolean hasSavedFlowerPos() {
        return this.savedFlowerPos != null;
    }

    private static void tickOccupants(Level $$0, BlockPos $$1, BlockState $$2, List<BeeData> $$3, @Nullable BlockPos $$4) {
        boolean $$5 = false;
        Iterator $$6 = $$3.iterator();
        while ($$6.hasNext()) {
            BeeData $$7 = (BeeData)$$6.next();
            if ($$7.ticksInHive > $$7.minOccupationTicks) {
                BeeReleaseStatus $$8;
                BeeReleaseStatus beeReleaseStatus = $$8 = $$7.entityData.getBoolean(HAS_NECTAR) ? BeeReleaseStatus.HONEY_DELIVERED : BeeReleaseStatus.BEE_RELEASED;
                if (BeehiveBlockEntity.releaseOccupant($$0, $$1, $$2, $$7, null, $$8, $$4)) {
                    $$5 = true;
                    $$6.remove();
                }
            }
            ++$$7.ticksInHive;
        }
        if ($$5) {
            BeehiveBlockEntity.setChanged($$0, $$1, $$2);
        }
    }

    public static void serverTick(Level $$0, BlockPos $$1, BlockState $$2, BeehiveBlockEntity $$3) {
        BeehiveBlockEntity.tickOccupants($$0, $$1, $$2, $$3.stored, $$3.savedFlowerPos);
        if (!$$3.stored.isEmpty() && $$0.getRandom().nextDouble() < 0.005) {
            double $$4 = (double)$$1.getX() + 0.5;
            double $$5 = $$1.getY();
            double $$6 = (double)$$1.getZ() + 0.5;
            $$0.playSound(null, $$4, $$5, $$6, SoundEvents.BEEHIVE_WORK, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
        DebugPackets.sendHiveInfo($$0, $$1, $$2, $$3);
    }

    @Override
    public void load(CompoundTag $$0) {
        super.load($$0);
        this.stored.clear();
        ListTag $$1 = $$0.getList(BEES, 10);
        for (int $$2 = 0; $$2 < $$1.size(); ++$$2) {
            CompoundTag $$3 = $$1.getCompound($$2);
            BeeData $$4 = new BeeData($$3.getCompound(ENTITY_DATA), $$3.getInt(TICKS_IN_HIVE), $$3.getInt(MIN_OCCUPATION_TICKS));
            this.stored.add((Object)$$4);
        }
        this.savedFlowerPos = null;
        if ($$0.contains(TAG_FLOWER_POS)) {
            this.savedFlowerPos = NbtUtils.readBlockPos($$0.getCompound(TAG_FLOWER_POS));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag $$0) {
        super.saveAdditional($$0);
        $$0.put(BEES, this.writeBees());
        if (this.hasSavedFlowerPos()) {
            $$0.put(TAG_FLOWER_POS, NbtUtils.writeBlockPos(this.savedFlowerPos));
        }
    }

    public ListTag writeBees() {
        ListTag $$0 = new ListTag();
        for (BeeData $$1 : this.stored) {
            CompoundTag $$2 = $$1.entityData.copy();
            $$2.remove("UUID");
            CompoundTag $$3 = new CompoundTag();
            $$3.put(ENTITY_DATA, $$2);
            $$3.putInt(TICKS_IN_HIVE, $$1.ticksInHive);
            $$3.putInt(MIN_OCCUPATION_TICKS, $$1.minOccupationTicks);
            $$0.add($$3);
        }
        return $$0;
    }

    private /* synthetic */ boolean lambda$releaseAllOccupants$0(BlockState $$0, List $$1, BeeReleaseStatus $$2, BeeData $$3) {
        return BeehiveBlockEntity.releaseOccupant(this.level, this.worldPosition, $$0, $$3, (List<Entity>)$$1, $$2, this.savedFlowerPos);
    }

    public static enum BeeReleaseStatus {
        HONEY_DELIVERED,
        BEE_RELEASED,
        EMERGENCY;

    }

    static class BeeData {
        final CompoundTag entityData;
        int ticksInHive;
        final int minOccupationTicks;

        BeeData(CompoundTag $$0, int $$1, int $$2) {
            BeehiveBlockEntity.removeIgnoredBeeTags($$0);
            this.entityData = $$0;
            this.ticksInHive = $$1;
            this.minOccupationTicks = $$2;
        }
    }
}