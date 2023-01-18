/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Collection
 *  java.util.List
 *  java.util.stream.Collectors
 */
package net.minecraft.realms;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsLabel;

public abstract class RealmsScreen
extends Screen {
    protected static final int TITLE_HEIGHT = 17;
    protected static final int COMPONENT_HEIGHT = 20;
    protected static final int EXPIRATION_NOTIFICATION_DAYS = 7;
    protected static final long SIZE_LIMIT = 0x140000000L;
    public static final int COLOR_WHITE = 0xFFFFFF;
    public static final int COLOR_GRAY = 0xA0A0A0;
    protected static final int COLOR_DARK_GRAY = 0x4C4C4C;
    protected static final int COLOR_MEDIUM_GRAY = 0x6C6C6C;
    protected static final int COLOR_GREEN = 0x7FFF7F;
    protected static final int COLOR_DARK_GREEN = 6077788;
    protected static final int COLOR_RED = 0xFF0000;
    protected static final int COLOR_RED_FADE = 15553363;
    protected static final int COLOR_BLACK = -1073741824;
    protected static final int COLOR_YELLOW = 0xCCAC5C;
    protected static final int COLOR_BRIGHT_YELLOW = -256;
    protected static final int COLOR_LINK = 0x3366BB;
    protected static final int COLOR_LINK_HOVER = 7107012;
    protected static final int COLOR_INFO = 8226750;
    protected static final int COLOR_BUTTON_YELLOW = 0xFFFFA0;
    protected static final String UPDATE_BREAKS_ADVENTURE_URL = "https://www.minecraft.net/realms/adventure-maps-in-1-9";
    protected static final int SKIN_FACE_SIZE = 8;
    private final List<RealmsLabel> labels = Lists.newArrayList();

    public RealmsScreen(Component $$0) {
        super($$0);
    }

    protected static int row(int $$0) {
        return 40 + $$0 * 13;
    }

    protected RealmsLabel addLabel(RealmsLabel $$0) {
        this.labels.add((Object)$$0);
        return this.addRenderableOnly($$0);
    }

    public Component createLabelNarration() {
        return CommonComponents.joinLines((Collection<? extends Component>)((Collection)this.labels.stream().map(RealmsLabel::getText).collect(Collectors.toList())));
    }
}