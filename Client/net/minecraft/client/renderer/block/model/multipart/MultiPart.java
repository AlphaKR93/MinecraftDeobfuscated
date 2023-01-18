/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.reflect.Type
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.HashSet
 *  java.util.List
 *  java.util.Objects
 *  java.util.Set
 *  java.util.function.Function
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.client.renderer.block.model.multipart.Selector;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.MultiPartBakedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class MultiPart
implements UnbakedModel {
    private final StateDefinition<Block, BlockState> definition;
    private final List<Selector> selectors;

    public MultiPart(StateDefinition<Block, BlockState> $$0, List<Selector> $$1) {
        this.definition = $$0;
        this.selectors = $$1;
    }

    public List<Selector> getSelectors() {
        return this.selectors;
    }

    public Set<MultiVariant> getMultiVariants() {
        HashSet $$0 = Sets.newHashSet();
        for (Selector $$1 : this.selectors) {
            $$0.add((Object)$$1.getVariant());
        }
        return $$0;
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 instanceof MultiPart) {
            MultiPart $$1 = (MultiPart)$$0;
            return Objects.equals(this.definition, $$1.definition) && Objects.equals(this.selectors, $$1.selectors);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash((Object[])new Object[]{this.definition, this.selectors});
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return (Collection)this.getSelectors().stream().flatMap($$0 -> $$0.getVariant().getDependencies().stream()).collect(Collectors.toSet());
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> $$0) {
        this.getSelectors().forEach($$1 -> $$1.getVariant().resolveParents($$0));
    }

    @Override
    @Nullable
    public BakedModel bake(ModelBaker $$0, Function<Material, TextureAtlasSprite> $$1, ModelState $$2, ResourceLocation $$3) {
        MultiPartBakedModel.Builder $$4 = new MultiPartBakedModel.Builder();
        for (Selector $$5 : this.getSelectors()) {
            BakedModel $$6 = $$5.getVariant().bake($$0, $$1, $$2, $$3);
            if ($$6 == null) continue;
            $$4.add($$5.getPredicate(this.definition), $$6);
        }
        return $$4.build();
    }

    public static class Deserializer
    implements JsonDeserializer<MultiPart> {
        private final BlockModelDefinition.Context context;

        public Deserializer(BlockModelDefinition.Context $$0) {
            this.context = $$0;
        }

        public MultiPart deserialize(JsonElement $$0, Type $$1, JsonDeserializationContext $$2) throws JsonParseException {
            return new MultiPart(this.context.getDefinition(), this.getSelectors($$2, $$0.getAsJsonArray()));
        }

        private List<Selector> getSelectors(JsonDeserializationContext $$0, JsonArray $$1) {
            ArrayList $$2 = Lists.newArrayList();
            for (JsonElement $$3 : $$1) {
                $$2.add((Object)((Selector)$$0.deserialize($$3, Selector.class)));
            }
            return $$2;
        }
    }
}