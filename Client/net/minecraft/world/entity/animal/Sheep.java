/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  java.lang.Byte
 *  java.lang.Enum
 *  java.lang.IncompatibleClassChangeError
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Arrays
 *  java.util.Map
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.animal;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.EatBlockGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class Sheep
extends Animal
implements Shearable {
    private static final int EAT_ANIMATION_TICKS = 40;
    private static final EntityDataAccessor<Byte> DATA_WOOL_ID = SynchedEntityData.defineId(Sheep.class, EntityDataSerializers.BYTE);
    private static final Map<DyeColor, ItemLike> ITEM_BY_DYE = (Map)Util.make(Maps.newEnumMap(DyeColor.class), $$0 -> {
        $$0.put((Enum)DyeColor.WHITE, (Object)Blocks.WHITE_WOOL);
        $$0.put((Enum)DyeColor.ORANGE, (Object)Blocks.ORANGE_WOOL);
        $$0.put((Enum)DyeColor.MAGENTA, (Object)Blocks.MAGENTA_WOOL);
        $$0.put((Enum)DyeColor.LIGHT_BLUE, (Object)Blocks.LIGHT_BLUE_WOOL);
        $$0.put((Enum)DyeColor.YELLOW, (Object)Blocks.YELLOW_WOOL);
        $$0.put((Enum)DyeColor.LIME, (Object)Blocks.LIME_WOOL);
        $$0.put((Enum)DyeColor.PINK, (Object)Blocks.PINK_WOOL);
        $$0.put((Enum)DyeColor.GRAY, (Object)Blocks.GRAY_WOOL);
        $$0.put((Enum)DyeColor.LIGHT_GRAY, (Object)Blocks.LIGHT_GRAY_WOOL);
        $$0.put((Enum)DyeColor.CYAN, (Object)Blocks.CYAN_WOOL);
        $$0.put((Enum)DyeColor.PURPLE, (Object)Blocks.PURPLE_WOOL);
        $$0.put((Enum)DyeColor.BLUE, (Object)Blocks.BLUE_WOOL);
        $$0.put((Enum)DyeColor.BROWN, (Object)Blocks.BROWN_WOOL);
        $$0.put((Enum)DyeColor.GREEN, (Object)Blocks.GREEN_WOOL);
        $$0.put((Enum)DyeColor.RED, (Object)Blocks.RED_WOOL);
        $$0.put((Enum)DyeColor.BLACK, (Object)Blocks.BLACK_WOOL);
    });
    private static final Map<DyeColor, float[]> COLORARRAY_BY_COLOR = Maps.newEnumMap((Map)((Map)Arrays.stream((Object[])DyeColor.values()).collect(Collectors.toMap($$0 -> $$0, Sheep::createSheepColor))));
    private int eatAnimationTick;
    private EatBlockGoal eatBlockGoal;

    private static float[] createSheepColor(DyeColor $$0) {
        if ($$0 == DyeColor.WHITE) {
            return new float[]{0.9019608f, 0.9019608f, 0.9019608f};
        }
        float[] $$1 = $$0.getTextureDiffuseColors();
        float $$2 = 0.75f;
        return new float[]{$$1[0] * 0.75f, $$1[1] * 0.75f, $$1[2] * 0.75f};
    }

    public static float[] getColorArray(DyeColor $$0) {
        return (float[])COLORARRAY_BY_COLOR.get((Object)$$0);
    }

    public Sheep(EntityType<? extends Sheep> $$0, Level $$1) {
        super((EntityType<? extends Animal>)$$0, $$1);
    }

    @Override
    protected void registerGoals() {
        this.eatBlockGoal = new EatBlockGoal(this);
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1, Ingredient.of(Items.WHEAT), false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(5, this.eatBlockGoal);
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    @Override
    protected void customServerAiStep() {
        this.eatAnimationTick = this.eatBlockGoal.getEatAnimationTick();
        super.customServerAiStep();
    }

    @Override
    public void aiStep() {
        if (this.level.isClientSide) {
            this.eatAnimationTick = Math.max((int)0, (int)(this.eatAnimationTick - 1));
        }
        super.aiStep();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 8.0).add(Attributes.MOVEMENT_SPEED, 0.23f);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_WOOL_ID, (byte)0);
    }

    @Override
    public ResourceLocation getDefaultLootTable() {
        if (this.isSheared()) {
            return this.getType().getDefaultLootTable();
        }
        return switch (this.getColor()) {
            default -> throw new IncompatibleClassChangeError();
            case DyeColor.WHITE -> BuiltInLootTables.SHEEP_WHITE;
            case DyeColor.ORANGE -> BuiltInLootTables.SHEEP_ORANGE;
            case DyeColor.MAGENTA -> BuiltInLootTables.SHEEP_MAGENTA;
            case DyeColor.LIGHT_BLUE -> BuiltInLootTables.SHEEP_LIGHT_BLUE;
            case DyeColor.YELLOW -> BuiltInLootTables.SHEEP_YELLOW;
            case DyeColor.LIME -> BuiltInLootTables.SHEEP_LIME;
            case DyeColor.PINK -> BuiltInLootTables.SHEEP_PINK;
            case DyeColor.GRAY -> BuiltInLootTables.SHEEP_GRAY;
            case DyeColor.LIGHT_GRAY -> BuiltInLootTables.SHEEP_LIGHT_GRAY;
            case DyeColor.CYAN -> BuiltInLootTables.SHEEP_CYAN;
            case DyeColor.PURPLE -> BuiltInLootTables.SHEEP_PURPLE;
            case DyeColor.BLUE -> BuiltInLootTables.SHEEP_BLUE;
            case DyeColor.BROWN -> BuiltInLootTables.SHEEP_BROWN;
            case DyeColor.GREEN -> BuiltInLootTables.SHEEP_GREEN;
            case DyeColor.RED -> BuiltInLootTables.SHEEP_RED;
            case DyeColor.BLACK -> BuiltInLootTables.SHEEP_BLACK;
        };
    }

    @Override
    public void handleEntityEvent(byte $$0) {
        if ($$0 == 10) {
            this.eatAnimationTick = 40;
        } else {
            super.handleEntityEvent($$0);
        }
    }

    public float getHeadEatPositionScale(float $$0) {
        if (this.eatAnimationTick <= 0) {
            return 0.0f;
        }
        if (this.eatAnimationTick >= 4 && this.eatAnimationTick <= 36) {
            return 1.0f;
        }
        if (this.eatAnimationTick < 4) {
            return ((float)this.eatAnimationTick - $$0) / 4.0f;
        }
        return -((float)(this.eatAnimationTick - 40) - $$0) / 4.0f;
    }

    public float getHeadEatAngleScale(float $$0) {
        if (this.eatAnimationTick > 4 && this.eatAnimationTick <= 36) {
            float $$1 = ((float)(this.eatAnimationTick - 4) - $$0) / 32.0f;
            return 0.62831855f + 0.21991149f * Mth.sin($$1 * 28.7f);
        }
        if (this.eatAnimationTick > 0) {
            return 0.62831855f;
        }
        return this.getXRot() * ((float)Math.PI / 180);
    }

    @Override
    public InteractionResult mobInteract(Player $$0, InteractionHand $$12) {
        ItemStack $$2 = $$0.getItemInHand($$12);
        if ($$2.is(Items.SHEARS)) {
            if (!this.level.isClientSide && this.readyForShearing()) {
                this.shear(SoundSource.PLAYERS);
                this.gameEvent(GameEvent.SHEAR, $$0);
                $$2.hurtAndBreak(1, $$0, $$1 -> $$1.broadcastBreakEvent($$12));
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }
        return super.mobInteract($$0, $$12);
    }

    @Override
    public void shear(SoundSource $$0) {
        this.level.playSound(null, this, SoundEvents.SHEEP_SHEAR, $$0, 1.0f, 1.0f);
        this.setSheared(true);
        int $$1 = 1 + this.random.nextInt(3);
        for (int $$2 = 0; $$2 < $$1; ++$$2) {
            ItemEntity $$3 = this.spawnAtLocation((ItemLike)ITEM_BY_DYE.get((Object)this.getColor()), 1);
            if ($$3 == null) continue;
            $$3.setDeltaMovement($$3.getDeltaMovement().add((this.random.nextFloat() - this.random.nextFloat()) * 0.1f, this.random.nextFloat() * 0.05f, (this.random.nextFloat() - this.random.nextFloat()) * 0.1f));
        }
    }

    @Override
    public boolean readyForShearing() {
        return this.isAlive() && !this.isSheared() && !this.isBaby();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putBoolean("Sheared", this.isSheared());
        $$0.putByte("Color", (byte)this.getColor().getId());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        this.setSheared($$0.getBoolean("Sheared"));
        this.setColor(DyeColor.byId($$0.getByte("Color")));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SHEEP_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.SHEEP_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SHEEP_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos $$0, BlockState $$1) {
        this.playSound(SoundEvents.SHEEP_STEP, 0.15f, 1.0f);
    }

    public DyeColor getColor() {
        return DyeColor.byId(this.entityData.get(DATA_WOOL_ID) & 0xF);
    }

    public void setColor(DyeColor $$0) {
        byte $$1 = this.entityData.get(DATA_WOOL_ID);
        this.entityData.set(DATA_WOOL_ID, (byte)($$1 & 0xF0 | $$0.getId() & 0xF));
    }

    public boolean isSheared() {
        return (this.entityData.get(DATA_WOOL_ID) & 0x10) != 0;
    }

    public void setSheared(boolean $$0) {
        byte $$1 = this.entityData.get(DATA_WOOL_ID);
        if ($$0) {
            this.entityData.set(DATA_WOOL_ID, (byte)($$1 | 0x10));
        } else {
            this.entityData.set(DATA_WOOL_ID, (byte)($$1 & 0xFFFFFFEF));
        }
    }

    public static DyeColor getRandomSheepColor(RandomSource $$0) {
        int $$1 = $$0.nextInt(100);
        if ($$1 < 5) {
            return DyeColor.BLACK;
        }
        if ($$1 < 10) {
            return DyeColor.GRAY;
        }
        if ($$1 < 15) {
            return DyeColor.LIGHT_GRAY;
        }
        if ($$1 < 18) {
            return DyeColor.BROWN;
        }
        if ($$0.nextInt(500) == 0) {
            return DyeColor.PINK;
        }
        return DyeColor.WHITE;
    }

    @Override
    @Nullable
    public Sheep getBreedOffspring(ServerLevel $$0, AgeableMob $$1) {
        Sheep $$2 = EntityType.SHEEP.create($$0);
        if ($$2 != null) {
            $$2.setColor(this.getOffspringColor(this, (Sheep)$$1));
        }
        return $$2;
    }

    @Override
    public void ate() {
        super.ate();
        this.setSheared(false);
        if (this.isBaby()) {
            this.ageUp(60);
        }
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor $$0, DifficultyInstance $$1, MobSpawnType $$2, @Nullable SpawnGroupData $$3, @Nullable CompoundTag $$4) {
        this.setColor(Sheep.getRandomSheepColor($$0.getRandom()));
        return super.finalizeSpawn($$0, $$1, $$2, $$3, $$4);
    }

    private DyeColor getOffspringColor(Animal $$0, Animal $$12) {
        DyeColor $$2 = ((Sheep)$$0).getColor();
        DyeColor $$3 = ((Sheep)$$12).getColor();
        CraftingContainer $$4 = Sheep.makeContainer($$2, $$3);
        return (DyeColor)this.level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, $$4, this.level).map($$1 -> $$1.assemble($$4)).map(ItemStack::getItem).filter(arg_0 -> DyeItem.class.isInstance(arg_0)).map(arg_0 -> DyeItem.class.cast(arg_0)).map(DyeItem::getDyeColor).orElseGet(() -> this.level.random.nextBoolean() ? $$2 : $$3);
    }

    private static CraftingContainer makeContainer(DyeColor $$0, DyeColor $$1) {
        CraftingContainer $$2 = new CraftingContainer(new AbstractContainerMenu(null, -1){

            @Override
            public ItemStack quickMoveStack(Player $$0, int $$1) {
                return ItemStack.EMPTY;
            }

            @Override
            public boolean stillValid(Player $$0) {
                return false;
            }
        }, 2, 1);
        $$2.setItem(0, new ItemStack(DyeItem.byColor($$0)));
        $$2.setItem(1, new ItemStack(DyeItem.byColor($$1)));
        return $$2;
    }

    @Override
    protected float getStandingEyeHeight(Pose $$0, EntityDimensions $$1) {
        return 0.95f * $$1.height;
    }
}