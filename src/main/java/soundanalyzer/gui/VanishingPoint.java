package soundanalyzer.gui;

import java.awt.Color;
import java.awt.Graphics;

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
	
	public void draw(Graphics g) {
		if (isAlive()) {
			Color c = g.getColor();
			int alpha = (int) (life / lifetime * 255);
			g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
			g.fillOval(x - size / 2, y - size / 2, size, size);
			g.setColor(c);
		}
	}
}
