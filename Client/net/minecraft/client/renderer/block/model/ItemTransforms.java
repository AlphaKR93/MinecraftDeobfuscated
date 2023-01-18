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
 */
package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.client.renderer.block.model.ItemTransform;

public class ItemTransforms {
    public static final ItemTransforms NO_TRANSFORMS = new ItemTransforms();
    public final ItemTransform thirdPersonLeftHand;
    public final ItemTransform thirdPersonRightHand;
    public final ItemTransform firstPersonLeftHand;
    public final ItemTransform firstPersonRightHand;
    public final ItemTransform head;
    public final ItemTransform gui;
    public final ItemTransform ground;
    public final ItemTransform fixed;

    private ItemTransforms() {
        this(ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM);
    }

    public ItemTransforms(ItemTransforms $$0) {
        this.thirdPersonLeftHand = $$0.thirdPersonLeftHand;
        this.thirdPersonRightHand = $$0.thirdPersonRightHand;
        this.firstPersonLeftHand = $$0.firstPersonLeftHand;
        this.firstPersonRightHand = $$0.firstPersonRightHand;
        this.head = $$0.head;
        this.gui = $$0.gui;
        this.ground = $$0.ground;
        this.fixed = $$0.fixed;
    }

    public ItemTransforms(ItemTransform $$0, ItemTransform $$1, ItemTransform $$2, ItemTransform $$3, ItemTransform $$4, ItemTransform $$5, ItemTransform $$6, ItemTransform $$7) {
        this.thirdPersonLeftHand = $$0;
        this.thirdPersonRightHand = $$1;
        this.firstPersonLeftHand = $$2;
        this.firstPersonRightHand = $$3;
        this.head = $$4;
        this.gui = $$5;
        this.ground = $$6;
        this.fixed = $$7;
    }

    public ItemTransform getTransform(TransformType $$0) {
        switch ($$0) {
            case THIRD_PERSON_LEFT_HAND: {
                return this.thirdPersonLeftHand;
            }
            case THIRD_PERSON_RIGHT_HAND: {
                return this.thirdPersonRightHand;
            }
            case FIRST_PERSON_LEFT_HAND: {
                return this.firstPersonLeftHand;
            }
            case FIRST_PERSON_RIGHT_HAND: {
                return this.firstPersonRightHand;
            }
            case HEAD: {
                return this.head;
            }
            case GUI: {
                return this.gui;
            }
            case GROUND: {
                return this.ground;
            }
            case FIXED: {
                return this.fixed;
            }
        }
        return ItemTransform.NO_TRANSFORM;
    }

    public boolean hasTransform(TransformType $$0) {
        return this.getTransform($$0) != ItemTransform.NO_TRANSFORM;
    }

    public static enum TransformType {
        NONE,
        THIRD_PERSON_LEFT_HAND,
        THIRD_PERSON_RIGHT_HAND,
        FIRST_PERSON_LEFT_HAND,
        FIRST_PERSON_RIGHT_HAND,
        HEAD,
        GUI,
        GROUND,
        FIXED;


        public boolean firstPerson() {
            return this == FIRST_PERSON_LEFT_HAND || this == FIRST_PERSON_RIGHT_HAND;
        }
    }

    protected static class Deserializer
    implements JsonDeserializer<ItemTransforms> {
        protected Deserializer() {
        }

        public ItemTransforms deserialize(JsonElement $$0, Type $$1, JsonDeserializationContext $$2) throws JsonParseException {
            JsonObject $$3 = $$0.getAsJsonObject();
            ItemTransform $$4 = this.getTransform($$2, $$3, "thirdperson_righthand");
            ItemTransform $$5 = this.getTransform($$2, $$3, "thirdperson_lefthand");
            if ($$5 == ItemTransform.NO_TRANSFORM) {
                $$5 = $$4;
            }
            ItemTransform $$6 = this.getTransform($$2, $$3, "firstperson_righthand");
            ItemTransform $$7 = this.getTransform($$2, $$3, "firstperson_lefthand");
            if ($$7 == ItemTransform.NO_TRANSFORM) {
                $$7 = $$6;
            }
            ItemTransform $$8 = this.getTransform($$2, $$3, "head");
            ItemTransform $$9 = this.getTransform($$2, $$3, "gui");
            ItemTransform $$10 = this.getTransform($$2, $$3, "ground");
            ItemTransform $$11 = this.getTransform($$2, $$3, "fixed");
            return new ItemTransforms($$5, $$4, $$7, $$6, $$8, $$9, $$10, $$11);
        }

        private ItemTransform getTransform(JsonDeserializationContext $$0, JsonObject $$1, String $$2) {
            if ($$1.has($$2)) {
                return (ItemTransform)$$0.deserialize($$1.get($$2), ItemTransform.class);
            }
            return ItemTransform.NO_TRANSFORM;
        }
    }
}