/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  java.lang.Comparable
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.Collection
 *  java.util.Iterator
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Set
 *  java.util.function.Consumer
 *  java.util.function.Function
 *  java.util.function.UnaryOperator
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.ArrayUtils
 */
package net.minecraft.advancements;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.apache.commons.lang3.ArrayUtils;

public class Advancement {
    @Nullable
    private final Advancement parent;
    @Nullable
    private final DisplayInfo display;
    private final AdvancementRewards rewards;
    private final ResourceLocation id;
    private final Map<String, Criterion> criteria;
    private final String[][] requirements;
    private final Set<Advancement> children = Sets.newLinkedHashSet();
    private final Component chatComponent;

    public Advancement(ResourceLocation $$0, @Nullable Advancement $$12, @Nullable DisplayInfo $$2, AdvancementRewards $$3, Map<String, Criterion> $$4, String[][] $$5) {
        this.id = $$0;
        this.display = $$2;
        this.criteria = ImmutableMap.copyOf($$4);
        this.parent = $$12;
        this.rewards = $$3;
        this.requirements = $$5;
        if ($$12 != null) {
            $$12.addChild(this);
        }
        if ($$2 == null) {
            this.chatComponent = Component.literal($$0.toString());
        } else {
            Component $$6 = $$2.getTitle();
            ChatFormatting $$7 = $$2.getFrame().getChatColor();
            MutableComponent $$8 = ComponentUtils.mergeStyles($$6.copy(), Style.EMPTY.withColor($$7)).append("\n").append($$2.getDescription());
            MutableComponent $$9 = $$6.copy().withStyle((UnaryOperator<Style>)((UnaryOperator)$$1 -> $$1.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, $$8))));
            this.chatComponent = ComponentUtils.wrapInSquareBrackets($$9).withStyle($$7);
        }
    }

    public Builder deconstruct() {
        return new Builder(this.parent == null ? null : this.parent.getId(), this.display, this.rewards, this.criteria, this.requirements);
    }

    @Nullable
    public Advancement getParent() {
        return this.parent;
    }

    public Advancement getRoot() {
        return Advancement.getRoot(this);
    }

    public static Advancement getRoot(Advancement $$0) {
        Advancement $$1 = $$0;
        Advancement $$2;
        while (($$2 = $$1.getParent()) != null) {
            $$1 = $$2;
        }
        return $$1;
    }

    @Nullable
    public DisplayInfo getDisplay() {
        return this.display;
    }

    public AdvancementRewards getRewards() {
        return this.rewards;
    }

    public String toString() {
        return "SimpleAdvancement{id=" + this.getId() + ", parent=" + (Comparable)(this.parent == null ? "null" : this.parent.getId()) + ", display=" + this.display + ", rewards=" + this.rewards + ", criteria=" + this.criteria + ", requirements=" + Arrays.deepToString((Object[])this.requirements) + "}";
    }

    public Iterable<Advancement> getChildren() {
        return this.children;
    }

    public Map<String, Criterion> getCriteria() {
        return this.criteria;
    }

    public int getMaxCriteraRequired() {
        return this.requirements.length;
    }

    public void addChild(Advancement $$0) {
        this.children.add((Object)$$0);
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if (!($$0 instanceof Advancement)) {
            return false;
        }
        Advancement $$1 = (Advancement)$$0;
        return this.id.equals($$1.id);
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public String[][] getRequirements() {
        return this.requirements;
    }

    public Component getChatComponent() {
        return this.chatComponent;
    }

    public static class Builder {
        @Nullable
        private ResourceLocation parentId;
        @Nullable
        private Advancement parent;
        @Nullable
        private DisplayInfo display;
        private AdvancementRewards rewards = AdvancementRewards.EMPTY;
        private Map<String, Criterion> criteria = Maps.newLinkedHashMap();
        @Nullable
        private String[][] requirements;
        private RequirementsStrategy requirementsStrategy = RequirementsStrategy.AND;

        Builder(@Nullable ResourceLocation $$0, @Nullable DisplayInfo $$1, AdvancementRewards $$2, Map<String, Criterion> $$3, String[][] $$4) {
            this.parentId = $$0;
            this.display = $$1;
            this.rewards = $$2;
            this.criteria = $$3;
            this.requirements = $$4;
        }

        private Builder() {
        }

        public static Builder advancement() {
            return new Builder();
        }

        public Builder parent(Advancement $$0) {
            this.parent = $$0;
            return this;
        }

        public Builder parent(ResourceLocation $$0) {
            this.parentId = $$0;
            return this;
        }

        public Builder display(ItemStack $$0, Component $$1, Component $$2, @Nullable ResourceLocation $$3, FrameType $$4, boolean $$5, boolean $$6, boolean $$7) {
            return this.display(new DisplayInfo($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7));
        }

        public Builder display(ItemLike $$0, Component $$1, Component $$2, @Nullable ResourceLocation $$3, FrameType $$4, boolean $$5, boolean $$6, boolean $$7) {
            return this.display(new DisplayInfo(new ItemStack($$0.asItem()), $$1, $$2, $$3, $$4, $$5, $$6, $$7));
        }

        public Builder display(DisplayInfo $$0) {
            this.display = $$0;
            return this;
        }

        public Builder rewards(AdvancementRewards.Builder $$0) {
            return this.rewards($$0.build());
        }

        public Builder rewards(AdvancementRewards $$0) {
            this.rewards = $$0;
            return this;
        }

        public Builder addCriterion(String $$0, CriterionTriggerInstance $$1) {
            return this.addCriterion($$0, new Criterion($$1));
        }

        public Builder addCriterion(String $$0, Criterion $$1) {
            if (this.criteria.containsKey((Object)$$0)) {
                throw new IllegalArgumentException("Duplicate criterion " + $$0);
            }
            this.criteria.put((Object)$$0, (Object)$$1);
            return this;
        }

        public Builder requirements(RequirementsStrategy $$0) {
            this.requirementsStrategy = $$0;
            return this;
        }

        public Builder requirements(String[][] $$0) {
            this.requirements = $$0;
            return this;
        }

        public boolean canBuild(Function<ResourceLocation, Advancement> $$0) {
            if (this.parentId == null) {
                return true;
            }
            if (this.parent == null) {
                this.parent = (Advancement)$$0.apply((Object)this.parentId);
            }
            return this.parent != null;
        }

        public Advancement build(ResourceLocation $$02) {
            if (!this.canBuild((Function<ResourceLocation, Advancement>)((Function)$$0 -> null))) {
                throw new IllegalStateException("Tried to build incomplete advancement!");
            }
            if (this.requirements == null) {
                this.requirements = this.requirementsStrategy.createRequirements((Collection<String>)this.criteria.keySet());
            }
            return new Advancement($$02, this.parent, this.display, this.rewards, this.criteria, this.requirements);
        }

        public Advancement save(Consumer<Advancement> $$0, String $$1) {
            Advancement $$2 = this.build(new ResourceLocation($$1));
            $$0.accept((Object)$$2);
            return $$2;
        }

        public JsonObject serializeToJson() {
            if (this.requirements == null) {
                this.requirements = this.requirementsStrategy.createRequirements((Collection<String>)this.criteria.keySet());
            }
            JsonObject $$0 = new JsonObject();
            if (this.parent != null) {
                $$0.addProperty("parent", this.parent.getId().toString());
            } else if (this.parentId != null) {
                $$0.addProperty("parent", this.parentId.toString());
            }
            if (this.display != null) {
                $$0.add("display", this.display.serializeToJson());
            }
            $$0.add("rewards", this.rewards.serializeToJson());
            JsonObject $$1 = new JsonObject();
            for (Map.Entry $$2 : this.criteria.entrySet()) {
                $$1.add((String)$$2.getKey(), ((Criterion)$$2.getValue()).serializeToJson());
            }
            $$0.add("criteria", (JsonElement)$$1);
            JsonArray $$3 = new JsonArray();
            for (String[] $$4 : this.requirements) {
                JsonArray $$5 = new JsonArray();
                for (String $$6 : $$4) {
                    $$5.add($$6);
                }
                $$3.add((JsonElement)$$5);
            }
            $$0.add("requirements", (JsonElement)$$3);
            return $$0;
        }

        public void serializeToNetwork(FriendlyByteBuf $$02) {
            if (this.requirements == null) {
                this.requirements = this.requirementsStrategy.createRequirements((Collection<String>)this.criteria.keySet());
            }
            $$02.writeNullable(this.parentId, FriendlyByteBuf::writeResourceLocation);
            $$02.writeNullable(this.display, ($$0, $$1) -> $$1.serializeToNetwork((FriendlyByteBuf)((Object)$$0)));
            Criterion.serializeToNetwork(this.criteria, $$02);
            $$02.writeVarInt(this.requirements.length);
            for (String[] $$12 : this.requirements) {
                $$02.writeVarInt($$12.length);
                for (String $$2 : $$12) {
                    $$02.writeUtf($$2);
                }
            }
        }

        public String toString() {
            return "Task Advancement{parentId=" + this.parentId + ", display=" + this.display + ", rewards=" + this.rewards + ", criteria=" + this.criteria + ", requirements=" + Arrays.deepToString((Object[])this.requirements) + "}";
        }

        public static Builder fromJson(JsonObject $$0, DeserializationContext $$1) {
            ResourceLocation $$2 = $$0.has("parent") ? new ResourceLocation(GsonHelper.getAsString($$0, "parent")) : null;
            DisplayInfo $$3 = $$0.has("display") ? DisplayInfo.fromJson(GsonHelper.getAsJsonObject($$0, "display")) : null;
            AdvancementRewards $$4 = $$0.has("rewards") ? AdvancementRewards.deserialize(GsonHelper.getAsJsonObject($$0, "rewards")) : AdvancementRewards.EMPTY;
            Map<String, Criterion> $$5 = Criterion.criteriaFromJson(GsonHelper.getAsJsonObject($$0, "criteria"), $$1);
            if ($$5.isEmpty()) {
                throw new JsonSyntaxException("Advancement criteria cannot be empty");
            }
            JsonArray $$6 = GsonHelper.getAsJsonArray($$0, "requirements", new JsonArray());
            Iterator $$7 = new String[$$6.size()][];
            for (int $$8 = 0; $$8 < $$6.size(); ++$$8) {
                JsonArray $$9 = GsonHelper.convertToJsonArray($$6.get($$8), "requirements[" + $$8 + "]");
                $$7[$$8] = new String[$$9.size()];
                for (int $$10 = 0; $$10 < $$9.size(); ++$$10) {
                    $$7[$$8][$$10] = GsonHelper.convertToString($$9.get($$10), "requirements[" + $$8 + "][" + $$10 + "]");
                }
            }
            if (((String[][])$$7).length == 0) {
                $$7 = new String[$$5.size()][];
                int $$11 = 0;
                for (String $$12 : $$5.keySet()) {
                    $$7[$$11++] = new String[]{$$12};
                }
            }
            for (String[] $$13 : $$7) {
                if ($$13.length == 0 && $$5.isEmpty()) {
                    throw new JsonSyntaxException("Requirement entry cannot be empty");
                }
                String[] stringArray = $$13;
                int n = stringArray.length;
                for (int i = 0; i < n; ++i) {
                    String $$14 = stringArray[i];
                    if ($$5.containsKey((Object)$$14)) continue;
                    throw new JsonSyntaxException("Unknown required criterion '" + $$14 + "'");
                }
            }
            for (String $$15 : $$5.keySet()) {
                boolean $$16 = false;
                for (Iterator $$17 : $$7) {
                    if (!ArrayUtils.contains((Object[])$$17, (Object)$$15)) continue;
                    $$16 = true;
                    break;
                }
                if ($$16) continue;
                throw new JsonSyntaxException("Criterion '" + $$15 + "' isn't a requirement for completion. This isn't supported behaviour, all criteria must be required.");
            }
            return new Builder($$2, $$3, $$4, $$5, (String[][])$$7);
        }

        public static Builder fromNetwork(FriendlyByteBuf $$0) {
            ResourceLocation $$1 = (ResourceLocation)$$0.readNullable(FriendlyByteBuf::readResourceLocation);
            DisplayInfo $$2 = (DisplayInfo)$$0.readNullable(DisplayInfo::fromNetwork);
            Map<String, Criterion> $$3 = Criterion.criteriaFromNetwork($$0);
            String[][] $$4 = new String[$$0.readVarInt()][];
            for (int $$5 = 0; $$5 < $$4.length; ++$$5) {
                $$4[$$5] = new String[$$0.readVarInt()];
                for (int $$6 = 0; $$6 < $$4[$$5].length; ++$$6) {
                    $$4[$$5][$$6] = $$0.readUtf();
                }
            }
            return new Builder($$1, $$2, AdvancementRewards.EMPTY, $$3, $$4);
        }

        public Map<String, Criterion> getCriteria() {
            return this.criteria;
        }
    }
}