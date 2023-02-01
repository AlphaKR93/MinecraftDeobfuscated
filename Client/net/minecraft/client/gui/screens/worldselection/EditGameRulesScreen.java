/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  java.lang.Boolean
 *  java.lang.FunctionalInterface
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Comparator
 *  java.util.HashMap
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Optional
 *  java.util.Set
 *  java.util.function.Consumer
 *  java.util.function.Function
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.worldselection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.GameRules;

public class EditGameRulesScreen
extends Screen {
    private final Consumer<Optional<GameRules>> exitCallback;
    private RuleList rules;
    private final Set<RuleEntry> invalidEntries = Sets.newHashSet();
    private Button doneButton;
    @Nullable
    private List<FormattedCharSequence> tooltip;
    private final GameRules gameRules;

    public EditGameRulesScreen(GameRules $$0, Consumer<Optional<GameRules>> $$1) {
        super(Component.translatable("editGamerule.title"));
        this.gameRules = $$0;
        this.exitCallback = $$1;
    }

    @Override
    protected void init() {
        this.rules = new RuleList(this.gameRules);
        this.addWidget(this.rules);
        GridLayout.RowHelper $$02 = new GridLayout().columnSpacing(10).createRowHelper(2);
        this.doneButton = $$02.addChild(Button.builder(CommonComponents.GUI_DONE, $$0 -> this.exitCallback.accept((Object)Optional.of((Object)this.gameRules))).build());
        $$02.addChild(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> this.exitCallback.accept((Object)Optional.empty())).build());
        $$02.getGrid().visitWidgets((Consumer<AbstractWidget>)((Consumer)$$1 -> {
            AbstractWidget cfr_ignored_0 = (AbstractWidget)this.addRenderableWidget($$1);
        }));
        $$02.getGrid().setPosition(this.width / 2 - 155, this.height - 28);
        $$02.getGrid().arrangeElements();
    }

    @Override
    public void onClose() {
        this.exitCallback.accept((Object)Optional.empty());
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.tooltip = null;
        this.rules.render($$0, $$1, $$2, $$3);
        EditGameRulesScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render($$0, $$1, $$2, $$3);
    }

    private void updateDoneButton() {
        this.doneButton.active = this.invalidEntries.isEmpty();
    }

    void markInvalid(RuleEntry $$0) {
        this.invalidEntries.add((Object)$$0);
        this.updateDoneButton();
    }

    void clearInvalid(RuleEntry $$0) {
        this.invalidEntries.remove((Object)$$0);
        this.updateDoneButton();
    }

    public class RuleList
    extends ContainerObjectSelectionList<RuleEntry> {
        public RuleList(final GameRules $$1) {
            super(EditGameRulesScreen.this.minecraft, EditGameRulesScreen.this.width, EditGameRulesScreen.this.height, 43, EditGameRulesScreen.this.height - 32, 24);
            HashMap $$2 = Maps.newHashMap();
            GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor(){
                final /* synthetic */ Map val$entries;
                {
                    this.val$entries = map;
                }

                @Override
                public void visitBoolean(GameRules.Key<GameRules.BooleanValue> $$02, GameRules.Type<GameRules.BooleanValue> $$12) {
                    this.addEntry($$02, ($$0, $$1, $$2, $$3) -> new BooleanRuleEntry($$0, (List<FormattedCharSequence>)$$1, $$2, (GameRules.BooleanValue)$$3));
                }

                @Override
                public void visitInteger(GameRules.Key<GameRules.IntegerValue> $$02, GameRules.Type<GameRules.IntegerValue> $$12) {
                    this.addEntry($$02, ($$0, $$1, $$2, $$3) -> new IntegerRuleEntry($$0, (List<FormattedCharSequence>)$$1, $$2, (GameRules.IntegerValue)$$3));
                }

                private <T extends GameRules.Value<T>> void addEntry(GameRules.Key<T> $$02, EntryFactory<T> $$14) {
                    String $$13;
                    ImmutableList $$12;
                    MutableComponent $$2 = Component.translatable($$02.getDescriptionId());
                    MutableComponent $$3 = Component.literal($$02.getId()).withStyle(ChatFormatting.YELLOW);
                    T $$4 = $$1.getRule($$02);
                    String $$5 = ((GameRules.Value)$$4).serialize();
                    MutableComponent $$6 = Component.translatable("editGamerule.default", Component.literal($$5)).withStyle(ChatFormatting.GRAY);
                    String $$7 = $$02.getDescriptionId() + ".description";
                    if (I18n.exists($$7)) {
                        ImmutableList.Builder $$8 = ImmutableList.builder().add((Object)$$3.getVisualOrderText());
                        MutableComponent $$9 = Component.translatable($$7);
                        EditGameRulesScreen.this.font.split($$9, 150).forEach(arg_0 -> ((ImmutableList.Builder)$$8).add(arg_0));
                        ImmutableList $$10 = $$8.add((Object)$$6.getVisualOrderText()).build();
                        String $$11 = $$9.getString() + "\n" + $$6.getString();
                    } else {
                        $$12 = ImmutableList.of((Object)$$3.getVisualOrderText(), (Object)$$6.getVisualOrderText());
                        $$13 = $$6.getString();
                    }
                    ((Map)this.val$entries.computeIfAbsent((Object)$$02.getCategory(), $$0 -> Maps.newHashMap())).put($$02, (Object)$$14.create($$2, (List<FormattedCharSequence>)$$12, $$13, $$4));
                }
            });
            $$2.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach($$02 -> {
                this.addEntry(new CategoryRuleEntry(Component.translatable(((GameRules.Category)((Object)((Object)$$02.getKey()))).getDescriptionId()).withStyle(ChatFormatting.BOLD, ChatFormatting.YELLOW)));
                ((Map)$$02.getValue()).entrySet().stream().sorted(Map.Entry.comparingByKey((Comparator)Comparator.comparing(GameRules.Key::getId))).forEach($$0 -> this.addEntry((RuleEntry)$$0.getValue()));
            });
        }

        @Override
        public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
            super.render($$0, $$1, $$2, $$3);
            RuleEntry $$4 = (RuleEntry)this.getHovered();
            if ($$4 != null && $$4.tooltip != null) {
                EditGameRulesScreen.this.setTooltipForNextRenderPass($$4.tooltip);
            }
        }
    }

    public class IntegerRuleEntry
    extends GameRuleEntry {
        private final EditBox input;

        public IntegerRuleEntry(Component $$12, List<FormattedCharSequence> $$2, String $$3, GameRules.IntegerValue $$4) {
            super($$2, $$12);
            this.input = new EditBox(((EditGameRulesScreen)EditGameRulesScreen.this).minecraft.font, 10, 5, 42, 20, $$12.copy().append("\n").append($$3).append("\n"));
            this.input.setValue(Integer.toString((int)$$4.get()));
            this.input.setResponder((Consumer<String>)((Consumer)$$1 -> {
                if ($$4.tryDeserialize((String)$$1)) {
                    this.input.setTextColor(0xE0E0E0);
                    EditGameRulesScreen.this.clearInvalid(this);
                } else {
                    this.input.setTextColor(0xFF0000);
                    EditGameRulesScreen.this.markInvalid(this);
                }
            }));
            this.children.add((Object)this.input);
        }

        @Override
        public void render(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            this.renderLabel($$0, $$2, $$3);
            this.input.setX($$3 + $$4 - 44);
            this.input.setY($$2);
            this.input.render($$0, $$6, $$7, $$9);
        }
    }

    public class BooleanRuleEntry
    extends GameRuleEntry {
        private final CycleButton<Boolean> checkbox;

        public BooleanRuleEntry(Component $$12, List<FormattedCharSequence> $$22, String $$3, GameRules.BooleanValue $$4) {
            super($$22, $$12);
            this.checkbox = CycleButton.onOffBuilder($$4.get()).displayOnlyValue().withCustomNarration((Function<CycleButton<Boolean>, MutableComponent>)((Function)$$1 -> $$1.createDefaultNarrationMessage().append("\n").append($$3))).create(10, 5, 44, 20, $$12, ($$1, $$2) -> $$4.set((boolean)$$2, null));
            this.children.add(this.checkbox);
        }

        @Override
        public void render(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            this.renderLabel($$0, $$2, $$3);
            this.checkbox.setX($$3 + $$4 - 45);
            this.checkbox.setY($$2);
            this.checkbox.render($$0, $$6, $$7, $$9);
        }
    }

    public abstract class GameRuleEntry
    extends RuleEntry {
        private final List<FormattedCharSequence> label;
        protected final List<AbstractWidget> children;

        public GameRuleEntry(List<FormattedCharSequence> $$1, Component $$2) {
            super($$1);
            this.children = Lists.newArrayList();
            this.label = ((EditGameRulesScreen)EditGameRulesScreen.this).minecraft.font.split($$2, 175);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return this.children;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return this.children;
        }

        protected void renderLabel(PoseStack $$0, int $$1, int $$2) {
            if (this.label.size() == 1) {
                ((EditGameRulesScreen)EditGameRulesScreen.this).minecraft.font.draw($$0, (FormattedCharSequence)this.label.get(0), (float)$$2, (float)($$1 + 5), 0xFFFFFF);
            } else if (this.label.size() >= 2) {
                ((EditGameRulesScreen)EditGameRulesScreen.this).minecraft.font.draw($$0, (FormattedCharSequence)this.label.get(0), (float)$$2, (float)$$1, 0xFFFFFF);
                ((EditGameRulesScreen)EditGameRulesScreen.this).minecraft.font.draw($$0, (FormattedCharSequence)this.label.get(1), (float)$$2, (float)($$1 + 10), 0xFFFFFF);
            }
        }
    }

    @FunctionalInterface
    static interface EntryFactory<T extends GameRules.Value<T>> {
        public RuleEntry create(Component var1, List<FormattedCharSequence> var2, String var3, T var4);
    }

    public class CategoryRuleEntry
    extends RuleEntry {
        final Component label;

        public CategoryRuleEntry(Component $$1) {
            super(null);
            this.label = $$1;
        }

        @Override
        public void render(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            GuiComponent.drawCenteredString($$0, ((EditGameRulesScreen)EditGameRulesScreen.this).minecraft.font, this.label, $$3 + $$4 / 2, $$2 + 5, 0xFFFFFF);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of();
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of((Object)new NarratableEntry(){

                @Override
                public NarratableEntry.NarrationPriority narrationPriority() {
                    return NarratableEntry.NarrationPriority.HOVERED;
                }

                @Override
                public void updateNarration(NarrationElementOutput $$0) {
                    $$0.add(NarratedElementType.TITLE, CategoryRuleEntry.this.label);
                }
            });
        }
    }

    public static abstract class RuleEntry
    extends ContainerObjectSelectionList.Entry<RuleEntry> {
        @Nullable
        final List<FormattedCharSequence> tooltip;

        public RuleEntry(@Nullable List<FormattedCharSequence> $$0) {
            this.tooltip = $$0;
        }
    }
}