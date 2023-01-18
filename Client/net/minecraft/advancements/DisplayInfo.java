/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  java.lang.Boolean
 *  java.lang.Object
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.advancements.FrameType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class DisplayInfo {
    private final Component title;
    private final Component description;
    private final ItemStack icon;
    @Nullable
    private final ResourceLocation background;
    private final FrameType frame;
    private final boolean showToast;
    private final boolean announceChat;
    private final boolean hidden;
    private float x;
    private float y;

    public DisplayInfo(ItemStack $$0, Component $$1, Component $$2, @Nullable ResourceLocation $$3, FrameType $$4, boolean $$5, boolean $$6, boolean $$7) {
        this.title = $$1;
        this.description = $$2;
        this.icon = $$0;
        this.background = $$3;
        this.frame = $$4;
        this.showToast = $$5;
        this.announceChat = $$6;
        this.hidden = $$7;
    }

    public void setLocation(float $$0, float $$1) {
        this.x = $$0;
        this.y = $$1;
    }

    public Component getTitle() {
        return this.title;
    }

    public Component getDescription() {
        return this.description;
    }

    public ItemStack getIcon() {
        return this.icon;
    }

    @Nullable
    public ResourceLocation getBackground() {
        return this.background;
    }

    public FrameType getFrame() {
        return this.frame;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public boolean shouldShowToast() {
        return this.showToast;
    }

    public boolean shouldAnnounceChat() {
        return this.announceChat;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public static DisplayInfo fromJson(JsonObject $$0) {
        MutableComponent $$1 = Component.Serializer.fromJson($$0.get("title"));
        MutableComponent $$2 = Component.Serializer.fromJson($$0.get("description"));
        if ($$1 == null || $$2 == null) {
            throw new JsonSyntaxException("Both title and description must be set");
        }
        ItemStack $$3 = DisplayInfo.getIcon(GsonHelper.getAsJsonObject($$0, "icon"));
        ResourceLocation $$4 = $$0.has("background") ? new ResourceLocation(GsonHelper.getAsString($$0, "background")) : null;
        FrameType $$5 = $$0.has("frame") ? FrameType.byName(GsonHelper.getAsString($$0, "frame")) : FrameType.TASK;
        boolean $$6 = GsonHelper.getAsBoolean($$0, "show_toast", true);
        boolean $$7 = GsonHelper.getAsBoolean($$0, "announce_to_chat", true);
        boolean $$8 = GsonHelper.getAsBoolean($$0, "hidden", false);
        return new DisplayInfo($$3, $$1, $$2, $$4, $$5, $$6, $$7, $$8);
    }

    private static ItemStack getIcon(JsonObject $$0) {
        if (!$$0.has("item")) {
            throw new JsonSyntaxException("Unsupported icon type, currently only items are supported (add 'item' key)");
        }
        Item $$1 = GsonHelper.getAsItem($$0, "item");
        if ($$0.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        }
        ItemStack $$2 = new ItemStack($$1);
        if ($$0.has("nbt")) {
            try {
                CompoundTag $$3 = TagParser.parseTag(GsonHelper.convertToString($$0.get("nbt"), "nbt"));
                $$2.setTag($$3);
            }
            catch (CommandSyntaxException $$4) {
                throw new JsonSyntaxException("Invalid nbt tag: " + $$4.getMessage());
            }
        }
        return $$2;
    }

    public void serializeToNetwork(FriendlyByteBuf $$0) {
        $$0.writeComponent(this.title);
        $$0.writeComponent(this.description);
        $$0.writeItem(this.icon);
        $$0.writeEnum(this.frame);
        int $$1 = 0;
        if (this.background != null) {
            $$1 |= 1;
        }
        if (this.showToast) {
            $$1 |= 2;
        }
        if (this.hidden) {
            $$1 |= 4;
        }
        $$0.writeInt($$1);
        if (this.background != null) {
            $$0.writeResourceLocation(this.background);
        }
        $$0.writeFloat(this.x);
        $$0.writeFloat(this.y);
    }

    public static DisplayInfo fromNetwork(FriendlyByteBuf $$0) {
        Component $$1 = $$0.readComponent();
        Component $$2 = $$0.readComponent();
        ItemStack $$3 = $$0.readItem();
        FrameType $$4 = $$0.readEnum(FrameType.class);
        int $$5 = $$0.readInt();
        ResourceLocation $$6 = ($$5 & 1) != 0 ? $$0.readResourceLocation() : null;
        boolean $$7 = ($$5 & 2) != 0;
        boolean $$8 = ($$5 & 4) != 0;
        DisplayInfo $$9 = new DisplayInfo($$3, $$1, $$2, $$6, $$4, $$7, false, $$8);
        $$9.setLocation($$0.readFloat(), $$0.readFloat());
        return $$9;
    }

    public JsonElement serializeToJson() {
        JsonObject $$0 = new JsonObject();
        $$0.add("icon", (JsonElement)this.serializeIcon());
        $$0.add("title", Component.Serializer.toJsonTree(this.title));
        $$0.add("description", Component.Serializer.toJsonTree(this.description));
        $$0.addProperty("frame", this.frame.getName());
        $$0.addProperty("show_toast", Boolean.valueOf((boolean)this.showToast));
        $$0.addProperty("announce_to_chat", Boolean.valueOf((boolean)this.announceChat));
        $$0.addProperty("hidden", Boolean.valueOf((boolean)this.hidden));
        if (this.background != null) {
            $$0.addProperty("background", this.background.toString());
        }
        return $$0;
    }

    private JsonObject serializeIcon() {
        JsonObject $$0 = new JsonObject();
        $$0.addProperty("item", BuiltInRegistries.ITEM.getKey(this.icon.getItem()).toString());
        if (this.icon.hasTag()) {
            $$0.addProperty("nbt", this.icon.getTag().toString());
        }
        return $$0;
    }
}