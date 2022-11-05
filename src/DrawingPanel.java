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

/**
 * @param DrawingPanel
 *                         tableau contenant les coordonner de dessin a
 *                         selectionner
 * @param shapeList
 *                         Tableau contenant les formes geometriques
 * @param currentshape
 *                         lorsqu'un figure est selectionner
 * @param lastPointPressed
 *                         le point precedent
 * @param allSelected
 *                         une facon pour faire la verification en cas ou on a
 *                         selectionner
 *                         ou nom voir mouseDragged
 * @param allSelected
 *                         sert a montre si tout est selectionner une methode
 *                         cree pour
 *                         verifier
 * 
 */
public class DrawingPanel extends JPanel {

	private ArrayList<Shape> shapeList;
	private ArrayList<Shape> selectedShapeList;
	private Shape currentshape;
	private Point2D lastPointPressed;
	private Shape lastShape = null;
	private String lastFile = ".";
	private int allSelected;
	private Color color;
	private int DEFAULT_SHAPE_SIZE = 30;
	private Shape.Type shapeType = Shape.Type.RECTANGLE;
	private Object object;
	private int key;

	public DrawingPanel() {
		this.shapeList = new ArrayList<Shape>();
		this.selectedShapeList = new ArrayList<Shape>();
		this.currentshape = null;
		this.addMouseListener(new MouseHandler());
		this.addMouseMotionListener(new MouseMotionHandler());
	}

	public void setLastFile(String lastFile) {
		this.lastFile = lastFile;
	}

	public String getLastFile() {
		return this.lastFile;
	}

	public void clearAllFromShapeListAndSelectedShapeList() {
		this.clearSelectedShapeList();
		this.shapeList.clear();
		this.paintComponent(this.getGraphics());
	}

	public void clearSelectedShapeList() {
		for (Shape shape : selectedShapeList) {
			shape.setSelected(false);
		}
		this.selectedShapeList.clear();
		this.repaint();
	}

	public void deleteAllOfSelectedShapeList() {
		for (Shape shape : selectedShapeList) {
			this.removeFromShapeList(shape);
		}
		this.selectedShapeList.clear();
		this.repaint();
	}

	public void selectAllShapes() {
		this.allSelected = 1;
		this.selectedShapeList.clear();
		this.selectedShapeList.addAll(this.shapeList);
		for (Shape shape : this.selectedShapeList) {
			shape.setSelected(true);
		}
		this.repaint();
	}

	public void coloreAllSelectedShape() {
		Color color = Shape.getCurrentColor();
		for (Shape shape : this.selectedShapeList) {
			shape.setColor(color);
		}
		paintComponent(getGraphics());
	}

	public void setShapeCurrentColor(Color color) {
		Shape.setCurrentColor(color);
	}

	public void setShapeType(Shape.Type type) {
		this.shapeType = type;
	}

	public Shape findShapeByPointFromShapeList(Point2D point) {
		for (Shape shape : shapeList) {
			if (shape.contains(point)) {
				return shape;
			}
		}
		return null;
	}

	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		Graphics2D g2 = (Graphics2D) graphics;
		for (Shape shape : shapeList) {
			shape.draw(g2);
			if (currentshape != null) {
				this.addSelectionMarks(g2, currentshape);
				this.repaint();
			}
			if (allSelected == 1) {
				for (Shape selec : selectedShapeList) {
					this.addSelectionMarks(g2, selec);
					this.repaint();
				}
			}
		}
	}

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

	public void addToShapeList(Shape shape) {
		this.shapeList.add(shape);
	}

	public void removeFromShapeList(Shape shape) {
		this.shapeList.remove(shape);
	}

	private class MouseHandler extends MouseAdapter {
		public void mousePressed(MouseEvent event) {
			Point point = event.getPoint();
			currentshape = findShapeByPointFromShapeList(point);
			lastPointPressed = point;
			lastShape = null;
		}

		public void mouseClicked(MouseEvent event) {
			allSelected = 0;
			Point point = event.getPoint();
			double x = point.getX();
			double y = point.getY();
			if (currentshape == null && lastShape == null) {
				Shape shape = new Shape(
						shapeType, x - DEFAULT_SHAPE_SIZE / 2,
						y - DEFAULT_SHAPE_SIZE / 2,
						DEFAULT_SHAPE_SIZE,
						DEFAULT_SHAPE_SIZE);
				shape.setColor(Shape.getCurrentColor());
				addToShapeList(shape);
			} else {
				if (!selectedShapeList.contains(currentshape)) {
					currentshape.setSelected(true);
					selectedShapeList.add(currentshape);
				}
			}
			repaint();
		}
	}

	/**
	 * Lorsque on appui sur Maj(shift) enfonce.
	 * 
	 * @exception ConcurrentModificationException
	 *                                            Cette exception peut être levée
	 *                                            par les méthodes qui ont détecté
	 *                                            une modification
	 *                                            concurrente d'un objet lorsque
	 *                                            cette modification n'est pas
	 *                                            autorisée.
	 *                                            "http://docs.oracle.com/javase/7/docs/api/java/util/ConcurrentModificationException.html"
	 */
	private class MouseMotionHandler implements MouseMotionListener {
		public void mouseMoved(MouseEvent event) {
			if (findShapeByPointFromShapeList(event.getPoint()) == null)
				setCursor(Cursor.getDefaultCursor());
			else
				setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		}

		public void mouseDragged(MouseEvent event) {
			Point p = event.getPoint();
			if (currentshape == null) {
				if (lastShape == null) {
					lastShape = new Shape(shapeType);
					lastShape.setColor(Shape.getCurrentColor());
					addToShapeList(lastShape);
				}
				lastShape.setFrameFromDiagonal(lastPointPressed, p);
			} else {
				double dx = p.getX() - lastPointPressed.getX();
				double dy = p.getY() - lastPointPressed.getY();
				if (!selectedShapeList.contains(currentshape)) {
					currentshape.moveBy(dx, dy);
				}
				try {
					for (Shape f : selectedShapeList) {
						f.moveBy(dx, dy);
						if (allSelected != 1) {
							if (selectedShapeList.size() >= 0)
								selectedShapeList.clear();
						}
					}
				} catch (ConcurrentModificationException e) {
					allSelected = 0;
				}
				lastPointPressed = p;
			}
			repaint();
		}
	}

	public void saveFile(ObjectOutputStream out) {
		try {
			for (Shape f : shapeList) {
				f.setSelected(true);
				out.writeObject(f);
			}
		} catch (IOException io) {
			System.exit(1);
		}
	}
	
	public void openFile(ObjectInputStream in) {
		try {
			shapeList.clear();
			object = in.readObject();
			while (object != null) {
				if (object != null) {
					shapeList.add((Shape) object);
					object = in.readObject();
				}
			}
			in.close();
			repaint();
		}
		catch (IOException | ClassNotFoundException e) {
			shapeList.add((Shape) object);
			repaint();
		}
	}
}