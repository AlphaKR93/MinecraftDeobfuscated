/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.yggdrasil.ServicesKeyInfo
 *  com.mojang.logging.LogUtils
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Throwable
 *  java.security.PublicKey
 *  java.security.Signature
 *  java.security.SignatureException
 *  org.slf4j.Logger
 */
package net.minecraft.util;

import com.mojang.authlib.yggdrasil.ServicesKeyInfo;
import com.mojang.logging.LogUtils;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import net.minecraft.util.SignatureUpdater;
import org.slf4j.Logger;

public interface SignatureValidator {
    public static final SignatureValidator NO_VALIDATION = ($$0, $$1) -> true;
    public static final Logger LOGGER = LogUtils.getLogger();

    public boolean validate(SignatureUpdater var1, byte[] var2);

    default public boolean validate(byte[] $$0, byte[] $$12) {
        return this.validate($$1 -> $$1.update($$0), $$12);
    }

    private static boolean verifySignature(SignatureUpdater $$0, byte[] $$1, Signature $$2) throws SignatureException {
        $$0.update(arg_0 -> ((Signature)$$2).update(arg_0));
        return $$2.verify($$1);
    }

    public static SignatureValidator from(PublicKey $$0, String $$1) {
        return ($$2, $$3) -> {
            try {
                Signature $$4 = Signature.getInstance((String)$$1);
                $$4.initVerify($$0);
                return SignatureValidator.verifySignature($$2, $$3, $$4);
            }
            catch (Exception $$5) {
                LOGGER.error("Failed to verify signature", (Throwable)$$5);
                return false;
            }
        };
    }

    public static SignatureValidator from(ServicesKeyInfo $$0) {
        return ($$1, $$2) -> {
            Signature $$3 = $$0.signature();
            try {
                return SignatureValidator.verifySignature($$1, $$2, $$3);
            }
            catch (SignatureException $$4) {
                LOGGER.error("Failed to verify Services signature", (Throwable)$$4);
                return false;
            }
        };
    }
}