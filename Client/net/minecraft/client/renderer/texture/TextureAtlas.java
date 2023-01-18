/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.io.BufferedWriter
 *  java.io.IOException
 *  java.lang.Deprecated
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.nio.file.Files
 *  java.nio.file.OpenOption
 *  java.nio.file.Path
 *  java.nio.file.attribute.FileAttribute
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.List
 *  java.util.Locale
 *  java.util.Map
 *  java.util.Map$Entry
 *  org.slf4j.Logger
 */
package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.inventory.InventoryMenu;
import org.slf4j.Logger;

public class TextureAtlas
extends AbstractTexture
implements Tickable {
    private static final Logger LOGGER = LogUtils.getLogger();
    @Deprecated
    public static final ResourceLocation LOCATION_BLOCKS = InventoryMenu.BLOCK_ATLAS;
    @Deprecated
    public static final ResourceLocation LOCATION_PARTICLES = new ResourceLocation("textures/atlas/particles.png");
    private List<SpriteContents> sprites = List.of();
    private List<TextureAtlasSprite.Ticker> animatedTextures = List.of();
    private Map<ResourceLocation, TextureAtlasSprite> texturesByName = Map.of();
    private final ResourceLocation location;
    private final int maxSupportedTextureSize;

    public TextureAtlas(ResourceLocation $$0) {
        this.location = $$0;
        this.maxSupportedTextureSize = RenderSystem.maxSupportedTextureSize();
    }

    @Override
    public void load(ResourceManager $$0) {
    }

    public void upload(SpriteLoader.Preparations $$0) {
        LOGGER.info("Created: {}x{}x{} {}-atlas", new Object[]{$$0.width(), $$0.height(), $$0.mipLevel(), this.location});
        TextureUtil.prepareImage(this.getId(), $$0.mipLevel(), $$0.width(), $$0.height());
        this.clearTextureData();
        this.texturesByName = Map.copyOf($$0.regions());
        ArrayList $$1 = new ArrayList();
        ArrayList $$2 = new ArrayList();
        for (TextureAtlasSprite $$3 : $$0.regions().values()) {
            $$1.add((Object)$$3.contents());
            try {
                $$3.uploadFirstFrame();
            }
            catch (Throwable $$4) {
                CrashReport $$5 = CrashReport.forThrowable($$4, "Stitching texture atlas");
                CrashReportCategory $$6 = $$5.addCategory("Texture being stitched together");
                $$6.setDetail("Atlas path", this.location);
                $$6.setDetail("Sprite", $$3);
                throw new ReportedException($$5);
            }
            TextureAtlasSprite.Ticker $$7 = $$3.createTicker();
            if ($$7 == null) continue;
            $$2.add((Object)$$7);
        }
        this.sprites = List.copyOf((Collection)$$1);
        this.animatedTextures = List.copyOf((Collection)$$2);
    }

    private void dumpContents(int $$0, int $$1, int $$2) {
        String $$3 = this.location.toDebugFileName();
        Path $$4 = TextureUtil.getDebugTexturePath();
        try {
            Files.createDirectories((Path)$$4, (FileAttribute[])new FileAttribute[0]);
            TextureUtil.writeAsPNG($$4, $$3, this.getId(), $$0, $$1, $$2);
            TextureAtlas.dumpSpriteNames($$4, $$3, this.texturesByName);
        }
        catch (IOException $$5) {
            LOGGER.warn("Failed to dump atlas contents to {}", (Object)$$4);
        }
    }

    private static void dumpSpriteNames(Path $$0, String $$1, Map<ResourceLocation, TextureAtlasSprite> $$2) {
        Path $$3 = $$0.resolve($$1 + ".txt");
        try (BufferedWriter $$4 = Files.newBufferedWriter((Path)$$3, (OpenOption[])new OpenOption[0]);){
            for (Map.Entry $$5 : $$2.entrySet().stream().sorted(Map.Entry.comparingByKey()).toList()) {
                TextureAtlasSprite $$6 = (TextureAtlasSprite)$$5.getValue();
                $$4.write(String.format((Locale)Locale.ROOT, (String)"%s\tx=%d\ty=%d\tw=%d\th=%d%n", (Object[])new Object[]{$$5.getKey(), $$6.getX(), $$6.getY(), $$6.contents().width(), $$6.contents().height()}));
            }
        }
        catch (IOException $$7) {
            LOGGER.warn("Failed to write file {}", (Object)$$3, (Object)$$7);
        }
    }

    public void cycleAnimationFrames() {
        this.bind();
        for (TextureAtlasSprite.Ticker $$0 : this.animatedTextures) {
            $$0.tickAndUpload();
        }
    }

    @Override
    public void tick() {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(this::cycleAnimationFrames);
        } else {
            this.cycleAnimationFrames();
        }
    }

    public TextureAtlasSprite getSprite(ResourceLocation $$0) {
        TextureAtlasSprite $$1 = (TextureAtlasSprite)this.texturesByName.get((Object)$$0);
        if ($$1 == null) {
            return (TextureAtlasSprite)this.texturesByName.get((Object)MissingTextureAtlasSprite.getLocation());
        }
        return $$1;
    }

    public void clearTextureData() {
        this.sprites.forEach(SpriteContents::close);
        this.animatedTextures.forEach(TextureAtlasSprite.Ticker::close);
        this.sprites = List.of();
        this.animatedTextures = List.of();
        this.texturesByName = Map.of();
    }

    public ResourceLocation location() {
        return this.location;
    }

    public int maxSupportedTextureSize() {
        return this.maxSupportedTextureSize;
    }

    public void updateFilter(SpriteLoader.Preparations $$0) {
        this.setFilter(false, $$0.mipLevel() > 0);
    }
}