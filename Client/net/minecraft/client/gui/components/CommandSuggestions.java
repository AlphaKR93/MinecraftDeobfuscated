/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.ParseResults
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.context.CommandContextBuilder
 *  com.mojang.brigadier.context.ParsedArgument
 *  com.mojang.brigadier.context.SuggestionContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.suggestion.Suggestion
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  java.lang.CharSequence
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.List
 *  java.util.Locale
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.concurrent.CompletableFuture
 *  java.util.function.BiFunction
 *  java.util.regex.Matcher
 *  java.util.regex.Pattern
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.components;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

public class CommandSuggestions {
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile((String)"(\\s+)");
    private static final Style UNPARSED_STYLE = Style.EMPTY.withColor(ChatFormatting.RED);
    private static final Style LITERAL_STYLE = Style.EMPTY.withColor(ChatFormatting.GRAY);
    private static final List<Style> ARGUMENT_STYLES = (List)Stream.of((Object[])new ChatFormatting[]{ChatFormatting.AQUA, ChatFormatting.YELLOW, ChatFormatting.GREEN, ChatFormatting.LIGHT_PURPLE, ChatFormatting.GOLD}).map(Style.EMPTY::withColor).collect(ImmutableList.toImmutableList());
    final Minecraft minecraft;
    final Screen screen;
    final EditBox input;
    final Font font;
    private final boolean commandsOnly;
    private final boolean onlyShowIfCursorPastError;
    final int lineStartOffset;
    final int suggestionLineLimit;
    final boolean anchorToBottom;
    final int fillColor;
    private final List<FormattedCharSequence> commandUsage = Lists.newArrayList();
    private int commandUsagePosition;
    private int commandUsageWidth;
    @Nullable
    private ParseResults<SharedSuggestionProvider> currentParse;
    @Nullable
    private CompletableFuture<Suggestions> pendingSuggestions;
    @Nullable
    private SuggestionsList suggestions;
    private boolean allowSuggestions;
    boolean keepSuggestions;

    public CommandSuggestions(Minecraft $$0, Screen $$1, EditBox $$2, Font $$3, boolean $$4, boolean $$5, int $$6, int $$7, boolean $$8, int $$9) {
        this.minecraft = $$0;
        this.screen = $$1;
        this.input = $$2;
        this.font = $$3;
        this.commandsOnly = $$4;
        this.onlyShowIfCursorPastError = $$5;
        this.lineStartOffset = $$6;
        this.suggestionLineLimit = $$7;
        this.anchorToBottom = $$8;
        this.fillColor = $$9;
        $$2.setFormatter((BiFunction<String, Integer, FormattedCharSequence>)((BiFunction)this::formatChat));
    }

    public void setAllowSuggestions(boolean $$0) {
        this.allowSuggestions = $$0;
        if (!$$0) {
            this.suggestions = null;
        }
    }

    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if (this.suggestions != null && this.suggestions.keyPressed($$0, $$1, $$2)) {
            return true;
        }
        if (this.screen.getFocused() == this.input && $$0 == 258) {
            this.showSuggestions(true);
            return true;
        }
        return false;
    }

    public boolean mouseScrolled(double $$0) {
        return this.suggestions != null && this.suggestions.mouseScrolled(Mth.clamp($$0, -1.0, 1.0));
    }

    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        return this.suggestions != null && this.suggestions.mouseClicked((int)$$0, (int)$$1, $$2);
    }

    public void showSuggestions(boolean $$0) {
        Suggestions $$1;
        if (this.pendingSuggestions != null && this.pendingSuggestions.isDone() && !($$1 = (Suggestions)this.pendingSuggestions.join()).isEmpty()) {
            int $$2 = 0;
            for (Suggestion $$3 : $$1.getList()) {
                $$2 = Math.max((int)$$2, (int)this.font.width($$3.getText()));
            }
            int $$4 = Mth.clamp(this.input.getScreenX($$1.getRange().getStart()), 0, this.input.getScreenX(0) + this.input.getInnerWidth() - $$2);
            int $$5 = this.anchorToBottom ? this.screen.height - 12 : 72;
            this.suggestions = new SuggestionsList($$4, $$5, $$2, this.sortSuggestions($$1), $$0);
        }
    }

    public void hide() {
        this.suggestions = null;
    }

    private List<Suggestion> sortSuggestions(Suggestions $$0) {
        String $$1 = this.input.getValue().substring(0, this.input.getCursorPosition());
        int $$2 = CommandSuggestions.getLastWordIndex($$1);
        String $$3 = $$1.substring($$2).toLowerCase(Locale.ROOT);
        ArrayList $$4 = Lists.newArrayList();
        ArrayList $$5 = Lists.newArrayList();
        for (Suggestion $$6 : $$0.getList()) {
            if ($$6.getText().startsWith($$3) || $$6.getText().startsWith("minecraft:" + $$3)) {
                $$4.add((Object)$$6);
                continue;
            }
            $$5.add((Object)$$6);
        }
        $$4.addAll((Collection)$$5);
        return $$4;
    }

    public void updateCommandInfo() {
        boolean $$2;
        String $$0 = this.input.getValue();
        if (this.currentParse != null && !this.currentParse.getReader().getString().equals((Object)$$0)) {
            this.currentParse = null;
        }
        if (!this.keepSuggestions) {
            this.input.setSuggestion(null);
            this.suggestions = null;
        }
        this.commandUsage.clear();
        StringReader $$1 = new StringReader($$0);
        boolean bl = $$2 = $$1.canRead() && $$1.peek() == '/';
        if ($$2) {
            $$1.skip();
        }
        boolean $$3 = this.commandsOnly || $$2;
        int $$4 = this.input.getCursorPosition();
        if ($$3) {
            int $$6;
            CommandDispatcher<SharedSuggestionProvider> $$5 = this.minecraft.player.connection.getCommands();
            if (this.currentParse == null) {
                this.currentParse = $$5.parse($$1, (Object)this.minecraft.player.connection.getSuggestionsProvider());
            }
            int n = $$6 = this.onlyShowIfCursorPastError ? $$1.getCursor() : 1;
            if (!($$4 < $$6 || this.suggestions != null && this.keepSuggestions)) {
                this.pendingSuggestions = $$5.getCompletionSuggestions(this.currentParse, $$4);
                this.pendingSuggestions.thenRun(() -> {
                    if (!this.pendingSuggestions.isDone()) {
                        return;
                    }
                    this.updateUsageInfo();
                });
            }
        } else {
            String $$7 = $$0.substring(0, $$4);
            int $$8 = CommandSuggestions.getLastWordIndex($$7);
            Collection<String> $$9 = this.minecraft.player.connection.getSuggestionsProvider().getCustomTabSugggestions();
            this.pendingSuggestions = SharedSuggestionProvider.suggest($$9, new SuggestionsBuilder($$7, $$8));
        }
    }

    private static int getLastWordIndex(String $$0) {
        if (Strings.isNullOrEmpty((String)$$0)) {
            return 0;
        }
        int $$1 = 0;
        Matcher $$2 = WHITESPACE_PATTERN.matcher((CharSequence)$$0);
        while ($$2.find()) {
            $$1 = $$2.end();
        }
        return $$1;
    }

    private static FormattedCharSequence getExceptionMessage(CommandSyntaxException $$0) {
        Component $$1 = ComponentUtils.fromMessage($$0.getRawMessage());
        String $$2 = $$0.getContext();
        if ($$2 == null) {
            return $$1.getVisualOrderText();
        }
        return Component.translatable("command.context.parse_error", $$1, $$0.getCursor(), $$2).getVisualOrderText();
    }

    private void updateUsageInfo() {
        if (this.input.getCursorPosition() == this.input.getValue().length()) {
            if (((Suggestions)this.pendingSuggestions.join()).isEmpty() && !this.currentParse.getExceptions().isEmpty()) {
                int $$0 = 0;
                for (Map.Entry $$1 : this.currentParse.getExceptions().entrySet()) {
                    CommandSyntaxException $$2 = (CommandSyntaxException)$$1.getValue();
                    if ($$2.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
                        ++$$0;
                        continue;
                    }
                    this.commandUsage.add((Object)CommandSuggestions.getExceptionMessage($$2));
                }
                if ($$0 > 0) {
                    this.commandUsage.add((Object)CommandSuggestions.getExceptionMessage(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create()));
                }
            } else if (this.currentParse.getReader().canRead()) {
                this.commandUsage.add((Object)CommandSuggestions.getExceptionMessage(Commands.getParseException(this.currentParse)));
            }
        }
        this.commandUsagePosition = 0;
        this.commandUsageWidth = this.screen.width;
        if (this.commandUsage.isEmpty()) {
            this.fillNodeUsage(ChatFormatting.GRAY);
        }
        this.suggestions = null;
        if (this.allowSuggestions && this.minecraft.options.autoSuggestions().get().booleanValue()) {
            this.showSuggestions(false);
        }
    }

    private void fillNodeUsage(ChatFormatting $$0) {
        CommandContextBuilder $$1 = this.currentParse.getContext();
        SuggestionContext $$2 = $$1.findSuggestionContext(this.input.getCursorPosition());
        Map $$3 = this.minecraft.player.connection.getCommands().getSmartUsage($$2.parent, (Object)this.minecraft.player.connection.getSuggestionsProvider());
        ArrayList $$4 = Lists.newArrayList();
        int $$5 = 0;
        Style $$6 = Style.EMPTY.withColor($$0);
        for (Map.Entry $$7 : $$3.entrySet()) {
            if ($$7.getKey() instanceof LiteralCommandNode) continue;
            $$4.add((Object)FormattedCharSequence.forward((String)$$7.getValue(), $$6));
            $$5 = Math.max((int)$$5, (int)this.font.width((String)$$7.getValue()));
        }
        if (!$$4.isEmpty()) {
            this.commandUsage.addAll((Collection)$$4);
            this.commandUsagePosition = Mth.clamp(this.input.getScreenX($$2.startPos), 0, this.input.getScreenX(0) + this.input.getInnerWidth() - $$5);
            this.commandUsageWidth = $$5;
        }
    }

    private FormattedCharSequence formatChat(String $$0, int $$1) {
        if (this.currentParse != null) {
            return CommandSuggestions.formatText(this.currentParse, $$0, $$1);
        }
        return FormattedCharSequence.forward($$0, Style.EMPTY);
    }

    @Nullable
    static String calculateSuggestionSuffix(String $$0, String $$1) {
        if ($$1.startsWith($$0)) {
            return $$1.substring($$0.length());
        }
        return null;
    }

    private static FormattedCharSequence formatText(ParseResults<SharedSuggestionProvider> $$0, String $$1, int $$2) {
        int $$10;
        ArrayList $$3 = Lists.newArrayList();
        int $$4 = 0;
        int $$5 = -1;
        CommandContextBuilder $$6 = $$0.getContext().getLastChild();
        for (ParsedArgument $$7 : $$6.getArguments().values()) {
            int $$8;
            if (++$$5 >= ARGUMENT_STYLES.size()) {
                $$5 = 0;
            }
            if (($$8 = Math.max((int)($$7.getRange().getStart() - $$2), (int)0)) >= $$1.length()) break;
            int $$9 = Math.min((int)($$7.getRange().getEnd() - $$2), (int)$$1.length());
            if ($$9 <= 0) continue;
            $$3.add((Object)FormattedCharSequence.forward($$1.substring($$4, $$8), LITERAL_STYLE));
            $$3.add((Object)FormattedCharSequence.forward($$1.substring($$8, $$9), (Style)ARGUMENT_STYLES.get($$5)));
            $$4 = $$9;
        }
        if ($$0.getReader().canRead() && ($$10 = Math.max((int)($$0.getReader().getCursor() - $$2), (int)0)) < $$1.length()) {
            int $$11 = Math.min((int)($$10 + $$0.getReader().getRemainingLength()), (int)$$1.length());
            $$3.add((Object)FormattedCharSequence.forward($$1.substring($$4, $$10), LITERAL_STYLE));
            $$3.add((Object)FormattedCharSequence.forward($$1.substring($$10, $$11), UNPARSED_STYLE));
            $$4 = $$11;
        }
        $$3.add((Object)FormattedCharSequence.forward($$1.substring($$4), LITERAL_STYLE));
        return FormattedCharSequence.composite((List<FormattedCharSequence>)$$3);
    }

    public void render(PoseStack $$0, int $$1, int $$2) {
        if (!this.renderSuggestions($$0, $$1, $$2)) {
            this.renderUsage($$0);
        }
    }

    public boolean renderSuggestions(PoseStack $$0, int $$1, int $$2) {
        if (this.suggestions != null) {
            this.suggestions.render($$0, $$1, $$2);
            return true;
        }
        return false;
    }

    public void renderUsage(PoseStack $$0) {
        int $$1 = 0;
        for (FormattedCharSequence $$2 : this.commandUsage) {
            int $$3 = this.anchorToBottom ? this.screen.height - 14 - 13 - 12 * $$1 : 72 + 12 * $$1;
            GuiComponent.fill($$0, this.commandUsagePosition - 1, $$3, this.commandUsagePosition + this.commandUsageWidth + 1, $$3 + 12, this.fillColor);
            this.font.drawShadow($$0, $$2, (float)this.commandUsagePosition, (float)($$3 + 2), -1);
            ++$$1;
        }
    }

    public String getNarrationMessage() {
        if (this.suggestions != null) {
            return "\n" + this.suggestions.getNarrationMessage();
        }
        return "";
    }

    public class SuggestionsList {
        private final Rect2i rect;
        private final String originalContents;
        private final List<Suggestion> suggestionList;
        private int offset;
        private int current;
        private Vec2 lastMouse = Vec2.ZERO;
        private boolean tabCycles;
        private int lastNarratedEntry;

        SuggestionsList(int $$1, int $$2, int $$3, List<Suggestion> $$4, boolean $$5) {
            int $$6 = $$1 - 1;
            int $$7 = CommandSuggestions.this.anchorToBottom ? $$2 - 3 - Math.min((int)$$4.size(), (int)CommandSuggestions.this.suggestionLineLimit) * 12 : $$2;
            this.rect = new Rect2i($$6, $$7, $$3 + 1, Math.min((int)$$4.size(), (int)CommandSuggestions.this.suggestionLineLimit) * 12);
            this.originalContents = CommandSuggestions.this.input.getValue();
            this.lastNarratedEntry = $$5 ? -1 : 0;
            this.suggestionList = $$4;
            this.select(0);
        }

        public void render(PoseStack $$0, int $$1, int $$2) {
            Message $$14;
            boolean $$8;
            int $$3 = Math.min((int)this.suggestionList.size(), (int)CommandSuggestions.this.suggestionLineLimit);
            int $$4 = -5592406;
            boolean $$5 = this.offset > 0;
            boolean $$6 = this.suggestionList.size() > this.offset + $$3;
            boolean $$7 = $$5 || $$6;
            boolean bl = $$8 = this.lastMouse.x != (float)$$1 || this.lastMouse.y != (float)$$2;
            if ($$8) {
                this.lastMouse = new Vec2($$1, $$2);
            }
            if ($$7) {
                GuiComponent.fill($$0, this.rect.getX(), this.rect.getY() - 1, this.rect.getX() + this.rect.getWidth(), this.rect.getY(), CommandSuggestions.this.fillColor);
                GuiComponent.fill($$0, this.rect.getX(), this.rect.getY() + this.rect.getHeight(), this.rect.getX() + this.rect.getWidth(), this.rect.getY() + this.rect.getHeight() + 1, CommandSuggestions.this.fillColor);
                if ($$5) {
                    for (int $$9 = 0; $$9 < this.rect.getWidth(); ++$$9) {
                        if ($$9 % 2 != 0) continue;
                        GuiComponent.fill($$0, this.rect.getX() + $$9, this.rect.getY() - 1, this.rect.getX() + $$9 + 1, this.rect.getY(), -1);
                    }
                }
                if ($$6) {
                    for (int $$10 = 0; $$10 < this.rect.getWidth(); ++$$10) {
                        if ($$10 % 2 != 0) continue;
                        GuiComponent.fill($$0, this.rect.getX() + $$10, this.rect.getY() + this.rect.getHeight(), this.rect.getX() + $$10 + 1, this.rect.getY() + this.rect.getHeight() + 1, -1);
                    }
                }
            }
            boolean $$11 = false;
            for (int $$12 = 0; $$12 < $$3; ++$$12) {
                Suggestion $$13 = (Suggestion)this.suggestionList.get($$12 + this.offset);
                GuiComponent.fill($$0, this.rect.getX(), this.rect.getY() + 12 * $$12, this.rect.getX() + this.rect.getWidth(), this.rect.getY() + 12 * $$12 + 12, CommandSuggestions.this.fillColor);
                if ($$1 > this.rect.getX() && $$1 < this.rect.getX() + this.rect.getWidth() && $$2 > this.rect.getY() + 12 * $$12 && $$2 < this.rect.getY() + 12 * $$12 + 12) {
                    if ($$8) {
                        this.select($$12 + this.offset);
                    }
                    $$11 = true;
                }
                CommandSuggestions.this.font.drawShadow($$0, $$13.getText(), (float)(this.rect.getX() + 1), (float)(this.rect.getY() + 2 + 12 * $$12), $$12 + this.offset == this.current ? -256 : -5592406);
            }
            if ($$11 && ($$14 = ((Suggestion)this.suggestionList.get(this.current)).getTooltip()) != null) {
                CommandSuggestions.this.screen.renderTooltip($$0, ComponentUtils.fromMessage($$14), $$1, $$2);
            }
        }

        public boolean mouseClicked(int $$0, int $$1, int $$2) {
            if (!this.rect.contains($$0, $$1)) {
                return false;
            }
            int $$3 = ($$1 - this.rect.getY()) / 12 + this.offset;
            if ($$3 >= 0 && $$3 < this.suggestionList.size()) {
                this.select($$3);
                this.useSuggestion();
            }
            return true;
        }

        public boolean mouseScrolled(double $$0) {
            int $$2;
            int $$1 = (int)(CommandSuggestions.this.minecraft.mouseHandler.xpos() * (double)CommandSuggestions.this.minecraft.getWindow().getGuiScaledWidth() / (double)CommandSuggestions.this.minecraft.getWindow().getScreenWidth());
            if (this.rect.contains($$1, $$2 = (int)(CommandSuggestions.this.minecraft.mouseHandler.ypos() * (double)CommandSuggestions.this.minecraft.getWindow().getGuiScaledHeight() / (double)CommandSuggestions.this.minecraft.getWindow().getScreenHeight()))) {
                this.offset = Mth.clamp((int)((double)this.offset - $$0), 0, Math.max((int)(this.suggestionList.size() - CommandSuggestions.this.suggestionLineLimit), (int)0));
                return true;
            }
            return false;
        }

        public boolean keyPressed(int $$0, int $$1, int $$2) {
            if ($$0 == 265) {
                this.cycle(-1);
                this.tabCycles = false;
                return true;
            }
            if ($$0 == 264) {
                this.cycle(1);
                this.tabCycles = false;
                return true;
            }
            if ($$0 == 258) {
                if (this.tabCycles) {
                    this.cycle(Screen.hasShiftDown() ? -1 : 1);
                }
                this.useSuggestion();
                return true;
            }
            if ($$0 == 256) {
                CommandSuggestions.this.hide();
                return true;
            }
            return false;
        }

        public void cycle(int $$0) {
            this.select(this.current + $$0);
            int $$1 = this.offset;
            int $$2 = this.offset + CommandSuggestions.this.suggestionLineLimit - 1;
            if (this.current < $$1) {
                this.offset = Mth.clamp(this.current, 0, Math.max((int)(this.suggestionList.size() - CommandSuggestions.this.suggestionLineLimit), (int)0));
            } else if (this.current > $$2) {
                this.offset = Mth.clamp(this.current + CommandSuggestions.this.lineStartOffset - CommandSuggestions.this.suggestionLineLimit, 0, Math.max((int)(this.suggestionList.size() - CommandSuggestions.this.suggestionLineLimit), (int)0));
            }
        }

        public void select(int $$0) {
            this.current = $$0;
            if (this.current < 0) {
                this.current += this.suggestionList.size();
            }
            if (this.current >= this.suggestionList.size()) {
                this.current -= this.suggestionList.size();
            }
            Suggestion $$1 = (Suggestion)this.suggestionList.get(this.current);
            CommandSuggestions.this.input.setSuggestion(CommandSuggestions.calculateSuggestionSuffix(CommandSuggestions.this.input.getValue(), $$1.apply(this.originalContents)));
            if (this.lastNarratedEntry != this.current) {
                CommandSuggestions.this.minecraft.getNarrator().sayNow(this.getNarrationMessage());
            }
        }

        public void useSuggestion() {
            Suggestion $$0 = (Suggestion)this.suggestionList.get(this.current);
            CommandSuggestions.this.keepSuggestions = true;
            CommandSuggestions.this.input.setValue($$0.apply(this.originalContents));
            int $$1 = $$0.getRange().getStart() + $$0.getText().length();
            CommandSuggestions.this.input.setCursorPosition($$1);
            CommandSuggestions.this.input.setHighlightPos($$1);
            this.select(this.current);
            CommandSuggestions.this.keepSuggestions = false;
            this.tabCycles = true;
        }

        Component getNarrationMessage() {
            this.lastNarratedEntry = this.current;
            Suggestion $$0 = (Suggestion)this.suggestionList.get(this.current);
            Message $$1 = $$0.getTooltip();
            if ($$1 != null) {
                return Component.translatable("narration.suggestion.tooltip", this.current + 1, this.suggestionList.size(), $$0.getText(), $$1);
            }
            return Component.translatable("narration.suggestion", this.current + 1, this.suggestionList.size(), $$0.getText());
        }
    }
}