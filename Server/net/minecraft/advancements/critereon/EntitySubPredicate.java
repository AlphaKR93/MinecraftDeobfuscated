/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.ImmutableBiMap
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Optional
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements.critereon;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.EntityVariantPredicate;
import net.minecraft.advancements.critereon.FishingHookPredicate;
import net.minecraft.advancements.critereon.LighthingBoltPredicate;
import net.minecraft.advancements.critereon.PlayerPredicate;
import net.minecraft.advancements.critereon.SlimePredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.animal.horse.Variant;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;

public interface EntitySubPredicate {
    public static final EntitySubPredicate ANY = new EntitySubPredicate(){

        @Override
        public boolean matches(Entity $$0, ServerLevel $$1, @Nullable Vec3 $$2) {
            return true;
        }

        @Override
        public JsonObject serializeCustomData() {
            return new JsonObject();
        }

        @Override
        public Type type() {
            return Types.ANY;
        }
    };

    public static EntitySubPredicate fromJson(@Nullable JsonElement $$0) {
        if ($$0 == null || $$0.isJsonNull()) {
            return ANY;
        }
        JsonObject $$1 = GsonHelper.convertToJsonObject($$0, "type_specific");
        String $$2 = GsonHelper.getAsString($$1, "type", null);
        if ($$2 == null) {
            return ANY;
        }
        Type $$3 = (Type)Types.TYPES.get((Object)$$2);
        if ($$3 == null) {
            throw new JsonSyntaxException("Unknown sub-predicate type: " + $$2);
        }
        return $$3.deserialize($$1);
    }

    public boolean matches(Entity var1, ServerLevel var2, @Nullable Vec3 var3);

    public JsonObject serializeCustomData();

    default public JsonElement serialize() {
        if (this.type() == Types.ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject $$0 = this.serializeCustomData();
        String $$1 = (String)Types.TYPES.inverse().get((Object)this.type());
        $$0.addProperty("type", $$1);
        return $$0;
    }

    public Type type();

    public static EntitySubPredicate variant(CatVariant $$0) {
        return Types.CAT.createPredicate($$0);
    }

    public static EntitySubPredicate variant(FrogVariant $$0) {
        return Types.FROG.createPredicate($$0);
    }

    public static final class Types {
        public static final Type ANY = $$0 -> ANY;
        public static final Type LIGHTNING = LighthingBoltPredicate::fromJson;
        public static final Type FISHING_HOOK = FishingHookPredicate::fromJson;
        public static final Type PLAYER = PlayerPredicate::fromJson;
        public static final Type SLIME = SlimePredicate::fromJson;
        public static final EntityVariantPredicate<CatVariant> CAT = EntityVariantPredicate.create(BuiltInRegistries.CAT_VARIANT, $$0 -> {
            Optional optional;
            if ($$0 instanceof Cat) {
                Cat $$1 = (Cat)$$0;
                optional = Optional.of((Object)((Object)$$1.getVariant()));
            } else {
                optional = Optional.empty();
            }
            return optional;
        });
        public static final EntityVariantPredicate<FrogVariant> FROG = EntityVariantPredicate.create(BuiltInRegistries.FROG_VARIANT, $$0 -> {
            Optional optional;
            if ($$0 instanceof Frog) {
                Frog $$1 = (Frog)$$0;
                optional = Optional.of((Object)((Object)$$1.getVariant()));
            } else {
                optional = Optional.empty();
            }
            return optional;
        });
        public static final EntityVariantPredicate<Axolotl.Variant> AXOLOTL = EntityVariantPredicate.create(Axolotl.Variant.CODEC, $$0 -> {
            Optional optional;
            if ($$0 instanceof Axolotl) {
                Axolotl $$1 = (Axolotl)$$0;
                optional = Optional.of((Object)$$1.getVariant());
            } else {
                optional = Optional.empty();
            }
            return optional;
        });
        public static final EntityVariantPredicate<Boat.Type> BOAT = EntityVariantPredicate.create(Boat.Type.CODEC, $$0 -> {
            Optional optional;
            if ($$0 instanceof Boat) {
                Boat $$1 = (Boat)$$0;
                optional = Optional.of((Object)$$1.getVariant());
            } else {
                optional = Optional.empty();
            }
            return optional;
        });
        public static final EntityVariantPredicate<Fox.Type> FOX = EntityVariantPredicate.create(Fox.Type.CODEC, $$0 -> {
            Optional optional;
            if ($$0 instanceof Fox) {
                Fox $$1 = (Fox)$$0;
                optional = Optional.of((Object)$$1.getVariant());
            } else {
                optional = Optional.empty();
            }
            return optional;
        });
        public static final EntityVariantPredicate<MushroomCow.MushroomType> MOOSHROOM = EntityVariantPredicate.create(MushroomCow.MushroomType.CODEC, $$0 -> {
            Optional optional;
            if ($$0 instanceof MushroomCow) {
                MushroomCow $$1 = (MushroomCow)$$0;
                optional = Optional.of((Object)$$1.getVariant());
            } else {
                optional = Optional.empty();
            }
            return optional;
        });
        public static final EntityVariantPredicate<Holder<PaintingVariant>> PAINTING = EntityVariantPredicate.create(BuiltInRegistries.PAINTING_VARIANT.holderByNameCodec(), $$0 -> {
            Optional optional;
            if ($$0 instanceof Painting) {
                Painting $$1 = (Painting)$$0;
                optional = Optional.of((Object)$$1.getVariant());
            } else {
                optional = Optional.empty();
            }
            return optional;
        });
        public static final EntityVariantPredicate<Rabbit.Variant> RABBIT = EntityVariantPredicate.create(Rabbit.Variant.CODEC, $$0 -> {
            Optional optional;
            if ($$0 instanceof Rabbit) {
                Rabbit $$1 = (Rabbit)$$0;
                optional = Optional.of((Object)$$1.getVariant());
            } else {
                optional = Optional.empty();
            }
            return optional;
        });
        public static final EntityVariantPredicate<Variant> HORSE = EntityVariantPredicate.create(Variant.CODEC, $$0 -> {
            Optional optional;
            if ($$0 instanceof Horse) {
                Horse $$1 = (Horse)$$0;
                optional = Optional.of((Object)$$1.getVariant());
            } else {
                optional = Optional.empty();
            }
            return optional;
        });
        public static final EntityVariantPredicate<Llama.Variant> LLAMA = EntityVariantPredicate.create(Llama.Variant.CODEC, $$0 -> {
            Optional optional;
            if ($$0 instanceof Llama) {
                Llama $$1 = (Llama)$$0;
                optional = Optional.of((Object)$$1.getVariant());
            } else {
                optional = Optional.empty();
            }
            return optional;
        });
        public static final EntityVariantPredicate<VillagerType> VILLAGER = EntityVariantPredicate.create(BuiltInRegistries.VILLAGER_TYPE.byNameCodec(), $$0 -> {
            Optional optional;
            if ($$0 instanceof VillagerDataHolder) {
                VillagerDataHolder $$1 = (VillagerDataHolder)((Object)$$0);
                optional = Optional.of((Object)$$1.getVariant());
            } else {
                optional = Optional.empty();
            }
            return optional;
        });
        public static final EntityVariantPredicate<Parrot.Variant> PARROT = EntityVariantPredicate.create(Parrot.Variant.CODEC, $$0 -> {
            Optional optional;
            if ($$0 instanceof Parrot) {
                Parrot $$1 = (Parrot)$$0;
                optional = Optional.of((Object)$$1.getVariant());
            } else {
                optional = Optional.empty();
            }
            return optional;
        });
        public static final EntityVariantPredicate<TropicalFish.Pattern> TROPICAL_FISH = EntityVariantPredicate.create(TropicalFish.Pattern.CODEC, $$0 -> {
            Optional optional;
            if ($$0 instanceof TropicalFish) {
                TropicalFish $$1 = (TropicalFish)$$0;
                optional = Optional.of((Object)$$1.getVariant());
            } else {
                optional = Optional.empty();
            }
            return optional;
        });
        public static final BiMap<String, Type> TYPES = ImmutableBiMap.builder().put((Object)"any", (Object)ANY).put((Object)"lightning", (Object)LIGHTNING).put((Object)"fishing_hook", (Object)FISHING_HOOK).put((Object)"player", (Object)PLAYER).put((Object)"slime", (Object)SLIME).put((Object)"cat", (Object)CAT.type()).put((Object)"frog", (Object)FROG.type()).put((Object)"axolotl", (Object)AXOLOTL.type()).put((Object)"boat", (Object)BOAT.type()).put((Object)"fox", (Object)FOX.type()).put((Object)"mooshroom", (Object)MOOSHROOM.type()).put((Object)"painting", (Object)PAINTING.type()).put((Object)"rabbit", (Object)RABBIT.type()).put((Object)"horse", (Object)HORSE.type()).put((Object)"llama", (Object)LLAMA.type()).put((Object)"villager", (Object)VILLAGER.type()).put((Object)"parrot", (Object)PARROT.type()).put((Object)"tropical_fish", (Object)TROPICAL_FISH.type()).buildOrThrow();
    }

    public static interface Type {
        public EntitySubPredicate deserialize(JsonObject var1);
    }
}