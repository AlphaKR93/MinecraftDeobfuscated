/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.reflect.Type
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;

public class BlockElementFace {
    public static final int NO_TINT = -1;
    public final Direction cullForDirection;
    public final int tintIndex;
    public final String texture;
    public final BlockFaceUV uv;

    public BlockElementFace(@Nullable Direction $$0, int $$1, String $$2, BlockFaceUV $$3) {
        this.cullForDirection = $$0;
        this.tintIndex = $$1;
        this.texture = $$2;
        this.uv = $$3;
    }

    protected static class Deserializer
    implements JsonDeserializer<BlockElementFace> {
        private static final int DEFAULT_TINT_INDEX = -1;

        protected Deserializer() {
        }

        public BlockElementFace deserialize(JsonElement $$0, Type $$1, JsonDeserializationContext $$2) throws JsonParseException {
            JsonObject $$3 = $$0.getAsJsonObject();
            Direction $$4 = this.getCullFacing($$3);
            int $$5 = this.getTintIndex($$3);
            String $$6 = this.getTexture($$3);
            BlockFaceUV $$7 = (BlockFaceUV)$$2.deserialize((JsonElement)$$3, BlockFaceUV.class);
            return new BlockElementFace($$4, $$5, $$6, $$7);
        }

        protected int getTintIndex(JsonObject $$0) {
            return GsonHelper.getAsInt($$0, "tintindex", -1);
        }

        private String getTexture(JsonObject $$0) {
            return GsonHelper.getAsString($$0, "texture");
        }

        @Nullable
        private Direction getCullFacing(JsonObject $$0) {
            String $$1 = GsonHelper.getAsString($$0, "cullface", "");
            return Direction.byName($$1);
        }
    }
}