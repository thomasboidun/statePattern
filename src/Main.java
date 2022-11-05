import java.awt.EventQueue;

import javax.swing.JFrame;

public class Main {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				App app = new App("Paint Drawing");
				app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				app.setSize(800, 600);
				app.setVisible(true);

				System.out.println("App started!");
			}
		});
	}
}