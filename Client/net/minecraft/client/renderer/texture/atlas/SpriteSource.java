/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.function.Predicate
 *  java.util.function.Supplier
 */
package net.minecraft.client.renderer.texture.atlas;

import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public interface SpriteSource {
    public void run(ResourceManager var1, Output var2);

    public SpriteSourceType type();

    public static interface SpriteSupplier
    extends Supplier<SpriteContents> {
        default public void discard() {
        }
    }

    public static interface Output {
        default public void add(ResourceLocation $$0, Resource $$1) {
            this.add($$0, () -> SpriteLoader.loadSprite($$0, $$1));
        }

        public void add(ResourceLocation var1, SpriteSupplier var2);

        public void removeAll(Predicate<ResourceLocation> var1);
    }
}