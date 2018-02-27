package soundanalyzer.gui.graph;

import soundanalyzer.analyzer.AnalyzerService;
import soundanalyzer.analyzer.FormatConverter;
import soundanalyzer.analyzer.FramingService;
import soundanalyzer.config.ApplicationContextProvider;
import soundanalyzer.config.AudioConfig;
import soundanalyzer.model.SinWave;

import java.util.Arrays;
import java.util.List;

import javax.swing.*;
import java.awt.*;
import java.util.stream.Collectors;

public class DataGraph extends JPanel {
    private static final long serialVersionUID = -2426472248202716611L;

    private double[][] data;

    public DataGraph() {
        setOpaque(true);
        setPreferredSize(new Dimension(400, 200));
        data = new double[][]{};
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Points drawing
        if (data.length > 0) {
            double xStep = (double)(getWidth()) / data.length;
            double yStep = (double)(getHeight()) / data[0].length;
            int pWidth = Math.max(1, (int)xStep);
            int pHeight = Math.max(1, (int)yStep);
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[0].length; j++) {
                    g2d.setColor(ColorPalettes.viridis(data[i][j]));
                    g2d.fillRect((int)(xStep * i), (int)(getHeight() - yStep * j), pWidth + 1, pHeight + 1);
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
                    if (data[i][j] > 0) {
                        data[i][j] = Math.log(data[i][j]);
                    }
                }
            }
        }

        repaint();
    }
}
