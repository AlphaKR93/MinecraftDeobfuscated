/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  java.lang.Float
 *  java.lang.Object
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Objects
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemOverrides {
    public static final ItemOverrides EMPTY = new ItemOverrides();
    public static final float NO_OVERRIDE = Float.NEGATIVE_INFINITY;
    private final BakedOverride[] overrides;
    private final ResourceLocation[] properties;

    private ItemOverrides() {
        this.overrides = new BakedOverride[0];
        this.properties = new ResourceLocation[0];
    }

    public ItemOverrides(ModelBaker $$0, BlockModel $$1, List<ItemOverride> $$2) {
        this.properties = (ResourceLocation[])$$2.stream().flatMap(ItemOverride::getPredicates).map(ItemOverride.Predicate::getProperty).distinct().toArray(ResourceLocation[]::new);
        Object2IntOpenHashMap $$3 = new Object2IntOpenHashMap();
        for (int $$4 = 0; $$4 < this.properties.length; ++$$4) {
            $$3.put((Object)this.properties[$$4], $$4);
        }
        ArrayList $$5 = Lists.newArrayList();
        for (int $$6 = $$2.size() - 1; $$6 >= 0; --$$6) {
            ItemOverride $$7 = (ItemOverride)$$2.get($$6);
            BakedModel $$8 = this.bakeModel($$0, $$1, $$7);
            PropertyMatcher[] $$9 = (PropertyMatcher[])$$7.getPredicates().map(arg_0 -> ItemOverrides.lambda$new$1((Object2IntMap)$$3, arg_0)).toArray(PropertyMatcher[]::new);
            $$5.add((Object)new BakedOverride($$9, $$8));
        }
        this.overrides = (BakedOverride[])$$5.toArray((Object[])new BakedOverride[0]);
    }

    @Nullable
    private BakedModel bakeModel(ModelBaker $$0, BlockModel $$1, ItemOverride $$2) {
        UnbakedModel $$3 = $$0.getModel($$2.getModel());
        if (Objects.equals((Object)$$3, (Object)$$1)) {
            return null;
        }
        return $$0.bake($$2.getModel(), BlockModelRotation.X0_Y0);
    }

    @Nullable
    public BakedModel resolve(BakedModel $$0, ItemStack $$1, @Nullable ClientLevel $$2, @Nullable LivingEntity $$3, int $$4) {
        if (this.overrides.length != 0) {
            Item $$5 = $$1.getItem();
            int $$6 = this.properties.length;
            float[] $$7 = new float[$$6];
            for (int $$8 = 0; $$8 < $$6; ++$$8) {
                ResourceLocation $$9 = this.properties[$$8];
                ItemPropertyFunction $$10 = ItemProperties.getProperty($$5, $$9);
                $$7[$$8] = $$10 != null ? $$10.call($$1, $$2, $$3, $$4) : Float.NEGATIVE_INFINITY;
            }
            for (BakedOverride $$11 : this.overrides) {
                if (!$$11.test($$7)) continue;
                BakedModel $$12 = $$11.model;
                if ($$12 == null) {
                    return $$0;
                }
                return $$12;
            }
        }
        return $$0;
    }

    private static /* synthetic */ PropertyMatcher lambda$new$1(Object2IntMap $$0, ItemOverride.Predicate $$1) {
        int $$2 = $$0.getInt((Object)$$1.getProperty());
        return new PropertyMatcher($$2, $$1.getValue());
    }

    static class BakedOverride {
        private final PropertyMatcher[] matchers;
        @Nullable
        final BakedModel model;

        BakedOverride(PropertyMatcher[] $$0, @Nullable BakedModel $$1) {
            this.matchers = $$0;
            this.model = $$1;
        }

        boolean test(float[] $$0) {
            for (PropertyMatcher $$1 : this.matchers) {
                float $$2 = $$0[$$1.index];
                if (!($$2 < $$1.value)) continue;
                return false;
            }
            return true;
        }
    }

    static class PropertyMatcher {
        public final int index;
        public final float value;

        PropertyMatcher(int $$0, float $$1) {
            this.index = $$0;
            this.value = $$1;
        }
    }
}