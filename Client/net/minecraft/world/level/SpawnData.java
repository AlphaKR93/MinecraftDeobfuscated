/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Optional
 */
package net.minecraft.world.level;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.random.SimpleWeightedRandomList;

public record SpawnData(CompoundTag entityToSpawn, Optional<CustomSpawnRules> customSpawnRules) {
    public static final String ENTITY_TAG = "entity";
    public static final Codec<SpawnData> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)CompoundTag.CODEC.fieldOf(ENTITY_TAG).forGetter($$0 -> $$0.entityToSpawn), (App)CustomSpawnRules.CODEC.optionalFieldOf("custom_spawn_rules").forGetter($$0 -> $$0.customSpawnRules)).apply((Applicative)$$02, SpawnData::new));
    public static final Codec<SimpleWeightedRandomList<SpawnData>> LIST_CODEC = SimpleWeightedRandomList.wrappedCodecAllowingEmpty(CODEC);

    public SpawnData() {
        this(new CompoundTag(), (Optional<CustomSpawnRules>)Optional.empty());
    }

    public SpawnData {
        if ($$0.contains("id")) {
            ResourceLocation $$2 = ResourceLocation.tryParse($$0.getString("id"));
            if ($$2 != null) {
                $$0.putString("id", $$2.toString());
            } else {
                $$0.remove("id");
            }
        }
    }

    public CompoundTag getEntityToSpawn() {
        return this.entityToSpawn;
    }

    public Optional<CustomSpawnRules> getCustomSpawnRules() {
        return this.customSpawnRules;
    }

    public record CustomSpawnRules(InclusiveRange<Integer> blockLightLimit, InclusiveRange<Integer> skyLightLimit) {
        private static final InclusiveRange<Integer> LIGHT_RANGE = new InclusiveRange<Integer>(0, 15);
        public static final Codec<CustomSpawnRules> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)InclusiveRange.INT.optionalFieldOf("block_light_limit", LIGHT_RANGE).flatXmap(CustomSpawnRules::checkLightBoundaries, CustomSpawnRules::checkLightBoundaries).forGetter($$0 -> $$0.blockLightLimit), (App)InclusiveRange.INT.optionalFieldOf("sky_light_limit", LIGHT_RANGE).flatXmap(CustomSpawnRules::checkLightBoundaries, CustomSpawnRules::checkLightBoundaries).forGetter($$0 -> $$0.skyLightLimit)).apply((Applicative)$$02, CustomSpawnRules::new));

        private static DataResult<InclusiveRange<Integer>> checkLightBoundaries(InclusiveRange<Integer> $$0) {
            if (!LIGHT_RANGE.contains($$0)) {
                return DataResult.error((String)("Light values must be withing range " + LIGHT_RANGE));
            }
            return DataResult.success($$0);
        }
    }
}