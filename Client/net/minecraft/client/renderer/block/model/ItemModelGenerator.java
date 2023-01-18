/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.util.Either
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.HashMap
 *  java.util.List
 *  java.util.Map
 *  java.util.function.Function
 *  org.joml.Vector3f
 */
package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import org.joml.Vector3f;

public class ItemModelGenerator {
    public static final List<String> LAYERS = Lists.newArrayList((Object[])new String[]{"layer0", "layer1", "layer2", "layer3", "layer4"});
    private static final float MIN_Z = 7.5f;
    private static final float MAX_Z = 8.5f;

    public BlockModel generateBlockModel(Function<Material, TextureAtlasSprite> $$0, BlockModel $$1) {
        String $$5;
        HashMap $$2 = Maps.newHashMap();
        ArrayList $$3 = Lists.newArrayList();
        for (int $$4 = 0; $$4 < LAYERS.size() && $$1.hasTexture($$5 = (String)LAYERS.get($$4)); ++$$4) {
            Material $$6 = $$1.getMaterial($$5);
            $$2.put((Object)$$5, (Object)Either.left((Object)$$6));
            SpriteContents $$7 = ((TextureAtlasSprite)$$0.apply((Object)$$6)).contents();
            $$3.addAll(this.processFrames($$4, $$5, $$7));
        }
        $$2.put((Object)"particle", (Object)($$1.hasTexture("particle") ? Either.left((Object)$$1.getMaterial("particle")) : (Either)$$2.get((Object)"layer0")));
        BlockModel $$8 = new BlockModel(null, (List<BlockElement>)$$3, (Map<String, Either<Material, String>>)$$2, false, $$1.getGuiLight(), $$1.getTransforms(), $$1.getOverrides());
        $$8.name = $$1.name;
        return $$8;
    }

    private List<BlockElement> processFrames(int $$0, String $$1, SpriteContents $$2) {
        HashMap $$3 = Maps.newHashMap();
        $$3.put((Object)Direction.SOUTH, (Object)new BlockElementFace(null, $$0, $$1, new BlockFaceUV(new float[]{0.0f, 0.0f, 16.0f, 16.0f}, 0)));
        $$3.put((Object)Direction.NORTH, (Object)new BlockElementFace(null, $$0, $$1, new BlockFaceUV(new float[]{16.0f, 0.0f, 0.0f, 16.0f}, 0)));
        ArrayList $$4 = Lists.newArrayList();
        $$4.add((Object)new BlockElement(new Vector3f(0.0f, 0.0f, 7.5f), new Vector3f(16.0f, 16.0f, 8.5f), (Map<Direction, BlockElementFace>)$$3, null, true));
        $$4.addAll(this.createSideElements($$2, $$1, $$0));
        return $$4;
    }

    private List<BlockElement> createSideElements(SpriteContents $$0, String $$1, int $$2) {
        float $$3 = $$0.width();
        float $$4 = $$0.height();
        ArrayList $$5 = Lists.newArrayList();
        for (Span $$6 : this.getSpans($$0)) {
            float $$7 = 0.0f;
            float $$8 = 0.0f;
            float $$9 = 0.0f;
            float $$10 = 0.0f;
            float $$11 = 0.0f;
            float $$12 = 0.0f;
            float $$13 = 0.0f;
            float $$14 = 0.0f;
            float $$15 = 16.0f / $$3;
            float $$16 = 16.0f / $$4;
            float $$17 = $$6.getMin();
            float $$18 = $$6.getMax();
            float $$19 = $$6.getAnchor();
            SpanFacing $$20 = $$6.getFacing();
            switch ($$20) {
                case UP: {
                    $$7 = $$11 = $$17;
                    $$9 = $$12 = $$18 + 1.0f;
                    $$8 = $$13 = $$19;
                    $$10 = $$19;
                    $$14 = $$19 + 1.0f;
                    break;
                }
                case DOWN: {
                    $$13 = $$19;
                    $$14 = $$19 + 1.0f;
                    $$7 = $$11 = $$17;
                    $$9 = $$12 = $$18 + 1.0f;
                    $$8 = $$19 + 1.0f;
                    $$10 = $$19 + 1.0f;
                    break;
                }
                case LEFT: {
                    $$7 = $$11 = $$19;
                    $$9 = $$19;
                    $$12 = $$19 + 1.0f;
                    $$8 = $$14 = $$17;
                    $$10 = $$13 = $$18 + 1.0f;
                    break;
                }
                case RIGHT: {
                    $$11 = $$19;
                    $$12 = $$19 + 1.0f;
                    $$7 = $$19 + 1.0f;
                    $$9 = $$19 + 1.0f;
                    $$8 = $$14 = $$17;
                    $$10 = $$13 = $$18 + 1.0f;
                }
            }
            $$7 *= $$15;
            $$9 *= $$15;
            $$8 *= $$16;
            $$10 *= $$16;
            $$8 = 16.0f - $$8;
            $$10 = 16.0f - $$10;
            HashMap $$21 = Maps.newHashMap();
            $$21.put((Object)$$20.getDirection(), (Object)new BlockElementFace(null, $$2, $$1, new BlockFaceUV(new float[]{$$11 *= $$15, $$13 *= $$16, $$12 *= $$15, $$14 *= $$16}, 0)));
            switch ($$20) {
                case UP: {
                    $$5.add((Object)new BlockElement(new Vector3f($$7, $$8, 7.5f), new Vector3f($$9, $$8, 8.5f), (Map<Direction, BlockElementFace>)$$21, null, true));
                    break;
                }
                case DOWN: {
                    $$5.add((Object)new BlockElement(new Vector3f($$7, $$10, 7.5f), new Vector3f($$9, $$10, 8.5f), (Map<Direction, BlockElementFace>)$$21, null, true));
                    break;
                }
                case LEFT: {
                    $$5.add((Object)new BlockElement(new Vector3f($$7, $$8, 7.5f), new Vector3f($$7, $$10, 8.5f), (Map<Direction, BlockElementFace>)$$21, null, true));
                    break;
                }
                case RIGHT: {
                    $$5.add((Object)new BlockElement(new Vector3f($$9, $$8, 7.5f), new Vector3f($$9, $$10, 8.5f), (Map<Direction, BlockElementFace>)$$21, null, true));
                }
            }
        }
        return $$5;
    }

    private List<Span> getSpans(SpriteContents $$0) {
        int $$1 = $$0.width();
        int $$2 = $$0.height();
        ArrayList $$3 = Lists.newArrayList();
        $$0.getUniqueFrames().forEach(arg_0 -> this.lambda$getSpans$0($$2, $$1, $$0, (List)$$3, arg_0));
        return $$3;
    }

    private void checkTransition(SpanFacing $$0, List<Span> $$1, SpriteContents $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8) {
        boolean $$9;
        boolean bl = $$9 = this.isTransparent($$2, $$3, $$4 + $$0.getXOffset(), $$5 + $$0.getYOffset(), $$6, $$7) && $$8;
        if ($$9) {
            this.createOrExpandSpan($$1, $$0, $$4, $$5);
        }
    }

    private void createOrExpandSpan(List<Span> $$0, SpanFacing $$1, int $$2, int $$3) {
        int $$8;
        Span $$4 = null;
        for (Span $$5 : $$0) {
            int $$6;
            if ($$5.getFacing() != $$1) continue;
            int n = $$6 = $$1.isHorizontal() ? $$3 : $$2;
            if ($$5.getAnchor() != $$6) continue;
            $$4 = $$5;
            break;
        }
        int $$7 = $$1.isHorizontal() ? $$3 : $$2;
        int n = $$8 = $$1.isHorizontal() ? $$2 : $$3;
        if ($$4 == null) {
            $$0.add((Object)new Span($$1, $$8, $$7));
        } else {
            $$4.expand($$8);
        }
    }

    private boolean isTransparent(SpriteContents $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
        if ($$2 < 0 || $$3 < 0 || $$2 >= $$4 || $$3 >= $$5) {
            return true;
        }
        return $$0.isTransparent($$1, $$2, $$3);
    }

    private /* synthetic */ void lambda$getSpans$0(int $$0, int $$1, SpriteContents $$2, List $$3, int $$4) {
        for (int $$5 = 0; $$5 < $$0; ++$$5) {
            for (int $$6 = 0; $$6 < $$1; ++$$6) {
                boolean $$7 = !this.isTransparent($$2, $$4, $$6, $$5, $$1, $$0);
                this.checkTransition(SpanFacing.UP, (List<Span>)$$3, $$2, $$4, $$6, $$5, $$1, $$0, $$7);
                this.checkTransition(SpanFacing.DOWN, (List<Span>)$$3, $$2, $$4, $$6, $$5, $$1, $$0, $$7);
                this.checkTransition(SpanFacing.LEFT, (List<Span>)$$3, $$2, $$4, $$6, $$5, $$1, $$0, $$7);
                this.checkTransition(SpanFacing.RIGHT, (List<Span>)$$3, $$2, $$4, $$6, $$5, $$1, $$0, $$7);
            }
        }
    }

    static class Span {
        private final SpanFacing facing;
        private int min;
        private int max;
        private final int anchor;

        public Span(SpanFacing $$0, int $$1, int $$2) {
            this.facing = $$0;
            this.min = $$1;
            this.max = $$1;
            this.anchor = $$2;
        }

        public void expand(int $$0) {
            if ($$0 < this.min) {
                this.min = $$0;
            } else if ($$0 > this.max) {
                this.max = $$0;
            }
        }

        public SpanFacing getFacing() {
            return this.facing;
        }

        public int getMin() {
            return this.min;
        }

        public int getMax() {
            return this.max;
        }

        public int getAnchor() {
            return this.anchor;
        }
    }

    static enum SpanFacing {
        UP(Direction.UP, 0, -1),
        DOWN(Direction.DOWN, 0, 1),
        LEFT(Direction.EAST, -1, 0),
        RIGHT(Direction.WEST, 1, 0);

        private final Direction direction;
        private final int xOffset;
        private final int yOffset;

        private SpanFacing(Direction $$0, int $$1, int $$2) {
            this.direction = $$0;
            this.xOffset = $$1;
            this.yOffset = $$2;
        }

        public Direction getDirection() {
            return this.direction;
        }

        public int getXOffset() {
            return this.xOffset;
        }

        public int getYOffset() {
            return this.yOffset;
        }

        boolean isHorizontal() {
            return this == DOWN || this == UP;
        }
    }
}