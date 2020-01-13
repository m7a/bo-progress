package ma.progress;

import javax.swing.JLabel;

class ProgressThread extends Thread {

	private static final char[] textProgress = { 
		'|', '/', '-', '\\',
		'|', '/', '-', '\\',
	};

	private ProgressDisplay pb;
	private JLabel tpb;
	private boolean iconified;
	private boolean gui;

	ProgressThread(boolean gui, ProgressDisplay display, JLabel tpb) {
		super("Progress Display Thread");
		pb        = display;
		this.gui  = gui;
		this.tpb  = tpb;
		iconified = false;
	}

	public void run() {
		// Separated loops for better performace.
		if(gui) {
			float cs = 0f;
			while(!isInterrupted()) {
				cs = pb.getStat();
				if(cs >= 1f) {
					pb.setStat(0f);
					continue;
				}
				tpb.setText(String.valueOf(textProgress[(int)(textProgress.length * cs)]));
				tpb.repaint();
				pb.setStat(cs + 0.005f);
				try {
					// To handle paused status
					do {
						sleep(40);
					} while(iconified);
				} catch(InterruptedException ex) {
					break;
				}
			}
		} else {
			int index = 0;
			while(!isInterrupted()) {
				if(index == textProgress.length) { index = 0; }
				System.out.print("\b" + textProgress[index]);
				index++;
				try {
					sleep(200);
				} catch(InterruptedException ex) {
					System.out.print("\b");
					break;
				}
			}
		}
	}

	protected void setPaused(boolean iconified) {
		this.iconified = iconified;
	}

}
