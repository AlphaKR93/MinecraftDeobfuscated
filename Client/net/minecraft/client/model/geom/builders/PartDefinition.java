/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.stream.Collectors
 */
package net.minecraft.client.model.geom.builders;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.CubeListBuilder;

public class PartDefinition {
    private final List<CubeDefinition> cubes;
    private final PartPose partPose;
    private final Map<String, PartDefinition> children = Maps.newHashMap();

    PartDefinition(List<CubeDefinition> $$0, PartPose $$1) {
        this.cubes = $$0;
        this.partPose = $$1;
    }

    public PartDefinition addOrReplaceChild(String $$0, CubeListBuilder $$1, PartPose $$2) {
        PartDefinition $$3 = new PartDefinition($$1.getCubes(), $$2);
        PartDefinition $$4 = (PartDefinition)this.children.put((Object)$$0, (Object)$$3);
        if ($$4 != null) {
            $$3.children.putAll($$4.children);
        }
        return $$3;
    }

    public ModelPart bake(int $$02, int $$12) {
        Object2ObjectArrayMap $$22 = (Object2ObjectArrayMap)this.children.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, $$2 -> ((PartDefinition)$$2.getValue()).bake($$02, $$12), ($$0, $$1) -> $$0, Object2ObjectArrayMap::new));
        List $$3 = (List)this.cubes.stream().map($$2 -> $$2.bake($$02, $$12)).collect(ImmutableList.toImmutableList());
        ModelPart $$4 = new ModelPart((List<ModelPart.Cube>)$$3, (Map<String, ModelPart>)$$22);
        $$4.setInitialPose(this.partPose);
        $$4.loadPose(this.partPose);
        return $$4;
    }

    public PartDefinition getChild(String $$0) {
        return (PartDefinition)this.children.get((Object)$$0);
    }
}