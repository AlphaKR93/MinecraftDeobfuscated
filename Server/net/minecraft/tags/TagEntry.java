/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.util.Collection
 *  java.util.function.Consumer
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.tags;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

public class TagEntry {
    private static final Codec<TagEntry> FULL_CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)ExtraCodecs.TAG_OR_ELEMENT_ID.fieldOf("id").forGetter(TagEntry::elementOrTag), (App)Codec.BOOL.optionalFieldOf("required", (Object)true).forGetter($$0 -> $$0.required)).apply((Applicative)$$02, TagEntry::new));
    public static final Codec<TagEntry> CODEC = Codec.either(ExtraCodecs.TAG_OR_ELEMENT_ID, FULL_CODEC).xmap($$02 -> (TagEntry)$$02.map($$0 -> new TagEntry((ExtraCodecs.TagOrElementLocation)((Object)((Object)$$0)), true), $$0 -> $$0), $$0 -> $$0.required ? Either.left((Object)((Object)$$0.elementOrTag())) : Either.right((Object)$$0));
    private final ResourceLocation id;
    private final boolean tag;
    private final boolean required;

    private TagEntry(ResourceLocation $$0, boolean $$1, boolean $$2) {
        this.id = $$0;
        this.tag = $$1;
        this.required = $$2;
    }

    private TagEntry(ExtraCodecs.TagOrElementLocation $$0, boolean $$1) {
        this.id = $$0.id();
        this.tag = $$0.tag();
        this.required = $$1;
    }

    private ExtraCodecs.TagOrElementLocation elementOrTag() {
        return new ExtraCodecs.TagOrElementLocation(this.id, this.tag);
    }

    public static TagEntry element(ResourceLocation $$0) {
        return new TagEntry($$0, false, true);
    }

    public static TagEntry optionalElement(ResourceLocation $$0) {
        return new TagEntry($$0, false, false);
    }

    public static TagEntry tag(ResourceLocation $$0) {
        return new TagEntry($$0, true, true);
    }

    public static TagEntry optionalTag(ResourceLocation $$0) {
        return new TagEntry($$0, true, false);
    }

    public <T> boolean build(Lookup<T> $$0, Consumer<T> $$1) {
        if (this.tag) {
            Collection<T> $$2 = $$0.tag(this.id);
            if ($$2 == null) {
                return !this.required;
            }
            $$2.forEach($$1);
        } else {
            T $$3 = $$0.element(this.id);
            if ($$3 == null) {
                return !this.required;
            }
            $$1.accept($$3);
        }
        return true;
    }

    public void visitRequiredDependencies(Consumer<ResourceLocation> $$0) {
        if (this.tag && this.required) {
            $$0.accept((Object)this.id);
        }
    }

    public void visitOptionalDependencies(Consumer<ResourceLocation> $$0) {
        if (this.tag && !this.required) {
            $$0.accept((Object)this.id);
        }
    }

    public boolean verifyIfPresent(Predicate<ResourceLocation> $$0, Predicate<ResourceLocation> $$1) {
        return !this.required || (this.tag ? $$1 : $$0).test((Object)this.id);
    }

    public String toString() {
        StringBuilder $$0 = new StringBuilder();
        if (this.tag) {
            $$0.append('#');
        }
        $$0.append((Object)this.id);
        if (!this.required) {
            $$0.append('?');
        }
        return $$0.toString();
    }

    public static interface Lookup<T> {
        @Nullable
        public T element(ResourceLocation var1);

        @Nullable
        public Collection<T> tag(ResourceLocation var1);
    }
}