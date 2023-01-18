/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Multimap
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  java.io.BufferedReader
 *  java.io.Reader
 *  java.lang.AutoCloseable
 *  java.lang.CharSequence
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Throwable
 *  java.lang.Void
 *  java.util.ArrayList
 *  java.util.IdentityHashMap
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Objects
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.function.BiFunction
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 *  org.slf4j.Logger
 */
package net.minecraft.client.resources.model;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.AtlasSet;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.slf4j.Logger;

public class ModelManager
implements PreparableReloadListener,
AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<ResourceLocation, ResourceLocation> VANILLA_ATLASES = Map.of((Object)Sheets.BANNER_SHEET, (Object)new ResourceLocation("banner_patterns"), (Object)Sheets.BED_SHEET, (Object)new ResourceLocation("beds"), (Object)Sheets.CHEST_SHEET, (Object)new ResourceLocation("chests"), (Object)Sheets.SHIELD_SHEET, (Object)new ResourceLocation("shield_patterns"), (Object)Sheets.SIGN_SHEET, (Object)new ResourceLocation("signs"), (Object)Sheets.SHULKER_SHEET, (Object)new ResourceLocation("shulker_boxes"), (Object)TextureAtlas.LOCATION_BLOCKS, (Object)new ResourceLocation("blocks"));
    private Map<ResourceLocation, BakedModel> bakedRegistry;
    private final AtlasSet atlases;
    private final BlockModelShaper blockModelShaper;
    private final BlockColors blockColors;
    private int maxMipmapLevels;
    private BakedModel missingModel;
    private Object2IntMap<BlockState> modelGroups;

    public ModelManager(TextureManager $$0, BlockColors $$1, int $$2) {
        this.blockColors = $$1;
        this.maxMipmapLevels = $$2;
        this.blockModelShaper = new BlockModelShaper(this);
        this.atlases = new AtlasSet(VANILLA_ATLASES, $$0);
    }

    public BakedModel getModel(ModelResourceLocation $$0) {
        return (BakedModel)this.bakedRegistry.getOrDefault((Object)$$0, (Object)this.missingModel);
    }

    public BakedModel getMissingModel() {
        return this.missingModel;
    }

    public BlockModelShaper getBlockModelShaper() {
        return this.blockModelShaper;
    }

    @Override
    public final CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier $$02, ResourceManager $$12, ProfilerFiller $$22, ProfilerFiller $$32, Executor $$4, Executor $$5) {
        $$22.startTick();
        CompletableFuture<Map<ResourceLocation, BlockModel>> $$6 = ModelManager.loadBlockModels($$12, $$4);
        CompletableFuture<Map<ResourceLocation, List<ModelBakery.LoadedJson>>> $$7 = ModelManager.loadBlockStates($$12, $$4);
        CompletableFuture $$8 = $$6.thenCombineAsync($$7, ($$1, $$2) -> new ModelBakery(this.blockColors, $$22, (Map<ResourceLocation, BlockModel>)$$1, (Map<ResourceLocation, List<ModelBakery.LoadedJson>>)$$2), $$4);
        Map<ResourceLocation, CompletableFuture<AtlasSet.StitchResult>> $$9 = this.atlases.scheduleLoad($$12, this.maxMipmapLevels, $$4);
        return CompletableFuture.allOf((CompletableFuture[])((CompletableFuture[])Stream.concat((Stream)$$9.values().stream(), (Stream)Stream.of((Object)$$8)).toArray(CompletableFuture[]::new))).thenApplyAsync($$3 -> this.loadModels($$22, (Map<ResourceLocation, AtlasSet.StitchResult>)((Map)$$9.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, $$0 -> (AtlasSet.StitchResult)((CompletableFuture)$$0.getValue()).join()))), (ModelBakery)$$8.join()), $$4).thenCompose($$0 -> $$0.readyForUpload.thenApply($$1 -> $$0)).thenCompose($$02::wait).thenAcceptAsync($$1 -> this.apply((ReloadState)((Object)$$1), $$32), $$5);
    }

    private static CompletableFuture<Map<ResourceLocation, BlockModel>> loadBlockModels(ResourceManager $$0, Executor $$12) {
        return CompletableFuture.supplyAsync(() -> ModelBakery.MODEL_LISTER.listMatchingResources($$0), (Executor)$$12).thenCompose($$1 -> {
            ArrayList $$2 = new ArrayList($$1.size());
            for (Map.Entry $$3 : $$1.entrySet()) {
                $$2.add((Object)CompletableFuture.supplyAsync(() -> {
                    Pair pair;
                    block8: {
                        Object $$1 = ((Resource)$$3.getValue()).openAsReader();
                        try {
                            pair = Pair.of((Object)((ResourceLocation)$$3.getKey()), (Object)BlockModel.fromStream((Reader)$$1));
                            if ($$1 == null) break block8;
                        }
                        catch (Throwable throwable) {
                            try {
                                if ($$1 != null) {
                                    try {
                                        $$1.close();
                                    }
                                    catch (Throwable throwable2) {
                                        throwable.addSuppressed(throwable2);
                                    }
                                }
                                throw throwable;
                            }
                            catch (Exception $$2) {
                                LOGGER.error("Failed to load model {}", $$3.getKey(), (Object)$$2);
                                return null;
                            }
                        }
                        $$1.close();
                    }
                    return pair;
                }, (Executor)$$12));
            }
            return Util.sequence($$2).thenApply($$0 -> (Map)$$0.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableMap(Pair::getFirst, Pair::getSecond)));
        });
    }

    private static CompletableFuture<Map<ResourceLocation, List<ModelBakery.LoadedJson>>> loadBlockStates(ResourceManager $$0, Executor $$12) {
        return CompletableFuture.supplyAsync(() -> ModelBakery.BLOCKSTATE_LISTER.listMatchingResourceStacks($$0), (Executor)$$12).thenCompose($$1 -> {
            ArrayList $$2 = new ArrayList($$1.size());
            for (Map.Entry $$3 : $$1.entrySet()) {
                $$2.add((Object)CompletableFuture.supplyAsync(() -> {
                    Object $$1 = (List)$$3.getValue();
                    ArrayList $$2 = new ArrayList($$1.size());
                    for (Resource $$3 : $$1) {
                        try {
                            BufferedReader $$4 = $$3.openAsReader();
                            try {
                                JsonObject $$5 = GsonHelper.parse((Reader)$$4);
                                $$2.add((Object)new ModelBakery.LoadedJson($$3.sourcePackId(), (JsonElement)$$5));
                            }
                            finally {
                                if ($$4 == null) continue;
                                $$4.close();
                            }
                        }
                        catch (Exception $$6) {
                            LOGGER.error("Failed to load blockstate {} from pack {}", new Object[]{$$3.getKey(), $$3.sourcePackId(), $$6});
                        }
                    }
                    return Pair.of((Object)((ResourceLocation)$$3.getKey()), (Object)$$2);
                }, (Executor)$$12));
            }
            return Util.sequence($$2).thenApply($$0 -> (Map)$$0.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableMap(Pair::getFirst, Pair::getSecond)));
        });
    }

    private ReloadState loadModels(ProfilerFiller $$0, Map<ResourceLocation, AtlasSet.StitchResult> $$12, ModelBakery $$2) {
        $$0.push("load");
        $$0.popPush("baking");
        HashMultimap $$3 = HashMultimap.create();
        $$2.bakeModels((BiFunction<ResourceLocation, Material, TextureAtlasSprite>)((BiFunction)(arg_0, arg_1) -> ModelManager.lambda$loadModels$15($$12, (Multimap)$$3, arg_0, arg_1)));
        $$3.asMap().forEach(($$02, $$1) -> LOGGER.warn("Missing textures in model {}:\n{}", $$02, $$1.stream().sorted(Material.COMPARATOR).map($$0 -> "    " + $$0.atlasLocation() + ":" + $$0.texture()).collect(Collectors.joining((CharSequence)"\n"))));
        $$0.popPush("dispatch");
        Map<ResourceLocation, BakedModel> $$4 = $$2.getBakedTopLevelModels();
        BakedModel $$5 = (BakedModel)$$4.get((Object)ModelBakery.MISSING_MODEL_LOCATION);
        IdentityHashMap $$6 = new IdentityHashMap();
        for (Block $$7 : BuiltInRegistries.BLOCK) {
            $$7.getStateDefinition().getPossibleStates().forEach(arg_0 -> ModelManager.lambda$loadModels$18($$4, $$5, (Map)$$6, arg_0));
        }
        CompletableFuture $$8 = CompletableFuture.allOf((CompletableFuture[])((CompletableFuture[])$$12.values().stream().map(AtlasSet.StitchResult::readyForUpload).toArray(CompletableFuture[]::new)));
        $$0.pop();
        $$0.endTick();
        return new ReloadState($$2, $$5, (Map<BlockState, BakedModel>)$$6, $$12, (CompletableFuture<Void>)$$8);
    }

    private void apply(ReloadState $$0, ProfilerFiller $$1) {
        $$1.startTick();
        $$1.push("upload");
        $$0.atlasPreparations.values().forEach(AtlasSet.StitchResult::upload);
        ModelBakery $$2 = $$0.modelBakery;
        this.bakedRegistry = $$2.getBakedTopLevelModels();
        this.modelGroups = $$2.getModelGroups();
        this.missingModel = $$0.missingModel;
        $$1.popPush("cache");
        this.blockModelShaper.replaceCache($$0.modelCache);
        $$1.pop();
        $$1.endTick();
    }

    public boolean requiresRender(BlockState $$0, BlockState $$1) {
        int $$3;
        if ($$0 == $$1) {
            return false;
        }
        int $$2 = this.modelGroups.getInt((Object)$$0);
        if ($$2 != -1 && $$2 == ($$3 = this.modelGroups.getInt((Object)$$1))) {
            FluidState $$5;
            FluidState $$4 = $$0.getFluidState();
            return $$4 != ($$5 = $$1.getFluidState());
        }
        return true;
    }

    public TextureAtlas getAtlas(ResourceLocation $$0) {
        return this.atlases.getAtlas($$0);
    }

    public void close() {
        this.atlases.close();
    }

    public void updateMaxMipLevel(int $$0) {
        this.maxMipmapLevels = $$0;
    }

    private static /* synthetic */ void lambda$loadModels$18(Map $$0, BakedModel $$1, Map $$2, BlockState $$3) {
        ResourceLocation $$4 = $$3.getBlock().builtInRegistryHolder().key().location();
        BakedModel $$5 = (BakedModel)$$0.getOrDefault((Object)BlockModelShaper.stateToModelLocation($$4, $$3), (Object)$$1);
        $$2.put((Object)$$3, (Object)$$5);
    }

    private static /* synthetic */ TextureAtlasSprite lambda$loadModels$15(Map $$0, Multimap $$1, ResourceLocation $$2, Material $$3) {
        AtlasSet.StitchResult $$4 = (AtlasSet.StitchResult)$$0.get((Object)$$3.atlasLocation());
        TextureAtlasSprite $$5 = $$4.getSprite($$3.texture());
        if ($$5 != null) {
            return $$5;
        }
        $$1.put((Object)$$2, (Object)$$3);
        return $$4.missing();
    }

    record ReloadState(ModelBakery modelBakery, BakedModel missingModel, Map<BlockState, BakedModel> modelCache, Map<ResourceLocation, AtlasSet.StitchResult> atlasPreparations, CompletableFuture<Void> readyForUpload) {
    }
}