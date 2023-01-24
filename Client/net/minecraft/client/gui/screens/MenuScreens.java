/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.logging.LogUtils
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.util.Map
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screens;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.gui.screens.inventory.BeaconScreen;
import net.minecraft.client.gui.screens.inventory.BlastFurnaceScreen;
import net.minecraft.client.gui.screens.inventory.BrewingStandScreen;
import net.minecraft.client.gui.screens.inventory.CartographyTableScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.DispenserScreen;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.client.gui.screens.inventory.FurnaceScreen;
import net.minecraft.client.gui.screens.inventory.GrindstoneScreen;
import net.minecraft.client.gui.screens.inventory.HopperScreen;
import net.minecraft.client.gui.screens.inventory.LecternScreen;
import net.minecraft.client.gui.screens.inventory.LegacySmithingScreen;
import net.minecraft.client.gui.screens.inventory.LoomScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.client.gui.screens.inventory.SmokerScreen;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.slf4j.Logger;

public class MenuScreens {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<MenuType<?>, ScreenConstructor<?, ?>> SCREENS = Maps.newHashMap();

    public static <T extends AbstractContainerMenu> void create(@Nullable MenuType<T> $$0, Minecraft $$1, int $$2, Component $$3) {
        if ($$0 == null) {
            LOGGER.warn("Trying to open invalid screen with name: {}", (Object)$$3.getString());
            return;
        }
        ScreenConstructor<T, ?> $$4 = MenuScreens.getConstructor($$0);
        if ($$4 == null) {
            LOGGER.warn("Failed to create screen for menu type: {}", (Object)BuiltInRegistries.MENU.getKey($$0));
            return;
        }
        $$4.fromPacket($$3, $$0, $$1, $$2);
    }

    @Nullable
    private static <T extends AbstractContainerMenu> ScreenConstructor<T, ?> getConstructor(MenuType<T> $$0) {
        return (ScreenConstructor)SCREENS.get($$0);
    }

    private static <M extends AbstractContainerMenu, U extends Screen> void register(MenuType<? extends M> $$0, ScreenConstructor<M, U> $$1) {
        ScreenConstructor $$2 = (ScreenConstructor)SCREENS.put($$0, $$1);
        if ($$2 != null) {
            throw new IllegalStateException("Duplicate registration for " + BuiltInRegistries.MENU.getKey($$0));
        }
    }

    public static boolean selfTest() {
        boolean $$0 = false;
        for (MenuType menuType : BuiltInRegistries.MENU) {
            if (SCREENS.containsKey((Object)menuType)) continue;
            LOGGER.debug("Menu {} has no matching screen", (Object)BuiltInRegistries.MENU.getKey(menuType));
            $$0 = true;
        }
        return $$0;
    }

    static {
        MenuScreens.register(MenuType.GENERIC_9x1, ContainerScreen::new);
        MenuScreens.register(MenuType.GENERIC_9x2, ContainerScreen::new);
        MenuScreens.register(MenuType.GENERIC_9x3, ContainerScreen::new);
        MenuScreens.register(MenuType.GENERIC_9x4, ContainerScreen::new);
        MenuScreens.register(MenuType.GENERIC_9x5, ContainerScreen::new);
        MenuScreens.register(MenuType.GENERIC_9x6, ContainerScreen::new);
        MenuScreens.register(MenuType.GENERIC_3x3, DispenserScreen::new);
        MenuScreens.register(MenuType.ANVIL, AnvilScreen::new);
        MenuScreens.register(MenuType.BEACON, BeaconScreen::new);
        MenuScreens.register(MenuType.BLAST_FURNACE, BlastFurnaceScreen::new);
        MenuScreens.register(MenuType.BREWING_STAND, BrewingStandScreen::new);
        MenuScreens.register(MenuType.CRAFTING, CraftingScreen::new);
        MenuScreens.register(MenuType.ENCHANTMENT, EnchantmentScreen::new);
        MenuScreens.register(MenuType.FURNACE, FurnaceScreen::new);
        MenuScreens.register(MenuType.GRINDSTONE, GrindstoneScreen::new);
        MenuScreens.register(MenuType.HOPPER, HopperScreen::new);
        MenuScreens.register(MenuType.LECTERN, LecternScreen::new);
        MenuScreens.register(MenuType.LOOM, LoomScreen::new);
        MenuScreens.register(MenuType.MERCHANT, MerchantScreen::new);
        MenuScreens.register(MenuType.SHULKER_BOX, ShulkerBoxScreen::new);
        MenuScreens.register(MenuType.LEGACY_SMITHING, LegacySmithingScreen::new);
        MenuScreens.register(MenuType.SMITHING, SmithingScreen::new);
        MenuScreens.register(MenuType.SMOKER, SmokerScreen::new);
        MenuScreens.register(MenuType.CARTOGRAPHY_TABLE, CartographyTableScreen::new);
        MenuScreens.register(MenuType.STONECUTTER, StonecutterScreen::new);
    }

    static interface ScreenConstructor<T extends AbstractContainerMenu, U extends Screen> {
        default public void fromPacket(Component $$0, MenuType<T> $$1, Minecraft $$2, int $$3) {
            U $$4 = this.create($$1.create($$3, $$2.player.getInventory()), $$2.player.getInventory(), $$0);
            $$2.player.containerMenu = ((MenuAccess)$$4).getMenu();
            $$2.setScreen((Screen)$$4);
        }

        public U create(T var1, Inventory var2, Component var3);
    }
}