package soundanalyzer.gui;

import java.awt.*;

public class MovingGraphValue {
    private double x;
    private int y, size;

    public MovingGraphValue(double x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public void update(double delta) {
        x -= delta;
    }

    public boolean isAlive() {
        return x > 0;
    }

    public boolean isDead() {
        return x <= 0;
    }

    public void draw(Graphics2D g2d) {
        if (isAlive()) {
            //Stroke s = g2d.getStroke();
            //g2d.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.fillOval((int)x - size/2, y - size/2, size, size);
            //g2d.setStroke(s);
        }
    }
}
