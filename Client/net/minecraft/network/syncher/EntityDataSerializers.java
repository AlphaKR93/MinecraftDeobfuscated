/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Boolean
 *  java.lang.Byte
 *  java.lang.Float
 *  java.lang.Integer
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Optional
 *  java.util.OptionalInt
 *  java.util.UUID
 *  javax.annotation.Nullable
 */
package net.minecraft.network.syncher;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Rotations;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class EntityDataSerializers {
    private static final CrudeIncrementalIntIdentityHashBiMap<EntityDataSerializer<?>> SERIALIZERS = CrudeIncrementalIntIdentityHashBiMap.create(16);
    public static final EntityDataSerializer<Byte> BYTE = EntityDataSerializer.simple(($$0, $$1) -> $$0.writeByte($$1.byteValue()), FriendlyByteBuf::readByte);
    public static final EntityDataSerializer<Integer> INT = EntityDataSerializer.simple(FriendlyByteBuf::writeVarInt, FriendlyByteBuf::readVarInt);
    public static final EntityDataSerializer<Long> LONG = EntityDataSerializer.simple(FriendlyByteBuf::writeVarLong, FriendlyByteBuf::readVarLong);
    public static final EntityDataSerializer<Float> FLOAT = EntityDataSerializer.simple(FriendlyByteBuf::writeFloat, FriendlyByteBuf::readFloat);
    public static final EntityDataSerializer<String> STRING = EntityDataSerializer.simple(FriendlyByteBuf::writeUtf, FriendlyByteBuf::readUtf);
    public static final EntityDataSerializer<Component> COMPONENT = EntityDataSerializer.simple(FriendlyByteBuf::writeComponent, FriendlyByteBuf::readComponent);
    public static final EntityDataSerializer<Optional<Component>> OPTIONAL_COMPONENT = EntityDataSerializer.optional(FriendlyByteBuf::writeComponent, FriendlyByteBuf::readComponent);
    public static final EntityDataSerializer<ItemStack> ITEM_STACK = new EntityDataSerializer<ItemStack>(){

        @Override
        public void write(FriendlyByteBuf $$0, ItemStack $$1) {
            $$0.writeItem($$1);
        }

        @Override
        public ItemStack read(FriendlyByteBuf $$0) {
            return $$0.readItem();
        }

        @Override
        public ItemStack copy(ItemStack $$0) {
            return $$0.copy();
        }
    };
    public static final EntityDataSerializer<Optional<BlockState>> BLOCK_STATE = new EntityDataSerializer.ForValueType<Optional<BlockState>>(){

        @Override
        public void write(FriendlyByteBuf $$0, Optional<BlockState> $$1) {
            if ($$1.isPresent()) {
                $$0.writeVarInt(Block.getId((BlockState)$$1.get()));
            } else {
                $$0.writeVarInt(0);
            }
        }

        @Override
        public Optional<BlockState> read(FriendlyByteBuf $$0) {
            int $$1 = $$0.readVarInt();
            if ($$1 == 0) {
                return Optional.empty();
            }
            return Optional.of((Object)Block.stateById($$1));
        }
    };
    public static final EntityDataSerializer<Boolean> BOOLEAN = EntityDataSerializer.simple(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean);
    public static final EntityDataSerializer<ParticleOptions> PARTICLE = new EntityDataSerializer.ForValueType<ParticleOptions>(){

        @Override
        public void write(FriendlyByteBuf $$0, ParticleOptions $$1) {
            $$0.writeId(BuiltInRegistries.PARTICLE_TYPE, $$1.getType());
            $$1.writeToNetwork($$0);
        }

        @Override
        public ParticleOptions read(FriendlyByteBuf $$0) {
            return this.readParticle($$0, $$0.readById(BuiltInRegistries.PARTICLE_TYPE));
        }

        private <T extends ParticleOptions> T readParticle(FriendlyByteBuf $$0, ParticleType<T> $$1) {
            return $$1.getDeserializer().fromNetwork($$1, $$0);
        }
    };
    public static final EntityDataSerializer<Rotations> ROTATIONS = new EntityDataSerializer.ForValueType<Rotations>(){

        @Override
        public void write(FriendlyByteBuf $$0, Rotations $$1) {
            $$0.writeFloat($$1.getX());
            $$0.writeFloat($$1.getY());
            $$0.writeFloat($$1.getZ());
        }

        @Override
        public Rotations read(FriendlyByteBuf $$0) {
            return new Rotations($$0.readFloat(), $$0.readFloat(), $$0.readFloat());
        }
    };
    public static final EntityDataSerializer<BlockPos> BLOCK_POS = EntityDataSerializer.simple(FriendlyByteBuf::writeBlockPos, FriendlyByteBuf::readBlockPos);
    public static final EntityDataSerializer<Optional<BlockPos>> OPTIONAL_BLOCK_POS = EntityDataSerializer.optional(FriendlyByteBuf::writeBlockPos, FriendlyByteBuf::readBlockPos);
    public static final EntityDataSerializer<Direction> DIRECTION = EntityDataSerializer.simpleEnum(Direction.class);
    public static final EntityDataSerializer<Optional<UUID>> OPTIONAL_UUID = EntityDataSerializer.optional(FriendlyByteBuf::writeUUID, FriendlyByteBuf::readUUID);
    public static final EntityDataSerializer<Optional<GlobalPos>> OPTIONAL_GLOBAL_POS = EntityDataSerializer.optional(FriendlyByteBuf::writeGlobalPos, FriendlyByteBuf::readGlobalPos);
    public static final EntityDataSerializer<CompoundTag> COMPOUND_TAG = new EntityDataSerializer<CompoundTag>(){

        @Override
        public void write(FriendlyByteBuf $$0, CompoundTag $$1) {
            $$0.writeNbt($$1);
        }

        @Override
        public CompoundTag read(FriendlyByteBuf $$0) {
            return $$0.readNbt();
        }

        @Override
        public CompoundTag copy(CompoundTag $$0) {
            return $$0.copy();
        }
    };
    public static final EntityDataSerializer<VillagerData> VILLAGER_DATA = new EntityDataSerializer.ForValueType<VillagerData>(){

        @Override
        public void write(FriendlyByteBuf $$0, VillagerData $$1) {
            $$0.writeId(BuiltInRegistries.VILLAGER_TYPE, $$1.getType());
            $$0.writeId(BuiltInRegistries.VILLAGER_PROFESSION, $$1.getProfession());
            $$0.writeVarInt($$1.getLevel());
        }

        @Override
        public VillagerData read(FriendlyByteBuf $$0) {
            return new VillagerData($$0.readById(BuiltInRegistries.VILLAGER_TYPE), $$0.readById(BuiltInRegistries.VILLAGER_PROFESSION), $$0.readVarInt());
        }
    };
    public static final EntityDataSerializer<OptionalInt> OPTIONAL_UNSIGNED_INT = new EntityDataSerializer.ForValueType<OptionalInt>(){

        @Override
        public void write(FriendlyByteBuf $$0, OptionalInt $$1) {
            $$0.writeVarInt($$1.orElse(-1) + 1);
        }

        @Override
        public OptionalInt read(FriendlyByteBuf $$0) {
            int $$1 = $$0.readVarInt();
            return $$1 == 0 ? OptionalInt.empty() : OptionalInt.of((int)($$1 - 1));
        }
    };
    public static final EntityDataSerializer<Pose> POSE = EntityDataSerializer.simpleEnum(Pose.class);
    public static final EntityDataSerializer<CatVariant> CAT_VARIANT = EntityDataSerializer.simpleId(BuiltInRegistries.CAT_VARIANT);
    public static final EntityDataSerializer<FrogVariant> FROG_VARIANT = EntityDataSerializer.simpleId(BuiltInRegistries.FROG_VARIANT);
    public static final EntityDataSerializer<Holder<PaintingVariant>> PAINTING_VARIANT = EntityDataSerializer.simpleId(BuiltInRegistries.PAINTING_VARIANT.asHolderIdMap());

    public static void registerSerializer(EntityDataSerializer<?> $$0) {
        SERIALIZERS.add($$0);
    }

    @Nullable
    public static EntityDataSerializer<?> getSerializer(int $$0) {
        return SERIALIZERS.byId($$0);
    }

    public static int getSerializedId(EntityDataSerializer<?> $$0) {
        return SERIALIZERS.getId($$0);
    }

    private EntityDataSerializers() {
    }

    static {
        EntityDataSerializers.registerSerializer(BYTE);
        EntityDataSerializers.registerSerializer(INT);
        EntityDataSerializers.registerSerializer(LONG);
        EntityDataSerializers.registerSerializer(FLOAT);
        EntityDataSerializers.registerSerializer(STRING);
        EntityDataSerializers.registerSerializer(COMPONENT);
        EntityDataSerializers.registerSerializer(OPTIONAL_COMPONENT);
        EntityDataSerializers.registerSerializer(ITEM_STACK);
        EntityDataSerializers.registerSerializer(BOOLEAN);
        EntityDataSerializers.registerSerializer(ROTATIONS);
        EntityDataSerializers.registerSerializer(BLOCK_POS);
        EntityDataSerializers.registerSerializer(OPTIONAL_BLOCK_POS);
        EntityDataSerializers.registerSerializer(DIRECTION);
        EntityDataSerializers.registerSerializer(OPTIONAL_UUID);
        EntityDataSerializers.registerSerializer(BLOCK_STATE);
        EntityDataSerializers.registerSerializer(COMPOUND_TAG);
        EntityDataSerializers.registerSerializer(PARTICLE);
        EntityDataSerializers.registerSerializer(VILLAGER_DATA);
        EntityDataSerializers.registerSerializer(OPTIONAL_UNSIGNED_INT);
        EntityDataSerializers.registerSerializer(POSE);
        EntityDataSerializers.registerSerializer(CAT_VARIANT);
        EntityDataSerializers.registerSerializer(FROG_VARIANT);
        EntityDataSerializers.registerSerializer(OPTIONAL_GLOBAL_POS);
        EntityDataSerializers.registerSerializer(PAINTING_VARIANT);
    }
}