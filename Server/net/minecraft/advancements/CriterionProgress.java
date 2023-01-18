/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSyntaxException
 *  java.io.Serializable
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Throwable
 *  java.text.ParseException
 *  java.text.SimpleDateFormat
 *  java.util.Date
 *  java.util.Locale
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;

public class CriterionProgress {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.ROOT);
    @Nullable
    private Date obtained;

    public boolean isDone() {
        return this.obtained != null;
    }

    public void grant() {
        this.obtained = new Date();
    }

    public void revoke() {
        this.obtained = null;
    }

    @Nullable
    public Date getObtained() {
        return this.obtained;
    }

    public String toString() {
        return "CriterionProgress{obtained=" + (Serializable)(this.obtained == null ? "false" : this.obtained) + "}";
    }

    public void serializeToNetwork(FriendlyByteBuf $$0) {
        $$0.writeNullable(this.obtained, FriendlyByteBuf::writeDate);
    }

    public JsonElement serializeToJson() {
        if (this.obtained != null) {
            return new JsonPrimitive(DATE_FORMAT.format(this.obtained));
        }
        return JsonNull.INSTANCE;
    }

    public static CriterionProgress fromNetwork(FriendlyByteBuf $$0) {
        CriterionProgress $$1 = new CriterionProgress();
        $$1.obtained = (Date)$$0.readNullable(FriendlyByteBuf::readDate);
        return $$1;
    }

    public static CriterionProgress fromJson(String $$0) {
        CriterionProgress $$1 = new CriterionProgress();
        try {
            $$1.obtained = DATE_FORMAT.parse($$0);
        }
        catch (ParseException $$2) {
            throw new JsonSyntaxException("Invalid datetime: " + $$0, (Throwable)$$2);
        }
        return $$1;
    }
}