package soundanalyzer.analyzer;

import org.springframework.stereotype.Service;

@Service
public class FormatConverter {

    public double[] rawToSamples(byte[] raw) {
        int halfLength = raw.length / 2;
        double[] samples = new double[halfLength];
        long temp;
        double sample;
        for (int i = 0; i < halfLength; i++) {
            temp = ((raw[2*i] & 0xffL) << 8L) |
                    (raw[2*i + 1] & 0xffL);
            sample = (temp << 48) >> 48;
            sample = sample / Math.pow(2, 15);
            samples[i] = sample;
        }

        return samples;
    }
}
