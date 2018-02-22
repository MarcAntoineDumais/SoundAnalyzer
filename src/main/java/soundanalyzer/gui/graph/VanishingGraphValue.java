package soundanalyzer.gui.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

public class VanishingGraphValue {
    private int x, y, size;
    private int yEnd;
    private double lifetime, life;

    public VanishingGraphValue(int x, int y, int yEnd, int size, double lifetime) {
        this.x = x;
        this.y = y;
        this.yEnd = yEnd;
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
            Stroke s = g2d.getStroke();
            int alpha = (int) (life / lifetime * 255);
            g2d.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
            g2d.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawLine(x, y, x, yEnd);
            g2d.setColor(c);
            g2d.setStroke(s);
        }
    }
}
