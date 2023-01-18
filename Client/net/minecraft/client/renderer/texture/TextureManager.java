/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.logging.LogUtils
 *  java.io.IOException
 *  java.lang.AutoCloseable
 *  java.lang.Exception
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runnable
 *  java.lang.String
 *  java.lang.Throwable
 *  java.lang.Void
 *  java.util.Iterator
 *  java.util.Locale
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Set
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  org.slf4j.Logger
 */
package net.minecraft.client.renderer.texture;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.PreloadedTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public class TextureManager
implements PreparableReloadListener,
Tickable,
AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final ResourceLocation INTENTIONAL_MISSING_TEXTURE = new ResourceLocation("");
    private final Map<ResourceLocation, AbstractTexture> byPath = Maps.newHashMap();
    private final Set<Tickable> tickableTextures = Sets.newHashSet();
    private final Map<String, Integer> prefixRegister = Maps.newHashMap();
    private final ResourceManager resourceManager;

    public TextureManager(ResourceManager $$0) {
        this.resourceManager = $$0;
    }

    public void bindForSetup(ResourceLocation $$0) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> this._bind($$0));
        } else {
            this._bind($$0);
        }
    }

    private void _bind(ResourceLocation $$0) {
        AbstractTexture $$1 = (AbstractTexture)this.byPath.get((Object)$$0);
        if ($$1 == null) {
            $$1 = new SimpleTexture($$0);
            this.register($$0, $$1);
        }
        $$1.bind();
    }

    public void register(ResourceLocation $$0, AbstractTexture $$1) {
        AbstractTexture $$2 = (AbstractTexture)this.byPath.put((Object)$$0, (Object)($$1 = this.loadTexture($$0, $$1)));
        if ($$2 != $$1) {
            if ($$2 != null && $$2 != MissingTextureAtlasSprite.getTexture()) {
                this.tickableTextures.remove((Object)$$2);
                this.safeClose($$0, $$2);
            }
            if ($$1 instanceof Tickable) {
                this.tickableTextures.add((Object)((Tickable)((Object)$$1)));
            }
        }
    }

    private void safeClose(ResourceLocation $$0, AbstractTexture $$1) {
        if ($$1 != MissingTextureAtlasSprite.getTexture()) {
            try {
                $$1.close();
            }
            catch (Exception $$2) {
                LOGGER.warn("Failed to close texture {}", (Object)$$0, (Object)$$2);
            }
        }
        $$1.releaseId();
    }

    private AbstractTexture loadTexture(ResourceLocation $$0, AbstractTexture $$1) {
        try {
            $$1.load(this.resourceManager);
            return $$1;
        }
        catch (IOException $$2) {
            if ($$0 != INTENTIONAL_MISSING_TEXTURE) {
                LOGGER.warn("Failed to load texture: {}", (Object)$$0, (Object)$$2);
            }
            return MissingTextureAtlasSprite.getTexture();
        }
        catch (Throwable $$3) {
            CrashReport $$4 = CrashReport.forThrowable($$3, "Registering texture");
            CrashReportCategory $$5 = $$4.addCategory("Resource location being registered");
            $$5.setDetail("Resource location", $$0);
            $$5.setDetail("Texture object class", () -> $$1.getClass().getName());
            throw new ReportedException($$4);
        }
    }

    public AbstractTexture getTexture(ResourceLocation $$0) {
        AbstractTexture $$1 = (AbstractTexture)this.byPath.get((Object)$$0);
        if ($$1 == null) {
            $$1 = new SimpleTexture($$0);
            this.register($$0, $$1);
        }
        return $$1;
    }

    public AbstractTexture getTexture(ResourceLocation $$0, AbstractTexture $$1) {
        return (AbstractTexture)this.byPath.getOrDefault((Object)$$0, (Object)$$1);
    }

    public ResourceLocation register(String $$0, DynamicTexture $$1) {
        Integer $$2 = (Integer)this.prefixRegister.get((Object)$$0);
        if ($$2 == null) {
            $$2 = 1;
        } else {
            Integer n = $$2;
            $$2 = $$2 + 1;
        }
        this.prefixRegister.put((Object)$$0, (Object)$$2);
        ResourceLocation $$3 = new ResourceLocation(String.format((Locale)Locale.ROOT, (String)"dynamic/%s_%d", (Object[])new Object[]{$$0, $$2}));
        this.register($$3, (AbstractTexture)$$1);
        return $$3;
    }

    public CompletableFuture<Void> preload(ResourceLocation $$0, Executor $$1) {
        if (!this.byPath.containsKey((Object)$$0)) {
            PreloadedTexture $$2 = new PreloadedTexture(this.resourceManager, $$0, $$1);
            this.byPath.put((Object)$$0, (Object)$$2);
            return $$2.getFuture().thenRunAsync(() -> this.register($$0, $$2), TextureManager::execute);
        }
        return CompletableFuture.completedFuture(null);
    }

    private static void execute(Runnable $$0) {
        Minecraft.getInstance().execute(() -> RenderSystem.recordRenderCall(() -> ((Runnable)$$0).run()));
    }

    @Override
    public void tick() {
        for (Tickable $$0 : this.tickableTextures) {
            $$0.tick();
        }
    }

    public void release(ResourceLocation $$0) {
        AbstractTexture $$1 = this.getTexture($$0, MissingTextureAtlasSprite.getTexture());
        if ($$1 != MissingTextureAtlasSprite.getTexture()) {
            TextureUtil.releaseTextureId($$1.getId());
        }
    }

    public void close() {
        this.byPath.forEach(this::safeClose);
        this.byPath.clear();
        this.tickableTextures.clear();
        this.prefixRegister.clear();
    }

    @Override
    public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier $$02, ResourceManager $$1, ProfilerFiller $$2, ProfilerFiller $$32, Executor $$4, Executor $$5) {
        CompletableFuture $$6 = new CompletableFuture();
        CompletableFuture.allOf((CompletableFuture[])new CompletableFuture[]{TitleScreen.preloadResources(this, $$4), this.preload(AbstractWidget.WIDGETS_LOCATION, $$4)}).thenCompose($$02::wait).thenAcceptAsync($$3 -> {
            MissingTextureAtlasSprite.getTexture();
            RealmsMainScreen.updateTeaserImages(this.resourceManager);
            Iterator $$4 = this.byPath.entrySet().iterator();
            while ($$4.hasNext()) {
                Map.Entry $$5 = (Map.Entry)$$4.next();
                ResourceLocation $$6 = (ResourceLocation)$$5.getKey();
                AbstractTexture $$7 = (AbstractTexture)$$5.getValue();
                if ($$7 == MissingTextureAtlasSprite.getTexture() && !$$6.equals(MissingTextureAtlasSprite.getLocation())) {
                    $$4.remove();
                    continue;
                }
                $$7.reset(this, $$1, $$6, $$5);
            }
            Minecraft.getInstance().tell(() -> $$6.complete(null));
        }, $$0 -> RenderSystem.recordRenderCall(() -> ((Runnable)$$0).run()));
        return $$6;
    }
}