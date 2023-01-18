/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.util.UUID
 */
package net.minecraft.network.protocol.game;

import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class DebugEntityNameGenerator {
    private static final String[] NAMES_FIRST_PART = new String[]{"Slim", "Far", "River", "Silly", "Fat", "Thin", "Fish", "Bat", "Dark", "Oak", "Sly", "Bush", "Zen", "Bark", "Cry", "Slack", "Soup", "Grim", "Hook", "Dirt", "Mud", "Sad", "Hard", "Crook", "Sneak", "Stink", "Weird", "Fire", "Soot", "Soft", "Rough", "Cling", "Scar"};
    private static final String[] NAMES_SECOND_PART = new String[]{"Fox", "Tail", "Jaw", "Whisper", "Twig", "Root", "Finder", "Nose", "Brow", "Blade", "Fry", "Seek", "Wart", "Tooth", "Foot", "Leaf", "Stone", "Fall", "Face", "Tongue", "Voice", "Lip", "Mouth", "Snail", "Toe", "Ear", "Hair", "Beard", "Shirt", "Fist"};

    public static String getEntityName(Entity $$0) {
        if ($$0 instanceof Player) {
            return $$0.getName().getString();
        }
        Component $$1 = $$0.getCustomName();
        if ($$1 != null) {
            return $$1.getString();
        }
        return DebugEntityNameGenerator.getEntityName($$0.getUUID());
    }

    public static String getEntityName(UUID $$0) {
        RandomSource $$1 = DebugEntityNameGenerator.getRandom($$0);
        return DebugEntityNameGenerator.getRandomString($$1, NAMES_FIRST_PART) + DebugEntityNameGenerator.getRandomString($$1, NAMES_SECOND_PART);
    }

    private static String getRandomString(RandomSource $$0, String[] $$1) {
        return Util.getRandom($$1, $$0);
    }

    private static RandomSource getRandom(UUID $$0) {
        return RandomSource.create($$0.hashCode() >> 2);
    }
}