import javax.swing.*;
import java.awt.*;

public class ColorIcon implements Icon {

    private int width = 32;
    private int height = 32;
	private Color col;

	public ColorIcon(int w, int h, Color c) {
		System.out.println("ColorIcon");//Daniel Remove
		width = w;
		height = h;
		col = c;
	}

    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(col);
        g2d.fillRect(2 , 2, width-4 ,height-4);
        g2d.dispose();
    }

    public int getIconWidth() {
        return width;
    }

    public int getIconHeight() {
        return height;
    }
}

