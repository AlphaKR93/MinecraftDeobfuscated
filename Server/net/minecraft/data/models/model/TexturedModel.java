/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  java.lang.String
 *  java.util.function.BiConsumer
 *  java.util.function.Consumer
 *  java.util.function.Function
 *  java.util.function.Supplier
 */
package net.minecraft.data.models.model;

import com.google.gson.JsonElement;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class TexturedModel {
    public static final Provider CUBE = TexturedModel.createDefault((Function<Block, TextureMapping>)((Function)TextureMapping::cube), ModelTemplates.CUBE_ALL);
    public static final Provider CUBE_MIRRORED = TexturedModel.createDefault((Function<Block, TextureMapping>)((Function)TextureMapping::cube), ModelTemplates.CUBE_MIRRORED_ALL);
    public static final Provider COLUMN = TexturedModel.createDefault((Function<Block, TextureMapping>)((Function)TextureMapping::column), ModelTemplates.CUBE_COLUMN);
    public static final Provider COLUMN_HORIZONTAL = TexturedModel.createDefault((Function<Block, TextureMapping>)((Function)TextureMapping::column), ModelTemplates.CUBE_COLUMN_HORIZONTAL);
    public static final Provider CUBE_TOP_BOTTOM = TexturedModel.createDefault((Function<Block, TextureMapping>)((Function)TextureMapping::cubeBottomTop), ModelTemplates.CUBE_BOTTOM_TOP);
    public static final Provider CUBE_TOP = TexturedModel.createDefault((Function<Block, TextureMapping>)((Function)TextureMapping::cubeTop), ModelTemplates.CUBE_TOP);
    public static final Provider ORIENTABLE_ONLY_TOP = TexturedModel.createDefault((Function<Block, TextureMapping>)((Function)TextureMapping::orientableCubeOnlyTop), ModelTemplates.CUBE_ORIENTABLE);
    public static final Provider ORIENTABLE = TexturedModel.createDefault((Function<Block, TextureMapping>)((Function)TextureMapping::orientableCube), ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM);
    public static final Provider CARPET = TexturedModel.createDefault((Function<Block, TextureMapping>)((Function)TextureMapping::wool), ModelTemplates.CARPET);
    public static final Provider GLAZED_TERRACOTTA = TexturedModel.createDefault((Function<Block, TextureMapping>)((Function)TextureMapping::pattern), ModelTemplates.GLAZED_TERRACOTTA);
    public static final Provider CORAL_FAN = TexturedModel.createDefault((Function<Block, TextureMapping>)((Function)TextureMapping::fan), ModelTemplates.CORAL_FAN);
    public static final Provider PARTICLE_ONLY = TexturedModel.createDefault((Function<Block, TextureMapping>)((Function)TextureMapping::particle), ModelTemplates.PARTICLE_ONLY);
    public static final Provider ANVIL = TexturedModel.createDefault((Function<Block, TextureMapping>)((Function)TextureMapping::top), ModelTemplates.ANVIL);
    public static final Provider LEAVES = TexturedModel.createDefault((Function<Block, TextureMapping>)((Function)TextureMapping::cube), ModelTemplates.LEAVES);
    public static final Provider LANTERN = TexturedModel.createDefault((Function<Block, TextureMapping>)((Function)TextureMapping::lantern), ModelTemplates.LANTERN);
    public static final Provider HANGING_LANTERN = TexturedModel.createDefault((Function<Block, TextureMapping>)((Function)TextureMapping::lantern), ModelTemplates.HANGING_LANTERN);
    public static final Provider SEAGRASS = TexturedModel.createDefault((Function<Block, TextureMapping>)((Function)TextureMapping::defaultTexture), ModelTemplates.SEAGRASS);
    public static final Provider COLUMN_ALT = TexturedModel.createDefault((Function<Block, TextureMapping>)((Function)TextureMapping::logColumn), ModelTemplates.CUBE_COLUMN);
    public static final Provider COLUMN_HORIZONTAL_ALT = TexturedModel.createDefault((Function<Block, TextureMapping>)((Function)TextureMapping::logColumn), ModelTemplates.CUBE_COLUMN_HORIZONTAL);
    public static final Provider TOP_BOTTOM_WITH_WALL = TexturedModel.createDefault((Function<Block, TextureMapping>)((Function)TextureMapping::cubeBottomTopWithWall), ModelTemplates.CUBE_BOTTOM_TOP);
    public static final Provider COLUMN_WITH_WALL = TexturedModel.createDefault((Function<Block, TextureMapping>)((Function)TextureMapping::columnWithWall), ModelTemplates.CUBE_COLUMN);
    private final TextureMapping mapping;
    private final ModelTemplate template;

    private TexturedModel(TextureMapping $$0, ModelTemplate $$1) {
        this.mapping = $$0;
        this.template = $$1;
    }

    public ModelTemplate getTemplate() {
        return this.template;
    }

    public TextureMapping getMapping() {
        return this.mapping;
    }

    public TexturedModel updateTextures(Consumer<TextureMapping> $$0) {
        $$0.accept((Object)this.mapping);
        return this;
    }

    public ResourceLocation create(Block $$0, BiConsumer<ResourceLocation, Supplier<JsonElement>> $$1) {
        return this.template.create($$0, this.mapping, $$1);
    }

    public ResourceLocation createWithSuffix(Block $$0, String $$1, BiConsumer<ResourceLocation, Supplier<JsonElement>> $$2) {
        return this.template.createWithSuffix($$0, $$1, this.mapping, $$2);
    }

    private static Provider createDefault(Function<Block, TextureMapping> $$0, ModelTemplate $$1) {
        return $$2 -> new TexturedModel((TextureMapping)$$0.apply((Object)$$2), $$1);
    }

    public static TexturedModel createAllSame(ResourceLocation $$0) {
        return new TexturedModel(TextureMapping.cube($$0), ModelTemplates.CUBE_ALL);
    }

    @FunctionalInterface
    public static interface Provider {
        public TexturedModel get(Block var1);

        default public ResourceLocation create(Block $$0, BiConsumer<ResourceLocation, Supplier<JsonElement>> $$1) {
            return this.get($$0).create($$0, $$1);
        }

        default public ResourceLocation createWithSuffix(Block $$0, String $$1, BiConsumer<ResourceLocation, Supplier<JsonElement>> $$2) {
            return this.get($$0).createWithSuffix($$0, $$1, $$2);
        }

        default public Provider updateTexture(Consumer<TextureMapping> $$0) {
            return $$1 -> this.get($$1).updateTextures($$0);
        }
    }
}