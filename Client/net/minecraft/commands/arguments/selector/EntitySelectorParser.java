/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.primitives.Doubles
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  java.lang.Double
 *  java.lang.IllegalArgumentException
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Collections
 *  java.util.List
 *  java.util.UUID
 *  java.util.concurrent.CompletableFuture
 *  java.util.function.BiConsumer
 *  java.util.function.BiFunction
 *  java.util.function.Consumer
 *  java.util.function.Function
 *  java.util.function.Predicate
 *  java.util.function.ToDoubleFunction
 *  javax.annotation.Nullable
 */
package net.minecraft.commands.arguments.selector;

import com.google.common.primitives.Doubles;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.WrappedMinMaxBounds;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.options.EntitySelectorOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EntitySelectorParser {
    public static final char SYNTAX_SELECTOR_START = '@';
    private static final char SYNTAX_OPTIONS_START = '[';
    private static final char SYNTAX_OPTIONS_END = ']';
    public static final char SYNTAX_OPTIONS_KEY_VALUE_SEPARATOR = '=';
    private static final char SYNTAX_OPTIONS_SEPARATOR = ',';
    public static final char SYNTAX_NOT = '!';
    public static final char SYNTAX_TAG = '#';
    private static final char SELECTOR_NEAREST_PLAYER = 'p';
    private static final char SELECTOR_ALL_PLAYERS = 'a';
    private static final char SELECTOR_RANDOM_PLAYERS = 'r';
    private static final char SELECTOR_CURRENT_ENTITY = 's';
    private static final char SELECTOR_ALL_ENTITIES = 'e';
    public static final SimpleCommandExceptionType ERROR_INVALID_NAME_OR_UUID = new SimpleCommandExceptionType((Message)Component.translatable("argument.entity.invalid"));
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_SELECTOR_TYPE = new DynamicCommandExceptionType($$0 -> Component.translatable("argument.entity.selector.unknown", $$0));
    public static final SimpleCommandExceptionType ERROR_SELECTORS_NOT_ALLOWED = new SimpleCommandExceptionType((Message)Component.translatable("argument.entity.selector.not_allowed"));
    public static final SimpleCommandExceptionType ERROR_MISSING_SELECTOR_TYPE = new SimpleCommandExceptionType((Message)Component.translatable("argument.entity.selector.missing"));
    public static final SimpleCommandExceptionType ERROR_EXPECTED_END_OF_OPTIONS = new SimpleCommandExceptionType((Message)Component.translatable("argument.entity.options.unterminated"));
    public static final DynamicCommandExceptionType ERROR_EXPECTED_OPTION_VALUE = new DynamicCommandExceptionType($$0 -> Component.translatable("argument.entity.options.valueless", $$0));
    public static final BiConsumer<Vec3, List<? extends Entity>> ORDER_NEAREST = ($$0, $$12) -> $$12.sort(($$1, $$2) -> Doubles.compare((double)$$1.distanceToSqr((Vec3)$$0), (double)$$2.distanceToSqr((Vec3)$$0)));
    public static final BiConsumer<Vec3, List<? extends Entity>> ORDER_FURTHEST = ($$0, $$12) -> $$12.sort(($$1, $$2) -> Doubles.compare((double)$$2.distanceToSqr((Vec3)$$0), (double)$$1.distanceToSqr((Vec3)$$0)));
    public static final BiConsumer<Vec3, List<? extends Entity>> ORDER_RANDOM = ($$0, $$1) -> Collections.shuffle((List)$$1);
    public static final BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> SUGGEST_NOTHING = ($$0, $$1) -> $$0.buildFuture();
    private final StringReader reader;
    private final boolean allowSelectors;
    private int maxResults;
    private boolean includesEntities;
    private boolean worldLimited;
    private MinMaxBounds.Doubles distance = MinMaxBounds.Doubles.ANY;
    private MinMaxBounds.Ints level = MinMaxBounds.Ints.ANY;
    @Nullable
    private Double x;
    @Nullable
    private Double y;
    @Nullable
    private Double z;
    @Nullable
    private Double deltaX;
    @Nullable
    private Double deltaY;
    @Nullable
    private Double deltaZ;
    private WrappedMinMaxBounds rotX = WrappedMinMaxBounds.ANY;
    private WrappedMinMaxBounds rotY = WrappedMinMaxBounds.ANY;
    private Predicate<Entity> predicate = $$0 -> true;
    private BiConsumer<Vec3, List<? extends Entity>> order = EntitySelector.ORDER_ARBITRARY;
    private boolean currentEntity;
    @Nullable
    private String playerName;
    private int startPosition;
    @Nullable
    private UUID entityUUID;
    private BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> suggestions = SUGGEST_NOTHING;
    private boolean hasNameEquals;
    private boolean hasNameNotEquals;
    private boolean isLimited;
    private boolean isSorted;
    private boolean hasGamemodeEquals;
    private boolean hasGamemodeNotEquals;
    private boolean hasTeamEquals;
    private boolean hasTeamNotEquals;
    @Nullable
    private EntityType<?> type;
    private boolean typeInverse;
    private boolean hasScores;
    private boolean hasAdvancements;
    private boolean usesSelectors;

    public EntitySelectorParser(StringReader $$0) {
        this($$0, true);
    }

    public EntitySelectorParser(StringReader $$02, boolean $$1) {
        this.reader = $$02;
        this.allowSelectors = $$1;
    }

    public EntitySelector getSelector() {
        Function $$5;
        AABB $$3;
        if (this.deltaX != null || this.deltaY != null || this.deltaZ != null) {
            AABB $$02 = this.createAabb(this.deltaX == null ? 0.0 : this.deltaX, this.deltaY == null ? 0.0 : this.deltaY, this.deltaZ == null ? 0.0 : this.deltaZ);
        } else if (this.distance.getMax() != null) {
            double $$1 = (Double)this.distance.getMax();
            AABB $$2 = new AABB(-$$1, -$$1, -$$1, $$1 + 1.0, $$1 + 1.0, $$1 + 1.0);
        } else {
            $$3 = null;
        }
        if (this.x == null && this.y == null && this.z == null) {
            Function $$4 = $$0 -> $$0;
        } else {
            $$5 = $$0 -> new Vec3(this.x == null ? $$0.x : this.x, this.y == null ? $$0.y : this.y, this.z == null ? $$0.z : this.z);
        }
        return new EntitySelector(this.maxResults, this.includesEntities, this.worldLimited, this.predicate, this.distance, (Function<Vec3, Vec3>)$$5, $$3, this.order, this.currentEntity, this.playerName, this.entityUUID, this.type, this.usesSelectors);
    }

    private AABB createAabb(double $$0, double $$1, double $$2) {
        boolean $$3 = $$0 < 0.0;
        boolean $$4 = $$1 < 0.0;
        boolean $$5 = $$2 < 0.0;
        double $$6 = $$3 ? $$0 : 0.0;
        double $$7 = $$4 ? $$1 : 0.0;
        double $$8 = $$5 ? $$2 : 0.0;
        double $$9 = ($$3 ? 0.0 : $$0) + 1.0;
        double $$10 = ($$4 ? 0.0 : $$1) + 1.0;
        double $$11 = ($$5 ? 0.0 : $$2) + 1.0;
        return new AABB($$6, $$7, $$8, $$9, $$10, $$11);
    }

    private void finalizePredicates() {
        if (this.rotX != WrappedMinMaxBounds.ANY) {
            this.predicate = this.predicate.and(this.createRotationPredicate(this.rotX, (ToDoubleFunction<Entity>)((ToDoubleFunction)Entity::getXRot)));
        }
        if (this.rotY != WrappedMinMaxBounds.ANY) {
            this.predicate = this.predicate.and(this.createRotationPredicate(this.rotY, (ToDoubleFunction<Entity>)((ToDoubleFunction)Entity::getYRot)));
        }
        if (!this.level.isAny()) {
            this.predicate = this.predicate.and($$0 -> {
                if (!($$0 instanceof ServerPlayer)) {
                    return false;
                }
                return this.level.matches(((ServerPlayer)$$0).experienceLevel);
            });
        }
    }

    private Predicate<Entity> createRotationPredicate(WrappedMinMaxBounds $$0, ToDoubleFunction<Entity> $$1) {
        double $$2 = Mth.wrapDegrees($$0.getMin() == null ? 0.0f : $$0.getMin().floatValue());
        double $$32 = Mth.wrapDegrees($$0.getMax() == null ? 359.0f : $$0.getMax().floatValue());
        return $$3 -> {
            double $$4 = Mth.wrapDegrees($$1.applyAsDouble($$3));
            if ($$2 > $$32) {
                return $$4 >= $$2 || $$4 <= $$32;
            }
            return $$4 >= $$2 && $$4 <= $$32;
        };
    }

    protected void parseSelector() throws CommandSyntaxException {
        this.usesSelectors = true;
        this.suggestions = this::suggestSelector;
        if (!this.reader.canRead()) {
            throw ERROR_MISSING_SELECTOR_TYPE.createWithContext((ImmutableStringReader)this.reader);
        }
        int $$0 = this.reader.getCursor();
        char $$1 = this.reader.read();
        if ($$1 == 'p') {
            this.maxResults = 1;
            this.includesEntities = false;
            this.order = ORDER_NEAREST;
            this.limitToType(EntityType.PLAYER);
        } else if ($$1 == 'a') {
            this.maxResults = Integer.MAX_VALUE;
            this.includesEntities = false;
            this.order = EntitySelector.ORDER_ARBITRARY;
            this.limitToType(EntityType.PLAYER);
        } else if ($$1 == 'r') {
            this.maxResults = 1;
            this.includesEntities = false;
            this.order = ORDER_RANDOM;
            this.limitToType(EntityType.PLAYER);
        } else if ($$1 == 's') {
            this.maxResults = 1;
            this.includesEntities = true;
            this.currentEntity = true;
        } else if ($$1 == 'e') {
            this.maxResults = Integer.MAX_VALUE;
            this.includesEntities = true;
            this.order = EntitySelector.ORDER_ARBITRARY;
            this.predicate = Entity::isAlive;
        } else {
            this.reader.setCursor($$0);
            throw ERROR_UNKNOWN_SELECTOR_TYPE.createWithContext((ImmutableStringReader)this.reader, (Object)("@" + String.valueOf((char)$$1)));
        }
        this.suggestions = this::suggestOpenOptions;
        if (this.reader.canRead() && this.reader.peek() == '[') {
            this.reader.skip();
            this.suggestions = this::suggestOptionsKeyOrClose;
            this.parseOptions();
        }
    }

    protected void parseNameOrUUID() throws CommandSyntaxException {
        if (this.reader.canRead()) {
            this.suggestions = this::suggestName;
        }
        int $$0 = this.reader.getCursor();
        String $$1 = this.reader.readString();
        try {
            this.entityUUID = UUID.fromString((String)$$1);
            this.includesEntities = true;
        }
        catch (IllegalArgumentException $$2) {
            if ($$1.isEmpty() || $$1.length() > 16) {
                this.reader.setCursor($$0);
                throw ERROR_INVALID_NAME_OR_UUID.createWithContext((ImmutableStringReader)this.reader);
            }
            this.includesEntities = false;
            this.playerName = $$1;
        }
        this.maxResults = 1;
    }

    protected void parseOptions() throws CommandSyntaxException {
        this.suggestions = this::suggestOptionsKey;
        this.reader.skipWhitespace();
        while (this.reader.canRead() && this.reader.peek() != ']') {
            this.reader.skipWhitespace();
            int $$0 = this.reader.getCursor();
            String $$1 = this.reader.readString();
            EntitySelectorOptions.Modifier $$2 = EntitySelectorOptions.get(this, $$1, $$0);
            this.reader.skipWhitespace();
            if (!this.reader.canRead() || this.reader.peek() != '=') {
                this.reader.setCursor($$0);
                throw ERROR_EXPECTED_OPTION_VALUE.createWithContext((ImmutableStringReader)this.reader, (Object)$$1);
            }
            this.reader.skip();
            this.reader.skipWhitespace();
            this.suggestions = SUGGEST_NOTHING;
            $$2.handle(this);
            this.reader.skipWhitespace();
            this.suggestions = this::suggestOptionsNextOrClose;
            if (!this.reader.canRead()) continue;
            if (this.reader.peek() == ',') {
                this.reader.skip();
                this.suggestions = this::suggestOptionsKey;
                continue;
            }
            if (this.reader.peek() == ']') break;
            throw ERROR_EXPECTED_END_OF_OPTIONS.createWithContext((ImmutableStringReader)this.reader);
        }
        if (!this.reader.canRead()) {
            throw ERROR_EXPECTED_END_OF_OPTIONS.createWithContext((ImmutableStringReader)this.reader);
        }
        this.reader.skip();
        this.suggestions = SUGGEST_NOTHING;
    }

    public boolean shouldInvertValue() {
        this.reader.skipWhitespace();
        if (this.reader.canRead() && this.reader.peek() == '!') {
            this.reader.skip();
            this.reader.skipWhitespace();
            return true;
        }
        return false;
    }

    public boolean isTag() {
        this.reader.skipWhitespace();
        if (this.reader.canRead() && this.reader.peek() == '#') {
            this.reader.skip();
            this.reader.skipWhitespace();
            return true;
        }
        return false;
    }

    public StringReader getReader() {
        return this.reader;
    }

    public void addPredicate(Predicate<Entity> $$0) {
        this.predicate = this.predicate.and($$0);
    }

    public void setWorldLimited() {
        this.worldLimited = true;
    }

    public MinMaxBounds.Doubles getDistance() {
        return this.distance;
    }

    public void setDistance(MinMaxBounds.Doubles $$0) {
        this.distance = $$0;
    }

    public MinMaxBounds.Ints getLevel() {
        return this.level;
    }

    public void setLevel(MinMaxBounds.Ints $$0) {
        this.level = $$0;
    }

    public WrappedMinMaxBounds getRotX() {
        return this.rotX;
    }

    public void setRotX(WrappedMinMaxBounds $$0) {
        this.rotX = $$0;
    }

    public WrappedMinMaxBounds getRotY() {
        return this.rotY;
    }

    public void setRotY(WrappedMinMaxBounds $$0) {
        this.rotY = $$0;
    }

    @Nullable
    public Double getX() {
        return this.x;
    }

    @Nullable
    public Double getY() {
        return this.y;
    }

    @Nullable
    public Double getZ() {
        return this.z;
    }

    public void setX(double $$0) {
        this.x = $$0;
    }

    public void setY(double $$0) {
        this.y = $$0;
    }

    public void setZ(double $$0) {
        this.z = $$0;
    }

    public void setDeltaX(double $$0) {
        this.deltaX = $$0;
    }

    public void setDeltaY(double $$0) {
        this.deltaY = $$0;
    }

    public void setDeltaZ(double $$0) {
        this.deltaZ = $$0;
    }

    @Nullable
    public Double getDeltaX() {
        return this.deltaX;
    }

    @Nullable
    public Double getDeltaY() {
        return this.deltaY;
    }

    @Nullable
    public Double getDeltaZ() {
        return this.deltaZ;
    }

    public void setMaxResults(int $$0) {
        this.maxResults = $$0;
    }

    public void setIncludesEntities(boolean $$0) {
        this.includesEntities = $$0;
    }

    public BiConsumer<Vec3, List<? extends Entity>> getOrder() {
        return this.order;
    }

    public void setOrder(BiConsumer<Vec3, List<? extends Entity>> $$0) {
        this.order = $$0;
    }

    public EntitySelector parse() throws CommandSyntaxException {
        this.startPosition = this.reader.getCursor();
        this.suggestions = this::suggestNameOrSelector;
        if (this.reader.canRead() && this.reader.peek() == '@') {
            if (!this.allowSelectors) {
                throw ERROR_SELECTORS_NOT_ALLOWED.createWithContext((ImmutableStringReader)this.reader);
            }
            this.reader.skip();
            this.parseSelector();
        } else {
            this.parseNameOrUUID();
        }
        this.finalizePredicates();
        return this.getSelector();
    }

    private static void fillSelectorSuggestions(SuggestionsBuilder $$0) {
        $$0.suggest("@p", (Message)Component.translatable("argument.entity.selector.nearestPlayer"));
        $$0.suggest("@a", (Message)Component.translatable("argument.entity.selector.allPlayers"));
        $$0.suggest("@r", (Message)Component.translatable("argument.entity.selector.randomPlayer"));
        $$0.suggest("@s", (Message)Component.translatable("argument.entity.selector.self"));
        $$0.suggest("@e", (Message)Component.translatable("argument.entity.selector.allEntities"));
    }

    private CompletableFuture<Suggestions> suggestNameOrSelector(SuggestionsBuilder $$0, Consumer<SuggestionsBuilder> $$1) {
        $$1.accept((Object)$$0);
        if (this.allowSelectors) {
            EntitySelectorParser.fillSelectorSuggestions($$0);
        }
        return $$0.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestName(SuggestionsBuilder $$0, Consumer<SuggestionsBuilder> $$1) {
        SuggestionsBuilder $$2 = $$0.createOffset(this.startPosition);
        $$1.accept((Object)$$2);
        return $$0.add($$2).buildFuture();
    }

    private CompletableFuture<Suggestions> suggestSelector(SuggestionsBuilder $$0, Consumer<SuggestionsBuilder> $$1) {
        SuggestionsBuilder $$2 = $$0.createOffset($$0.getStart() - 1);
        EntitySelectorParser.fillSelectorSuggestions($$2);
        $$0.add($$2);
        return $$0.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestOpenOptions(SuggestionsBuilder $$0, Consumer<SuggestionsBuilder> $$1) {
        $$0.suggest(String.valueOf((char)'['));
        return $$0.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestOptionsKeyOrClose(SuggestionsBuilder $$0, Consumer<SuggestionsBuilder> $$1) {
        $$0.suggest(String.valueOf((char)']'));
        EntitySelectorOptions.suggestNames(this, $$0);
        return $$0.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestOptionsKey(SuggestionsBuilder $$0, Consumer<SuggestionsBuilder> $$1) {
        EntitySelectorOptions.suggestNames(this, $$0);
        return $$0.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestOptionsNextOrClose(SuggestionsBuilder $$0, Consumer<SuggestionsBuilder> $$1) {
        $$0.suggest(String.valueOf((char)','));
        $$0.suggest(String.valueOf((char)']'));
        return $$0.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestEquals(SuggestionsBuilder $$0, Consumer<SuggestionsBuilder> $$1) {
        $$0.suggest(String.valueOf((char)'='));
        return $$0.buildFuture();
    }

    public boolean isCurrentEntity() {
        return this.currentEntity;
    }

    public void setSuggestions(BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> $$0) {
        this.suggestions = $$0;
    }

    public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder $$0, Consumer<SuggestionsBuilder> $$1) {
        return (CompletableFuture)this.suggestions.apply((Object)$$0.createOffset(this.reader.getCursor()), $$1);
    }

    public boolean hasNameEquals() {
        return this.hasNameEquals;
    }

    public void setHasNameEquals(boolean $$0) {
        this.hasNameEquals = $$0;
    }

    public boolean hasNameNotEquals() {
        return this.hasNameNotEquals;
    }

    public void setHasNameNotEquals(boolean $$0) {
        this.hasNameNotEquals = $$0;
    }

    public boolean isLimited() {
        return this.isLimited;
    }

    public void setLimited(boolean $$0) {
        this.isLimited = $$0;
    }

    public boolean isSorted() {
        return this.isSorted;
    }

    public void setSorted(boolean $$0) {
        this.isSorted = $$0;
    }

    public boolean hasGamemodeEquals() {
        return this.hasGamemodeEquals;
    }

    public void setHasGamemodeEquals(boolean $$0) {
        this.hasGamemodeEquals = $$0;
    }

    public boolean hasGamemodeNotEquals() {
        return this.hasGamemodeNotEquals;
    }

    public void setHasGamemodeNotEquals(boolean $$0) {
        this.hasGamemodeNotEquals = $$0;
    }

    public boolean hasTeamEquals() {
        return this.hasTeamEquals;
    }

    public void setHasTeamEquals(boolean $$0) {
        this.hasTeamEquals = $$0;
    }

    public boolean hasTeamNotEquals() {
        return this.hasTeamNotEquals;
    }

    public void setHasTeamNotEquals(boolean $$0) {
        this.hasTeamNotEquals = $$0;
    }

    public void limitToType(EntityType<?> $$0) {
        this.type = $$0;
    }

    public void setTypeLimitedInversely() {
        this.typeInverse = true;
    }

    public boolean isTypeLimited() {
        return this.type != null;
    }

    public boolean isTypeLimitedInversely() {
        return this.typeInverse;
    }

    public boolean hasScores() {
        return this.hasScores;
    }

    public void setHasScores(boolean $$0) {
        this.hasScores = $$0;
    }

    public boolean hasAdvancements() {
        return this.hasAdvancements;
    }

    public void setHasAdvancements(boolean $$0) {
        this.hasAdvancements = $$0;
    }
}