package soundanalyzer.audio;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.springframework.stereotype.Service;
import soundanalyzer.model.RawPoint;

@Service
public class AudioInput {
	private List<AudioListener> listeners;
	private AudioDataProcessor thread;
	private Mixer.Info mixerInfo;
	
	private AudioFormat format;
	private static final int SAMPLE_SIZE_IN_BITS = 16;
	private static final int CHANNELS = 1;
	private static final boolean SIGNED = true;
	private static final boolean BIG_ENDIEN = true;
	
	public AudioInput() {
		listeners = new ArrayList<AudioListener>();
		format = new AudioFormat(16000, SAMPLE_SIZE_IN_BITS, CHANNELS, SIGNED, BIG_ENDIEN);
	}
	
	public void start() {
		stop();
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		try {
			TargetDataLine line;
			if (mixerInfo == null) {
				line = (TargetDataLine)AudioSystem.getLine(info);
				line.close();
				thread = new AudioDataProcessor(line, listeners);
				thread.start();
			} else {
				Mixer mixer = AudioSystem.getMixer(mixerInfo);
				line = (TargetDataLine)mixer.getLine(info);
				line.close();
				thread = new AudioDataProcessor(line, listeners);
				thread.start();
			}			
		} catch (LineUnavailableException e) {
			System.err.println("Could not initialize microphone input");
			stop();
			e.printStackTrace();
		}
	}
	
	public void stop() {
		if (thread != null) {
			thread.stopProcessing();
			while (!thread.closed);
		}
	}
	
	public double getMaxFrequency() {
		return format.getSampleRate() / 2.0;
	}
	
	public void setMaxFrequency(int maxFrequency) {
		format = new AudioFormat(maxFrequency * 2f, SAMPLE_SIZE_IN_BITS, CHANNELS, SIGNED, BIG_ENDIEN);
		if (thread != null && !thread.isStopped()) {
			start();
		}
	}
	
	public void setMixerInfo(Mixer.Info mixerInfo) {
		this.mixerInfo = mixerInfo;
	}
	
	public void subscribe(AudioListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	public void unsubscribe(AudioListener listener) {
		listeners.remove(listener);
	}
	
	private class AudioDataProcessor extends Thread {
		private boolean stopped;
		private boolean closed;
		private TargetDataLine line;
		private List<AudioListener> listeners;
		
		public AudioDataProcessor(TargetDataLine line, List<AudioListener> listeners) {
			this.line = line;
			this.listeners = listeners;
			this.closed = false;
		}
		
		@Override
		public void run() {
			stopped = false;
			
			try {
				line.open();
				line.start();
				listeners.stream().forEach(listener -> listener.lineOpened());
				int bytesRead;
				byte[] data = new byte[Math.max(1, line.getBufferSize() / 10)];
				long temp;
				double sample;
				while (!stopped) {
					bytesRead = line.read(data, 0, data.length);
					RawPoint[] samples = new RawPoint[bytesRead/2];
					for (int i = 0; i < bytesRead/2; i++) {
						temp = ((data[2*i] & 0xffL) << 8L) |
								(data[2*i + 1] & 0xffL);
						sample = (temp << 48) >> 48;
						sample = sample / Math.pow(2, 15);
						samples[i] = new RawPoint(sample);
					}
					
					listeners.stream().forEach(listener -> listener.readData(samples));
				}
			} catch (LineUnavailableException e) {
				System.err.println("Could not initialize microphone input");
				e.printStackTrace();
			} finally {
				listeners.stream().forEach(listener -> listener.lineClosed());
				closed = true;
			}			
		}
		
		public void stopProcessing() {
			this.stopped = true;
			line.close();
		}
		
		public boolean isStopped() {
			return stopped;
		}
		
		public boolean isClosed() {
			return closed;
		}
	}
}
