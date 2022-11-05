public abstract class SelectionState {
    public DrawingPanel drawingPanel;

    public SelectionState(DrawingPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
    }

    public abstract void clearSelection();
    public abstract void selectAll();
    public abstract void selectOne();
    public abstract void removeSelection();
    public abstract void coloreSelection();
}
