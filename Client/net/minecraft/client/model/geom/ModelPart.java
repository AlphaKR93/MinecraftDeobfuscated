/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 *  java.util.Map
 *  java.util.NoSuchElementException
 *  java.util.stream.Stream
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 */
package net.minecraft.client.model.geom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;

public final class ModelPart {
    public static final float DEFAULT_SCALE = 1.0f;
    public float x;
    public float y;
    public float z;
    public float xRot;
    public float yRot;
    public float zRot;
    public float xScale = 1.0f;
    public float yScale = 1.0f;
    public float zScale = 1.0f;
    public boolean visible = true;
    public boolean skipDraw;
    private final List<Cube> cubes;
    private final Map<String, ModelPart> children;
    private PartPose initialPose = PartPose.ZERO;

    public ModelPart(List<Cube> $$0, Map<String, ModelPart> $$1) {
        this.cubes = $$0;
        this.children = $$1;
    }

    public PartPose storePose() {
        return PartPose.offsetAndRotation(this.x, this.y, this.z, this.xRot, this.yRot, this.zRot);
    }

    public PartPose getInitialPose() {
        return this.initialPose;
    }

    public void setInitialPose(PartPose $$0) {
        this.initialPose = $$0;
    }

    public void resetPose() {
        this.loadPose(this.initialPose);
    }

    public void loadPose(PartPose $$0) {
        this.x = $$0.x;
        this.y = $$0.y;
        this.z = $$0.z;
        this.xRot = $$0.xRot;
        this.yRot = $$0.yRot;
        this.zRot = $$0.zRot;
        this.xScale = 1.0f;
        this.yScale = 1.0f;
        this.zScale = 1.0f;
    }

    public void copyFrom(ModelPart $$0) {
        this.xScale = $$0.xScale;
        this.yScale = $$0.yScale;
        this.zScale = $$0.zScale;
        this.xRot = $$0.xRot;
        this.yRot = $$0.yRot;
        this.zRot = $$0.zRot;
        this.x = $$0.x;
        this.y = $$0.y;
        this.z = $$0.z;
    }

    public boolean hasChild(String $$0) {
        return this.children.containsKey((Object)$$0);
    }

    public ModelPart getChild(String $$0) {
        ModelPart $$1 = (ModelPart)this.children.get((Object)$$0);
        if ($$1 == null) {
            throw new NoSuchElementException("Can't find part " + $$0);
        }
        return $$1;
    }

    public void setPos(float $$0, float $$1, float $$2) {
        this.x = $$0;
        this.y = $$1;
        this.z = $$2;
    }

    public void setRotation(float $$0, float $$1, float $$2) {
        this.xRot = $$0;
        this.yRot = $$1;
        this.zRot = $$2;
    }

    public void render(PoseStack $$0, VertexConsumer $$1, int $$2, int $$3) {
        this.render($$0, $$1, $$2, $$3, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void render(PoseStack $$0, VertexConsumer $$1, int $$2, int $$3, float $$4, float $$5, float $$6, float $$7) {
        if (!this.visible) {
            return;
        }
        if (this.cubes.isEmpty() && this.children.isEmpty()) {
            return;
        }
        $$0.pushPose();
        this.translateAndRotate($$0);
        if (!this.skipDraw) {
            this.compile($$0.last(), $$1, $$2, $$3, $$4, $$5, $$6, $$7);
        }
        for (ModelPart $$8 : this.children.values()) {
            $$8.render($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
        }
        $$0.popPose();
    }

    public void visit(PoseStack $$0, Visitor $$1) {
        this.visit($$0, $$1, "");
    }

    private void visit(PoseStack $$0, Visitor $$1, String $$2) {
        if (this.cubes.isEmpty() && this.children.isEmpty()) {
            return;
        }
        $$0.pushPose();
        this.translateAndRotate($$0);
        PoseStack.Pose $$32 = $$0.last();
        for (int $$42 = 0; $$42 < this.cubes.size(); ++$$42) {
            $$1.visit($$32, $$2, $$42, (Cube)this.cubes.get($$42));
        }
        String $$5 = $$2 + "/";
        this.children.forEach(($$3, $$4) -> $$4.visit($$0, $$1, $$5 + $$3));
        $$0.popPose();
    }

    public void translateAndRotate(PoseStack $$0) {
        $$0.translate(this.x / 16.0f, this.y / 16.0f, this.z / 16.0f);
        if (this.xRot != 0.0f || this.yRot != 0.0f || this.zRot != 0.0f) {
            $$0.mulPose(new Quaternionf().rotationZYX(this.zRot, this.yRot, this.xRot));
        }
        if (this.xScale != 1.0f || this.yScale != 1.0f || this.zScale != 1.0f) {
            $$0.scale(this.xScale, this.yScale, this.zScale);
        }
    }

    private void compile(PoseStack.Pose $$0, VertexConsumer $$1, int $$2, int $$3, float $$4, float $$5, float $$6, float $$7) {
        for (Cube $$8 : this.cubes) {
            $$8.compile($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
        }
    }

    public Cube getRandomCube(RandomSource $$0) {
        return (Cube)this.cubes.get($$0.nextInt(this.cubes.size()));
    }

    public boolean isEmpty() {
        return this.cubes.isEmpty();
    }

    public void offsetPos(Vector3f $$0) {
        this.x += $$0.x();
        this.y += $$0.y();
        this.z += $$0.z();
    }

    public void offsetRotation(Vector3f $$0) {
        this.xRot += $$0.x();
        this.yRot += $$0.y();
        this.zRot += $$0.z();
    }

    public void offsetScale(Vector3f $$0) {
        this.xScale += $$0.x();
        this.yScale += $$0.y();
        this.zScale += $$0.z();
    }

    public Stream<ModelPart> getAllParts() {
        return Stream.concat((Stream)Stream.of((Object)this), (Stream)this.children.values().stream().flatMap(ModelPart::getAllParts));
    }

    @FunctionalInterface
    public static interface Visitor {
        public void visit(PoseStack.Pose var1, String var2, int var3, Cube var4);
    }

    public static class Cube {
        private final Polygon[] polygons;
        public final float minX;
        public final float minY;
        public final float minZ;
        public final float maxX;
        public final float maxY;
        public final float maxZ;

        public Cube(int $$0, int $$1, float $$2, float $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9, float $$10, boolean $$11, float $$12, float $$13) {
            this.minX = $$2;
            this.minY = $$3;
            this.minZ = $$4;
            this.maxX = $$2 + $$5;
            this.maxY = $$3 + $$6;
            this.maxZ = $$4 + $$7;
            this.polygons = new Polygon[6];
            float $$14 = $$2 + $$5;
            float $$15 = $$3 + $$6;
            float $$16 = $$4 + $$7;
            $$2 -= $$8;
            $$3 -= $$9;
            $$4 -= $$10;
            $$14 += $$8;
            $$15 += $$9;
            $$16 += $$10;
            if ($$11) {
                float $$17 = $$14;
                $$14 = $$2;
                $$2 = $$17;
            }
            Vertex $$18 = new Vertex($$2, $$3, $$4, 0.0f, 0.0f);
            Vertex $$19 = new Vertex($$14, $$3, $$4, 0.0f, 8.0f);
            Vertex $$20 = new Vertex($$14, $$15, $$4, 8.0f, 8.0f);
            Vertex $$21 = new Vertex($$2, $$15, $$4, 8.0f, 0.0f);
            Vertex $$22 = new Vertex($$2, $$3, $$16, 0.0f, 0.0f);
            Vertex $$23 = new Vertex($$14, $$3, $$16, 0.0f, 8.0f);
            Vertex $$24 = new Vertex($$14, $$15, $$16, 8.0f, 8.0f);
            Vertex $$25 = new Vertex($$2, $$15, $$16, 8.0f, 0.0f);
            float $$26 = $$0;
            float $$27 = (float)$$0 + $$7;
            float $$28 = (float)$$0 + $$7 + $$5;
            float $$29 = (float)$$0 + $$7 + $$5 + $$5;
            float $$30 = (float)$$0 + $$7 + $$5 + $$7;
            float $$31 = (float)$$0 + $$7 + $$5 + $$7 + $$5;
            float $$32 = $$1;
            float $$33 = (float)$$1 + $$7;
            float $$34 = (float)$$1 + $$7 + $$6;
            this.polygons[2] = new Polygon(new Vertex[]{$$23, $$22, $$18, $$19}, $$27, $$32, $$28, $$33, $$12, $$13, $$11, Direction.DOWN);
            this.polygons[3] = new Polygon(new Vertex[]{$$20, $$21, $$25, $$24}, $$28, $$33, $$29, $$32, $$12, $$13, $$11, Direction.UP);
            this.polygons[1] = new Polygon(new Vertex[]{$$18, $$22, $$25, $$21}, $$26, $$33, $$27, $$34, $$12, $$13, $$11, Direction.WEST);
            this.polygons[4] = new Polygon(new Vertex[]{$$19, $$18, $$21, $$20}, $$27, $$33, $$28, $$34, $$12, $$13, $$11, Direction.NORTH);
            this.polygons[0] = new Polygon(new Vertex[]{$$23, $$19, $$20, $$24}, $$28, $$33, $$30, $$34, $$12, $$13, $$11, Direction.EAST);
            this.polygons[5] = new Polygon(new Vertex[]{$$22, $$23, $$24, $$25}, $$30, $$33, $$31, $$34, $$12, $$13, $$11, Direction.SOUTH);
        }

        public void compile(PoseStack.Pose $$0, VertexConsumer $$1, int $$2, int $$3, float $$4, float $$5, float $$6, float $$7) {
            Matrix4f $$8 = $$0.pose();
            Matrix3f $$9 = $$0.normal();
            for (Polygon $$10 : this.polygons) {
                Vector3f $$11 = $$9.transform(new Vector3f((Vector3fc)$$10.normal));
                float $$12 = $$11.x();
                float $$13 = $$11.y();
                float $$14 = $$11.z();
                for (Vertex $$15 : $$10.vertices) {
                    float $$16 = $$15.pos.x() / 16.0f;
                    float $$17 = $$15.pos.y() / 16.0f;
                    float $$18 = $$15.pos.z() / 16.0f;
                    Vector4f $$19 = $$8.transform(new Vector4f($$16, $$17, $$18, 1.0f));
                    $$1.vertex($$19.x(), $$19.y(), $$19.z(), $$4, $$5, $$6, $$7, $$15.u, $$15.v, $$3, $$2, $$12, $$13, $$14);
                }
            }
        }
    }

    static class Vertex {
        public final Vector3f pos;
        public final float u;
        public final float v;

        public Vertex(float $$0, float $$1, float $$2, float $$3, float $$4) {
            this(new Vector3f($$0, $$1, $$2), $$3, $$4);
        }

        public Vertex remap(float $$0, float $$1) {
            return new Vertex(this.pos, $$0, $$1);
        }

        public Vertex(Vector3f $$0, float $$1, float $$2) {
            this.pos = $$0;
            this.u = $$1;
            this.v = $$2;
        }
    }

    static class Polygon {
        public final Vertex[] vertices;
        public final Vector3f normal;

        public Polygon(Vertex[] $$0, float $$1, float $$2, float $$3, float $$4, float $$5, float $$6, boolean $$7, Direction $$8) {
            this.vertices = $$0;
            float $$9 = 0.0f / $$5;
            float $$10 = 0.0f / $$6;
            $$0[0] = $$0[0].remap($$3 / $$5 - $$9, $$2 / $$6 + $$10);
            $$0[1] = $$0[1].remap($$1 / $$5 + $$9, $$2 / $$6 + $$10);
            $$0[2] = $$0[2].remap($$1 / $$5 + $$9, $$4 / $$6 - $$10);
            $$0[3] = $$0[3].remap($$3 / $$5 - $$9, $$4 / $$6 - $$10);
            if ($$7) {
                int $$11 = $$0.length;
                for (int $$12 = 0; $$12 < $$11 / 2; ++$$12) {
                    Vertex $$13 = $$0[$$12];
                    $$0[$$12] = $$0[$$11 - 1 - $$12];
                    $$0[$$11 - 1 - $$12] = $$13;
                }
            }
            this.normal = $$8.step();
            if ($$7) {
                this.normal.mul(-1.0f, 1.0f, 1.0f);
            }
        }
    }
}