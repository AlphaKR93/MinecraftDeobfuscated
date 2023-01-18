/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Math
 *  java.lang.Object
 */
package net.minecraft.world.entity.npc;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;

public class VillagerData {
    public static final int MIN_VILLAGER_LEVEL = 1;
    public static final int MAX_VILLAGER_LEVEL = 5;
    private static final int[] NEXT_LEVEL_XP_THRESHOLDS = new int[]{0, 10, 70, 150, 250};
    public static final Codec<VillagerData> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)BuiltInRegistries.VILLAGER_TYPE.byNameCodec().fieldOf("type").orElseGet(() -> VillagerType.PLAINS).forGetter($$0 -> $$0.type), (App)BuiltInRegistries.VILLAGER_PROFESSION.byNameCodec().fieldOf("profession").orElseGet(() -> VillagerProfession.NONE).forGetter($$0 -> $$0.profession), (App)Codec.INT.fieldOf("level").orElse((Object)1).forGetter($$0 -> $$0.level)).apply((Applicative)$$02, VillagerData::new));
    private final VillagerType type;
    private final VillagerProfession profession;
    private final int level;

    public VillagerData(VillagerType $$0, VillagerProfession $$1, int $$2) {
        this.type = $$0;
        this.profession = $$1;
        this.level = Math.max((int)1, (int)$$2);
    }

    public VillagerType getType() {
        return this.type;
    }

    public VillagerProfession getProfession() {
        return this.profession;
    }

    public int getLevel() {
        return this.level;
    }

    public VillagerData setType(VillagerType $$0) {
        return new VillagerData($$0, this.profession, this.level);
    }

    public VillagerData setProfession(VillagerProfession $$0) {
        return new VillagerData(this.type, $$0, this.level);
    }

    public VillagerData setLevel(int $$0) {
        return new VillagerData(this.type, this.profession, $$0);
    }

    public static int getMinXpPerLevel(int $$0) {
        return VillagerData.canLevelUp($$0) ? NEXT_LEVEL_XP_THRESHOLDS[$$0 - 1] : 0;
    }

    public static int getMaxXpPerLevel(int $$0) {
        return VillagerData.canLevelUp($$0) ? NEXT_LEVEL_XP_THRESHOLDS[$$0] : 0;
    }

    public static boolean canLevelUp(int $$0) {
        return $$0 >= 1 && $$0 < 5;
    }
}