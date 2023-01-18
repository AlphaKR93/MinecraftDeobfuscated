/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Record
 *  java.lang.String
 *  java.lang.invoke.MethodHandle
 *  java.lang.runtime.ObjectMethods
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;

public interface ComponentPath {
    public static ComponentPath leaf(GuiEventListener $$0) {
        return new Leaf($$0);
    }

    @Nullable
    public static ComponentPath path(ContainerEventHandler $$0, @Nullable ComponentPath $$1) {
        if ($$1 == null) {
            return null;
        }
        return new Path($$0, $$1);
    }

    public static ComponentPath path(GuiEventListener $$0, ContainerEventHandler ... $$1) {
        ComponentPath $$2 = ComponentPath.leaf($$0);
        for (ContainerEventHandler $$3 : $$1) {
            $$2 = ComponentPath.path($$3, $$2);
        }
        return $$2;
    }

    public GuiEventListener component();

    public void applyFocus(boolean var1);

    public record Leaf(GuiEventListener component) implements ComponentPath
    {
        @Override
        public void applyFocus(boolean $$0) {
            this.component.setFocused($$0);
        }
    }

    public static final class Path
    extends Record
    implements ComponentPath {
        private final ContainerEventHandler component;
        private final ComponentPath childPath;

        public Path(ContainerEventHandler $$0, ComponentPath $$1) {
            this.component = $$0;
            this.childPath = $$1;
        }

        @Override
        public void applyFocus(boolean $$0) {
            if (!$$0) {
                this.component.setFocused(null);
            } else {
                this.component.setFocused(this.childPath.component());
            }
            this.childPath.applyFocus($$0);
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Path.class, "component;childPath", "component", "childPath"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Path.class, "component;childPath", "component", "childPath"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Path.class, "component;childPath", "component", "childPath"}, this, $$0);
        }

        @Override
        public ContainerEventHandler component() {
            return this.component;
        }

        public ComponentPath childPath() {
            return this.childPath;
        }
    }
}