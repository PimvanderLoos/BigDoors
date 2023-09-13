package net.minecraft.server.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.JComponent;
import javax.swing.Timer;
import net.minecraft.SystemUtils;
import net.minecraft.server.MinecraftServer;

public class GuiStatsComponent extends JComponent {

    private static final DecimalFormat DECIMAL_FORMAT = (DecimalFormat) SystemUtils.make(new DecimalFormat("########0.000"), (decimalformat) -> {
        decimalformat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
    });
    private final int[] values = new int[256];
    private int vp;
    private final String[] msgs = new String[11];
    private final MinecraftServer server;
    private final Timer timer;

    public GuiStatsComponent(MinecraftServer minecraftserver) {
        this.server = minecraftserver;
        this.setPreferredSize(new Dimension(456, 246));
        this.setMinimumSize(new Dimension(456, 246));
        this.setMaximumSize(new Dimension(456, 246));
        this.timer = new Timer(500, (actionevent) -> {
            this.tick();
        });
        this.timer.start();
        this.setBackground(Color.BLACK);
    }

    private void tick() {
        long i = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        this.msgs[0] = "Memory use: " + i / 1024L / 1024L + " mb (" + Runtime.getRuntime().freeMemory() * 100L / Runtime.getRuntime().maxMemory() + "% free)";
        String[] astring = this.msgs;
        DecimalFormat decimalformat = GuiStatsComponent.DECIMAL_FORMAT;
        double d0 = this.getAverage(this.server.tickTimes);

        astring[1] = "Avg tick: " + decimalformat.format(d0 * 1.0E-6D) + " ms";
        this.values[this.vp++ & 255] = (int) (i * 100L / Runtime.getRuntime().maxMemory());
        this.repaint();
    }

    private double getAverage(long[] along) {
        long i = 0L;
        long[] along1 = along;
        int j = along.length;

        for (int k = 0; k < j; ++k) {
            long l = along1[k];

            i += l;
        }

        return (double) i / (double) along.length;
    }

    public void paint(Graphics graphics) {
        graphics.setColor(new Color(16777215));
        graphics.fillRect(0, 0, 456, 246);

        int i;

        for (i = 0; i < 256; ++i) {
            int j = this.values[i + this.vp & 255];

            graphics.setColor(new Color(j + 28 << 16));
            graphics.fillRect(i, 100 - j, 1, j);
        }

        graphics.setColor(Color.BLACK);

        for (i = 0; i < this.msgs.length; ++i) {
            String s = this.msgs[i];

            if (s != null) {
                graphics.drawString(s, 32, 116 + i * 16);
            }
        }

    }

    public void close() {
        this.timer.stop();
    }
}
