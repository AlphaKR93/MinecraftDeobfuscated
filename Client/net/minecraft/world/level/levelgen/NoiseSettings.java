/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.IllegalStateException
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.util.function.Function
 */
package net.minecraft.world.level.levelgen;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.dimension.DimensionType;

public record NoiseSettings(int minY, int height, int noiseSizeHorizontal, int noiseSizeVertical) {
    public static final Codec<NoiseSettings> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)Codec.intRange((int)DimensionType.MIN_Y, (int)DimensionType.MAX_Y).fieldOf("min_y").forGetter(NoiseSettings::minY), (App)Codec.intRange((int)0, (int)DimensionType.Y_SIZE).fieldOf("height").forGetter(NoiseSettings::height), (App)Codec.intRange((int)1, (int)4).fieldOf("size_horizontal").forGetter(NoiseSettings::noiseSizeHorizontal), (App)Codec.intRange((int)1, (int)4).fieldOf("size_vertical").forGetter(NoiseSettings::noiseSizeVertical)).apply((Applicative)$$0, NoiseSettings::new)).comapFlatMap(NoiseSettings::guardY, Function.identity());
    protected static final NoiseSettings OVERWORLD_NOISE_SETTINGS = NoiseSettings.create(-64, 384, 1, 2);
    protected static final NoiseSettings NETHER_NOISE_SETTINGS = NoiseSettings.create(0, 128, 1, 2);
    protected static final NoiseSettings END_NOISE_SETTINGS = NoiseSettings.create(0, 128, 2, 1);
    protected static final NoiseSettings CAVES_NOISE_SETTINGS = NoiseSettings.create(-64, 192, 1, 2);
    protected static final NoiseSettings FLOATING_ISLANDS_NOISE_SETTINGS = NoiseSettings.create(0, 256, 2, 1);

    private static DataResult<NoiseSettings> guardY(NoiseSettings $$0) {
        if ($$0.minY() + $$0.height() > DimensionType.MAX_Y + 1) {
            return DataResult.error((String)("min_y + height cannot be higher than: " + (DimensionType.MAX_Y + 1)));
        }
        if ($$0.height() % 16 != 0) {
            return DataResult.error((String)"height has to be a multiple of 16");
        }
        if ($$0.minY() % 16 != 0) {
            return DataResult.error((String)"min_y has to be a multiple of 16");
        }
        return DataResult.success((Object)((Object)$$0));
    }

    public static NoiseSettings create(int $$02, int $$1, int $$2, int $$3) {
        NoiseSettings $$4 = new NoiseSettings($$02, $$1, $$2, $$3);
        NoiseSettings.guardY($$4).error().ifPresent($$0 -> {
            throw new IllegalStateException($$0.message());
        });
        return $$4;
    }

    public int getCellHeight() {
        return QuartPos.toBlock(this.noiseSizeVertical());
    }

    public int getCellWidth() {
        return QuartPos.toBlock(this.noiseSizeHorizontal());
    }

    public NoiseSettings clampToHeightAccessor(LevelHeightAccessor $$0) {
        int $$1 = Math.max((int)this.minY, (int)$$0.getMinBuildHeight());
        int $$2 = Math.min((int)(this.minY + this.height), (int)$$0.getMaxBuildHeight()) - $$1;
        return new NoiseSettings($$1, $$2, this.noiseSizeHorizontal, this.noiseSizeVertical);
    }
}