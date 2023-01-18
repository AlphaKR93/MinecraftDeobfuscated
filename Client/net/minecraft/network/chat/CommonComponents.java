/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.Arrays
 *  java.util.Collection
 */
package net.minecraft.network.chat;

import java.util.Arrays;
import java.util.Collection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;

public class CommonComponents {
    public static final Component EMPTY = Component.empty();
    public static final Component OPTION_ON = Component.translatable("options.on");
    public static final Component OPTION_OFF = Component.translatable("options.off");
    public static final Component GUI_DONE = Component.translatable("gui.done");
    public static final Component GUI_CANCEL = Component.translatable("gui.cancel");
    public static final Component GUI_YES = Component.translatable("gui.yes");
    public static final Component GUI_NO = Component.translatable("gui.no");
    public static final Component GUI_PROCEED = Component.translatable("gui.proceed");
    public static final Component GUI_CONTINUE = Component.translatable("gui.continue");
    public static final Component GUI_BACK = Component.translatable("gui.back");
    public static final Component GUI_ACKNOWLEDGE = Component.translatable("gui.acknowledge");
    public static final Component CONNECT_FAILED = Component.translatable("connect.failed");
    public static final Component NEW_LINE = Component.literal("\n");
    public static final Component NARRATION_SEPARATOR = Component.literal(". ");
    public static final Component ELLIPSIS = Component.literal("...");
    public static final Component SPACE = CommonComponents.space();

    public static MutableComponent space() {
        return Component.literal(" ");
    }

    public static MutableComponent days(long $$0) {
        return Component.translatable("gui.days", $$0);
    }

    public static MutableComponent hours(long $$0) {
        return Component.translatable("gui.hours", $$0);
    }

    public static MutableComponent minutes(long $$0) {
        return Component.translatable("gui.minutes", $$0);
    }

    public static Component optionStatus(boolean $$0) {
        return $$0 ? OPTION_ON : OPTION_OFF;
    }

    public static MutableComponent optionStatus(Component $$0, boolean $$1) {
        return Component.translatable($$1 ? "options.on.composed" : "options.off.composed", $$0);
    }

    public static MutableComponent optionNameValue(Component $$0, Component $$1) {
        return Component.translatable("options.generic_value", $$0, $$1);
    }

    public static MutableComponent joinForNarration(Component $$0, Component $$1) {
        return Component.empty().append($$0).append(NARRATION_SEPARATOR).append($$1);
    }

    public static Component joinLines(Component ... $$0) {
        return CommonComponents.joinLines((Collection<? extends Component>)Arrays.asList((Object[])$$0));
    }

    public static Component joinLines(Collection<? extends Component> $$0) {
        return ComponentUtils.formatList($$0, NEW_LINE);
    }
}