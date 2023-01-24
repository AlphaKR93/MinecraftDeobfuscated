/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Optional
 *  org.slf4j.Logger
 */
package net.minecraft.client.renderer.texture.atlas.sources;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;

public class SingleFile
implements SpriteSource {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Codec<SingleFile> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)ResourceLocation.CODEC.fieldOf("resource").forGetter($$0 -> $$0.resourceId), (App)ResourceLocation.CODEC.optionalFieldOf("sprite").forGetter($$0 -> $$0.spriteId)).apply((Applicative)$$02, SingleFile::new));
    private final ResourceLocation resourceId;
    private final Optional<ResourceLocation> spriteId;

    public SingleFile(ResourceLocation $$0, Optional<ResourceLocation> $$1) {
        this.resourceId = $$0;
        this.spriteId = $$1;
    }

    @Override
    public void run(ResourceManager $$0, SpriteSource.Output $$1) {
        ResourceLocation $$2 = TEXTURE_ID_CONVERTER.idToFile(this.resourceId);
        Optional $$3 = $$0.getResource($$2);
        if ($$3.isPresent()) {
            $$1.add((ResourceLocation)this.spriteId.orElse((Object)this.resourceId), (Resource)$$3.get());
        } else {
            LOGGER.warn("Missing sprite: {}", (Object)$$2);
        }
    }

    @Override
    public SpriteSourceType type() {
        return SpriteSources.SINGLE_FILE;
    }
}