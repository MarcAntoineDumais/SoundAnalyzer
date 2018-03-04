package soundanalyzer.analyzer;

import org.springframework.stereotype.Service;

import soundanalyzer.config.AudioConfig;

import java.util.HashMap;
import java.util.Map;

@Service
public class FramingService {
    
    private AudioConfig config;
    private Map<Integer, double[]> hammingMap;
    
    public FramingService(final AudioConfig config) {
        this.config = config;
        this.hammingMap = new HashMap<>();
    }
    
    public double[][] samplesToFrames(double[] samples) {
        int frameLength = config.getFormat().getSampleRate() / 1000 * config.getProcessing().getFrameLength();
        int frameStep = config.getFormat().getSampleRate() / 1000 * config.getProcessing().getFrameStep();
        
        int frameCount = (samples.length - frameLength) / frameStep;
        
        double[][] data = new double[frameCount][frameLength];
        for (int i = 0; i < frameCount; i++) {
            int start = i * frameStep;
            for (int j = 0; j < frameLength; j++) {
                data[i][j] = samples[start + j];
            }

            applyHammingWindow(data[i]);
        }
        
        return data;
    }

    public void applyHammingWindow(double[] samples) {
        double[] hamming = getHamming(samples.length);
        for (int i = 0; i < samples.length; i++) {
            samples[i] *= hamming[i];
        }
    }

    private double[] getHamming(int n) {
        if (hammingMap.containsKey(n)) {
            return hammingMap.get(n);
        }

        double[] hamming = new double[n];
        double alpha = 0.53836;
        double beta = 0.46164;
        for (int i = 0; i < n; i++) {
            hamming[i] = alpha - beta * Math.cos(2.0*Math.PI * i / (n - 1.0));
        }
        hammingMap.put(n, hamming);

        return hamming;
    }
}
