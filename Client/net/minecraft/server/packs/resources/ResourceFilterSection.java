/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 */
package net.minecraft.server.packs.resources;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.util.ResourceLocationPattern;

public class ResourceFilterSection {
    private static final Codec<ResourceFilterSection> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Codec.list(ResourceLocationPattern.CODEC).fieldOf("block").forGetter($$0 -> $$0.blockList)).apply((Applicative)$$02, ResourceFilterSection::new));
    public static final MetadataSectionType<ResourceFilterSection> TYPE = MetadataSectionType.fromCodec("filter", CODEC);
    private final List<ResourceLocationPattern> blockList;

    public ResourceFilterSection(List<ResourceLocationPattern> $$0) {
        this.blockList = List.copyOf($$0);
    }

    public boolean isNamespaceFiltered(String $$0) {
        return this.blockList.stream().anyMatch($$1 -> $$1.namespacePredicate().test((Object)$$0));
    }

    public boolean isPathFiltered(String $$0) {
        return this.blockList.stream().anyMatch($$1 -> $$1.pathPredicate().test((Object)$$0));
    }
}