package soundanalyzer.gui.graph;

import soundanalyzer.config.ApplicationContextProvider;
import soundanalyzer.config.AudioConfig;
import soundanalyzer.model.Vector2;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class RawGraph extends JPanel implements Runnable {
    private static final long serialVersionUID = -2423272741202713669L;

    private final int desiredFPS = 30;

    private int width, height;

    private double amplitude, speed;
    private double translation;

    private Queue<Double> queue;
    private List<Vector2> points;
    
    public RawGraph() {
        speed = 0.4;
        amplitude = 8.95;
        translation = 0;
        setOpaque(true);
        setPreferredSize(new Dimension(400, 200));

        queue = new ConcurrentLinkedQueue<>();
        points = Collections.synchronizedList(new ArrayList<>());

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
        g2d.setStroke(new BasicStroke(1));
        AffineTransform transform = g2d.getTransform();
        g2d.translate(-translation, 0);
        GeneralPath path = new GeneralPath();
        synchronized(points) {
            for (int i = 0; i < points.size(); i++) {
                Vector2 point = points.get(i);
                if (i == 0) {
                    path.moveTo(point.x, point.y);
                } else {
                    path.lineTo(point.x, point.y);
                }
            }
        }
        g2d.draw(path);
        g2d.setTransform(transform);
    }

    public void addPoints(double[] points) {
        queue.addAll(Arrays.stream(points).boxed().collect(Collectors.toList()));
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
    }

    @Override
    public void run() {
        AudioConfig formatConfig = ApplicationContextProvider.getApplicationContext().getBean(AudioConfig.class);
        double sampleSeparation = 1000.0 / formatConfig.getFormat().getSampleRate();
        long t = System.currentTimeMillis();
        long sleepTime = 1000 / desiredFPS;
        while (true) {
            long newT = System.currentTimeMillis();
            long elapsed = newT - t;
            t = newT;
            translation += elapsed * speed;
            synchronized(points) {
                points.removeIf(p -> p.x < translation);
            }

            int newPoints = queue.size();
            for (int i = 0; i < newPoints; i++) {
                double d = queue.poll();
                d *= amplitude;
                points.add(new Vector2(width - ((newPoints - i - 1) * sampleSeparation * speed) + translation,
                                       height/2.0 - (d * height/2.0)));
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
