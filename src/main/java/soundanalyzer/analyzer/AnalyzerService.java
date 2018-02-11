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
	
	public List<SinWave> fourierTransform(double[] samples) {
		int length = samples.length;
		int halfLength = length / 2;
		DoubleFFT_1D fft = new DoubleFFT_1D(length);
		double[] processed = new double[length*2];
		System.arraycopy(samples, 0, processed, 0, length);
		fft.complexForward(processed);
		
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
}
