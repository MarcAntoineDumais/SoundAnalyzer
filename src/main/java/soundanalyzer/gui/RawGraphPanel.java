package soundanalyzer.gui;

import soundanalyzer.model.RawPoint;
import soundanalyzer.model.SinWave;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RawGraphPanel extends JPanel implements Runnable {
    private static final long serialVersionUID = -2423272741202713669L;

    private final int desiredFPS = 30;
    private final int pointSize = 4;

    private int width, height;

    private double amplitude, speed;

    private Queue<RawPoint> queue;
    private List<MovingGraphValue> points;

    public RawGraphPanel() {
        speed = 0.4;
        amplitude = 1.0;
        setOpaque(true);
        setPreferredSize(new Dimension(400, 200));

        queue = new ConcurrentLinkedQueue<>();
        points = Collections.synchronizedList(new ArrayList<MovingGraphValue>());

        new Thread(this).start();
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;

        width = getWidth();
        height = getHeight();

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // Axis drawing
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(4));
        g2d.drawLine(0, height / 2, width, height / 2);

        // Points drawing
        g2d.setColor(Color.BLUE);
        synchronized(points) {
            for (MovingGraphValue p : points) {
                p.draw(g2d);
            }
        }
    }

    public void addPoints(List<RawPoint> points) {
        queue.addAll(points);
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
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
                for (MovingGraphValue p : points) {
                    p.update(elapsed * speed);
                }

                points.removeIf(MovingGraphValue::isDead);
            }

            int newPoints = queue.size();
            for (int i = 0; i < newPoints; i++) {
                RawPoint p = queue.poll();
                p.value *= amplitude;

                //System.out.println(width + "," + t + ", " + p.time + ", " + speed);
                points.add(new MovingGraphValue(
                        width - ((t - p.time) * speed),
                        height/2 - (int)(p.value * height/2.0),
                        height/2 + (int)(p.value * height/2.0),
                        pointSize));
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
