package soundanalyzer.model;

public class SinWave {
	public double frequency, amplitude, phase;
	
	public SinWave(double frequency) {
		this.frequency = frequency;
		this.amplitude = 1;
		this.phase = 0;
	}
	
	public SinWave(double frequency, double amplitude, double phase) {
		this.frequency = frequency;
		this.amplitude = amplitude;
		this.phase = phase;
	}
	
	public SinWave copy() {
		return new SinWave(frequency, amplitude, phase);
	}
	
	@Override
	public String toString() {
		return "F: " + frequency + ", A: " + amplitude + ", P: " + phase;
	}
}
