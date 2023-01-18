/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.io.BufferedReader
 *  java.io.IOException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.Calendar
 *  java.util.Collections
 *  java.util.Date
 *  java.util.List
 *  java.util.Locale
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 */
package net.minecraft.client.resources;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;

public class SplashManager
extends SimplePreparableReloadListener<List<String>> {
    private static final ResourceLocation SPLASHES_LOCATION = new ResourceLocation("texts/splashes.txt");
    private static final RandomSource RANDOM = RandomSource.create();
    private final List<String> splashes = Lists.newArrayList();
    private final User user;

    public SplashManager(User $$0) {
        this.user = $$0;
    }

    @Override
    protected List<String> prepare(ResourceManager $$02, ProfilerFiller $$1) {
        List list;
        block8: {
            BufferedReader $$2 = Minecraft.getInstance().getResourceManager().openAsReader(SPLASHES_LOCATION);
            try {
                list = (List)$$2.lines().map(String::trim).filter($$0 -> $$0.hashCode() != 125780783).collect(Collectors.toList());
                if ($$2 == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if ($$2 != null) {
                        try {
                            $$2.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException $$3) {
                    return Collections.emptyList();
                }
            }
            $$2.close();
        }
        return list;
    }

    @Override
    protected void apply(List<String> $$0, ResourceManager $$1, ProfilerFiller $$2) {
        this.splashes.clear();
        this.splashes.addAll($$0);
    }

    @Nullable
    public String getSplash() {
        Calendar $$0 = Calendar.getInstance();
        $$0.setTime(new Date());
        if ($$0.get(2) + 1 == 12 && $$0.get(5) == 24) {
            return "Merry X-mas!";
        }
        if ($$0.get(2) + 1 == 1 && $$0.get(5) == 1) {
            return "Happy new year!";
        }
        if ($$0.get(2) + 1 == 10 && $$0.get(5) == 31) {
            return "OOoooOOOoooo! Spooky!";
        }
        if (this.splashes.isEmpty()) {
            return null;
        }
        if (this.user != null && RANDOM.nextInt(this.splashes.size()) == 42) {
            return this.user.getName().toUpperCase(Locale.ROOT) + " IS YOU";
        }
        return (String)this.splashes.get(RANDOM.nextInt(this.splashes.size()));
    }
}