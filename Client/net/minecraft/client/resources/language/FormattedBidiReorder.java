/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.ibm.icu.lang.UCharacter
 *  com.ibm.icu.text.ArabicShaping
 *  com.ibm.icu.text.Bidi
 *  com.ibm.icu.text.BidiRun
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.function.UnaryOperator
 */
package net.minecraft.client.resources.language;

import com.google.common.collect.Lists;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.Bidi;
import com.ibm.icu.text.BidiRun;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.SubStringSource;
import net.minecraft.util.FormattedCharSequence;

public class FormattedBidiReorder {
    public static FormattedCharSequence reorder(FormattedText $$0, boolean $$1) {
        SubStringSource $$2 = SubStringSource.create($$0, UCharacter::getMirror, (UnaryOperator<String>)((UnaryOperator)FormattedBidiReorder::shape));
        Bidi $$3 = new Bidi($$2.getPlainText(), $$1 ? 127 : 126);
        $$3.setReorderingMode(0);
        ArrayList $$4 = Lists.newArrayList();
        int $$5 = $$3.countRuns();
        for (int $$6 = 0; $$6 < $$5; ++$$6) {
            BidiRun $$7 = $$3.getVisualRun($$6);
            $$4.addAll($$2.substring($$7.getStart(), $$7.getLength(), $$7.isOddRun()));
        }
        return FormattedCharSequence.composite((List<FormattedCharSequence>)$$4);
    }

    private static String shape(String $$0) {
        try {
            return new ArabicShaping(8).shape($$0);
        }
        catch (Exception $$1) {
            return $$0;
        }
    }
}