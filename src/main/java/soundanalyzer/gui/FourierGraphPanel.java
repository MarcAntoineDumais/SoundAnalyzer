package soundanalyzer.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.*;

import soundanalyzer.model.SinWave;

import java.awt.BasicStroke;
import java.awt.Color;

public class FourierGraphPanel extends JPanel implements Runnable {
    private static final long serialVersionUID = -2423472748202716661L;

    private final int desiredFPS = 30;
    private final int pointSize = 4;
    private final int pointDuration = 400;

    private int width, xStart, xEnd, xSize,
    height, yStart, yEnd, ySize,
    maxFrequency, xStep;
    private double amplitude;

    private Queue<SinWave> queue;
    private List<VanishingGraphValue> points;
    private Thread thread;

    public FourierGraphPanel() {
        maxFrequency = 500;
        xStep = maxFrequency / 10;
        amplitude = 1.0;
        setOpaque(true);
        setPreferredSize(new Dimension(400, 200));

        queue = new ConcurrentLinkedQueue<>();
        points = Collections.synchronizedList(new ArrayList<VanishingGraphValue>());
        recalculatePositions();

        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;

        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, width, height);

        // Axis drawing
        g2d.setColor(Color.GREEN);
        g2d.setStroke(new BasicStroke(2));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setFont(new Font("Serif", Font.PLAIN, 12));
        FontMetrics fm = g2d.getFontMetrics();
        // X-axis
        g2d.drawLine(xStart, yEnd, xEnd, yEnd);
        for (double freq = xStep; freq <= maxFrequency; freq += xStep) {
            int x = xStart + (int)(freq * xSize / maxFrequency);
            g2d.drawLine(x, yEnd + 1, x, yEnd - 1);
            String text = "" + (int)freq;
            g2d.drawString(text,
                    x - fm.stringWidth(text) / 2,
                    height - 2);
        }

        // Y-axis

        //		g2d.drawLine(xStart, yEnd, xStart, yStart);
        //		for (int i = 1; i <= 10; i++) {
        //			int y = yEnd - (int)(i * ySize / 10.0);
        //			g2d.drawLine(xStart - 1, y, xStart + 1, y);
        //			
        //			String text = "" + i;
        //			g2d.drawString(text,
        //					xStart - 5 - fm.stringWidth(text),
        //					y + fm.getHeight() / 2 - 4);
        //			
        //		}


        // Points drawing
        g2d.setColor(Color.CYAN);
        synchronized(points) {
            for (VanishingGraphValue p : points) {
                p.draw(g2d);
            }
        }
    }

    public void addWaves(List<SinWave> waves) {
        waves.stream().forEach(wave -> {
            if (wave.frequency <= maxFrequency && wave.amplitude > 0.01) {
                this.queue.add(wave.copy());
            }
        });
    }

    public void recalculatePositions() {
        width = getWidth();
        height = getHeight();
        xStart = 20;
        xEnd = width - 20;
        xSize = xEnd - xStart;
        yStart = 10;
        yEnd = height - 16;
        ySize = yEnd - yStart;
        points.clear();
    }

    public void setMaxFrequency(int freq) {
        maxFrequency = freq;
        xStep = Math.max(25, (maxFrequency / 10) / 50 * 50);
        points.clear();
    }

    public void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
        points.clear();
    }

    @Override
    public void run() {
        long t = System.currentTimeMillis();
        long sleepTime = 1000 / desiredFPS;
        while (true) {
            long newT = System.currentTimeMillis();
            long elapsed = newT - t;
            t = newT;

            synchronized(points) {
                for (VanishingGraphValue p : points) {
                    p.update(elapsed);
                }

                points.removeIf(VanishingGraphValue::isDead);
            }
            int newPoints = queue.size();
            for (int i = 0; i < newPoints; i++) {
                SinWave wave = queue.poll();
                double amp = Math.sqrt(wave.amplitude * this.amplitude);
                points.add(new VanishingGraphValue(
                        xStart + (int)(wave.frequency * xSize / maxFrequency),
                        yEnd - (int)(amp * ySize),
                        yEnd, pointSize, pointDuration));
            }

            SwingUtilities.invokeLater(() -> repaint());

            try {
                Thread.sleep(Math.max(sleepTime - elapsed, 0));
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
