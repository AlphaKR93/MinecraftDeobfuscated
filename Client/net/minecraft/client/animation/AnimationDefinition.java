/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 *  java.util.Map
 *  org.apache.commons.compress.utils.Lists
 */
package net.minecraft.client.animation;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.client.animation.AnimationChannel;
import org.apache.commons.compress.utils.Lists;

public record AnimationDefinition(float lengthInSeconds, boolean looping, Map<String, List<AnimationChannel>> boneAnimations) {

    public static class Builder {
        private final float length;
        private final Map<String, List<AnimationChannel>> animationByBone = Maps.newHashMap();
        private boolean looping;

        public static Builder withLength(float $$0) {
            return new Builder($$0);
        }

        private Builder(float $$0) {
            this.length = $$0;
        }

        public Builder looping() {
            this.looping = true;
            return this;
        }

        public Builder addAnimation(String $$02, AnimationChannel $$1) {
            ((List)this.animationByBone.computeIfAbsent((Object)$$02, $$0 -> Lists.newArrayList())).add((Object)$$1);
            return this;
        }

        public AnimationDefinition build() {
            return new AnimationDefinition(this.length, this.looping, this.animationByBone);
        }
    }
}