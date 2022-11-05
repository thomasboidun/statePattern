import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import javax.swing.JPanel;

public class DrawingPanel extends JPanel {

	private class MouseHandler extends MouseAdapter {
		private DrawingPanel drawingPanel;

		public MouseHandler(DrawingPanel drawingPanel) {
			this.drawingPanel = drawingPanel;
		}

		public void mousePressed(MouseEvent event) {
			Point point = event.getPoint();
			currentShape = findShapeByPoint(point);
			lastPointPressed = point;
			lastShape = null;
		}

		public void mouseClicked(MouseEvent event) {
			changeSelectionState(new OneSelectionState(drawingPanel));
		}
	}

	private class MouseMotionHandler implements MouseMotionListener {
		public void mouseMoved(MouseEvent event) {
			if (findShapeByPoint(event.getPoint()) == null)
				setCursor(Cursor.getDefaultCursor());
			else
				setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		}

		public void mouseDragged(MouseEvent event) {
			Point p = event.getPoint();
			if (currentShape == null) {
				if (lastShape == null) {
					lastShape = new Shape(shapeType);
					lastShape.setColor(Shape.getCurrentColor());
					addToShapes(lastShape);
				}
				lastShape.setFrameFromDiagonal(lastPointPressed, p);
			} else {
				double dx = p.getX() - lastPointPressed.getX();
				double dy = p.getY() - lastPointPressed.getY();
				if (!selectedShapes.contains(currentShape)) {
					currentShape.moveBy(dx, dy);
				}
				try {
					for (Shape f : selectedShapes) {
						f.moveBy(dx, dy);
						if (!allShapesAreSelected()) {
							if (selectedShapes.size() >= 0)
								selectedShapes.clear();
						}
					}
				} catch (ConcurrentModificationException e) {
					System.out.print(e);
				}
				lastPointPressed = p;
			}
			repaint();
		}
	}

	private String file = ".";
	private SelectionState selectionState;
	private ArrayList<Shape> shapes = new ArrayList<Shape>();
	private ArrayList<Shape> selectedShapes = new ArrayList<Shape>();
	private Shape currentShape = null;
	private Shape lastShape = null;
	private Point2D lastPointPressed = null;
	private Shape.Type shapeType = Shape.Type.RECTANGLE;
	private Object object = null;

	public DrawingPanel() {
		this.selectionState = new NoSelectionState(this);
		this.addMouseListener(new MouseHandler(this));
		this.addMouseMotionListener(new MouseMotionHandler());
	}

	// GETTERS & SETTERS

	public String getFile() {
		return this.file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public void changeSelectionState(SelectionState state) {
		this.selectionState = state;
	}

	public SelectionState getSelectionState() {
		return this.selectionState;
	}

	public ArrayList<Shape> getShapes() {
		return this.shapes;
	}

	public ArrayList<Shape> getSelectedShapes() {
		return this.selectedShapes;
	}

	public void setSelectedShapes(ArrayList<Shape> shapes) {
		this.selectedShapes.clear();
		this.selectedShapes.addAll(shapes);
		for (Shape shape : this.selectedShapes) {
			shape.setSelected(true);
		}
	}

	public Shape getCurrentShape() {
		return this.currentShape;
	}

	public void setCurrentShape(Shape shape) {
		this.currentShape = shape;
	}

	public Shape getLastShape() {
		return this.lastShape;
	}

	public void setLastShape(Shape shape) {
		this.currentShape = shape;
	}

	public Point2D getLastPointPressed() {
		return this.lastPointPressed;
	}

	public void setLastPointPressed(Point point) {
		this.lastPointPressed = point;
	}

	public Shape.Type getShapeType() {
		return this.shapeType;
	}

	public void setShapeType(Shape.Type type) {
		this.shapeType = type;
	}

	// Utils

	public void addSelectionMarks(Graphics2D g2D, Shape f) {
		double x = f.getX();
		double y = f.getY();
		double w = f.getWidth();
		double h = f.getHeight();
		g2D.setColor(Color.BLACK);

		g2D.fill(new Rectangle.Double(x + w * 0.5 - 3.0, y - 3.0, 6.0, 6.0));

		g2D.fill(new Rectangle.Double(x - 3.0, y + h * 0.5 - 3.0, 6.0, 6.0));
		g2D.fill(new Rectangle.Double(x + w - 3.0, y + h * 0.5 - 3.0, 6.0, 6.0));

		g2D.fill(new Rectangle.Double(x + w * 0.5 - 3.0, y + h - 3.0, 6.0, 6.0));

	}

	public void addToSelectedShapes(Shape shape) {
		this.selectedShapes.add(shape);
	}

	public void addToShapes(Shape shape) {
		this.shapes.add(shape);
	}

	public boolean allShapesAreSelected() {
		return this.shapes.size() == this.selectedShapes.size();
	}

	public void clearSelectedShapes() {
		this.currentShape = null;
		this.lastShape = null;
		this.lastPointPressed = null;

		for (Shape shape : selectedShapes) {
			shape.setSelected(false);
		}

		this.selectedShapes.clear();
		this.repaint();
	}

	public void clearShapesAndSelectedShapes() {
		this.clearSelectedShapes();
		this.shapes.clear();
		this.paintComponent(this.getGraphics());
	}

	// public void coloreSelectedShapes() {
	// 	Color color = Shape.getCurrentColor();
	// 	for (Shape shape : this.selectedShapes) {
	// 		shape.setColor(color);
	// 	}
	// 	paintComponent(getGraphics());
	// }

	public Shape findShapeByPoint(Point2D point) {
		for (Shape shape : shapes) {
			if (shape.contains(point)) {
				return shape;
			}
		}
		return null;
	}

	public void openFile(ObjectInputStream in) {
		try {
			shapes.clear();
			object = in.readObject();
			while (object != null) {
				if (object != null) {
					shapes.add((Shape) object);
					object = in.readObject();
				}
			}
			in.close();
			repaint();
		} catch (IOException | ClassNotFoundException e) {
			shapes.add((Shape) object);
			repaint();
		}
	}

	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		Graphics2D g2 = (Graphics2D) graphics;
		for (Shape shape : shapes) {
			shape.draw(g2);
			if (currentShape != null) {
				this.addSelectionMarks(g2, currentShape);
				this.repaint();
			}
			if (this.allShapesAreSelected()) {
				for (Shape selec : selectedShapes) {
					this.addSelectionMarks(g2, selec);
					this.repaint();
				}
			}
		}
	}

	public void removeFromShapes(Shape shape) {
		this.shapes.remove(shape);
	}

	// public void removeSelectedShapes() {
	// 	if(this.allShapesAreSelected()) {
	// 		for (Shape shape : this.shapes) {
	// 			this.removeFromShapes(shape);
	// 		}
	// 	} else {
	// 		this.removeFromShapes(this.findShapeByPoint(this.lastPointPressed));
	// 	}
		
	// 	this.selectedShapes.clear();

	// 	this.repaint();
	// 	this.changeSelectionState(new NoSelectionState(this));
	// }

	public void saveFile(ObjectOutputStream out) {
		try {
			for (Shape f : shapes) {
				f.setSelected(true);
				out.writeObject(f);
			}
		} catch (IOException io) {
			System.exit(1);
		}
	}

	// public void selectAllShapes() {
	// 	this.selectedShapes.clear();
	// 	this.selectedShapes.addAll(this.shapes);
	// 	for (Shape shape : this.selectedShapes) {
	// 		shape.setSelected(true);
	// 	}
	// 	this.repaint();
	// }

	public void setShapeCurrentColor(Color color) {
		Shape.setCurrentColor(color);
	}
}