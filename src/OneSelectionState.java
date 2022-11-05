import java.awt.Point;
import java.util.ArrayList;

public class OneSelectionState extends SelectionState {
    public OneSelectionState(DrawingPanel drawingPanel) {
        super(drawingPanel);
        selectOne();
    }

    @Override
    public void clearSelection() {
        drawingPanel.changeSelectionState(new NoSelectionState(drawingPanel));
    }

    @Override
    public void selectAll() {
        drawingPanel.changeSelectionState(new AllSelectionState(drawingPanel));        
    }

    @Override
    public void selectOne() {
        Point point = (Point) drawingPanel.getLastPointPressed();
        double x = point.getX();
        double y = point.getY();

        Shape currentShape = drawingPanel.getCurrentShape();
        Shape lastShape = drawingPanel.getLastShape();

        if (currentShape == null && lastShape == null) {
            Shape shape = new Shape(
                    drawingPanel.getShapeType(), x - Shape.DEFAULT_SIZE / 2,
                    y - Shape.DEFAULT_SIZE / 2,
                    Shape.DEFAULT_SIZE,
                    Shape.DEFAULT_SIZE);
                    
            shape.setColor(Shape.getCurrentColor());
            drawingPanel.addToShapes(shape);
        } else {
            ArrayList<Shape> selectedShapes = drawingPanel.getSelectedShapes();
            if (!selectedShapes.contains(currentShape)) {
                currentShape.setSelected(true);
                selectedShapes.add(currentShape);
                drawingPanel.setSelectedShapes(selectedShapes);
            }
        }
        drawingPanel.repaint();        
    }

    @Override
    public void removeSelection() {
        drawingPanel.removeFromShapes(drawingPanel.findShapeByPoint(drawingPanel.getLastPointPressed()));
        drawingPanel.clearSelectedShapes();
        drawingPanel.repaint();
        drawingPanel.changeSelectionState(new NoSelectionState(drawingPanel));
    }

    @Override
    public void coloreSelection() {
        drawingPanel.findShapeByPoint(drawingPanel.getLastPointPressed()).setColor(Shape.getCurrentColor());
        drawingPanel.paintComponent(drawingPanel.getGraphics());
    }
}
