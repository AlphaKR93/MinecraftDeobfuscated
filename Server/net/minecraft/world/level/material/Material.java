/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.world.level.material;

import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;

public final class Material {
    public static final Material AIR = new Builder(MaterialColor.NONE).noCollider().notSolidBlocking().nonSolid().replaceable().build();
    public static final Material STRUCTURAL_AIR = new Builder(MaterialColor.NONE).noCollider().notSolidBlocking().nonSolid().replaceable().build();
    public static final Material PORTAL = new Builder(MaterialColor.NONE).noCollider().notSolidBlocking().nonSolid().notPushable().build();
    public static final Material CLOTH_DECORATION = new Builder(MaterialColor.WOOL).noCollider().notSolidBlocking().nonSolid().flammable().build();
    public static final Material PLANT = new Builder(MaterialColor.PLANT).noCollider().notSolidBlocking().nonSolid().destroyOnPush().build();
    public static final Material WATER_PLANT = new Builder(MaterialColor.WATER).noCollider().notSolidBlocking().nonSolid().destroyOnPush().build();
    public static final Material REPLACEABLE_PLANT = new Builder(MaterialColor.PLANT).noCollider().notSolidBlocking().nonSolid().destroyOnPush().replaceable().flammable().build();
    public static final Material REPLACEABLE_FIREPROOF_PLANT = new Builder(MaterialColor.PLANT).noCollider().notSolidBlocking().nonSolid().destroyOnPush().replaceable().build();
    public static final Material REPLACEABLE_WATER_PLANT = new Builder(MaterialColor.WATER).noCollider().notSolidBlocking().nonSolid().destroyOnPush().replaceable().build();
    public static final Material WATER = new Builder(MaterialColor.WATER).noCollider().notSolidBlocking().nonSolid().destroyOnPush().replaceable().liquid().build();
    public static final Material BUBBLE_COLUMN = new Builder(MaterialColor.WATER).noCollider().notSolidBlocking().nonSolid().destroyOnPush().replaceable().liquid().build();
    public static final Material LAVA = new Builder(MaterialColor.FIRE).noCollider().notSolidBlocking().nonSolid().destroyOnPush().replaceable().liquid().build();
    public static final Material TOP_SNOW = new Builder(MaterialColor.SNOW).noCollider().notSolidBlocking().nonSolid().destroyOnPush().replaceable().build();
    public static final Material FIRE = new Builder(MaterialColor.NONE).noCollider().notSolidBlocking().nonSolid().destroyOnPush().replaceable().build();
    public static final Material DECORATION = new Builder(MaterialColor.NONE).noCollider().notSolidBlocking().nonSolid().destroyOnPush().build();
    public static final Material WEB = new Builder(MaterialColor.WOOL).noCollider().notSolidBlocking().destroyOnPush().build();
    public static final Material SCULK = new Builder(MaterialColor.COLOR_BLACK).build();
    public static final Material BUILDABLE_GLASS = new Builder(MaterialColor.NONE).build();
    public static final Material CLAY = new Builder(MaterialColor.CLAY).build();
    public static final Material DIRT = new Builder(MaterialColor.DIRT).build();
    public static final Material GRASS = new Builder(MaterialColor.GRASS).build();
    public static final Material ICE_SOLID = new Builder(MaterialColor.ICE).build();
    public static final Material SAND = new Builder(MaterialColor.SAND).build();
    public static final Material SPONGE = new Builder(MaterialColor.COLOR_YELLOW).build();
    public static final Material SHULKER_SHELL = new Builder(MaterialColor.COLOR_PURPLE).build();
    public static final Material WOOD = new Builder(MaterialColor.WOOD).flammable().build();
    public static final Material NETHER_WOOD = new Builder(MaterialColor.WOOD).build();
    public static final Material BAMBOO_SAPLING = new Builder(MaterialColor.WOOD).flammable().destroyOnPush().noCollider().build();
    public static final Material BAMBOO = new Builder(MaterialColor.WOOD).flammable().destroyOnPush().build();
    public static final Material WOOL = new Builder(MaterialColor.WOOL).flammable().build();
    public static final Material EXPLOSIVE = new Builder(MaterialColor.FIRE).flammable().notSolidBlocking().build();
    public static final Material LEAVES = new Builder(MaterialColor.PLANT).flammable().notSolidBlocking().destroyOnPush().build();
    public static final Material GLASS = new Builder(MaterialColor.NONE).notSolidBlocking().build();
    public static final Material ICE = new Builder(MaterialColor.ICE).notSolidBlocking().build();
    public static final Material CACTUS = new Builder(MaterialColor.PLANT).notSolidBlocking().destroyOnPush().build();
    public static final Material STONE = new Builder(MaterialColor.STONE).build();
    public static final Material METAL = new Builder(MaterialColor.METAL).build();
    public static final Material SNOW = new Builder(MaterialColor.SNOW).build();
    public static final Material HEAVY_METAL = new Builder(MaterialColor.METAL).notPushable().build();
    public static final Material BARRIER = new Builder(MaterialColor.NONE).notPushable().build();
    public static final Material PISTON = new Builder(MaterialColor.STONE).notPushable().build();
    public static final Material MOSS = new Builder(MaterialColor.PLANT).destroyOnPush().build();
    public static final Material VEGETABLE = new Builder(MaterialColor.PLANT).destroyOnPush().build();
    public static final Material EGG = new Builder(MaterialColor.PLANT).destroyOnPush().build();
    public static final Material CAKE = new Builder(MaterialColor.NONE).destroyOnPush().build();
    public static final Material AMETHYST = new Builder(MaterialColor.COLOR_PURPLE).build();
    public static final Material POWDER_SNOW = new Builder(MaterialColor.SNOW).nonSolid().noCollider().build();
    public static final Material FROGSPAWN = new Builder(MaterialColor.WATER).noCollider().notSolidBlocking().nonSolid().destroyOnPush().build();
    public static final Material FROGLIGHT = new Builder(MaterialColor.NONE).build();
    private final MaterialColor color;
    private final PushReaction pushReaction;
    private final boolean blocksMotion;
    private final boolean flammable;
    private final boolean liquid;
    private final boolean solidBlocking;
    private final boolean replaceable;
    private final boolean solid;

    public Material(MaterialColor $$0, boolean $$1, boolean $$2, boolean $$3, boolean $$4, boolean $$5, boolean $$6, PushReaction $$7) {
        this.color = $$0;
        this.liquid = $$1;
        this.solid = $$2;
        this.blocksMotion = $$3;
        this.solidBlocking = $$4;
        this.flammable = $$5;
        this.replaceable = $$6;
        this.pushReaction = $$7;
    }

    public boolean isLiquid() {
        return this.liquid;
    }

    public boolean isSolid() {
        return this.solid;
    }

    public boolean blocksMotion() {
        return this.blocksMotion;
    }

    public boolean isFlammable() {
        return this.flammable;
    }

    public boolean isReplaceable() {
        return this.replaceable;
    }

    public boolean isSolidBlocking() {
        return this.solidBlocking;
    }

    public PushReaction getPushReaction() {
        return this.pushReaction;
    }

    public MaterialColor getColor() {
        return this.color;
    }

    public static class Builder {
        private PushReaction pushReaction = PushReaction.NORMAL;
        private boolean blocksMotion = true;
        private boolean flammable;
        private boolean liquid;
        private boolean replaceable;
        private boolean solid = true;
        private final MaterialColor color;
        private boolean solidBlocking = true;

        public Builder(MaterialColor $$0) {
            this.color = $$0;
        }

        public Builder liquid() {
            this.liquid = true;
            return this;
        }

        public Builder nonSolid() {
            this.solid = false;
            return this;
        }

        public Builder noCollider() {
            this.blocksMotion = false;
            return this;
        }

        Builder notSolidBlocking() {
            this.solidBlocking = false;
            return this;
        }

        protected Builder flammable() {
            this.flammable = true;
            return this;
        }

        public Builder replaceable() {
            this.replaceable = true;
            return this;
        }

        protected Builder destroyOnPush() {
            this.pushReaction = PushReaction.DESTROY;
            return this;
        }

        protected Builder notPushable() {
            this.pushReaction = PushReaction.BLOCK;
            return this;
        }

        public Material build() {
            return new Material(this.color, this.liquid, this.solid, this.blocksMotion, this.solidBlocking, this.flammable, this.replaceable, this.pushReaction);
        }
    }
}