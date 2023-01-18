/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogQueues
 *  com.mojang.logging.LogUtils
 *  java.awt.BorderLayout
 *  java.awt.Component
 *  java.awt.Dimension
 *  java.awt.Font
 *  java.awt.LayoutManager
 *  java.awt.event.FocusAdapter
 *  java.awt.event.FocusEvent
 *  java.awt.event.FocusListener
 *  java.awt.event.WindowAdapter
 *  java.awt.event.WindowEvent
 *  java.awt.event.WindowListener
 *  java.lang.Exception
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.String
 *  java.lang.Thread
 *  java.lang.Thread$UncaughtExceptionHandler
 *  java.lang.Throwable
 *  java.util.Collection
 *  java.util.concurrent.atomic.AtomicBoolean
 *  javax.swing.JComponent
 *  javax.swing.JFrame
 *  javax.swing.JPanel
 *  javax.swing.JScrollBar
 *  javax.swing.JScrollPane
 *  javax.swing.JTextArea
 *  javax.swing.JTextField
 *  javax.swing.SwingUtilities
 *  javax.swing.UIManager
 *  javax.swing.border.Border
 *  javax.swing.border.EtchedBorder
 *  javax.swing.border.TitledBorder
 *  javax.swing.text.BadLocationException
 *  javax.swing.text.Document
 *  org.slf4j.Logger
 */
package net.minecraft.server.gui;

import com.google.common.collect.Lists;
import com.mojang.logging.LogQueues;
import com.mojang.logging.LogUtils;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.gui.PlayerListComponent;
import net.minecraft.server.gui.StatsComponent;
import org.slf4j.Logger;

public class MinecraftServerGui
extends JComponent {
    private static final Font MONOSPACED = new Font("Monospaced", 0, 12);
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String TITLE = "Minecraft server";
    private static final String SHUTDOWN_TITLE = "Minecraft server - shutting down!";
    private final DedicatedServer server;
    private Thread logAppenderThread;
    private final Collection<Runnable> finalizers = Lists.newArrayList();
    final AtomicBoolean isClosing = new AtomicBoolean();

    public static MinecraftServerGui showFrameFor(final DedicatedServer $$0) {
        try {
            UIManager.setLookAndFeel((String)UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception exception) {
            // empty catch block
        }
        final JFrame $$1 = new JFrame(TITLE);
        final MinecraftServerGui $$2 = new MinecraftServerGui($$0);
        $$1.setDefaultCloseOperation(2);
        $$1.add((Component)$$2);
        $$1.pack();
        $$1.setLocationRelativeTo(null);
        $$1.setVisible(true);
        $$1.addWindowListener((WindowListener)new WindowAdapter(){

            public void windowClosing(WindowEvent $$02) {
                if (!$$2.isClosing.getAndSet(true)) {
                    $$1.setTitle(MinecraftServerGui.SHUTDOWN_TITLE);
                    $$0.halt(true);
                    $$2.runFinalizers();
                }
            }
        });
        $$2.addFinalizer(() -> ((JFrame)$$1).dispose());
        $$2.start();
        return $$2;
    }

    private MinecraftServerGui(DedicatedServer $$0) {
        this.server = $$0;
        this.setPreferredSize(new Dimension(854, 480));
        this.setLayout((LayoutManager)new BorderLayout());
        try {
            this.add((Component)this.buildChatPanel(), "Center");
            this.add((Component)this.buildInfoPanel(), "West");
        }
        catch (Exception $$1) {
            LOGGER.error("Couldn't build server GUI", (Throwable)$$1);
        }
    }

    public void addFinalizer(Runnable $$0) {
        this.finalizers.add((Object)$$0);
    }

    private JComponent buildInfoPanel() {
        JPanel $$0 = new JPanel((LayoutManager)new BorderLayout());
        StatsComponent $$1 = new StatsComponent(this.server);
        this.finalizers.add($$1::close);
        $$0.add((Component)$$1, (Object)"North");
        $$0.add((Component)this.buildPlayerPanel(), (Object)"Center");
        $$0.setBorder((Border)new TitledBorder((Border)new EtchedBorder(), "Stats"));
        return $$0;
    }

    private JComponent buildPlayerPanel() {
        PlayerListComponent $$0 = new PlayerListComponent(this.server);
        JScrollPane $$1 = new JScrollPane((Component)$$0, 22, 30);
        $$1.setBorder((Border)new TitledBorder((Border)new EtchedBorder(), "Players"));
        return $$1;
    }

    private JComponent buildChatPanel() {
        JPanel $$0 = new JPanel((LayoutManager)new BorderLayout());
        JTextArea $$12 = new JTextArea();
        JScrollPane $$2 = new JScrollPane((Component)$$12, 22, 30);
        $$12.setEditable(false);
        $$12.setFont(MONOSPACED);
        JTextField $$3 = new JTextField();
        $$3.addActionListener($$1 -> {
            String $$2 = $$3.getText().trim();
            if (!$$2.isEmpty()) {
                this.server.handleConsoleInput($$2, this.server.createCommandSourceStack());
            }
            $$3.setText("");
        });
        $$12.addFocusListener((FocusListener)new FocusAdapter(){

            public void focusGained(FocusEvent $$0) {
            }
        });
        $$0.add((Component)$$2, (Object)"Center");
        $$0.add((Component)$$3, (Object)"South");
        $$0.setBorder((Border)new TitledBorder((Border)new EtchedBorder(), "Log and chat"));
        this.logAppenderThread = new Thread(() -> {
            String $$2;
            while (($$2 = LogQueues.getNextLogEvent((String)"ServerGuiConsole")) != null) {
                this.print($$12, $$2, $$2);
            }
        });
        this.logAppenderThread.setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new DefaultUncaughtExceptionHandler(LOGGER));
        this.logAppenderThread.setDaemon(true);
        return $$0;
    }

    public void start() {
        this.logAppenderThread.start();
    }

    public void close() {
        if (!this.isClosing.getAndSet(true)) {
            this.runFinalizers();
        }
    }

    void runFinalizers() {
        this.finalizers.forEach(Runnable::run);
    }

    public void print(JTextArea $$0, JScrollPane $$1, String $$2) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> this.print($$0, $$1, $$2));
            return;
        }
        Document $$3 = $$0.getDocument();
        JScrollBar $$4 = $$1.getVerticalScrollBar();
        boolean $$5 = false;
        if ($$1.getViewport().getView() == $$0) {
            $$5 = (double)$$4.getValue() + $$4.getSize().getHeight() + (double)(MONOSPACED.getSize() * 4) > (double)$$4.getMaximum();
        }
        try {
            $$3.insertString($$3.getLength(), $$2, null);
        }
        catch (BadLocationException badLocationException) {
            // empty catch block
        }
        if ($$5) {
            $$4.setValue(Integer.MAX_VALUE);
        }
    }
}