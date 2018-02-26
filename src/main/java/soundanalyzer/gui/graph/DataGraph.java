package soundanalyzer.gui.graph;

import soundanalyzer.analyzer.AnalyzerService;
import soundanalyzer.analyzer.FormatConverter;
import soundanalyzer.config.ApplicationContextProvider;

import javax.swing.*;
import java.awt.*;

public class DataGraph extends JPanel {
    private static final long serialVersionUID = -2423472748202716661L;

    private int width, xStart, xEnd, xSize,
    height, yStart, yEnd, ySize;

    private double[][] data;

    public DataGraph() {
        setOpaque(true);
        setPreferredSize(new Dimension(400, 200));
        data = new double[][]{};
        recalculatePositions();
    }

    @Override
    public void paintComponent(Graphics g) {
        recalculatePositions();
        Graphics2D g2d = (Graphics2D)g;

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // Axis drawing
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setFont(new Font("Serif", Font.PLAIN, 12));
        FontMetrics fm = g2d.getFontMetrics();

        // X-axis
        g2d.drawLine(xStart, yEnd, xEnd, yEnd);
        if (data.length > 0) {
            int maxSample = data.length;
            int xStep = maxSample / 10;
            for (int sample = xStep; sample <= maxSample; sample += xStep) {
                int x = xStart + sample * xSize / maxSample;
                g2d.drawLine(x, yEnd + 1, x, yEnd - 1);
                String text = "" + sample;
                g2d.drawString(text,
                        x - fm.stringWidth(text) / 2,
                        height - 2);
            }
        }

        // Y-axis
        g2d.drawLine(xStart, yStart, xStart, yEnd);
        if (data.length > 0) {
            int maxHeight = data[0].length;
            int yStep = maxHeight / 10;
            for (int val = yStep; val <= maxHeight; val += yStep) {
                int y = yStart + val * ySize / maxHeight;
                g2d.drawLine(xStart - 1, y, xStart + 1, y);
                String text = "" + val;
                g2d.drawString(text,
                        xStart - fm.stringWidth(text) - 2,
                        y - fm.getHeight());
            }
        }

        // Points drawing
        if (data.length > 0) {
            int pWidth = xSize / data[0].length;
            int pHeight = ySize / data.length;
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[0].length; j++) {
                    g2d.setColor(Color.CYAN);

                    g2d.fillRect(xStart + pWidth * j, yEnd - pHeight * i, pWidth, pHeight);
                }
            }
        }
    }

    public void loadData(byte[] data) {
        FormatConverter formatConverter = ApplicationContextProvider.getApplicationContext().getBean(FormatConverter.class);
        AnalyzerService analyzerService = ApplicationContextProvider.getApplicationContext().getBean(AnalyzerService.class);

        double[] samples = formatConverter.rawToSamples(data);


        repaint();
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
    }
}
