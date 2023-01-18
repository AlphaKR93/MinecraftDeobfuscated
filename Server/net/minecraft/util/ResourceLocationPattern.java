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
 *  java.util.Optional
 *  java.util.function.Predicate
 *  java.util.regex.Pattern
 */
package net.minecraft.util;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

public class ResourceLocationPattern {
    public static final Codec<ResourceLocationPattern> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)ExtraCodecs.PATTERN.optionalFieldOf("namespace").forGetter($$0 -> $$0.namespacePattern), (App)ExtraCodecs.PATTERN.optionalFieldOf("path").forGetter($$0 -> $$0.pathPattern)).apply((Applicative)$$02, ResourceLocationPattern::new));
    private final Optional<Pattern> namespacePattern;
    private final Predicate<String> namespacePredicate;
    private final Optional<Pattern> pathPattern;
    private final Predicate<String> pathPredicate;
    private final Predicate<ResourceLocation> locationPredicate;

    private ResourceLocationPattern(Optional<Pattern> $$02, Optional<Pattern> $$1) {
        this.namespacePattern = $$02;
        this.namespacePredicate = (Predicate)$$02.map(Pattern::asPredicate).orElse($$0 -> true);
        this.pathPattern = $$1;
        this.pathPredicate = (Predicate)$$1.map(Pattern::asPredicate).orElse($$0 -> true);
        this.locationPredicate = $$0 -> this.namespacePredicate.test((Object)$$0.getNamespace()) && this.pathPredicate.test((Object)$$0.getPath());
    }

    public Predicate<String> namespacePredicate() {
        return this.namespacePredicate;
    }

    public Predicate<String> pathPredicate() {
        return this.pathPredicate;
    }

    public Predicate<ResourceLocation> locationPredicate() {
        return this.locationPredicate;
    }
}