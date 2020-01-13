import javax.swing.*;
import java.awt.*;
import java.io.*;

public class Progress extends JFrame {

	private static final char STATES[] = { '|', '/', '-', '\\', '|', '/', '-', '\\' };	

	private Progress() {
		super("Working...");
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout(10, 10));
		final JProgressBar progress = new JProgressBar(0,100);
		progress.setStringPainted(true);
		cp.add("Center", progress);

		cp.add("North", new JLabel(" "));
		cp.add("South", new JLabel(" "));
		cp.add("West", new JLabel(" "));
		cp.add("East", new JLabel(" "));

		Thread t = new Thread() {
			public void run() {
				int j = 0;
				while(true) {
					for(int i = 0; i < 100; i++) {
						j++;
						progress.setValue(i);
						progress.setString("Work in progress, j=" + j);
						try {
							sleep(100);
						} catch(Exception ex) {
							ex.printStackTrace();
							System.exit(0);
						}
					}
				}
			}
		};
		t.start();		

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	private static void cmdLineProgress() {
		System.out.print(" ");

		Thread action = new Thread() {
			private boolean on = true;
			
			public void run() {
				while(on) {
					for(int i = 0; i < STATES.length; i++) {
						System.out.print(STATES[i] + " ");
						try {
							sleep(200);
						} catch(Exception ex) {
							on = false;
							ex.printStackTrace();
							System.exit(0);
						}
						System.out.print("\b\b");
					}
				}
				System.out.print('\b');
			}

			public void interrupt() {
				on = false;
			}
		};
		action.start();

		// Ausschalter (Über die Enter taste betätigbar...)
		InputStreamReader in = new InputStreamReader(System.in);
		try {
			in.read();
		} catch(Exception ex) {
			action.interrupt();
			ex.printStackTrace();
			System.exit(0);
		}
		action.interrupt();
	}

	public static void main(String[] args) {
		if(args.length == 0) {
			new Progress();
		} else if(args[0].equals("-cmd")) {
			cmdLineProgress();
		} else {
			System.out.println("Use -cmd for commandline progress display.");
		}
	}
}
