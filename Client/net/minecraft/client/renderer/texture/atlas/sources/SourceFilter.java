/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.texture.atlas.sources;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ResourceLocationPattern;

public class SourceFilter
implements SpriteSource {
    public static final Codec<SourceFilter> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)ResourceLocationPattern.CODEC.fieldOf("pattern").forGetter($$0 -> $$0.filter)).apply((Applicative)$$02, SourceFilter::new));
    private final ResourceLocationPattern filter;

    public SourceFilter(ResourceLocationPattern $$0) {
        this.filter = $$0;
    }

    @Override
    public void run(ResourceManager $$0, SpriteSource.Output $$1) {
        $$1.removeAll(this.filter.locationPredicate());
    }

    @Override
    public SpriteSourceType type() {
        return SpriteSources.FILTER;
    }
}