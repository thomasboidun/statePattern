import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;

class App extends JFrame {
	private DrawingPanel drawingPanel;
	private ColorPalette colorPalette;

	public App(String title) {
		System.out.println("Start of the initialization of the application...");

		this.setTitle(title);

		this.drawingPanel = new DrawingPanel();
		this.drawingPanel.setShapeCurrentColor(Color.BLACK);
		this.drawingPanel.setBackground(Color.WHITE);

		this.add(this.drawingPanel, BorderLayout.CENTER);
		this.add(new JLabel("Click and drag for draw."), BorderLayout.SOUTH);

		JMenuBar topMenu = new JMenuBar();
		this.setJMenuBar(topMenu);

		JMenu fileMenu = this.addMenu("File");
		this.addMenuItem("Open", "ctrl O", this.getOpenAction(), fileMenu, false);
		this.addMenuItem("Save", "ctrl S", this.getSaveAction(), fileMenu, true);
		this.addMenuItem("Delete", "ctrl D", this.getDeleteAction(), fileMenu, false);

		JMenu selectionMenu = this.addMenu("Selection");
		this.addMenuItem("Select All", "ctrl A", this.getSelectAllAction(), selectionMenu, false);
		this.addMenuItem("No selection", "ESCAPE", this.getNoSelectionAction(), selectionMenu, false);
		this.addMenuItem("Colore selection", "ctrl C", this.getColoreSelectionAction(), selectionMenu, false);
		this.addMenuItem("Remove selection", "BACK_SPACE", this.getRemoveSelectionAction(), selectionMenu, false);

		this.addToolsBar();

		System.out.println("End of the initialization of the application.");
	}

	private JMenu addMenu(String name) {
		return this.getJMenuBar().add(new JMenu(name));
	}

	private void addMenuItem(String name, String accelerator, ActionListener action, JMenu menu, boolean separator) {
		JMenuItem menuItem = new JMenuItem(name);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(accelerator));
		menuItem.addActionListener(action);
		menu.add(menuItem);
		if (separator) {
			menu.addSeparator();
		}
	}

	private void addToolsBar() {
		JPanel toolsBar = new JPanel();
		String imgPath = System.getProperty("user.dir") + File.separator + "images" + File.separator;

		JRadioButton rectangle = new JRadioButton(new ImageIcon(imgPath + "rectangle.png"));
		rectangle.setSelectedIcon(new ImageIcon(imgPath + "rectangleSelect.png"));

		JRadioButton ellipse = new JRadioButton(new ImageIcon(imgPath + "ellipse.png"));
		ellipse.setSelectedIcon(new ImageIcon(imgPath + "ellipseSelect.png"));

		toolsBar.add(rectangle);
		toolsBar.add(ellipse);

		colorPalette = new ColorPalette(Color.BLACK);
		colorPalette.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				Color defeaultColor = getBackground();
				Color selected = JColorChooser.showDialog(App.this,
						"Palette de Color", defeaultColor);

				if (selected != null) {
					colorPalette.setColor(selected);
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

		toolsBar.add(colorPalette);

		toolsBar.setPreferredSize(new Dimension(54, 70));
		toolsBar.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));

		this.add(toolsBar, BorderLayout.NORTH);
	}

	private ActionListener getOpenAction() {
		return new ActionListener() {
			@Override
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
		};
	}

	private ActionListener getSaveAction() {
		return new ActionListener() {
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
		};
	}

	private ActionListener getDeleteAction() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawingPanel.changeSelectionState(new NoSelectionState(drawingPanel));
				drawingPanel.clearShapesAndSelectedShapes();
			}
		};
	}

	private ActionListener getSelectAllAction() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// drawingPanel.selectAllShapes();
				drawingPanel.getSelectionState().selectAll();
			}
		};
	}

	private ActionListener getNoSelectionAction() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// drawingPanel.clearSelectedShapes();
				drawingPanel.getSelectionState().clearSelection();
			}
		};
	}

	private ActionListener getColoreSelectionAction() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				drawingPanel.getSelectionState().coloreSelection();
			}
		};
	}

	private ActionListener getRemoveSelectionAction() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				drawingPanel.getSelectionState().removeSelection();;
			}
		};
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
