package NationMania.ui;

import javax.swing.*;

/**
 * Class for constructing GUI Buttons.<br>
 * For convinience only - extends Java Swings JButton by enabling to set the Button bounds in the Constructor.
 * 	 */

public class Button extends JButton {
	
	private static final long serialVersionUID = 1L;
	private String name;
	
	/**Constructor
	 * @param name unique name for the button for debugging purposes
	 * @param text written text on the button
	 * @param xPos button horizontal location on the screen
	 * @param yPos button vertical location on the screen
	 * @param width button weight
	 * @param height button hight
	 */
	public Button(String name, String text, int xPos, int yPos, int width,int height) {
		this.name = name;
		setBounds(xPos, yPos, width, height);
		setText(text);
	}
	
	public String getName() {
		return name;
	}
}
