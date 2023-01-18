/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Exception
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Objects
 *  java.util.UUID
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.entity.ai.attributes;

import com.mojang.logging.LogUtils;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.slf4j.Logger;

public class AttributeModifier {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final double amount;
    private final Operation operation;
    private final Supplier<String> nameGetter;
    private final UUID id;

    public AttributeModifier(String $$0, double $$1, Operation $$2) {
        this(Mth.createInsecureUUID(RandomSource.createNewThreadLocalInstance()), (Supplier<String>)((Supplier)() -> $$0), $$1, $$2);
    }

    public AttributeModifier(UUID $$0, String $$1, double $$2, Operation $$3) {
        this($$0, (Supplier<String>)((Supplier)() -> $$1), $$2, $$3);
    }

    public AttributeModifier(UUID $$0, Supplier<String> $$1, double $$2, Operation $$3) {
        this.id = $$0;
        this.nameGetter = $$1;
        this.amount = $$2;
        this.operation = $$3;
    }

    public UUID getId() {
        return this.id;
    }

    public String getName() {
        return (String)this.nameGetter.get();
    }

    public Operation getOperation() {
        return this.operation;
    }

    public double getAmount() {
        return this.amount;
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 == null || this.getClass() != $$0.getClass()) {
            return false;
        }
        AttributeModifier $$1 = (AttributeModifier)$$0;
        return Objects.equals((Object)this.id, (Object)$$1.id);
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public String toString() {
        return "AttributeModifier{amount=" + this.amount + ", operation=" + this.operation + ", name='" + (String)this.nameGetter.get() + "', id=" + this.id + "}";
    }

    public CompoundTag save() {
        CompoundTag $$0 = new CompoundTag();
        $$0.putString("Name", this.getName());
        $$0.putDouble("Amount", this.amount);
        $$0.putInt("Operation", this.operation.toValue());
        $$0.putUUID("UUID", this.id);
        return $$0;
    }

    @Nullable
    public static AttributeModifier load(CompoundTag $$0) {
        try {
            UUID $$1 = $$0.getUUID("UUID");
            Operation $$2 = Operation.fromValue($$0.getInt("Operation"));
            return new AttributeModifier($$1, $$0.getString("Name"), $$0.getDouble("Amount"), $$2);
        }
        catch (Exception $$3) {
            LOGGER.warn("Unable to create attribute: {}", (Object)$$3.getMessage());
            return null;
        }
    }

    public static enum Operation {
        ADDITION(0),
        MULTIPLY_BASE(1),
        MULTIPLY_TOTAL(2);

        private static final Operation[] OPERATIONS;
        private final int value;

        private Operation(int $$0) {
            this.value = $$0;
        }

        public int toValue() {
            return this.value;
        }

        public static Operation fromValue(int $$0) {
            if ($$0 < 0 || $$0 >= OPERATIONS.length) {
                throw new IllegalArgumentException("No operation with value " + $$0);
            }
            return OPERATIONS[$$0];
        }

        static {
            OPERATIONS = new Operation[]{ADDITION, MULTIPLY_BASE, MULTIPLY_TOTAL};
        }
    }
}