import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.io.Serializable;

public class Shape implements Serializable {
	public static enum Type {
		RECTANGLE, ELLIPSE
	};

	protected static Shape.Type Type;
	private static Color currentColor = Color.BLACK;
	public static int DEFAULT_SIZE = 30;

	private boolean selected;
	private RectangularShape render;
	private Color color;
	private Type type;

	public Shape(Type type, double x, double y, double width, double height) {
		this.type = type;
		if (this.type == Shape.Type.RECTANGLE) {
			this.render = new Rectangle2D.Double(x, y, width, height);
		} else if (type == Shape.Type.ELLIPSE) {
			this.render = new Ellipse2D.Double(x, y, width, height);
		}

	}

	public Shape(Type type) {
		this(type, 0, 0, 0, 0);
	}

	public static void setCurrentColor(Color color) {
		currentColor = color;
	}

	public static Color getCurrentColor() {
		return currentColor;
	}

	public void draw(Graphics2D graphics) {
		graphics.setPaint(color);
		graphics.fill(render);

	}

	public void setSelected(boolean value) {
		this.selected = value;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return this.color;
	}

	public boolean isSelected() {
		return this.selected;
	}

	public boolean contains(Point2D point) {
		return this.render.contains(point);
	}

	public double getWidth() {
		return this.render.getWidth();
	}

	public double getX() {
		return this.render.getX();
	}

	public double getHeight() {
		return this.render.getHeight();
	}

	public double getY() {
		return this.render.getY();
	}

	public void moveBy(double dx, double dy) {
		double x = this.render.getX();
		double y = this.render.getY();
		double w = this.render.getWidth();
		double h = this.render.getHeight();
		this.render.setFrame(x + dx, y + dy, w, h);
	}

	public void setFrameFromDiagonal(Point2D lastPointPressed, Point2D p) {
		this.render.setFrameFromDiagonal(lastPointPressed, p);
	}
}