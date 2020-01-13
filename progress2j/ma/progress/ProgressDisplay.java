package ma.progress;

import java.awt.*;
import javax.swing.JComponent;

public class ProgressDisplay extends JComponent {

	private int caw;
	private int cah;
	private int pbh;
	private int pbw;
	private int pbx;
	private int pby;

	private float stat;

	ProgressDisplay() {
		super();
		caw  = 0;
		cah  = 0;
		stat = 1.0f;
	}

	public void paintComponent(Graphics g) {
		if(!(caw == getWidth() && cah == getHeight())) {
			caw = getWidth();
			cah = getHeight();
			pbh = cah / 10;
			pbw = 8 * (caw / 10);
			pbx = (caw - pbw) / 2;
			pby = (cah - pbh) / 2;
		}
		g.setColor(Color.BLACK);	
		g.fillRect(0, 0, caw, cah);
		g.setColor(Color.YELLOW);
		g.fillRect(pbx, pby, (int)(pbw * stat), pbh);
	}

	void setStat(float stat) {
		this.stat = stat;
		repaint();
	}

	float getStat() { return stat; }

}
