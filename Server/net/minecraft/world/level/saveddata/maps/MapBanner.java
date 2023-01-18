/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Objects
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.saveddata.maps;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.saveddata.maps.MapDecoration;

public class MapBanner {
    private final BlockPos pos;
    private final DyeColor color;
    @Nullable
    private final Component name;

    public MapBanner(BlockPos $$0, DyeColor $$1, @Nullable Component $$2) {
        this.pos = $$0;
        this.color = $$1;
        this.name = $$2;
    }

    public static MapBanner load(CompoundTag $$0) {
        BlockPos $$1 = NbtUtils.readBlockPos($$0.getCompound("Pos"));
        DyeColor $$2 = DyeColor.byName($$0.getString("Color"), DyeColor.WHITE);
        MutableComponent $$3 = $$0.contains("Name") ? Component.Serializer.fromJson($$0.getString("Name")) : null;
        return new MapBanner($$1, $$2, $$3);
    }

    @Nullable
    public static MapBanner fromWorld(BlockGetter $$0, BlockPos $$1) {
        BlockEntity $$2 = $$0.getBlockEntity($$1);
        if ($$2 instanceof BannerBlockEntity) {
            BannerBlockEntity $$3 = (BannerBlockEntity)$$2;
            DyeColor $$4 = $$3.getBaseColor();
            Component $$5 = $$3.hasCustomName() ? $$3.getCustomName() : null;
            return new MapBanner($$1, $$4, $$5);
        }
        return null;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public DyeColor getColor() {
        return this.color;
    }

    public MapDecoration.Type getDecoration() {
        switch (this.color) {
            case WHITE: {
                return MapDecoration.Type.BANNER_WHITE;
            }
            case ORANGE: {
                return MapDecoration.Type.BANNER_ORANGE;
            }
            case MAGENTA: {
                return MapDecoration.Type.BANNER_MAGENTA;
            }
            case LIGHT_BLUE: {
                return MapDecoration.Type.BANNER_LIGHT_BLUE;
            }
            case YELLOW: {
                return MapDecoration.Type.BANNER_YELLOW;
            }
            case LIME: {
                return MapDecoration.Type.BANNER_LIME;
            }
            case PINK: {
                return MapDecoration.Type.BANNER_PINK;
            }
            case GRAY: {
                return MapDecoration.Type.BANNER_GRAY;
            }
            case LIGHT_GRAY: {
                return MapDecoration.Type.BANNER_LIGHT_GRAY;
            }
            case CYAN: {
                return MapDecoration.Type.BANNER_CYAN;
            }
            case PURPLE: {
                return MapDecoration.Type.BANNER_PURPLE;
            }
            case BLUE: {
                return MapDecoration.Type.BANNER_BLUE;
            }
            case BROWN: {
                return MapDecoration.Type.BANNER_BROWN;
            }
            case GREEN: {
                return MapDecoration.Type.BANNER_GREEN;
            }
            case RED: {
                return MapDecoration.Type.BANNER_RED;
            }
        }
        return MapDecoration.Type.BANNER_BLACK;
    }

    @Nullable
    public Component getName() {
        return this.name;
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 == null || this.getClass() != $$0.getClass()) {
            return false;
        }
        MapBanner $$1 = (MapBanner)$$0;
        return Objects.equals((Object)this.pos, (Object)$$1.pos) && this.color == $$1.color && Objects.equals((Object)this.name, (Object)$$1.name);
    }

    public int hashCode() {
        return Objects.hash((Object[])new Object[]{this.pos, this.color, this.name});
    }

    public CompoundTag save() {
        CompoundTag $$0 = new CompoundTag();
        $$0.put("Pos", NbtUtils.writeBlockPos(this.pos));
        $$0.putString("Color", this.color.getName());
        if (this.name != null) {
            $$0.putString("Name", Component.Serializer.toJson(this.name));
        }
        return $$0;
    }

    public String getId() {
        return "banner-" + this.pos.getX() + "," + this.pos.getY() + "," + this.pos.getZ();
    }
}