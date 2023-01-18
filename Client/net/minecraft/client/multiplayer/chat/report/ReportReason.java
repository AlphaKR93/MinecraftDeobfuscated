/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Locale
 *  javax.annotation.Nullable
 */
package net.minecraft.client.multiplayer.chat.report;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;

public enum ReportReason {
    FALSE_REPORTING(2, "false_reporting", false),
    HATE_SPEECH(5, "hate_speech", true),
    TERRORISM_OR_VIOLENT_EXTREMISM(16, "terrorism_or_violent_extremism", true),
    CHILD_SEXUAL_EXPLOITATION_OR_ABUSE(17, "child_sexual_exploitation_or_abuse", true),
    IMMINENT_HARM(18, "imminent_harm", true),
    NON_CONSENSUAL_INTIMATE_IMAGERY(19, "non_consensual_intimate_imagery", true),
    HARASSMENT_OR_BULLYING(21, "harassment_or_bullying", true),
    DEFAMATION_IMPERSONATION_FALSE_INFORMATION(27, "defamation_impersonation_false_information", true),
    SELF_HARM_OR_SUICIDE(31, "self_harm_or_suicide", true),
    ALCOHOL_TOBACCO_DRUGS(39, "alcohol_tobacco_drugs", true);

    private final int id;
    private final String backendName;
    private final boolean reportable;
    private final Component title;
    private final Component description;

    private ReportReason(int $$0, String $$1, boolean $$2) {
        this.id = $$0;
        this.backendName = $$1.toUpperCase(Locale.ROOT);
        this.reportable = $$2;
        String $$3 = "gui.abuseReport.reason." + $$1;
        this.title = Component.translatable($$3);
        this.description = Component.translatable($$3 + ".description");
    }

    public String backendName() {
        return this.backendName;
    }

    public Component title() {
        return this.title;
    }

    public Component description() {
        return this.description;
    }

    public boolean reportable() {
        return this.reportable;
    }

    @Nullable
    public static Component getTranslationById(int $$0) {
        for (ReportReason $$1 : ReportReason.values()) {
            if ($$1.id != $$0) continue;
            return $$1.title;
        }
        return null;
    }
}