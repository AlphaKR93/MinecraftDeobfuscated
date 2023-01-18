/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.Optional
 */
package net.minecraft.world.level.block.entity;

import java.util.Arrays;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class JigsawBlockEntity
extends BlockEntity {
    public static final String TARGET = "target";
    public static final String POOL = "pool";
    public static final String JOINT = "joint";
    public static final String NAME = "name";
    public static final String FINAL_STATE = "final_state";
    private ResourceLocation name = new ResourceLocation("empty");
    private ResourceLocation target = new ResourceLocation("empty");
    private ResourceKey<StructureTemplatePool> pool = ResourceKey.create(Registries.TEMPLATE_POOL, new ResourceLocation("empty"));
    private JointType joint = JointType.ROLLABLE;
    private String finalState = "minecraft:air";

    public JigsawBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.JIGSAW, $$0, $$1);
    }

    public ResourceLocation getName() {
        return this.name;
    }

    public ResourceLocation getTarget() {
        return this.target;
    }

    public ResourceKey<StructureTemplatePool> getPool() {
        return this.pool;
    }

    public String getFinalState() {
        return this.finalState;
    }

    public JointType getJoint() {
        return this.joint;
    }

    public void setName(ResourceLocation $$0) {
        this.name = $$0;
    }

    public void setTarget(ResourceLocation $$0) {
        this.target = $$0;
    }

    public void setPool(ResourceKey<StructureTemplatePool> $$0) {
        this.pool = $$0;
    }

    public void setFinalState(String $$0) {
        this.finalState = $$0;
    }

    public void setJoint(JointType $$0) {
        this.joint = $$0;
    }

    @Override
    protected void saveAdditional(CompoundTag $$0) {
        super.saveAdditional($$0);
        $$0.putString(NAME, this.name.toString());
        $$0.putString(TARGET, this.target.toString());
        $$0.putString(POOL, this.pool.location().toString());
        $$0.putString(FINAL_STATE, this.finalState);
        $$0.putString(JOINT, this.joint.getSerializedName());
    }

    @Override
    public void load(CompoundTag $$0) {
        super.load($$0);
        this.name = new ResourceLocation($$0.getString(NAME));
        this.target = new ResourceLocation($$0.getString(TARGET));
        this.pool = ResourceKey.create(Registries.TEMPLATE_POOL, new ResourceLocation($$0.getString(POOL)));
        this.finalState = $$0.getString(FINAL_STATE);
        this.joint = (JointType)JointType.byName($$0.getString(JOINT)).orElseGet(() -> JigsawBlock.getFrontFacing(this.getBlockState()).getAxis().isHorizontal() ? JointType.ALIGNED : JointType.ROLLABLE);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    public void generate(ServerLevel $$0, int $$1, boolean $$2) {
        Vec3i $$3 = this.getBlockPos().relative(this.getBlockState().getValue(JigsawBlock.ORIENTATION).front());
        Registry<StructureTemplatePool> $$4 = $$0.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL);
        Holder.Reference<StructureTemplatePool> $$5 = $$4.getHolderOrThrow(this.pool);
        JigsawPlacement.generateJigsaw($$0, $$5, this.target, $$1, (BlockPos)$$3, $$2);
    }

    public static enum JointType implements StringRepresentable
    {
        ROLLABLE("rollable"),
        ALIGNED("aligned");

        private final String name;

        private JointType(String $$0) {
            this.name = $$0;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public static Optional<JointType> byName(String $$0) {
            return Arrays.stream((Object[])JointType.values()).filter($$1 -> $$1.getSerializedName().equals((Object)$$0)).findFirst();
        }

        public Component getTranslatedName() {
            return Component.translatable("jigsaw_block.joint." + this.name);
        }
    }
}