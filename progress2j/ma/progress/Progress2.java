package ma.progress;

import ma.zentral.plg.SimplePlugin;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;

public class Progress2 extends JFrame implements WindowListener, SimplePlugin {

	private ProgressThread thread;
	private boolean direct;

	public Progress2() {
		super("Progress 2");
		direct = false;
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());

		Box line = new Box(BoxLayout.X_AXIS);
		cp.setBackground(Color.DARK_GRAY);

		JPanel workingPan = new JPanel(new FlowLayout(FlowLayout.LEFT));
		workingPan.setBackground(Color.DARK_GRAY);
		JLabel working = new JLabel(" Arbeit im Gange... Bitte warten.");
		working.setForeground(Color.LIGHT_GRAY);
		workingPan.add(working);
		line.add(workingPan);

		JPanel wheel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		wheel.setBackground(Color.DARK_GRAY);
		JLabel tbp = new JLabel("...");
		tbp.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
		tbp.setForeground(Color.GREEN);
		wheel.add(tbp);
		line.add(wheel);

		ProgressDisplay pb = new ProgressDisplay();

		cp.add("Center", pb);
		cp.add("North", line);

		thread = new ProgressThread(true, pb, tbp);
		thread.start();
		thread.setPaused(true);

		addWindowListener(this);
		setSize(400, 300);
		setLocation(10, 10);
	}

	public void signal(int signal) {
		switch(signal) {
			case SIGNAL_START: {
				setVisible(true);
				thread.setPaused(false);
				break;
			}
			case SIGNAL_TERM: {
				setVisible(false);
				thread.setPaused(true);
				if(direct) {
					System.exit(0);
				}
				break;
			}
			case SIGNAL_KILL: {
				thread.interrupt();
				dispose();
				break;
			}
		}
	}

	public void setDirect() {
		direct = true;
	}

	public static void main(String[] args) {
		System.out.println("Progress 2, Copyright (c) 2011 Ma_Sys.ma.");
		System.out.println("For further info send an e-mail to Ma_Sys.ma@web.de.");
		System.out.println();
		if(args.length == 1 && args[0].equals("-cmd")) {
			System.out.println("Press enter to exit the application...");
			System.out.println();
			ProgressThread thread = new ProgressThread(false, null, null);
			thread.start();
			try {
				new BufferedReader(new InputStreamReader(System.in)).readLine();
			} catch(IOException ex) {
				System.out.println();
				ex.printStackTrace();
			} finally {
				thread.interrupt();
			}
		} else if(args.length == 1) {
			System.out.println("USAGE: java Progress2 [-cmd]");
		} else {
			Progress2 progress = new Progress2();
			progress.setDirect();
			progress.signal(SIGNAL_START);
		}
	}

	public void windowClosing(WindowEvent event) {
		signal(SIGNAL_TERM);
	}

	public void windowDeiconified(WindowEvent event) {
		thread.setPaused(false);
	}

	public void windowIconified(WindowEvent event) {
		thread.setPaused(true);
	}

	public void windowActivated(WindowEvent e)   {}
	public void windowClosed(WindowEvent e)      {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowOpened(WindowEvent e)      {}

}
