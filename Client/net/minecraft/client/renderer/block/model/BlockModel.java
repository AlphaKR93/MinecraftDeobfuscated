/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Joiner
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.mojang.datafixers.util.Either
 *  com.mojang.logging.LogUtils
 *  java.io.Reader
 *  java.io.StringReader
 *  java.lang.CharSequence
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.reflect.Type
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.HashMap
 *  java.util.HashSet
 *  java.util.LinkedHashSet
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Objects
 *  java.util.Optional
 *  java.util.function.Function
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.renderer.block.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BuiltInModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

public class BlockModel
implements UnbakedModel {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final FaceBakery FACE_BAKERY = new FaceBakery();
    @VisibleForTesting
    static final Gson GSON = new GsonBuilder().registerTypeAdapter(BlockModel.class, (Object)new Deserializer()).registerTypeAdapter(BlockElement.class, (Object)new BlockElement.Deserializer()).registerTypeAdapter(BlockElementFace.class, (Object)new BlockElementFace.Deserializer()).registerTypeAdapter(BlockFaceUV.class, (Object)new BlockFaceUV.Deserializer()).registerTypeAdapter(ItemTransform.class, (Object)new ItemTransform.Deserializer()).registerTypeAdapter(ItemTransforms.class, (Object)new ItemTransforms.Deserializer()).registerTypeAdapter(ItemOverride.class, (Object)new ItemOverride.Deserializer()).create();
    private static final char REFERENCE_CHAR = '#';
    public static final String PARTICLE_TEXTURE_REFERENCE = "particle";
    private final List<BlockElement> elements;
    @Nullable
    private final GuiLight guiLight;
    private final boolean hasAmbientOcclusion;
    private final ItemTransforms transforms;
    private final List<ItemOverride> overrides;
    public String name = "";
    @VisibleForTesting
    protected final Map<String, Either<Material, String>> textureMap;
    @Nullable
    protected BlockModel parent;
    @Nullable
    protected ResourceLocation parentLocation;

    public static BlockModel fromStream(Reader $$0) {
        return GsonHelper.fromJson(GSON, $$0, BlockModel.class);
    }

    public static BlockModel fromString(String $$0) {
        return BlockModel.fromStream((Reader)new StringReader($$0));
    }

    public BlockModel(@Nullable ResourceLocation $$0, List<BlockElement> $$1, Map<String, Either<Material, String>> $$2, boolean $$3, @Nullable GuiLight $$4, ItemTransforms $$5, List<ItemOverride> $$6) {
        this.elements = $$1;
        this.hasAmbientOcclusion = $$3;
        this.guiLight = $$4;
        this.textureMap = $$2;
        this.parentLocation = $$0;
        this.transforms = $$5;
        this.overrides = $$6;
    }

    public List<BlockElement> getElements() {
        if (this.elements.isEmpty() && this.parent != null) {
            return this.parent.getElements();
        }
        return this.elements;
    }

    public boolean hasAmbientOcclusion() {
        if (this.parent != null) {
            return this.parent.hasAmbientOcclusion();
        }
        return this.hasAmbientOcclusion;
    }

    public GuiLight getGuiLight() {
        if (this.guiLight != null) {
            return this.guiLight;
        }
        if (this.parent != null) {
            return this.parent.getGuiLight();
        }
        return GuiLight.SIDE;
    }

    public boolean isResolved() {
        return this.parentLocation == null || this.parent != null && this.parent.isResolved();
    }

    public List<ItemOverride> getOverrides() {
        return this.overrides;
    }

    private ItemOverrides getItemOverrides(ModelBaker $$0, BlockModel $$1) {
        if (this.overrides.isEmpty()) {
            return ItemOverrides.EMPTY;
        }
        return new ItemOverrides($$0, $$1, this.overrides);
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        HashSet $$0 = Sets.newHashSet();
        for (ItemOverride $$1 : this.overrides) {
            $$0.add((Object)$$1.getModel());
        }
        if (this.parentLocation != null) {
            $$0.add((Object)this.parentLocation);
        }
        return $$0;
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> $$0) {
        LinkedHashSet $$12 = Sets.newLinkedHashSet();
        BlockModel $$2 = this;
        while ($$2.parentLocation != null && $$2.parent == null) {
            $$12.add((Object)$$2);
            UnbakedModel $$3 = (UnbakedModel)$$0.apply((Object)$$2.parentLocation);
            if ($$3 == null) {
                LOGGER.warn("No parent '{}' while loading model '{}'", (Object)this.parentLocation, (Object)$$2);
            }
            if ($$12.contains((Object)$$3)) {
                LOGGER.warn("Found 'parent' loop while loading model '{}' in chain: {} -> {}", new Object[]{$$2, $$12.stream().map(Object::toString).collect(Collectors.joining((CharSequence)" -> ")), this.parentLocation});
                $$3 = null;
            }
            if ($$3 == null) {
                $$2.parentLocation = ModelBakery.MISSING_MODEL_LOCATION;
                $$3 = (UnbakedModel)$$0.apply((Object)$$2.parentLocation);
            }
            if (!($$3 instanceof BlockModel)) {
                throw new IllegalStateException("BlockModel parent has to be a block model.");
            }
            $$2.parent = (BlockModel)$$3;
            $$2 = $$2.parent;
        }
        this.overrides.forEach($$1 -> {
            UnbakedModel $$2 = (UnbakedModel)$$0.apply((Object)$$1.getModel());
            if (Objects.equals((Object)$$2, (Object)this)) {
                return;
            }
            $$2.resolveParents($$0);
        });
    }

    @Override
    public BakedModel bake(ModelBaker $$0, Function<Material, TextureAtlasSprite> $$1, ModelState $$2, ResourceLocation $$3) {
        return this.bake($$0, this, $$1, $$2, $$3, true);
    }

    public BakedModel bake(ModelBaker $$0, BlockModel $$1, Function<Material, TextureAtlasSprite> $$2, ModelState $$3, ResourceLocation $$4, boolean $$5) {
        TextureAtlasSprite $$6 = (TextureAtlasSprite)$$2.apply((Object)this.getMaterial(PARTICLE_TEXTURE_REFERENCE));
        if (this.getRootModel() == ModelBakery.BLOCK_ENTITY_MARKER) {
            return new BuiltInModel(this.getTransforms(), this.getItemOverrides($$0, $$1), $$6, this.getGuiLight().lightLikeBlock());
        }
        SimpleBakedModel.Builder $$7 = new SimpleBakedModel.Builder(this, this.getItemOverrides($$0, $$1), $$5).particle($$6);
        for (BlockElement $$8 : this.getElements()) {
            for (Direction $$9 : $$8.faces.keySet()) {
                BlockElementFace $$10 = (BlockElementFace)$$8.faces.get((Object)$$9);
                TextureAtlasSprite $$11 = (TextureAtlasSprite)$$2.apply((Object)this.getMaterial($$10.texture));
                if ($$10.cullForDirection == null) {
                    $$7.addUnculledFace(BlockModel.bakeFace($$8, $$10, $$11, $$9, $$3, $$4));
                    continue;
                }
                $$7.addCulledFace(Direction.rotate($$3.getRotation().getMatrix(), $$10.cullForDirection), BlockModel.bakeFace($$8, $$10, $$11, $$9, $$3, $$4));
            }
        }
        return $$7.build();
    }

    private static BakedQuad bakeFace(BlockElement $$0, BlockElementFace $$1, TextureAtlasSprite $$2, Direction $$3, ModelState $$4, ResourceLocation $$5) {
        return FACE_BAKERY.bakeQuad($$0.from, $$0.to, $$1, $$2, $$3, $$4, $$0.rotation, $$0.shade, $$5);
    }

    public boolean hasTexture(String $$0) {
        return !MissingTextureAtlasSprite.getLocation().equals(this.getMaterial($$0).texture());
    }

    public Material getMaterial(String $$0) {
        if (BlockModel.isTextureReference($$0)) {
            $$0 = $$0.substring(1);
        }
        ArrayList $$1 = Lists.newArrayList();
        Either<Material, String> $$2;
        Optional $$3;
        while (!($$3 = ($$2 = this.findTextureEntry($$0)).left()).isPresent()) {
            $$0 = (String)$$2.right().get();
            if ($$1.contains((Object)$$0)) {
                LOGGER.warn("Unable to resolve texture due to reference chain {}->{} in {}", new Object[]{Joiner.on((String)"->").join((Iterable)$$1), $$0, this.name});
                return new Material(TextureAtlas.LOCATION_BLOCKS, MissingTextureAtlasSprite.getLocation());
            }
            $$1.add((Object)$$0);
        }
        return (Material)$$3.get();
    }

    private Either<Material, String> findTextureEntry(String $$0) {
        BlockModel $$1 = this;
        while ($$1 != null) {
            Either $$2 = (Either)$$1.textureMap.get((Object)$$0);
            if ($$2 != null) {
                return $$2;
            }
            $$1 = $$1.parent;
        }
        return Either.left((Object)new Material(TextureAtlas.LOCATION_BLOCKS, MissingTextureAtlasSprite.getLocation()));
    }

    static boolean isTextureReference(String $$0) {
        return $$0.charAt(0) == '#';
    }

    public BlockModel getRootModel() {
        return this.parent == null ? this : this.parent.getRootModel();
    }

    public ItemTransforms getTransforms() {
        ItemTransform $$0 = this.getTransform(ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND);
        ItemTransform $$1 = this.getTransform(ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
        ItemTransform $$2 = this.getTransform(ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND);
        ItemTransform $$3 = this.getTransform(ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND);
        ItemTransform $$4 = this.getTransform(ItemTransforms.TransformType.HEAD);
        ItemTransform $$5 = this.getTransform(ItemTransforms.TransformType.GUI);
        ItemTransform $$6 = this.getTransform(ItemTransforms.TransformType.GROUND);
        ItemTransform $$7 = this.getTransform(ItemTransforms.TransformType.FIXED);
        return new ItemTransforms($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    private ItemTransform getTransform(ItemTransforms.TransformType $$0) {
        if (this.parent != null && !this.transforms.hasTransform($$0)) {
            return this.parent.getTransform($$0);
        }
        return this.transforms.getTransform($$0);
    }

    public String toString() {
        return this.name;
    }

    public static enum GuiLight {
        FRONT("front"),
        SIDE("side");

        private final String name;

        private GuiLight(String $$0) {
            this.name = $$0;
        }

        public static GuiLight getByName(String $$0) {
            for (GuiLight $$1 : GuiLight.values()) {
                if (!$$1.name.equals((Object)$$0)) continue;
                return $$1;
            }
            throw new IllegalArgumentException("Invalid gui light: " + $$0);
        }

        public boolean lightLikeBlock() {
            return this == SIDE;
        }
    }

    public static class Deserializer
    implements JsonDeserializer<BlockModel> {
        private static final boolean DEFAULT_AMBIENT_OCCLUSION = true;

        public BlockModel deserialize(JsonElement $$0, Type $$1, JsonDeserializationContext $$2) throws JsonParseException {
            JsonObject $$3 = $$0.getAsJsonObject();
            List<BlockElement> $$4 = this.getElements($$2, $$3);
            String $$5 = this.getParentName($$3);
            Map<String, Either<Material, String>> $$6 = this.getTextureMap($$3);
            boolean $$7 = this.getAmbientOcclusion($$3);
            ItemTransforms $$8 = ItemTransforms.NO_TRANSFORMS;
            if ($$3.has("display")) {
                JsonObject $$9 = GsonHelper.getAsJsonObject($$3, "display");
                $$8 = (ItemTransforms)$$2.deserialize((JsonElement)$$9, ItemTransforms.class);
            }
            List<ItemOverride> $$10 = this.getOverrides($$2, $$3);
            GuiLight $$11 = null;
            if ($$3.has("gui_light")) {
                $$11 = GuiLight.getByName(GsonHelper.getAsString($$3, "gui_light"));
            }
            ResourceLocation $$12 = $$5.isEmpty() ? null : new ResourceLocation($$5);
            return new BlockModel($$12, $$4, $$6, $$7, $$11, $$8, $$10);
        }

        protected List<ItemOverride> getOverrides(JsonDeserializationContext $$0, JsonObject $$1) {
            ArrayList $$2 = Lists.newArrayList();
            if ($$1.has("overrides")) {
                JsonArray $$3 = GsonHelper.getAsJsonArray($$1, "overrides");
                for (JsonElement $$4 : $$3) {
                    $$2.add((Object)((ItemOverride)$$0.deserialize($$4, ItemOverride.class)));
                }
            }
            return $$2;
        }

        private Map<String, Either<Material, String>> getTextureMap(JsonObject $$0) {
            ResourceLocation $$1 = TextureAtlas.LOCATION_BLOCKS;
            HashMap $$2 = Maps.newHashMap();
            if ($$0.has("textures")) {
                JsonObject $$3 = GsonHelper.getAsJsonObject($$0, "textures");
                for (Map.Entry $$4 : $$3.entrySet()) {
                    $$2.put((Object)((String)$$4.getKey()), Deserializer.parseTextureLocationOrReference($$1, ((JsonElement)$$4.getValue()).getAsString()));
                }
            }
            return $$2;
        }

        private static Either<Material, String> parseTextureLocationOrReference(ResourceLocation $$0, String $$1) {
            if (BlockModel.isTextureReference($$1)) {
                return Either.right((Object)$$1.substring(1));
            }
            ResourceLocation $$2 = ResourceLocation.tryParse($$1);
            if ($$2 == null) {
                throw new JsonParseException($$1 + " is not valid resource location");
            }
            return Either.left((Object)new Material($$0, $$2));
        }

        private String getParentName(JsonObject $$0) {
            return GsonHelper.getAsString($$0, "parent", "");
        }

        protected boolean getAmbientOcclusion(JsonObject $$0) {
            return GsonHelper.getAsBoolean($$0, "ambientocclusion", true);
        }

        protected List<BlockElement> getElements(JsonDeserializationContext $$0, JsonObject $$1) {
            ArrayList $$2 = Lists.newArrayList();
            if ($$1.has("elements")) {
                for (JsonElement $$3 : GsonHelper.getAsJsonArray($$1, "elements")) {
                    $$2.add((Object)((BlockElement)$$0.deserialize($$3, BlockElement.class)));
                }
            }
            return $$2;
        }
    }

    public static class LoopException
    extends RuntimeException {
        public LoopException(String $$0) {
            super($$0);
        }
    }
}