package soundanalyzer.analyzer;

import org.springframework.stereotype.Service;

import soundanalyzer.config.AudioConfig;

@Service
public class FramingService {
    
    private AudioConfig config;
    
    public FramingService(final AudioConfig config) {
        this.config = config;
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
        }
        
        return data;
    }
}
