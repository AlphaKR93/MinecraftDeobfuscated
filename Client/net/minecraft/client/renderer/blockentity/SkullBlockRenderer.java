/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Maps
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture$Type
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.blockentity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PiglinHeadModel;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.dragon.DragonHeadModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;

public class SkullBlockRenderer
implements BlockEntityRenderer<SkullBlockEntity> {
    private final Map<SkullBlock.Type, SkullModelBase> modelByType;
    private static final Map<SkullBlock.Type, ResourceLocation> SKIN_BY_TYPE = (Map)Util.make(Maps.newHashMap(), $$0 -> {
        $$0.put((Object)SkullBlock.Types.SKELETON, (Object)new ResourceLocation("textures/entity/skeleton/skeleton.png"));
        $$0.put((Object)SkullBlock.Types.WITHER_SKELETON, (Object)new ResourceLocation("textures/entity/skeleton/wither_skeleton.png"));
        $$0.put((Object)SkullBlock.Types.ZOMBIE, (Object)new ResourceLocation("textures/entity/zombie/zombie.png"));
        $$0.put((Object)SkullBlock.Types.CREEPER, (Object)new ResourceLocation("textures/entity/creeper/creeper.png"));
        $$0.put((Object)SkullBlock.Types.DRAGON, (Object)new ResourceLocation("textures/entity/enderdragon/dragon.png"));
        $$0.put((Object)SkullBlock.Types.PIGLIN, (Object)new ResourceLocation("textures/entity/piglin/piglin.png"));
        $$0.put((Object)SkullBlock.Types.PLAYER, (Object)DefaultPlayerSkin.getDefaultSkin());
    });

    public static Map<SkullBlock.Type, SkullModelBase> createSkullRenderers(EntityModelSet $$0) {
        ImmutableMap.Builder $$1 = ImmutableMap.builder();
        $$1.put((Object)SkullBlock.Types.SKELETON, (Object)new SkullModel($$0.bakeLayer(ModelLayers.SKELETON_SKULL)));
        $$1.put((Object)SkullBlock.Types.WITHER_SKELETON, (Object)new SkullModel($$0.bakeLayer(ModelLayers.WITHER_SKELETON_SKULL)));
        $$1.put((Object)SkullBlock.Types.PLAYER, (Object)new SkullModel($$0.bakeLayer(ModelLayers.PLAYER_HEAD)));
        $$1.put((Object)SkullBlock.Types.ZOMBIE, (Object)new SkullModel($$0.bakeLayer(ModelLayers.ZOMBIE_HEAD)));
        $$1.put((Object)SkullBlock.Types.CREEPER, (Object)new SkullModel($$0.bakeLayer(ModelLayers.CREEPER_HEAD)));
        $$1.put((Object)SkullBlock.Types.DRAGON, (Object)new DragonHeadModel($$0.bakeLayer(ModelLayers.DRAGON_SKULL)));
        $$1.put((Object)SkullBlock.Types.PIGLIN, (Object)new PiglinHeadModel($$0.bakeLayer(ModelLayers.PIGLIN_HEAD)));
        return $$1.build();
    }

    public SkullBlockRenderer(BlockEntityRendererProvider.Context $$0) {
        this.modelByType = SkullBlockRenderer.createSkullRenderers($$0.getModelSet());
    }

    @Override
    public void render(SkullBlockEntity $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5) {
        float $$6 = $$0.getAnimation($$1);
        BlockState $$7 = $$0.getBlockState();
        boolean $$8 = $$7.getBlock() instanceof WallSkullBlock;
        Direction $$9 = $$8 ? $$7.getValue(WallSkullBlock.FACING) : null;
        int $$10 = $$8 ? RotationSegment.convertToSegment($$9) : $$7.getValue(SkullBlock.ROTATION);
        float $$11 = RotationSegment.convertToDegrees($$10);
        SkullBlock.Type $$12 = ((AbstractSkullBlock)$$7.getBlock()).getType();
        SkullModelBase $$13 = (SkullModelBase)this.modelByType.get((Object)$$12);
        RenderType $$14 = SkullBlockRenderer.getRenderType($$12, $$0.getOwnerProfile());
        SkullBlockRenderer.renderSkull($$9, $$11, $$6, $$2, $$3, $$4, $$13, $$14);
    }

    public static void renderSkull(@Nullable Direction $$0, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5, SkullModelBase $$6, RenderType $$7) {
        $$3.pushPose();
        if ($$0 == null) {
            $$3.translate(0.5f, 0.0f, 0.5f);
        } else {
            float $$8 = 0.25f;
            $$3.translate(0.5f - (float)$$0.getStepX() * 0.25f, 0.25f, 0.5f - (float)$$0.getStepZ() * 0.25f);
        }
        $$3.scale(-1.0f, -1.0f, 1.0f);
        VertexConsumer $$9 = $$4.getBuffer($$7);
        $$6.setupAnim($$2, $$1, 0.0f);
        $$6.renderToBuffer($$3, $$9, $$5, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        $$3.popPose();
    }

    public static RenderType getRenderType(SkullBlock.Type $$0, @Nullable GameProfile $$1) {
        ResourceLocation $$2 = (ResourceLocation)SKIN_BY_TYPE.get((Object)$$0);
        if ($$0 != SkullBlock.Types.PLAYER || $$1 == null) {
            return RenderType.entityCutoutNoCullZOffset($$2);
        }
        Minecraft $$3 = Minecraft.getInstance();
        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> $$4 = $$3.getSkinManager().getInsecureSkinInformation($$1);
        if ($$4.containsKey((Object)MinecraftProfileTexture.Type.SKIN)) {
            return RenderType.entityTranslucent($$3.getSkinManager().registerTexture((MinecraftProfileTexture)$$4.get((Object)MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN));
        }
        return RenderType.entityCutoutNoCull(DefaultPlayerSkin.getDefaultSkin(UUIDUtil.getOrCreatePlayerUUID($$1)));
    }
}