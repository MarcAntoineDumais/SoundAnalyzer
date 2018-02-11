package soundanalyzer.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JPanel;

import soundanalyzer.model.SinWave;

import java.awt.BasicStroke;
import java.awt.Color;

public class FourierGraphPanel extends JPanel implements Runnable {
	private static final long serialVersionUID = -2423472748202716661L;

	private final int desiredFPS = 30;
	private final int pointSize = 4;
	private final int pointDuration = 500;
	
	private int width, xStart, xEnd, xSize,
				height, yStart, yEnd, ySize;	
	
	private Queue<SinWave> queue;
	private List<VanishingPoint> points;
	private Thread thread;
	
	public FourierGraphPanel() {
		setOpaque(true);
		setPreferredSize(new Dimension(400, 200));
		
		queue = new ConcurrentLinkedQueue<SinWave>();
		points = Collections.synchronizedList(new ArrayList<VanishingPoint>());
		recalculatePositions();
		
		thread = new Thread(this);
		thread.start();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, width, height);
		
		// Axis drawing
		g2d.setColor(Color.GREEN);
		g2d.setStroke(new BasicStroke(2));
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setFont(new Font("Serif", Font.PLAIN, 12));
		FontMetrics fm = g2d.getFontMetrics();
		// X-axis
		g2d.drawLine(xStart, yEnd, xEnd, yEnd);
		for (int i = 1; i <= 8; i++) {
			int x = xStart + (int)(i * xSize / 8.0);
			g2d.drawLine(x, yEnd + 1, x, yEnd - 1);
			String text = "" + i*1000;
			g2d.drawString(text,
					x - fm.stringWidth(text) / 2,
					height - 2);
		}
		
		// Y-axis
		g2d.drawLine(xStart, yEnd, xStart, yStart);
		for (int i = 1; i <= 10; i++) {
			int y = yEnd - (int)(i * ySize / 10.0);
			g2d.drawLine(xStart - 1, y, xStart + 1, y);
			/*
			String text = "" + i;
			g2d.drawString(text,
					xStart - 5 - fm.stringWidth(text),
					y + fm.getHeight() / 2 - 4);
			*/
		}
		
		// Waves drawing
		synchronized(points) {
			for (VanishingPoint p : points) {
				p.draw(g2d);
			}
		}
		
	}
	
	public void addWaves(List<SinWave> waves) {
		waves.stream().forEach(wave -> this.queue.add(wave.copy()));
	}
	
	public void recalculatePositions() {
		width = getWidth();
		height = getHeight();
		xStart = 20;
		xEnd = width - 20;
		xSize = xEnd - xStart;
		yStart = 10;
		yEnd = height - 16;
		ySize = yEnd - yStart;
		points.clear();
	}

	@Override
	public void run() {
		long t = System.currentTimeMillis();
		long sleepTime = 1000 / desiredFPS;
		while (true) {
			long newT = System.currentTimeMillis();
			long elapsed = newT - t;
			t = newT;
			
			synchronized(points) {
				for (VanishingPoint p : points) {
					p.update(elapsed);
				}
				
				points.removeIf(VanishingPoint::isDead);
			}
			int newPoints = queue.size();
			for (int i = 0; i < newPoints; i++) {
				SinWave wave = queue.poll();
				points.add(new VanishingPoint(
						xStart + (int)(wave.frequency * xSize / 8000.0),
						yEnd - (int)(wave.amplitude * ySize / 10),
						pointSize, pointDuration));
			}
			
			repaint();
			
			try {
				Thread.sleep(Math.max(sleepTime - elapsed, 0));
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
	}
}