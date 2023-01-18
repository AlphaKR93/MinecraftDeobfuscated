/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.gson.JsonParser
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  java.io.BufferedReader
 *  java.io.Reader
 *  java.lang.Exception
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.HashMap
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.function.Predicate
 *  java.util.function.Supplier
 *  org.slf4j.Logger
 */
package net.minecraft.client.renderer.texture.atlas;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;

public class SpriteResourceLoader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final FileToIdConverter ATLAS_INFO_CONVERTER = new FileToIdConverter("atlases", ".json");
    private final List<SpriteSource> sources;

    private SpriteResourceLoader(List<SpriteSource> $$0) {
        this.sources = $$0;
    }

    public List<Supplier<SpriteContents>> list(ResourceManager $$0) {
        HashMap $$1 = new HashMap();
        SpriteSource.Output $$22 = new SpriteSource.Output(){
            final /* synthetic */ Map val$sprites;
            {
                this.val$sprites = map;
            }

            @Override
            public void add(ResourceLocation $$0, SpriteSource.SpriteSupplier $$1) {
                SpriteSource.SpriteSupplier $$2 = (SpriteSource.SpriteSupplier)this.val$sprites.put((Object)$$0, (Object)$$1);
                if ($$2 != null) {
                    $$2.discard();
                }
            }

            @Override
            public void removeAll(Predicate<ResourceLocation> $$0) {
                Iterator $$1 = this.val$sprites.entrySet().iterator();
                while ($$1.hasNext()) {
                    Map.Entry $$2 = (Map.Entry)$$1.next();
                    if (!$$0.test((Object)((ResourceLocation)$$2.getKey()))) continue;
                    ((SpriteSource.SpriteSupplier)$$2.getValue()).discard();
                    $$1.remove();
                }
            }
        };
        this.sources.forEach($$2 -> $$2.run($$0, $$22));
        ImmutableList.Builder $$3 = ImmutableList.builder();
        $$3.add(MissingTextureAtlasSprite::create);
        $$3.addAll((Iterable)$$1.values());
        return $$3.build();
    }

    public static SpriteResourceLoader load(ResourceManager $$0, ResourceLocation $$1) {
        ResourceLocation $$2 = ATLAS_INFO_CONVERTER.idToFile($$1);
        ArrayList $$3 = new ArrayList();
        for (Resource $$4 : $$0.getResourceStack($$2)) {
            try {
                BufferedReader $$5 = $$4.openAsReader();
                try {
                    Dynamic $$6 = new Dynamic((DynamicOps)JsonOps.INSTANCE, (Object)JsonParser.parseReader((Reader)$$5));
                    $$3.addAll((Collection)SpriteSources.FILE_CODEC.parse($$6).getOrThrow(false, arg_0 -> ((Logger)LOGGER).error(arg_0)));
                }
                finally {
                    if ($$5 == null) continue;
                    $$5.close();
                }
            }
            catch (Exception $$7) {
                LOGGER.warn("Failed to parse atlas definition {} in pack {}", new Object[]{$$2, $$4.sourcePackId(), $$7});
            }
        }
        return new SpriteResourceLoader((List<SpriteSource>)$$3);
    }
}