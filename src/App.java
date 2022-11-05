import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;

public class App {

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				DrawingFrame application = new DrawingFrame("Paint Drawing");
				application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				application.setSize(800, 600);
				application.setVisible(true);

			}
		});
	}
}

class DrawingFrame extends JFrame {
	private DrawingPanel drawingPanel;
	private ColorPalette colorpalette;

	public DrawingFrame(String title) {
		setTitle(title);
		drawingPanel = new DrawingPanel();
		drawingPanel.setShapeCurrentColor(Color.BLACK);
		drawingPanel.setBackground(Color.WHITE);
		this.add(drawingPanel, BorderLayout.CENTER);
		this.add(new JLabel("Click and drag for draw."), BorderLayout.SOUTH);

		JMenuBar topMenu = new JMenuBar();
		setJMenuBar(topMenu);

		JMenu fileMenu = new JMenu("File");
		topMenu.add(fileMenu);

		JMenuItem openItem = new JMenuItem("Open");
		openItem.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				int result = fileChooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					try {
						File selectedFile = fileChooser.getSelectedFile();
						FileInputStream fileInput = new FileInputStream(selectedFile);
						ObjectInputStream objectInput = new ObjectInputStream(fileInput);
						drawingPanel.openFile(objectInput);
					} catch (IOException io) {
						System.exit(1);
					}
				}
			}
		});

		JMenuItem saveItem = new JMenuItem("Save");
		saveItem.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
		saveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				int result = fileChooser.showSaveDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					try {
						File file = fileChooser.getSelectedFile();
						FileOutputStream fileOutput = new FileOutputStream(file);
						ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput);
						drawingPanel.saveFile(objectOutput);

					} catch (IOException io) {
						System.exit(1);
					}
				}
			}
		});

		JMenuItem deleteItem = new JMenuItem("Delete");
		deleteItem.setAccelerator(KeyStroke.getKeyStroke("ctrl C"));
		deleteItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawingPanel.clearAllFromShapeListAndSelectedShapeList();
			}
		});

		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		fileMenu.addSeparator();
		fileMenu.add(deleteItem);

		JMenu selectionMenu = new JMenu("Selection");
		topMenu.add(selectionMenu);

		JMenuItem SelectAllItem = new JMenuItem("Select All");
		SelectAllItem.setAccelerator(KeyStroke.getKeyStroke("ctrl A"));
		SelectAllItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				drawingPanel.selectAllShapes();
			}
		});

		JMenuItem NoSelectionItem = new JMenuItem("No Selection");
		NoSelectionItem.setAccelerator(KeyStroke.getKeyStroke("ESCAPE"));
		NoSelectionItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				drawingPanel.clearSelectedShapeList();
			}
		});

		JMenuItem coloredSelectionItem = new JMenuItem("Colore selection");
		coloredSelectionItem.setAccelerator(KeyStroke.getKeyStroke("ctrl R"));
		selectionMenu.addSeparator();
		coloredSelectionItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				drawingPanel.coloreAllSelectedShape();
			}
		});

		JMenuItem deleteSelectionItem = new JMenuItem("Delete");
		deleteSelectionItem.setAccelerator(KeyStroke.getKeyStroke("BACK_SPACE"));
		deleteSelectionItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				drawingPanel.deleteAllOfSelectedShapeList();
			}
		});

		selectionMenu.add(SelectAllItem);
		selectionMenu.add(NoSelectionItem);
		selectionMenu.add(coloredSelectionItem);
		selectionMenu.add(deleteSelectionItem);

		JPanel toolsBar = new JPanel();
		String imgPath = System.getProperty("user.dir") + File.separator + "images" + File.separator;

		JRadioButton rectangle = new JRadioButton(new ImageIcon(imgPath + "rectangle.png"));
		rectangle.setSelectedIcon(new ImageIcon(imgPath + "rectangleSelect.png"));

		JRadioButton ellipse = new JRadioButton(new ImageIcon(imgPath + "ellipse.png"));
		ellipse.setSelectedIcon(new ImageIcon(imgPath + "ellipseSelect.png"));

		toolsBar.add(rectangle);
		toolsBar.add(ellipse);

		colorpalette = new ColorPalette(Color.BLACK);
		colorpalette.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				Color defeaultColor = getBackground();
				Color selected = JColorChooser.showDialog(DrawingFrame.this,
						"Palette de Color", defeaultColor);

				if (selected != null) {
					colorpalette.setColor(selected);
					drawingPanel.setShapeCurrentColor(selected);
				}
			}
		});

		ButtonGroup group = new ButtonGroup();
		group.add(rectangle);
		group.add(ellipse);

		rectangle.setSelected(true);
		rectangle.addItemListener(new RadioButtonHandler("Rectangle"));
		ellipse.addItemListener(new RadioButtonHandler("Ellipse"));

		toolsBar.add(colorpalette);

		toolsBar.setPreferredSize(new Dimension(54, 70));
		toolsBar.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));

		this.add(toolsBar, BorderLayout.NORTH);
	}

	private class RadioButtonHandler implements ItemListener {
		private String name;

		public RadioButtonHandler(String name) {
			this.name = name;
		}

		public void itemStateChanged(ItemEvent e) {
			if (name.equals("Rectangle"))
				drawingPanel.setShapeType(Shape.Type.RECTANGLE);
			if (name.equals("Ellipse"))
				drawingPanel.setShapeType(Shape.Type.ELLIPSE);
		}
	}

	public static final int DEFAULT_WIDTH = 300;
	public static final int DEFAULT_HEIGHT = 400;
}

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
