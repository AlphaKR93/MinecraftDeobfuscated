/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.datafixers.util.Pair
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Arrays
 *  java.util.Comparator
 *  java.util.List
 *  java.util.Map
 *  java.util.function.Consumer
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.client.renderer;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.StringUtils;

public class BlockEntityWithoutLevelRenderer
implements ResourceManagerReloadListener {
    private static final ShulkerBoxBlockEntity[] SHULKER_BOXES = (ShulkerBoxBlockEntity[])Arrays.stream((Object[])DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map($$0 -> new ShulkerBoxBlockEntity((DyeColor)$$0, BlockPos.ZERO, Blocks.SHULKER_BOX.defaultBlockState())).toArray(ShulkerBoxBlockEntity[]::new);
    private static final ShulkerBoxBlockEntity DEFAULT_SHULKER_BOX = new ShulkerBoxBlockEntity(BlockPos.ZERO, Blocks.SHULKER_BOX.defaultBlockState());
    private final ChestBlockEntity chest = new ChestBlockEntity(BlockPos.ZERO, Blocks.CHEST.defaultBlockState());
    private final ChestBlockEntity trappedChest = new TrappedChestBlockEntity(BlockPos.ZERO, Blocks.TRAPPED_CHEST.defaultBlockState());
    private final EnderChestBlockEntity enderChest = new EnderChestBlockEntity(BlockPos.ZERO, Blocks.ENDER_CHEST.defaultBlockState());
    private final BannerBlockEntity banner = new BannerBlockEntity(BlockPos.ZERO, Blocks.WHITE_BANNER.defaultBlockState());
    private final BedBlockEntity bed = new BedBlockEntity(BlockPos.ZERO, Blocks.RED_BED.defaultBlockState());
    private final ConduitBlockEntity conduit = new ConduitBlockEntity(BlockPos.ZERO, Blocks.CONDUIT.defaultBlockState());
    private ShieldModel shieldModel;
    private TridentModel tridentModel;
    private Map<SkullBlock.Type, SkullModelBase> skullModels;
    private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
    private final EntityModelSet entityModelSet;

    public BlockEntityWithoutLevelRenderer(BlockEntityRenderDispatcher $$0, EntityModelSet $$1) {
        this.blockEntityRenderDispatcher = $$0;
        this.entityModelSet = $$1;
    }

    @Override
    public void onResourceManagerReload(ResourceManager $$0) {
        this.shieldModel = new ShieldModel(this.entityModelSet.bakeLayer(ModelLayers.SHIELD));
        this.tridentModel = new TridentModel(this.entityModelSet.bakeLayer(ModelLayers.TRIDENT));
        this.skullModels = SkullBlockRenderer.createSkullRenderers(this.entityModelSet);
    }

    /*
     * WARNING - void declaration
     */
    public void renderByItem(ItemStack $$0, ItemTransforms.TransformType $$12, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5) {
        Item $$6 = $$0.getItem();
        if ($$6 instanceof BlockItem) {
            void $$23;
            Block $$7 = ((BlockItem)$$6).getBlock();
            if ($$7 instanceof AbstractSkullBlock) {
                GameProfile $$8 = null;
                if ($$0.hasTag()) {
                    CompoundTag $$9 = $$0.getTag();
                    if ($$9.contains("SkullOwner", 10)) {
                        $$8 = NbtUtils.readGameProfile($$9.getCompound("SkullOwner"));
                    } else if ($$9.contains("SkullOwner", 8) && !StringUtils.isBlank((CharSequence)$$9.getString("SkullOwner"))) {
                        $$8 = new GameProfile(null, $$9.getString("SkullOwner"));
                        $$9.remove("SkullOwner");
                        SkullBlockEntity.updateGameprofile($$8, (Consumer<GameProfile>)((Consumer)$$1 -> $$9.put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), $$1))));
                    }
                }
                SkullBlock.Type $$10 = ((AbstractSkullBlock)$$7).getType();
                SkullModelBase $$11 = (SkullModelBase)this.skullModels.get((Object)$$10);
                RenderType $$122 = SkullBlockRenderer.getRenderType($$10, $$8);
                SkullBlockRenderer.renderSkull(null, 180.0f, 0.0f, $$2, $$3, $$4, $$11, $$122);
                return;
            }
            BlockState $$13 = $$7.defaultBlockState();
            if ($$7 instanceof AbstractBannerBlock) {
                this.banner.fromItem($$0, ((AbstractBannerBlock)$$7).getColor());
                BannerBlockEntity $$14 = this.banner;
            } else if ($$7 instanceof BedBlock) {
                this.bed.setColor(((BedBlock)$$7).getColor());
                BedBlockEntity $$15 = this.bed;
            } else if ($$13.is(Blocks.CONDUIT)) {
                ConduitBlockEntity $$16 = this.conduit;
            } else if ($$13.is(Blocks.CHEST)) {
                ChestBlockEntity $$17 = this.chest;
            } else if ($$13.is(Blocks.ENDER_CHEST)) {
                EnderChestBlockEntity $$18 = this.enderChest;
            } else if ($$13.is(Blocks.TRAPPED_CHEST)) {
                ChestBlockEntity $$19 = this.trappedChest;
            } else if ($$7 instanceof ShulkerBoxBlock) {
                DyeColor $$20 = ShulkerBoxBlock.getColorFromItem($$6);
                if ($$20 == null) {
                    ShulkerBoxBlockEntity $$21 = DEFAULT_SHULKER_BOX;
                } else {
                    ShulkerBoxBlockEntity $$22 = SHULKER_BOXES[$$20.getId()];
                }
            } else {
                return;
            }
            this.blockEntityRenderDispatcher.renderItem($$23, $$2, $$3, $$4, $$5);
            return;
        }
        if ($$0.is(Items.SHIELD)) {
            boolean $$24 = BlockItem.getBlockEntityData($$0) != null;
            $$2.pushPose();
            $$2.scale(1.0f, -1.0f, -1.0f);
            Material $$25 = $$24 ? ModelBakery.SHIELD_BASE : ModelBakery.NO_PATTERN_SHIELD;
            VertexConsumer $$26 = $$25.sprite().wrap(ItemRenderer.getFoilBufferDirect($$3, this.shieldModel.renderType($$25.atlasLocation()), true, $$0.hasFoil()));
            this.shieldModel.handle().render($$2, $$26, $$4, $$5, 1.0f, 1.0f, 1.0f, 1.0f);
            if ($$24) {
                List<Pair<Holder<BannerPattern>, DyeColor>> $$27 = BannerBlockEntity.createPatterns(ShieldItem.getColor($$0), BannerBlockEntity.getItemPatterns($$0));
                BannerRenderer.renderPatterns($$2, $$3, $$4, $$5, this.shieldModel.plate(), $$25, false, $$27, $$0.hasFoil());
            } else {
                this.shieldModel.plate().render($$2, $$26, $$4, $$5, 1.0f, 1.0f, 1.0f, 1.0f);
            }
            $$2.popPose();
        } else if ($$0.is(Items.TRIDENT)) {
            $$2.pushPose();
            $$2.scale(1.0f, -1.0f, -1.0f);
            VertexConsumer $$28 = ItemRenderer.getFoilBufferDirect($$3, this.tridentModel.renderType(TridentModel.TEXTURE), false, $$0.hasFoil());
            this.tridentModel.renderToBuffer($$2, $$28, $$4, $$5, 1.0f, 1.0f, 1.0f, 1.0f);
            $$2.popPose();
        }
    }
}