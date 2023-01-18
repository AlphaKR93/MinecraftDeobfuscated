/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Optional
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.decoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.decoration.PaintingVariants;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class Painting
extends HangingEntity
implements VariantHolder<Holder<PaintingVariant>> {
    private static final EntityDataAccessor<Holder<PaintingVariant>> DATA_PAINTING_VARIANT_ID = SynchedEntityData.defineId(Painting.class, EntityDataSerializers.PAINTING_VARIANT);
    private static final ResourceKey<PaintingVariant> DEFAULT_VARIANT = PaintingVariants.KEBAB;

    private static Holder<PaintingVariant> getDefaultVariant() {
        return BuiltInRegistries.PAINTING_VARIANT.getHolderOrThrow(DEFAULT_VARIANT);
    }

    public Painting(EntityType<? extends Painting> $$0, Level $$1) {
        super((EntityType<? extends HangingEntity>)$$0, $$1);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_PAINTING_VARIANT_ID, Painting.getDefaultVariant());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> $$0) {
        if (DATA_PAINTING_VARIANT_ID.equals($$0)) {
            this.recalculateBoundingBox();
        }
    }

    @Override
    public void setVariant(Holder<PaintingVariant> $$0) {
        this.entityData.set(DATA_PAINTING_VARIANT_ID, $$0);
    }

    @Override
    public Holder<PaintingVariant> getVariant() {
        return this.entityData.get(DATA_PAINTING_VARIANT_ID);
    }

    public static Optional<Painting> create(Level $$0, BlockPos $$12, Direction $$2) {
        Painting $$3 = new Painting($$0, $$12);
        ArrayList $$4 = new ArrayList();
        BuiltInRegistries.PAINTING_VARIANT.getTagOrEmpty(PaintingVariantTags.PLACEABLE).forEach(arg_0 -> ((List)$$4).add(arg_0));
        if ($$4.isEmpty()) {
            return Optional.empty();
        }
        $$3.setDirection($$2);
        $$4.removeIf($$1 -> {
            $$3.setVariant((Holder<PaintingVariant>)$$1);
            return !$$3.survives();
        });
        if ($$4.isEmpty()) {
            return Optional.empty();
        }
        int $$5 = $$4.stream().mapToInt(Painting::variantArea).max().orElse(0);
        $$4.removeIf($$1 -> Painting.variantArea($$1) < $$5);
        Optional $$6 = Util.getRandomSafe($$4, $$3.random);
        if ($$6.isEmpty()) {
            return Optional.empty();
        }
        $$3.setVariant((Holder)$$6.get());
        $$3.setDirection($$2);
        return Optional.of((Object)$$3);
    }

    private static int variantArea(Holder<PaintingVariant> $$0) {
        return $$0.value().getWidth() * $$0.value().getHeight();
    }

    private Painting(Level $$0, BlockPos $$1) {
        super(EntityType.PAINTING, $$0, $$1);
    }

    public Painting(Level $$0, BlockPos $$1, Direction $$2, Holder<PaintingVariant> $$3) {
        this($$0, $$1);
        this.setVariant($$3);
        this.setDirection($$2);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        $$0.putString("variant", ((ResourceKey)this.getVariant().unwrapKey().orElse(DEFAULT_VARIANT)).location().toString());
        $$0.putByte("facing", (byte)this.direction.get2DDataValue());
        super.addAdditionalSaveData($$0);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$02) {
        Holder $$1 = (Holder)Optional.ofNullable((Object)ResourceLocation.tryParse($$02.getString("variant"))).map($$0 -> ResourceKey.create(Registries.PAINTING_VARIANT, $$0)).flatMap(BuiltInRegistries.PAINTING_VARIANT::getHolder).map($$0 -> $$0).orElseGet(Painting::getDefaultVariant);
        this.setVariant($$1);
        this.direction = Direction.from2DDataValue($$02.getByte("facing"));
        super.readAdditionalSaveData($$02);
        this.setDirection(this.direction);
    }

    @Override
    public int getWidth() {
        return ((PaintingVariant)this.getVariant().value()).getWidth();
    }

    @Override
    public int getHeight() {
        return ((PaintingVariant)this.getVariant().value()).getHeight();
    }

    @Override
    public void dropItem(@Nullable Entity $$0) {
        if (!this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            return;
        }
        this.playSound(SoundEvents.PAINTING_BREAK, 1.0f, 1.0f);
        if ($$0 instanceof Player) {
            Player $$1 = (Player)$$0;
            if ($$1.getAbilities().instabuild) {
                return;
            }
        }
        this.spawnAtLocation(Items.PAINTING);
    }

    @Override
    public void playPlacementSound() {
        this.playSound(SoundEvents.PAINTING_PLACE, 1.0f, 1.0f);
    }

    @Override
    public void moveTo(double $$0, double $$1, double $$2, float $$3, float $$4) {
        this.setPos($$0, $$1, $$2);
    }

    @Override
    public void lerpTo(double $$0, double $$1, double $$2, float $$3, float $$4, int $$5, boolean $$6) {
        this.setPos($$0, $$1, $$2);
    }

    @Override
    public Vec3 trackingPosition() {
        return Vec3.atLowerCornerOf(this.pos);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, this.direction.get3DDataValue(), this.getPos());
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket $$0) {
        super.recreateFromPacket($$0);
        this.setDirection(Direction.from3DDataValue($$0.getData()));
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(Items.PAINTING);
    }
}