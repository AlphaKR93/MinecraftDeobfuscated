/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Object
 *  java.util.Collection
 *  java.util.List
 *  java.util.Map
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.stream.Collectors
 *  org.slf4j.Logger
 */
package net.minecraft.server;

import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.ServerFunctionLibrary;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleReloadInstance;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagManager;
import net.minecraft.util.Unit;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.ItemModifierManager;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.PredicateManager;
import org.slf4j.Logger;

public class ReloadableServerResources {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final CompletableFuture<Unit> DATA_RELOAD_INITIAL_TASK = CompletableFuture.completedFuture((Object)((Object)Unit.INSTANCE));
    private final CommandBuildContext.Configurable commandBuildContext;
    private final Commands commands;
    private final RecipeManager recipes = new RecipeManager();
    private final TagManager tagManager;
    private final PredicateManager predicateManager = new PredicateManager();
    private final LootTables lootTables = new LootTables(this.predicateManager);
    private final ItemModifierManager itemModifierManager = new ItemModifierManager(this.predicateManager, this.lootTables);
    private final ServerAdvancementManager advancements = new ServerAdvancementManager(this.predicateManager);
    private final ServerFunctionLibrary functionLibrary;

    public ReloadableServerResources(RegistryAccess.Frozen $$0, FeatureFlagSet $$1, Commands.CommandSelection $$2, int $$3) {
        this.tagManager = new TagManager($$0);
        this.commandBuildContext = CommandBuildContext.configurable($$0, $$1);
        this.commands = new Commands($$2, this.commandBuildContext);
        this.commandBuildContext.missingTagAccessPolicy(CommandBuildContext.MissingTagAccessPolicy.CREATE_NEW);
        this.functionLibrary = new ServerFunctionLibrary($$3, this.commands.getDispatcher());
    }

    public ServerFunctionLibrary getFunctionLibrary() {
        return this.functionLibrary;
    }

    public PredicateManager getPredicateManager() {
        return this.predicateManager;
    }

    public LootTables getLootTables() {
        return this.lootTables;
    }

    public ItemModifierManager getItemModifierManager() {
        return this.itemModifierManager;
    }

    public RecipeManager getRecipeManager() {
        return this.recipes;
    }

    public Commands getCommands() {
        return this.commands;
    }

    public ServerAdvancementManager getAdvancements() {
        return this.advancements;
    }

    public List<PreparableReloadListener> listeners() {
        return List.of((Object)this.tagManager, (Object)this.predicateManager, (Object)this.recipes, (Object)this.lootTables, (Object)this.itemModifierManager, (Object)this.functionLibrary, (Object)this.advancements);
    }

    public static CompletableFuture<ReloadableServerResources> loadResources(ResourceManager $$0, RegistryAccess.Frozen $$12, FeatureFlagSet $$22, Commands.CommandSelection $$3, int $$4, Executor $$5, Executor $$6) {
        ReloadableServerResources $$7 = new ReloadableServerResources($$12, $$22, $$3, $$4);
        return SimpleReloadInstance.create($$0, $$7.listeners(), $$5, $$6, DATA_RELOAD_INITIAL_TASK, LOGGER.isDebugEnabled()).done().whenComplete(($$1, $$2) -> $$0.commandBuildContext.missingTagAccessPolicy(CommandBuildContext.MissingTagAccessPolicy.FAIL)).thenApply($$1 -> $$7);
    }

    public void updateRegistryTags(RegistryAccess $$0) {
        this.tagManager.getResult().forEach($$1 -> ReloadableServerResources.updateRegistryTags($$0, $$1));
        Blocks.rebuildCache();
    }

    private static <T> void updateRegistryTags(RegistryAccess $$02, TagManager.LoadResult<T> $$12) {
        ResourceKey $$2 = $$12.key();
        Map $$3 = (Map)$$12.tags().entrySet().stream().collect(Collectors.toUnmodifiableMap($$1 -> TagKey.create($$2, (ResourceLocation)$$1.getKey()), $$0 -> List.copyOf((Collection)((Collection)$$0.getValue()))));
        $$02.registryOrThrow($$2).bindTags($$3);
    }
}