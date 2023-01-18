/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  java.lang.Boolean
 *  java.lang.Comparable
 *  java.lang.Integer
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.nio.file.Path
 *  java.util.concurrent.CompletableFuture
 */
package net.minecraft.data.info;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockListReport
implements DataProvider {
    private final PackOutput output;

    public BlockListReport(PackOutput $$0) {
        this.output = $$0;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput $$0) {
        JsonObject $$1 = new JsonObject();
        for (Block $$2 : BuiltInRegistries.BLOCK) {
            ResourceLocation $$3 = BuiltInRegistries.BLOCK.getKey($$2);
            JsonObject $$4 = new JsonObject();
            StateDefinition<Block, BlockState> $$5 = $$2.getStateDefinition();
            if (!$$5.getProperties().isEmpty()) {
                JsonObject $$6 = new JsonObject();
                for (Property $$7 : $$5.getProperties()) {
                    JsonArray $$8 = new JsonArray();
                    for (Comparable $$9 : $$7.getPossibleValues()) {
                        $$8.add(Util.getPropertyName($$7, $$9));
                    }
                    $$6.add($$7.getName(), (JsonElement)$$8);
                }
                $$4.add("properties", (JsonElement)$$6);
            }
            JsonArray $$10 = new JsonArray();
            for (BlockState $$11 : $$5.getPossibleStates()) {
                JsonObject $$12 = new JsonObject();
                JsonObject $$13 = new JsonObject();
                for (Property $$14 : $$5.getProperties()) {
                    $$13.addProperty($$14.getName(), Util.getPropertyName($$14, $$11.getValue($$14)));
                }
                if ($$13.size() > 0) {
                    $$12.add("properties", (JsonElement)$$13);
                }
                $$12.addProperty("id", (Number)Integer.valueOf((int)Block.getId($$11)));
                if ($$11 == $$2.defaultBlockState()) {
                    $$12.addProperty("default", Boolean.valueOf((boolean)true));
                }
                $$10.add((JsonElement)$$12);
            }
            $$4.add("states", (JsonElement)$$10);
            $$1.add($$3.toString(), (JsonElement)$$4);
        }
        Path $$15 = this.output.getOutputFolder(PackOutput.Target.REPORTS).resolve("blocks.json");
        return DataProvider.saveStable($$0, (JsonElement)$$1, $$15);
    }

    @Override
    public final String getName() {
        return "Block List";
    }
}