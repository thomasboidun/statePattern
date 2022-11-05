import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

class ColorPalette extends JComponent {
	private Color color;
	private Rectangle2D rectangle;

	public ColorPalette(Color color) {
		super();
		this.color = color;
		this.rectangle = new Rectangle2D.Double(5, 5, 15, 15);
		this.setPreferredSize(new Dimension(20, 20));
		this.setMaximumSize(new Dimension(25, 25));
	}

	public void paintComponent(Graphics graphics) {
		Graphics2D g2 = (Graphics2D) graphics;
		g2.setPaint(this.color);
		g2.fill(rectangle);
	}

	public Color getColor() {
		return this.color;
	}

	public void setColor(Color color) {
		this.color = color;
		repaint();
	}
}