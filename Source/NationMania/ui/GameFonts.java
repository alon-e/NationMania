package NationMania.ui;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;

import NationMania.core.Config;

/**
 * Class containing all the types and styles of messages or labels displayed to the user  
 *
 */
public final class GameFonts {
	
	//each number is associated with a style, except 0 that is mapped to null
	public static final int NULL = 0;
	public static final int VALID = 1;
	public static final int INVALID = 2;
	public static final int SCORE = 3;
	public static final int REPORT = 4;
	public static final int TITLE = 5;
	public static final int INSTRUCTIONS =  6;
	public static final int DIFFICULTY = 7;
	public static final int INFO = 8;
	public static final int ERROR = 9;
	public static final int FACT = 10;
	public static final int SMALL_FACT = 11;


	/**
	 * Configuration Constant - path to the file "impact.ttf", which is needed in order to import font with "impact" style<br>
	 * in Linux Systems.
	 */
	public static String IMPACT_PATH = Config.GET_VAR_STR("IMPACT_PATH").equals("NA") ? "lib"+File.separator+"impact.ttf" : Config.GET_VAR_STR("IMPACT_PATH");
	
	/**
	 * Map that connect a number with a certain pre-determined font style
	 */
	public static HashMap<Integer,Font> fontsMap = new HashMap<Integer,Font>();
	
	/**
	 * Constructor.<br>
	 * Fills the fontsMap with all the application veriaty of font styles
	 */
	public GameFonts() {

		//set null font for labels that don't present a string to the user
		fontsMap.put(NULL,null);
		fontsMap.put(VALID,createImpactFont().deriveFont(30F));
		fontsMap.put(INVALID,createImpactFont().deriveFont(24F));
		fontsMap.put(SCORE,createImpactFont().deriveFont(Font.ITALIC,20));
		fontsMap.put(REPORT,createImpactFont().deriveFont(17F));
		fontsMap.put(TITLE,createImpactFont().deriveFont(Font.BOLD,30));
		fontsMap.put(INSTRUCTIONS,createImpactFont().deriveFont(Font.PLAIN,19));
		fontsMap.put(DIFFICULTY,createImpactFont().deriveFont(Font.PLAIN,20));
		
		fontsMap.put(INFO,new Font("ariel",Font.PLAIN,16));
		fontsMap.put(ERROR,new Font("Tahoma",Font.BOLD,22));
		fontsMap.put(FACT,new Font("ariel",Font.PLAIN,12));
		fontsMap.put(SMALL_FACT,new Font("ariel",Font.PLAIN,10));

	}
	
	/**Special function for creating fonts with style "impact", since it's used alot in various sizes in the application.<br>
	 * The function reads from the file located in the IMPACT_PATH and retreave a new font<br>
	 * @return font of style impact
	 */
	private Font createImpactFont() {
		Font font = null;
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(IMPACT_PATH));
		} catch (Exception e) {
			System.out.println("ERROR Retreaving Font: " + e.getMessage());
		}
		return font;
	}
	
	/**Called by all GUI components that requires a certain font style.<br>
	 * @param font number of font, associated with a certain font style<br>
	 * @return the font style associated with the font number parameter<br>
	 */
	public static Font getFont(int font) {
		if (fontsMap.containsKey(font))
			return fontsMap.get(font);
		return null;
	}
}
