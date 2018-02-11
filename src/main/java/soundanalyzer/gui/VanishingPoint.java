package soundanalyzer.gui;

import java.awt.Color;
import java.awt.Graphics2D;

public class VanishingPoint {
	private int x, y, size;
	private double lifetime, life;
	
	public VanishingPoint(int x, int y, int size, double lifetime) {
		this.x = x;
		this.y = y;
		this.size = size;
		this.lifetime = lifetime;
		this.life = lifetime;
	}
	
	public void update(double delta) {
		life -= delta;
	}
	
	public boolean isAlive() {
		return life > 0;
	}
	
	public boolean isDead() {
		return life <= 0;
	}
	
	public void draw(Graphics2D g2d) {
		if (isAlive()) {
			Color c = g2d.getColor();
			int alpha = (int) (life / lifetime * 255);
			g2d.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
			g2d.fillOval(x - size / 2, y - size / 2, size, size);
			g2d.setColor(c);
		}
	}
}
