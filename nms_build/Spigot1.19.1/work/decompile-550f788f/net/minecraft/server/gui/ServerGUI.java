package net.minecraft.server.gui;

import com.google.common.collect.Lists;
import com.mojang.logging.LogQueues;
import com.mojang.logging.LogUtils;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.server.dedicated.DedicatedServer;
import org.slf4j.Logger;

public class ServerGUI extends JComponent {

    private static final Font MONOSPACED = new Font("Monospaced", 0, 12);
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String TITLE = "Minecraft server";
    private static final String SHUTDOWN_TITLE = "Minecraft server - shutting down!";
    private final DedicatedServer server;
    private Thread logAppenderThread;
    private final Collection<Runnable> finalizers = Lists.newArrayList();
    final AtomicBoolean isClosing = new AtomicBoolean();

    public static ServerGUI showFrameFor(final DedicatedServer dedicatedserver) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception exception) {
            ;
        }

        final JFrame jframe = new JFrame("Minecraft server");
        final ServerGUI servergui = new ServerGUI(dedicatedserver);

        jframe.setDefaultCloseOperation(2);
        jframe.add(servergui);
        jframe.pack();
        jframe.setLocationRelativeTo((Component) null);
        jframe.setVisible(true);
        jframe.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowevent) {
                if (!servergui.isClosing.getAndSet(true)) {
                    jframe.setTitle("Minecraft server - shutting down!");
                    dedicatedserver.halt(true);
                    servergui.runFinalizers();
                }

            }
        });
        Objects.requireNonNull(jframe);
        servergui.addFinalizer(jframe::dispose);
        servergui.start();
        return servergui;
    }

    private ServerGUI(DedicatedServer dedicatedserver) {
        this.server = dedicatedserver;
        this.setPreferredSize(new Dimension(854, 480));
        this.setLayout(new BorderLayout());

        try {
            this.add(this.buildChatPanel(), "Center");
            this.add(this.buildInfoPanel(), "West");
        } catch (Exception exception) {
            ServerGUI.LOGGER.error("Couldn't build server GUI", exception);
        }

    }

    public void addFinalizer(Runnable runnable) {
        this.finalizers.add(runnable);
    }

    private JComponent buildInfoPanel() {
        JPanel jpanel = new JPanel(new BorderLayout());
        GuiStatsComponent guistatscomponent = new GuiStatsComponent(this.server);
        Collection collection = this.finalizers;

        Objects.requireNonNull(guistatscomponent);
        collection.add(guistatscomponent::close);
        jpanel.add(guistatscomponent, "North");
        jpanel.add(this.buildPlayerPanel(), "Center");
        jpanel.setBorder(new TitledBorder(new EtchedBorder(), "Stats"));
        return jpanel;
    }

    private JComponent buildPlayerPanel() {
        JList<?> jlist = new PlayerListBox(this.server);
        JScrollPane jscrollpane = new JScrollPane(jlist, 22, 30);

        jscrollpane.setBorder(new TitledBorder(new EtchedBorder(), "Players"));
        return jscrollpane;
    }

    private JComponent buildChatPanel() {
        JPanel jpanel = new JPanel(new BorderLayout());
        JTextArea jtextarea = new JTextArea();
        JScrollPane jscrollpane = new JScrollPane(jtextarea, 22, 30);

        jtextarea.setEditable(false);
        jtextarea.setFont(ServerGUI.MONOSPACED);
        JTextField jtextfield = new JTextField();

        jtextfield.addActionListener((actionevent) -> {
            String s = jtextfield.getText().trim();

            if (!s.isEmpty()) {
                this.server.handleConsoleInput(s, this.server.createCommandSourceStack());
            }

            jtextfield.setText("");
        });
        jtextarea.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent focusevent) {}
        });
        jpanel.add(jscrollpane, "Center");
        jpanel.add(jtextfield, "South");
        jpanel.setBorder(new TitledBorder(new EtchedBorder(), "Log and chat"));
        this.logAppenderThread = new Thread(() -> {
            String s;

            while ((s = LogQueues.getNextLogEvent("ServerGuiConsole")) != null) {
                this.print(jtextarea, jscrollpane, s);
            }

        });
        this.logAppenderThread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(ServerGUI.LOGGER));
        this.logAppenderThread.setDaemon(true);
        return jpanel;
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

    public void print(JTextArea jtextarea, JScrollPane jscrollpane, String s) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> {
                this.print(jtextarea, jscrollpane, s);
            });
        } else {
            Document document = jtextarea.getDocument();
            JScrollBar jscrollbar = jscrollpane.getVerticalScrollBar();
            boolean flag = false;

            if (jscrollpane.getViewport().getView() == jtextarea) {
                flag = (double) jscrollbar.getValue() + jscrollbar.getSize().getHeight() + (double) (ServerGUI.MONOSPACED.getSize() * 4) > (double) jscrollbar.getMaximum();
            }

            try {
                document.insertString(document.getLength(), s, (AttributeSet) null);
            } catch (BadLocationException badlocationexception) {
                ;
            }

            if (flag) {
                jscrollbar.setValue(Integer.MAX_VALUE);
            }

        }
    }
}
