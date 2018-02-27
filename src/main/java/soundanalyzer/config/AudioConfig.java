package soundanalyzer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix="audio")
public class AudioConfig {
    
    public static class Format {
        private int sampleRate, sampleSizeInBits, channels, bufferSize;
        private boolean signed, bigEndien;
        
        public int getSampleRate() {
            return sampleRate;
        }
        public void setSampleRate(int sampleRate) {
            this.sampleRate = sampleRate;
        }
        public int getSampleSizeInBits() {
            return sampleSizeInBits;
        }
        public void setSampleSizeInBits(int sampleSizeInBits) {
            this.sampleSizeInBits = sampleSizeInBits;
        }
        public int getChannels() {
            return channels;
        }
        public void setChannels(int channels) {
            this.channels = channels;
        }
        public int getBufferSize() {
            return bufferSize;
        }

        public void setBufferSize(int bufferSize) {
            this.bufferSize = bufferSize;
        }

        public boolean isSigned() {
            return signed;
        }
        public void setSigned(boolean signed) {
            this.signed = signed;
        }
        public boolean isBigEndien() {
            return bigEndien;
        }
        public void setBigEndien(boolean bigEndien) {
            this.bigEndien = bigEndien;
        }
    }
    
    public static class Processing {
        private int frameLength, frameStep;
        private double preEmphasis;
        
        public int getFrameLength() {
            return frameLength;
        }
        public void setFrameLength(int frameLength) {
            this.frameLength = frameLength;
        }
        
        public int getFrameStep() {
            return frameStep;
        }
        public void setFrameStep(int frameStep) {
            this.frameStep = frameStep;
        }
        
        public double getPreEmphasis() {
            return preEmphasis;
        }
        public void setPreEmphasis(double preEmphasis) {
            this.preEmphasis = preEmphasis;
        }
    }
    
    private Format format;
    private Processing processing;
    
    public Format getFormat() {
        return format;
    }
    public void setFormat(Format format) {
        this.format = format;
    }
   
    public Processing getProcessing() {
        return processing;
    }
    public void setProcessing(Processing processing) {
        this.processing = processing;
    }
}
