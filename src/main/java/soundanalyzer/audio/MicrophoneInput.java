package soundanalyzer.audio;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import org.springframework.stereotype.Service;

@Service
public class MicrophoneInput {
	private List<MicrophoneListener> listeners;
	private MicrophoneDataProcessor thread;
	
	public MicrophoneInput() {
		listeners = new ArrayList<MicrophoneListener>();
	}
	
	public void start() {
		AudioFormat format = new AudioFormat(16000, 16, 1, true, true);
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		try {
			TargetDataLine line = (TargetDataLine)AudioSystem.getLine(info);
			
			thread = new MicrophoneDataProcessor(line, listeners);
			thread.start();
		} catch (LineUnavailableException e) {
			System.err.println("Could not initialize microphone input");
			e.printStackTrace();
		}
	}
	
	public void stop() {
		if (thread != null) {
			thread.stopProcessing();
		}
	}
	
	public void subscribe(MicrophoneListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	public void unsubscribe(MicrophoneListener listener) {
		listeners.remove(listener);
	}
	
	private class MicrophoneDataProcessor extends Thread {
		private boolean stopped;
		private TargetDataLine line;
		private List<MicrophoneListener> listeners;
		
		public MicrophoneDataProcessor(TargetDataLine line, List<MicrophoneListener> listeners) {
			this.line = line;
			this.listeners = listeners;
		}
		
		@Override
		public void run() {
			stopped = false;
			
			try {
				line.open();
				line.start();
				int bytesRead;
				byte[] data = new byte[line.getBufferSize() / 5];
				long temp;
				double sample;
				while (!stopped) {
					bytesRead = line.read(data, 0, data.length);
					double[] samples = new double[bytesRead/2];
					for (int i = 0; i < bytesRead/2; i++) {
						temp = ((data[2*i] & 0xffL) << 8L) |
								(data[2*i + 1] & 0xffL);
						sample = (temp << 48) >> 48;
						sample = sample / Math.pow(2, 15);
						samples[i] = sample;
					}
					
					listeners.stream().forEach(listener -> listener.readData(samples));
				}
			} catch (LineUnavailableException e) {
				System.err.println("Could not initialize microphone input");
				e.printStackTrace();
			} finally {
				line.close();
				listeners.stream().forEach(listener -> listener.lineClosed());
			}			
		}
		
		public void stopProcessing() {
			this.stopped = true;
		}
	}
}
