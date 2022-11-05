public class AllSelectionState extends SelectionState {
    public AllSelectionState(DrawingPanel drawingPanel) {
        super(drawingPanel);
        selectAll();
    }

    @Override
    public void clearSelection() {
        drawingPanel.changeSelectionState(new NoSelectionState(drawingPanel));
    }

    @Override
    public void selectAll() {
        drawingPanel.setSelectedShapes(drawingPanel.getShapes());
        drawingPanel.repaint();
    }

    @Override
    public void selectOne() {
        drawingPanel.changeSelectionState(new OneSelectionState(drawingPanel));
    }

    @Override
    public void removeSelection() {
        drawingPanel.clearShapesAndSelectedShapes();
        drawingPanel.changeSelectionState(new NoSelectionState(drawingPanel));
    }

    public void coloreSelection() {
		for (Shape shape : drawingPanel.getSelectedShapes()) {
			shape.setColor(Shape.getCurrentColor());
		}
		drawingPanel.paintComponent(drawingPanel.getGraphics());
    }
}
