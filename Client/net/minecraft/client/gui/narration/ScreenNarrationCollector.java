/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.util.Comparator
 *  java.util.Map
 *  java.util.function.Consumer
 */
package net.minecraft.client.gui.narration;

import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarrationThunk;

public class ScreenNarrationCollector {
    int generation;
    final Map<EntryKey, NarrationEntry> entries = Maps.newTreeMap((Comparator)Comparator.comparing($$0 -> $$0.type).thenComparing($$0 -> $$0.depth));

    public void update(Consumer<NarrationElementOutput> $$0) {
        ++this.generation;
        $$0.accept((Object)new Output(0));
    }

    public String collectNarrationText(boolean $$0) {
        final StringBuilder $$1 = new StringBuilder();
        Consumer<String> $$2 = new Consumer<String>(){
            private boolean firstEntry = true;

            public void accept(String $$0) {
                if (!this.firstEntry) {
                    $$1.append(". ");
                }
                this.firstEntry = false;
                $$1.append($$0);
            }
        };
        this.entries.forEach((arg_0, arg_1) -> this.lambda$collectNarrationText$2($$0, (Consumer)$$2, arg_0, arg_1));
        return $$1.toString();
    }

    private /* synthetic */ void lambda$collectNarrationText$2(boolean $$0, Consumer $$1, EntryKey $$2, NarrationEntry $$3) {
        if ($$3.generation == this.generation && ($$0 || !$$3.alreadyNarrated)) {
            $$3.contents.getText((Consumer<String>)$$1);
            $$3.alreadyNarrated = true;
        }
    }

    class Output
    implements NarrationElementOutput {
        private final int depth;

        Output(int $$0) {
            this.depth = $$0;
        }

        @Override
        public void add(NarratedElementType $$02, NarrationThunk<?> $$1) {
            ((NarrationEntry)ScreenNarrationCollector.this.entries.computeIfAbsent((Object)new EntryKey($$02, this.depth), $$0 -> new NarrationEntry())).update(ScreenNarrationCollector.this.generation, $$1);
        }

        @Override
        public NarrationElementOutput nest() {
            return new Output(this.depth + 1);
        }
    }

    static class NarrationEntry {
        NarrationThunk<?> contents = NarrationThunk.EMPTY;
        int generation = -1;
        boolean alreadyNarrated;

        NarrationEntry() {
        }

        public NarrationEntry update(int $$0, NarrationThunk<?> $$1) {
            if (!this.contents.equals($$1)) {
                this.contents = $$1;
                this.alreadyNarrated = false;
            } else if (this.generation + 1 != $$0) {
                this.alreadyNarrated = false;
            }
            this.generation = $$0;
            return this;
        }
    }

    static class EntryKey {
        final NarratedElementType type;
        final int depth;

        EntryKey(NarratedElementType $$0, int $$1) {
            this.type = $$0;
            this.depth = $$1;
        }
    }
}