package soundanalyzer.gui.graph;

import soundanalyzer.analyzer.AnalyzerService;
import soundanalyzer.analyzer.FormatConverter;
import soundanalyzer.analyzer.FramingService;
import soundanalyzer.config.ApplicationContextProvider;
import soundanalyzer.config.AudioConfig;
import soundanalyzer.model.SinWave;

import java.util.List;

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
                int y = yEnd + yStep - val * ySize / maxHeight;
                g2d.drawLine(xStart - 1, y, xStart + 1, y);
                String text = "" + val;
                g2d.drawString(text,
                        xStart - fm.stringWidth(text) - 2,
                        y - fm.getHeight());
            }
        }

        // Points drawing
        if (data.length > 0) {
            int pWidth = xSize / data.length;
            int pHeight = ySize / data[0].length;
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[0].length; j++) {
                    g2d.setColor(ColorPalettes.viridis(data[i][j]));
                    g2d.fillRect(xStart + pWidth * i, yEnd - pHeight * j, pWidth, pHeight);
                }
            }
        }
    }

    public void loadData(byte[] rawData) {
        FormatConverter formatConverter = ApplicationContextProvider.getApplicationContext().getBean(FormatConverter.class);
        AnalyzerService analyzerService = ApplicationContextProvider.getApplicationContext().getBean(AnalyzerService.class);
        FramingService framingService = ApplicationContextProvider.getApplicationContext().getBean(FramingService.class);
        AudioConfig config = ApplicationContextProvider.getApplicationContext().getBean(AudioConfig.class);
        
        double[] samples = formatConverter.rawToSamples(rawData);
        double[] preEmphasis = samples.clone();
        for (int i = 1; i < preEmphasis.length; i++) {
            preEmphasis[i] -= config.getProcessing().getPreEmphasis() * samples[i - 1];
        }
        
        double[][] frameData = framingService.samplesToFrames(preEmphasis);
        if (frameData.length > 0) {
            List<SinWave> freqs = analyzerService.fourierTransform(frameData[0], false);
            data = new double[frameData.length][freqs.size()];
            
            for (int i = 0; i < data.length; i++) {
                freqs = analyzerService.fourierTransform(frameData[i], false);
                for (int j = 0; j < data[0].length; j++) {
                    data[i][j] = freqs.get(j).amplitude;
                }
            }
        }
        
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
