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
 *  java.lang.String
 */
package net.minecraft.client.renderer.texture.atlas.sources;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public class DirectoryLister
implements SpriteSource {
    public static final Codec<DirectoryLister> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Codec.STRING.fieldOf("source").forGetter($$0 -> $$0.sourcePath), (App)Codec.STRING.fieldOf("prefix").forGetter($$0 -> $$0.idPrefix)).apply((Applicative)$$02, DirectoryLister::new));
    private final String sourcePath;
    private final String idPrefix;

    public DirectoryLister(String $$0, String $$1) {
        this.sourcePath = $$0;
        this.idPrefix = $$1;
    }

    @Override
    public void run(ResourceManager $$0, SpriteSource.Output $$1) {
        FileToIdConverter $$22 = new FileToIdConverter("textures/" + this.sourcePath, ".png");
        $$22.listMatchingResources($$0).forEach(($$2, $$3) -> {
            ResourceLocation $$4 = $$22.fileToId((ResourceLocation)$$2).withPrefix(this.idPrefix);
            $$1.add($$4, (Resource)$$3);
        });
    }

    @Override
    public SpriteSourceType type() {
        return SpriteSources.DIRECTORY;
    }
}