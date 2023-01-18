/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Map;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;

public class CustomHeadLayer<T extends LivingEntity, M extends EntityModel<T>>
extends RenderLayer<T, M> {
    private final float scaleX;
    private final float scaleY;
    private final float scaleZ;
    private final Map<SkullBlock.Type, SkullModelBase> skullModels;
    private final ItemInHandRenderer itemInHandRenderer;

    public CustomHeadLayer(RenderLayerParent<T, M> $$0, EntityModelSet $$1, ItemInHandRenderer $$2) {
        this($$0, $$1, 1.0f, 1.0f, 1.0f, $$2);
    }

    public CustomHeadLayer(RenderLayerParent<T, M> $$0, EntityModelSet $$1, float $$2, float $$3, float $$4, ItemInHandRenderer $$5) {
        super($$0);
        this.scaleX = $$2;
        this.scaleY = $$3;
        this.scaleZ = $$4;
        this.skullModels = SkullBlockRenderer.createSkullRenderers($$1);
        this.itemInHandRenderer = $$5;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, T $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9) {
        boolean $$12;
        ItemStack $$10 = ((LivingEntity)$$3).getItemBySlot(EquipmentSlot.HEAD);
        if ($$10.isEmpty()) {
            return;
        }
        Item $$11 = $$10.getItem();
        $$0.pushPose();
        $$0.scale(this.scaleX, this.scaleY, this.scaleZ);
        boolean bl = $$12 = $$3 instanceof Villager || $$3 instanceof ZombieVillager;
        if (((LivingEntity)$$3).isBaby() && !($$3 instanceof Villager)) {
            float $$13 = 2.0f;
            float $$14 = 1.4f;
            $$0.translate(0.0f, 0.03125f, 0.0f);
            $$0.scale(0.7f, 0.7f, 0.7f);
            $$0.translate(0.0f, 1.0f, 0.0f);
        }
        ((HeadedModel)this.getParentModel()).getHead().translateAndRotate($$0);
        if ($$11 instanceof BlockItem && ((BlockItem)$$11).getBlock() instanceof AbstractSkullBlock) {
            CompoundTag $$17;
            float $$15 = 1.1875f;
            $$0.scale(1.1875f, -1.1875f, -1.1875f);
            if ($$12) {
                $$0.translate(0.0f, 0.0625f, 0.0f);
            }
            GameProfile $$16 = null;
            if ($$10.hasTag() && ($$17 = $$10.getTag()).contains("SkullOwner", 10)) {
                $$16 = NbtUtils.readGameProfile($$17.getCompound("SkullOwner"));
            }
            $$0.translate(-0.5, 0.0, -0.5);
            SkullBlock.Type $$18 = ((AbstractSkullBlock)((BlockItem)$$11).getBlock()).getType();
            SkullModelBase $$19 = (SkullModelBase)this.skullModels.get((Object)$$18);
            RenderType $$20 = SkullBlockRenderer.getRenderType($$18, $$16);
            SkullBlockRenderer.renderSkull(null, 180.0f, $$4, $$0, $$1, $$2, $$19, $$20);
        } else if (!($$11 instanceof ArmorItem) || ((ArmorItem)$$11).getSlot() != EquipmentSlot.HEAD) {
            CustomHeadLayer.translateToHead($$0, $$12);
            this.itemInHandRenderer.renderItem((LivingEntity)$$3, $$10, ItemTransforms.TransformType.HEAD, false, $$0, $$1, $$2);
        }
        $$0.popPose();
    }

    public static void translateToHead(PoseStack $$0, boolean $$1) {
        float $$2 = 0.625f;
        $$0.translate(0.0f, -0.25f, 0.0f);
        $$0.mulPose(Axis.YP.rotationDegrees(180.0f));
        $$0.scale(0.625f, -0.625f, -0.625f);
        if ($$1) {
            $$0.translate(0.0f, 0.1875f, 0.0f);
        }
    }
}