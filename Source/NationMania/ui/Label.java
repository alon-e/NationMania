package NationMania.ui;

import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * Class for constructing GUI Labels<br>
 * For convenience only - extends Java Swings JLabel by enabling to set more features in the Constructor.
 * */

public class Label extends JLabel {
	
	private static final long serialVersionUID = 1L;
	private String name;
	
	/**Constructor
	 * @param name unique name for the label for debugging purposes
	 * @param text written text in the label
	 * @param xPos label horizontal location on the screen
	 * @param yPos label vertical location on the screen
	 * @param width label weight
	 * @param height label hight
	 * @param horizontalAlignment align the label to the left/right/center
	 * @param iconResource in case this label is taken from an imported icon, this contains the path for the icon, or null else
	 * @param fontStyle the style of the label's font. optional 
	 * @param color the color of the label's font. optional 
	 */
	public Label(String name, String text, int xPos, int yPos, int width,int height, int horizontalAlignment,
			String iconResource,int fontStyle, Color color) {
		this.name = name;
		setBounds(xPos, yPos, width, height);
		setText(text);
		setHorizontalAlignment(horizontalAlignment);
		setIcon(iconResource);
		setFont(GameFonts.getFont(fontStyle));
		setColor(color);
	}	

	public void setColor(Color color) {
		if (color != null)
			setForeground(color);
	}
	
	public void setIcon(String resource) {
		if (resource != null)
			setIcon(new ImageIcon(Main.class.getResource(resource)));
	}
	
	public String getName() {
		return name;
	}
}
