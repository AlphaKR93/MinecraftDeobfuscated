/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.types.templates.List$ListType
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.HashMap
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Map
 *  java.util.function.Function
 *  java.util.stream.Stream
 */
package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.datafix.fixes.References;

public class ChunkBedBlockEntityInjecterFix
extends DataFix {
    public ChunkBedBlockEntityInjecterFix(Schema $$0, boolean $$1) {
        super($$0, $$1);
    }

    public TypeRewriteRule makeRule() {
        Type $$0 = this.getOutputSchema().getType(References.CHUNK);
        Type $$1 = $$0.findFieldType("Level");
        Type $$2 = $$1.findFieldType("TileEntities");
        if (!($$2 instanceof List.ListType)) {
            throw new IllegalStateException("Tile entity type is not a list type.");
        }
        List.ListType $$3 = (List.ListType)$$2;
        return this.cap($$1, $$3);
    }

    private <TE> TypeRewriteRule cap(Type<?> $$0, List.ListType<TE> $$1) {
        Type $$2 = $$1.getElement();
        OpticFinder $$32 = DSL.fieldFinder((String)"Level", $$0);
        OpticFinder $$4 = DSL.fieldFinder((String)"TileEntities", $$1);
        int $$5 = 416;
        return TypeRewriteRule.seq((TypeRewriteRule)this.fixTypeEverywhere("InjectBedBlockEntityType", (Type)this.getInputSchema().findChoiceType(References.BLOCK_ENTITY), (Type)this.getOutputSchema().findChoiceType(References.BLOCK_ENTITY), $$02 -> $$0 -> $$0), (TypeRewriteRule)this.fixTypeEverywhereTyped("BedBlockEntityInjecter", this.getOutputSchema().getType(References.CHUNK), $$3 -> {
            Typed $$4 = $$3.getTyped($$32);
            Dynamic $$5 = (Dynamic)$$4.get(DSL.remainderFinder());
            int $$6 = $$5.get("xPos").asInt(0);
            int $$7 = $$5.get("zPos").asInt(0);
            ArrayList $$8 = Lists.newArrayList((Iterable)((Iterable)$$4.getOrCreate($$4)));
            List $$9 = $$5.get("Sections").asList(Function.identity());
            for (int $$10 = 0; $$10 < $$9.size(); ++$$10) {
                Dynamic $$11 = (Dynamic)$$9.get($$10);
                int $$12 = $$11.get("Y").asInt(0);
                Stream $$13 = $$11.get("Blocks").asStream().map($$0 -> $$0.asInt(0));
                int $$14 = 0;
                Iterator iterator = ((Iterable)() -> ((Stream)$$13).iterator()).iterator();
                while (iterator.hasNext()) {
                    int $$15 = (Integer)iterator.next();
                    if (416 == ($$15 & 0xFF) << 4) {
                        int $$16 = $$14 & 0xF;
                        int $$17 = $$14 >> 8 & 0xF;
                        int $$18 = $$14 >> 4 & 0xF;
                        HashMap $$19 = Maps.newHashMap();
                        $$19.put((Object)$$11.createString("id"), (Object)$$11.createString("minecraft:bed"));
                        $$19.put((Object)$$11.createString("x"), (Object)$$11.createInt($$16 + ($$6 << 4)));
                        $$19.put((Object)$$11.createString("y"), (Object)$$11.createInt($$17 + ($$12 << 4)));
                        $$19.put((Object)$$11.createString("z"), (Object)$$11.createInt($$18 + ($$7 << 4)));
                        $$19.put((Object)$$11.createString("color"), (Object)$$11.createShort((short)14));
                        $$8.add(((Pair)$$2.read($$11.createMap((Map)$$19)).result().orElseThrow(() -> new IllegalStateException("Could not parse newly created bed block entity."))).getFirst());
                    }
                    ++$$14;
                }
            }
            if (!$$8.isEmpty()) {
                return $$3.set($$32, $$4.set($$4, (Object)$$8));
            }
            return $$3;
        }));
    }
}