/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.util.UUID
 */
package net.minecraft.client.resources;

import java.util.UUID;
import net.minecraft.resources.ResourceLocation;

public class DefaultPlayerSkin {
    private static final SkinType[] DEFAULT_SKINS = new SkinType[]{new SkinType("textures/entity/player/slim/alex.png", ModelType.SLIM), new SkinType("textures/entity/player/slim/ari.png", ModelType.SLIM), new SkinType("textures/entity/player/slim/efe.png", ModelType.SLIM), new SkinType("textures/entity/player/slim/kai.png", ModelType.SLIM), new SkinType("textures/entity/player/slim/makena.png", ModelType.SLIM), new SkinType("textures/entity/player/slim/noor.png", ModelType.SLIM), new SkinType("textures/entity/player/slim/steve.png", ModelType.SLIM), new SkinType("textures/entity/player/slim/sunny.png", ModelType.SLIM), new SkinType("textures/entity/player/slim/zuri.png", ModelType.SLIM), new SkinType("textures/entity/player/wide/alex.png", ModelType.WIDE), new SkinType("textures/entity/player/wide/ari.png", ModelType.WIDE), new SkinType("textures/entity/player/wide/efe.png", ModelType.WIDE), new SkinType("textures/entity/player/wide/kai.png", ModelType.WIDE), new SkinType("textures/entity/player/wide/makena.png", ModelType.WIDE), new SkinType("textures/entity/player/wide/noor.png", ModelType.WIDE), new SkinType("textures/entity/player/wide/steve.png", ModelType.WIDE), new SkinType("textures/entity/player/wide/sunny.png", ModelType.WIDE), new SkinType("textures/entity/player/wide/zuri.png", ModelType.WIDE)};

    public static ResourceLocation getDefaultSkin() {
        return DEFAULT_SKINS[6].texture();
    }

    public static ResourceLocation getDefaultSkin(UUID $$0) {
        return DefaultPlayerSkin.getSkinType((UUID)$$0).texture;
    }

    public static String getSkinModelName(UUID $$0) {
        return DefaultPlayerSkin.getSkinType((UUID)$$0).model.id;
    }

    private static SkinType getSkinType(UUID $$0) {
        return DEFAULT_SKINS[Math.floorMod((int)$$0.hashCode(), (int)DEFAULT_SKINS.length)];
    }

    record SkinType(ResourceLocation texture, ModelType model) {
        public SkinType(String $$0, ModelType $$1) {
            this(new ResourceLocation($$0), $$1);
        }
    }

    static enum ModelType {
        SLIM("slim"),
        WIDE("default");

        final String id;

        private ModelType(String $$0) {
            this.id = $$0;
        }
    }
}