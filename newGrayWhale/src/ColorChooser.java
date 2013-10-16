import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.colorchooser.*;


public class ColorChooser extends JPanel implements ChangeListener, ActionListener {

    private static JFrame frame = null;
	
	private Color c = Color.red;
	private JColorChooser tcc;
	private OptionWindow ow;
	private String text;

    public ColorChooser(Color initialColor, String _text, OptionWindow _ow) {
    	System.out.println("ColorChooser");//Daniel Remove
        setLayout(null);
		ow = _ow;
		c = initialColor;
		text = _text;
        tcc = new JColorChooser(c);
        tcc.getSelectionModel().addChangeListener(this);
        tcc.setBorder(BorderFactory.createTitledBorder(text));
		tcc.setBounds(10, 10, 450, 270);

		JButton Ok = new JButton("Ok");
		Ok.setBounds(185, 290, 80, 20);
        Ok.addActionListener(this);

        add(tcc);
		add(Ok);

		createAndShowGUI(470, 340);
    }

    public void actionPerformed(ActionEvent e) {
		if(text.contains("ellipse"))
			ow.setEllipseColor(c);
		if(text.contains("center"))
			ow.setCenterColor(c);
		if(text.contains("control"))
			ow.setControlColor(c);
		if(text.contains("reference"))
			ow.setReferenceColor(c);
		frame.dispose();
    }
    public void stateChanged(ChangeEvent e) {
        c = tcc.getColor();
    }
	public Color getColor() {
		return c;
	}
    private void createAndShowGUI(int w, int h) {
        if(frame != null)
            frame.dispose();
        frame = new JFrame("I3S Gray Whale: Choose color");
        frame.setContentPane(this);
        frame.addWindowListener(new WindowAdapter() { public void windowClosing(WindowEvent e) { frame.dispose(); frame = null; } });
        frame.setLocation(200, 150);
        frame.setSize(w, h);
        frame.setResizable(false);

        ImageIcon imageIcon = new ImageIcon(this.getClass().getResource("images/i3scicon.gif"));
        frame.setIconImage(imageIcon.getImage());
        frame.setVisible(true);		
    }
}
