package soundanalyzer.analyzer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;
import soundanalyzer.audio.AudioInput;
import soundanalyzer.model.SinWave;

@Service
public class AnalyzerService {

    private AudioInput audioInput;

    public AnalyzerService(AudioInput audioInput) {
        this.audioInput = audioInput;
    }

    public List<SinWave> fourierTransform(double[] samples, boolean homemade) {
        int length = samples.length;
        int halfLength = length / 2;
        DoubleFFT_1D fft = new DoubleFFT_1D(length);
        double[] processed = new double[length];
        System.arraycopy(samples, 0, processed, 0, length);

        //long t = System.nanoTime();
        if (homemade) {
            calculateFourier(processed);
        } else {
            fft.realForward(processed);
        }
        //System.out.println("homemade " + homemade + " " + (System.nanoTime() - t) / 1000000.0 + "ms");

        ArrayList<SinWave> results = new ArrayList<SinWave>();
        for (int i = 1; i < halfLength; i++) {
            double real = processed[2*i];
            double imaginary = processed[2*i+1];
            double magnitude = real*real + imaginary*imaginary;
            double phase = 0;
            if (real != 0) {
                phase = Math.atan(imaginary / real);
            }
            results.add(new SinWave((i * audioInput.getMaxFrequency()) / halfLength, magnitude, phase));
        }
        return results;
    }

    private void calculateFourier(double[] data) {
        int length = data.length;
        int halfLength = length / 2;
        double[] solution = new double[length];
        double pi2 = Math.PI * 2;
        double angle;
        double[][] cos = new double[halfLength][length];
        double[][] sin = new double[halfLength][length];
        for (int i = 0; i < halfLength; i++) {
            for (int j = i; j < length; j++) {
                angle = pi2 * i * j / length;
                cos[i][j] = Math.cos(angle);
                sin[i][j] = Math.sin(angle);
                if (j < halfLength) {
                    cos[j][i] = cos[i][j];
                    sin[j][i] = sin[i][j];
                }
            }
        }

        for (int i = 0; i < halfLength; i++) {
            for (int j = 0; j < length; j++) {
                solution[2*i] += data[j] * cos[i][j];
                solution[2*i + 1] -= data[j] * sin[i][j];
            }
        }
        for (int i = 0; i < length; i++) {
            data[i] = solution[i];
        }
    }
}
