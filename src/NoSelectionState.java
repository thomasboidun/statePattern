public class NoSelectionState extends SelectionState {
    public NoSelectionState(DrawingPanel drawingPanel) {
        super(drawingPanel);
        clearSelection();
    }   

    @Override
    public void clearSelection() {
        drawingPanel.clearSelectedShapes();;
    }

    @Override
    public void selectAll() {
        drawingPanel.changeSelectionState(new AllSelectionState(drawingPanel));
    }

    @Override
    public void selectOne() {
        drawingPanel.changeSelectionState(new OneSelectionState(drawingPanel));
    }

    @Override
    public void removeSelection() {
        System.out.println("No selection");
     }

    @Override
    public void coloreSelection() {
        System.out.println("No selection");
    }
}
