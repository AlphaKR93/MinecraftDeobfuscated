/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  java.lang.Object
 *  java.util.List
 */
package net.minecraft.client.model.geom.builders;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class MeshDefinition {
    private final PartDefinition root = new PartDefinition((List<CubeDefinition>)ImmutableList.of(), PartPose.ZERO);

    public PartDefinition getRoot() {
        return this.root;
    }
}