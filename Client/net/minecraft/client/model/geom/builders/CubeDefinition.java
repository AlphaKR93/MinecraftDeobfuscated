/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  javax.annotation.Nullable
 *  org.joml.Vector3f
 */
package net.minecraft.client.model.geom.builders;

import javax.annotation.Nullable;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.UVPair;
import org.joml.Vector3f;

public final class CubeDefinition {
    @Nullable
    private final String comment;
    private final Vector3f origin;
    private final Vector3f dimensions;
    private final CubeDeformation grow;
    private final boolean mirror;
    private final UVPair texCoord;
    private final UVPair texScale;

    protected CubeDefinition(@Nullable String $$0, float $$1, float $$2, float $$3, float $$4, float $$5, float $$6, float $$7, float $$8, CubeDeformation $$9, boolean $$10, float $$11, float $$12) {
        this.comment = $$0;
        this.texCoord = new UVPair($$1, $$2);
        this.origin = new Vector3f($$3, $$4, $$5);
        this.dimensions = new Vector3f($$6, $$7, $$8);
        this.grow = $$9;
        this.mirror = $$10;
        this.texScale = new UVPair($$11, $$12);
    }

    public ModelPart.Cube bake(int $$0, int $$1) {
        return new ModelPart.Cube((int)this.texCoord.u(), (int)this.texCoord.v(), this.origin.x(), this.origin.y(), this.origin.z(), this.dimensions.x(), this.dimensions.y(), this.dimensions.z(), this.grow.growX, this.grow.growY, this.grow.growZ, this.mirror, (float)$$0 * this.texScale.u(), (float)$$1 * this.texScale.v());
    }
}