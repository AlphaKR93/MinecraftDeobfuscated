/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.logging.LogUtils
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Optional
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.network.chat.contents;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;

public class SelectorContents
implements ComponentContents {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final String pattern;
    @Nullable
    private final EntitySelector selector;
    protected final Optional<Component> separator;

    public SelectorContents(String $$0, Optional<Component> $$1) {
        this.pattern = $$0;
        this.separator = $$1;
        this.selector = SelectorContents.parseSelector($$0);
    }

    @Nullable
    private static EntitySelector parseSelector(String $$0) {
        EntitySelector $$1 = null;
        try {
            EntitySelectorParser $$2 = new EntitySelectorParser(new StringReader($$0));
            $$1 = $$2.parse();
        }
        catch (CommandSyntaxException $$3) {
            LOGGER.warn("Invalid selector component: {}: {}", (Object)$$0, (Object)$$3.getMessage());
        }
        return $$1;
    }

    public String getPattern() {
        return this.pattern;
    }

    @Nullable
    public EntitySelector getSelector() {
        return this.selector;
    }

    public Optional<Component> getSeparator() {
        return this.separator;
    }

    @Override
    public MutableComponent resolve(@Nullable CommandSourceStack $$0, @Nullable Entity $$1, int $$2) throws CommandSyntaxException {
        if ($$0 == null || this.selector == null) {
            return Component.empty();
        }
        Optional<MutableComponent> $$3 = ComponentUtils.updateForEntity($$0, this.separator, $$1, $$2);
        return ComponentUtils.formatList(this.selector.findEntities($$0), $$3, Entity::getDisplayName);
    }

    @Override
    public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> $$0, Style $$1) {
        return $$0.accept($$1, this.pattern);
    }

    @Override
    public <T> Optional<T> visit(FormattedText.ContentConsumer<T> $$0) {
        return $$0.accept(this.pattern);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if (!($$0 instanceof SelectorContents)) return false;
        SelectorContents $$1 = (SelectorContents)$$0;
        if (!this.pattern.equals((Object)$$1.pattern)) return false;
        if (!this.separator.equals($$1.separator)) return false;
        return true;
    }

    public int hashCode() {
        int $$0 = this.pattern.hashCode();
        $$0 = 31 * $$0 + this.separator.hashCode();
        return $$0;
    }

    public String toString() {
        return "pattern{" + this.pattern + "}";
    }
}