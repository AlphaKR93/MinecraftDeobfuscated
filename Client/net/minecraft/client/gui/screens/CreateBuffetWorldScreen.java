/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.ibm.icu.text.Collator
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Comparator
 *  java.util.Locale
 *  java.util.Objects
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens;

import com.ibm.icu.text.Collator;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public class CreateBuffetWorldScreen
extends Screen {
    private static final Component BIOME_SELECT_INFO = Component.translatable("createWorld.customize.buffet.biome");
    private final Screen parent;
    private final Consumer<Holder<Biome>> applySettings;
    final Registry<Biome> biomes;
    private BiomeList list;
    Holder<Biome> biome;
    private Button doneButton;

    public CreateBuffetWorldScreen(Screen $$0, WorldCreationContext $$1, Consumer<Holder<Biome>> $$2) {
        super(Component.translatable("createWorld.customize.buffet.title"));
        this.parent = $$0;
        this.applySettings = $$2;
        this.biomes = $$1.worldgenLoadContext().registryOrThrow(Registries.BIOME);
        Holder $$3 = (Holder)this.biomes.getHolder(Biomes.PLAINS).or(() -> this.biomes.holders().findAny()).orElseThrow();
        this.biome = (Holder)$$1.selectedDimensions().overworld().getBiomeSource().possibleBiomes().stream().findFirst().orElse((Object)$$3);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    @Override
    protected void init() {
        this.list = new BiomeList();
        this.addWidget(this.list);
        this.doneButton = this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, $$0 -> {
            this.applySettings.accept(this.biome);
            this.minecraft.setScreen(this.parent);
        }).bounds(this.width / 2 - 155, this.height - 28, 150, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> this.minecraft.setScreen(this.parent)).bounds(this.width / 2 + 5, this.height - 28, 150, 20).build());
        this.list.setSelected((BiomeList.Entry)this.list.children().stream().filter($$0 -> Objects.equals($$0.biome, this.biome)).findFirst().orElse(null));
    }

    void updateButtonValidity() {
        this.doneButton.active = this.list.getSelected() != null;
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderDirtBackground($$0);
        this.list.render($$0, $$1, $$2, $$3);
        CreateBuffetWorldScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 8, 0xFFFFFF);
        CreateBuffetWorldScreen.drawCenteredString($$0, this.font, BIOME_SELECT_INFO, this.width / 2, 28, 0xA0A0A0);
        super.render($$0, $$1, $$2, $$3);
    }

    class BiomeList
    extends ObjectSelectionList<Entry> {
        BiomeList() {
            super(CreateBuffetWorldScreen.this.minecraft, CreateBuffetWorldScreen.this.width, CreateBuffetWorldScreen.this.height, 40, CreateBuffetWorldScreen.this.height - 37, 16);
            Collator $$02 = Collator.getInstance((Locale)Locale.getDefault());
            CreateBuffetWorldScreen.this.biomes.holders().map($$0 -> new Entry((Holder.Reference<Biome>)$$0)).sorted(Comparator.comparing($$0 -> $$0.name.getString(), (Comparator)$$02)).forEach($$1 -> this.addEntry($$1));
        }

        @Override
        public void setSelected(@Nullable Entry $$0) {
            super.setSelected($$0);
            if ($$0 != null) {
                CreateBuffetWorldScreen.this.biome = $$0.biome;
            }
            CreateBuffetWorldScreen.this.updateButtonValidity();
        }

        class Entry
        extends ObjectSelectionList.Entry<Entry> {
            final Holder.Reference<Biome> biome;
            final Component name;

            public Entry(Holder.Reference<Biome> $$0) {
                this.biome = $$0;
                ResourceLocation $$1 = $$0.key().location();
                String $$2 = $$1.toLanguageKey("biome");
                this.name = Language.getInstance().has($$2) ? Component.translatable($$2) : Component.literal($$1.toString());
            }

            @Override
            public Component getNarration() {
                return Component.translatable("narrator.select", this.name);
            }

            @Override
            public void render(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
                GuiComponent.drawString($$0, CreateBuffetWorldScreen.this.font, this.name, $$3 + 5, $$2 + 2, 0xFFFFFF);
            }

            @Override
            public boolean mouseClicked(double $$0, double $$1, int $$2) {
                if ($$2 == 0) {
                    BiomeList.this.setSelected(this);
                    return true;
                }
                return false;
            }
        }
    }
}