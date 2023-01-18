/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 */
package net.minecraft.client.model.geom.builders;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.CubeDeformation;

public class CubeListBuilder {
    private final List<CubeDefinition> cubes = Lists.newArrayList();
    private int xTexOffs;
    private int yTexOffs;
    private boolean mirror;

    public CubeListBuilder texOffs(int $$0, int $$1) {
        this.xTexOffs = $$0;
        this.yTexOffs = $$1;
        return this;
    }

    public CubeListBuilder mirror() {
        return this.mirror(true);
    }

    public CubeListBuilder mirror(boolean $$0) {
        this.mirror = $$0;
        return this;
    }

    public CubeListBuilder addBox(String $$0, float $$1, float $$2, float $$3, int $$4, int $$5, int $$6, CubeDeformation $$7, int $$8, int $$9) {
        this.texOffs($$8, $$9);
        this.cubes.add((Object)new CubeDefinition($$0, this.xTexOffs, this.yTexOffs, $$1, $$2, $$3, $$4, $$5, $$6, $$7, this.mirror, 1.0f, 1.0f));
        return this;
    }

    public CubeListBuilder addBox(String $$0, float $$1, float $$2, float $$3, int $$4, int $$5, int $$6, int $$7, int $$8) {
        this.texOffs($$7, $$8);
        this.cubes.add((Object)new CubeDefinition($$0, this.xTexOffs, this.yTexOffs, $$1, $$2, $$3, $$4, $$5, $$6, CubeDeformation.NONE, this.mirror, 1.0f, 1.0f));
        return this;
    }

    public CubeListBuilder addBox(float $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        this.cubes.add((Object)new CubeDefinition(null, this.xTexOffs, this.yTexOffs, $$0, $$1, $$2, $$3, $$4, $$5, CubeDeformation.NONE, this.mirror, 1.0f, 1.0f));
        return this;
    }

    public CubeListBuilder addBox(String $$0, float $$1, float $$2, float $$3, float $$4, float $$5, float $$6) {
        this.cubes.add((Object)new CubeDefinition($$0, this.xTexOffs, this.yTexOffs, $$1, $$2, $$3, $$4, $$5, $$6, CubeDeformation.NONE, this.mirror, 1.0f, 1.0f));
        return this;
    }

    public CubeListBuilder addBox(String $$0, float $$1, float $$2, float $$3, float $$4, float $$5, float $$6, CubeDeformation $$7) {
        this.cubes.add((Object)new CubeDefinition($$0, this.xTexOffs, this.yTexOffs, $$1, $$2, $$3, $$4, $$5, $$6, $$7, this.mirror, 1.0f, 1.0f));
        return this;
    }

    public CubeListBuilder addBox(float $$0, float $$1, float $$2, float $$3, float $$4, float $$5, boolean $$6) {
        this.cubes.add((Object)new CubeDefinition(null, this.xTexOffs, this.yTexOffs, $$0, $$1, $$2, $$3, $$4, $$5, CubeDeformation.NONE, $$6, 1.0f, 1.0f));
        return this;
    }

    public CubeListBuilder addBox(float $$0, float $$1, float $$2, float $$3, float $$4, float $$5, CubeDeformation $$6, float $$7, float $$8) {
        this.cubes.add((Object)new CubeDefinition(null, this.xTexOffs, this.yTexOffs, $$0, $$1, $$2, $$3, $$4, $$5, $$6, this.mirror, $$7, $$8));
        return this;
    }

    public CubeListBuilder addBox(float $$0, float $$1, float $$2, float $$3, float $$4, float $$5, CubeDeformation $$6) {
        this.cubes.add((Object)new CubeDefinition(null, this.xTexOffs, this.yTexOffs, $$0, $$1, $$2, $$3, $$4, $$5, $$6, this.mirror, 1.0f, 1.0f));
        return this;
    }

    public List<CubeDefinition> getCubes() {
        return ImmutableList.copyOf(this.cubes);
    }

    public static CubeListBuilder create() {
        return new CubeListBuilder();
    }
}