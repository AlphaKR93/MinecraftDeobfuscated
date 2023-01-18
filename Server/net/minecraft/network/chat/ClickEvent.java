/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.Map
 *  java.util.stream.Collectors
 */
package net.minecraft.network.chat;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class ClickEvent {
    private final Action action;
    private final String value;

    public ClickEvent(Action $$0, String $$1) {
        this.action = $$0;
        this.value = $$1;
    }

    public Action getAction() {
        return this.action;
    }

    public String getValue() {
        return this.value;
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 == null || this.getClass() != $$0.getClass()) {
            return false;
        }
        ClickEvent $$1 = (ClickEvent)$$0;
        if (this.action != $$1.action) {
            return false;
        }
        return !(this.value != null ? !this.value.equals((Object)$$1.value) : $$1.value != null);
    }

    public String toString() {
        return "ClickEvent{action=" + this.action + ", value='" + this.value + "'}";
    }

    public int hashCode() {
        int $$0 = this.action.hashCode();
        $$0 = 31 * $$0 + (this.value != null ? this.value.hashCode() : 0);
        return $$0;
    }

    public static enum Action {
        OPEN_URL("open_url", true),
        OPEN_FILE("open_file", false),
        RUN_COMMAND("run_command", true),
        SUGGEST_COMMAND("suggest_command", true),
        CHANGE_PAGE("change_page", true),
        COPY_TO_CLIPBOARD("copy_to_clipboard", true);

        private static final Map<String, Action> LOOKUP;
        private final boolean allowFromServer;
        private final String name;

        private Action(String $$0, boolean $$1) {
            this.name = $$0;
            this.allowFromServer = $$1;
        }

        public boolean isAllowedFromServer() {
            return this.allowFromServer;
        }

        public String getName() {
            return this.name;
        }

        public static Action getByName(String $$0) {
            return (Action)((Object)LOOKUP.get((Object)$$0));
        }

        static {
            LOOKUP = (Map)Arrays.stream((Object[])Action.values()).collect(Collectors.toMap(Action::getName, $$0 -> $$0));
        }
    }
}