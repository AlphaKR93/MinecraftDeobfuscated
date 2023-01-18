/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  java.util.function.IntFunction
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.animal;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;

public class TropicalFish
extends AbstractSchoolingFish
implements VariantHolder<Pattern> {
    public static final String BUCKET_VARIANT_TAG = "BucketVariantTag";
    private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(TropicalFish.class, EntityDataSerializers.INT);
    public static final List<Variant> COMMON_VARIANTS = List.of((Object[])new Variant[]{new Variant(Pattern.STRIPEY, DyeColor.ORANGE, DyeColor.GRAY), new Variant(Pattern.FLOPPER, DyeColor.GRAY, DyeColor.GRAY), new Variant(Pattern.FLOPPER, DyeColor.GRAY, DyeColor.BLUE), new Variant(Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.GRAY), new Variant(Pattern.SUNSTREAK, DyeColor.BLUE, DyeColor.GRAY), new Variant(Pattern.KOB, DyeColor.ORANGE, DyeColor.WHITE), new Variant(Pattern.SPOTTY, DyeColor.PINK, DyeColor.LIGHT_BLUE), new Variant(Pattern.BLOCKFISH, DyeColor.PURPLE, DyeColor.YELLOW), new Variant(Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.RED), new Variant(Pattern.SPOTTY, DyeColor.WHITE, DyeColor.YELLOW), new Variant(Pattern.GLITTER, DyeColor.WHITE, DyeColor.GRAY), new Variant(Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.ORANGE), new Variant(Pattern.DASHER, DyeColor.CYAN, DyeColor.PINK), new Variant(Pattern.BRINELY, DyeColor.LIME, DyeColor.LIGHT_BLUE), new Variant(Pattern.BETTY, DyeColor.RED, DyeColor.WHITE), new Variant(Pattern.SNOOPER, DyeColor.GRAY, DyeColor.RED), new Variant(Pattern.BLOCKFISH, DyeColor.RED, DyeColor.WHITE), new Variant(Pattern.FLOPPER, DyeColor.WHITE, DyeColor.YELLOW), new Variant(Pattern.KOB, DyeColor.RED, DyeColor.WHITE), new Variant(Pattern.SUNSTREAK, DyeColor.GRAY, DyeColor.WHITE), new Variant(Pattern.DASHER, DyeColor.CYAN, DyeColor.YELLOW), new Variant(Pattern.FLOPPER, DyeColor.YELLOW, DyeColor.YELLOW)});
    private boolean isSchool = true;

    public TropicalFish(EntityType<? extends TropicalFish> $$0, Level $$1) {
        super((EntityType<? extends AbstractSchoolingFish>)$$0, $$1);
    }

    public static String getPredefinedName(int $$0) {
        return "entity.minecraft.tropical_fish.predefined." + $$0;
    }

    static int packVariant(Pattern $$0, DyeColor $$1, DyeColor $$2) {
        return $$0.getPackedId() & 0xFFFF | ($$1.getId() & 0xFF) << 16 | ($$2.getId() & 0xFF) << 24;
    }

    public static DyeColor getBaseColor(int $$0) {
        return DyeColor.byId($$0 >> 16 & 0xFF);
    }

    public static DyeColor getPatternColor(int $$0) {
        return DyeColor.byId($$0 >> 24 & 0xFF);
    }

    public static Pattern getPattern(int $$0) {
        return Pattern.byId($$0 & 0xFFFF);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_TYPE_VARIANT, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putInt("Variant", this.getPackedVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        this.setPackedVariant($$0.getInt("Variant"));
    }

    private void setPackedVariant(int $$0) {
        this.entityData.set(DATA_ID_TYPE_VARIANT, $$0);
    }

    @Override
    public boolean isMaxGroupSizeReached(int $$0) {
        return !this.isSchool;
    }

    private int getPackedVariant() {
        return this.entityData.get(DATA_ID_TYPE_VARIANT);
    }

    public DyeColor getBaseColor() {
        return TropicalFish.getBaseColor(this.getPackedVariant());
    }

    public DyeColor getPatternColor() {
        return TropicalFish.getPatternColor(this.getPackedVariant());
    }

    @Override
    public Pattern getVariant() {
        return TropicalFish.getPattern(this.getPackedVariant());
    }

    @Override
    public void setVariant(Pattern $$0) {
        int $$1 = this.getPackedVariant();
        DyeColor $$2 = TropicalFish.getBaseColor($$1);
        DyeColor $$3 = TropicalFish.getPatternColor($$1);
        this.setPackedVariant(TropicalFish.packVariant($$0, $$2, $$3));
    }

    @Override
    public void saveToBucketTag(ItemStack $$0) {
        super.saveToBucketTag($$0);
        CompoundTag $$1 = $$0.getOrCreateTag();
        $$1.putInt(BUCKET_VARIANT_TAG, this.getPackedVariant());
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(Items.TROPICAL_FISH_BUCKET);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.TROPICAL_FISH_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.TROPICAL_FISH_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.TROPICAL_FISH_HURT;
    }

    @Override
    protected SoundEvent getFlopSound() {
        return SoundEvents.TROPICAL_FISH_FLOP;
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor $$0, DifficultyInstance $$1, MobSpawnType $$2, @Nullable SpawnGroupData $$3, @Nullable CompoundTag $$4) {
        Variant $$14;
        $$3 = super.finalizeSpawn($$0, $$1, $$2, $$3, $$4);
        if ($$2 == MobSpawnType.BUCKET && $$4 != null && $$4.contains(BUCKET_VARIANT_TAG, 3)) {
            this.setPackedVariant($$4.getInt(BUCKET_VARIANT_TAG));
            return $$3;
        }
        RandomSource $$5 = $$0.getRandom();
        if ($$3 instanceof TropicalFishGroupData) {
            TropicalFishGroupData $$6 = (TropicalFishGroupData)$$3;
            Variant $$7 = $$6.variant;
        } else if ((double)$$5.nextFloat() < 0.9) {
            Variant $$8 = Util.getRandom(COMMON_VARIANTS, $$5);
            $$3 = new TropicalFishGroupData(this, $$8);
        } else {
            this.isSchool = false;
            Pattern[] $$9 = Pattern.values();
            DyeColor[] $$10 = DyeColor.values();
            Pattern $$11 = Util.getRandom($$9, $$5);
            DyeColor $$12 = Util.getRandom($$10, $$5);
            DyeColor $$13 = Util.getRandom($$10, $$5);
            $$14 = new Variant($$11, $$12, $$13);
        }
        this.setPackedVariant($$14.getPackedId());
        return $$3;
    }

    public static boolean checkTropicalFishSpawnRules(EntityType<TropicalFish> $$0, LevelAccessor $$1, MobSpawnType $$2, BlockPos $$3, RandomSource $$4) {
        return $$1.getFluidState((BlockPos)$$3.below()).is(FluidTags.WATER) && $$1.getBlockState((BlockPos)$$3.above()).is(Blocks.WATER) && ($$1.getBiome($$3).is(BiomeTags.ALLOWS_TROPICAL_FISH_SPAWNS_AT_ANY_HEIGHT) || WaterAnimal.checkSurfaceWaterAnimalSpawnRules($$0, $$1, $$2, $$3, $$4));
    }

    public static enum Pattern implements StringRepresentable
    {
        KOB("kob", Base.SMALL, 0),
        SUNSTREAK("sunstreak", Base.SMALL, 1),
        SNOOPER("snooper", Base.SMALL, 2),
        DASHER("dasher", Base.SMALL, 3),
        BRINELY("brinely", Base.SMALL, 4),
        SPOTTY("spotty", Base.SMALL, 5),
        FLOPPER("flopper", Base.LARGE, 0),
        STRIPEY("stripey", Base.LARGE, 1),
        GLITTER("glitter", Base.LARGE, 2),
        BLOCKFISH("blockfish", Base.LARGE, 3),
        BETTY("betty", Base.LARGE, 4),
        CLAYFISH("clayfish", Base.LARGE, 5);

        public static final Codec<Pattern> CODEC;
        private static final IntFunction<Pattern> BY_ID;
        private final String name;
        private final Component displayName;
        private final Base base;
        private final int packedId;

        private Pattern(String $$0, Base $$1, int $$2) {
            this.name = $$0;
            this.base = $$1;
            this.packedId = $$1.id | $$2 << 8;
            this.displayName = Component.translatable("entity.minecraft.tropical_fish.type." + this.name);
        }

        public static Pattern byId(int $$0) {
            return (Pattern)BY_ID.apply($$0);
        }

        public Base base() {
            return this.base;
        }

        public int getPackedId() {
            return this.packedId;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public Component displayName() {
            return this.displayName;
        }

        static {
            CODEC = StringRepresentable.fromEnum((Supplier<E[]>)((Supplier)Pattern::values));
            BY_ID = ByIdMap.sparse(Pattern::getPackedId, Pattern.values(), KOB);
        }
    }

    static class TropicalFishGroupData
    extends AbstractSchoolingFish.SchoolSpawnGroupData {
        final Variant variant;

        TropicalFishGroupData(TropicalFish $$0, Variant $$1) {
            super($$0);
            this.variant = $$1;
        }
    }

    public record Variant(Pattern pattern, DyeColor baseColor, DyeColor patternColor) {
        public int getPackedId() {
            return TropicalFish.packVariant(this.pattern, this.baseColor, this.patternColor);
        }
    }

    public static enum Base {
        SMALL(0),
        LARGE(1);

        final int id;

        private Base(int $$0) {
            this.id = $$0;
        }
    }
}