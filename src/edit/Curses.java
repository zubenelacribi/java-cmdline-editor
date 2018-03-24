/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edit;

/**
 * A simple helper class that is used to locate content on the Terminal window.
 * It uses cursor movements and character color & font settings.
 */
public class Curses {

	/**
	 * The escape character.
	 */
	public static final char ESC = 27;

	/**
	 * The beginning of an escape sequence.
	 */
	private static String esc = "" + ESC + '[';
	
	/**
	 * Short for System.out.print(char).
	 */
	public static void p(char ch) {
		System.out.print(ch);
	}
	
	/**
	 * Short for System.out.print(char).
	 */
	public static void p(int n) {
		System.out.print(n);
	}
	
	/**
	 * Short for System.out.print(String).
	 */
	public static void p(String s) {
		System.out.print(s);
	}
	
	/**
	 * Short for System.out.println(char).
	 */
	public static void pl(char ch) {
		System.out.println(ch);
	}
	
	/**
	 * Short for System.out.println(char).
	 */
	public static void pl(int n) {
		System.out.println(n);
	}
	
	/**
	 * Short for System.out.println(String).
	 */
	public static void pl(String s) {
		System.out.println(s);
	}
	
	/**
	 * Moves the cursor one place to the left.
	 */
	public static void left() {
		p(esc);
		p('D');
	}
	
	/**
	 * Moves the cursor N places to the left.
	 */
	public static void left(int n) {
		for (int i = 0; i < n; i++) {
			left();
		}
	}
	
	/**
	 * Moves the cursor one place to the right.
	 */
	public static void right() {
		p(esc);
		p('C');
	}

	/**
	 * Moves the cursor N places to the right.
	 */
	public static void right(int n) {
		for (int i = 0; i < n; i++) {
			right();
		}
	}
	
	/**
	 * Moves the cursor one place upwards.
	 */
	public static void up() {
		p(esc);
		p('A');
	}

	/**
	 * Moves the cursor N places upwards.
	 */
	public static void up(int n) {
		for (int i = 0; i < n; i++) {
			up();
		}
	}
	
	/**
	 * Moves the cursor one place downwards.
	 */
	public static void down() {
		p(esc);
		p('B');
	}

	/**
	 * Moves the cursor N places downwards.
	 */
	public static void down(int n) {
		for (int i = 0; i < n; i++) {
			down();
		}
	}
	
	public static void resetColor() {
		p(esc);
		p("0m");
	}

	/**
	 * Changes the character face color.
	 * @param bgr color code (blue-green-red).
	 */
	public static void color(int bgr) {
		color(bgr, 0);
	}
	
	/**
	 * Changes the character face color.
	 * @param bgr color code (blue-green-red).
	 * @param attr character attribute, e.g. BRIGHT, ITALIC, etc. (look at the table of attributes below).
	 */
	public static void color(int bgr, int attr) {
		assert bgr >= 0 && bgr < 7 : "bgr should be a bit mask";
		p(esc);
		p(30 + bgr);
		if (attr > 0) {
			p(";1");
		}
		p('m');
	}

	/*
	 * Character attributes. Use these constants as third arg of color(foregroundBgr, backgroundBgr, attr).
	 */
	public static final int BRIGHT = 1;
	public static final int ITALIC = 3;
	public static final int UNDERLINE = 4;
	public static final int BRIGHT_BG = 5;
	public static final int STRIKEOUT = 9;
	public static final int DOUBLE_UNDERLINE = 21;

	/*
	 * Color codes. Use these constants as first & second arg of color(foregroundBgr, backgroundBgr, attr).
	 */
	public static final int BLACK = 0;
	public static final int RED = 1;
	public static final int GREEN = 2;
	public static final int YELLOW = 3;
	public static final int BLUE = 4;
	public static final int MAGENTA = 5;
	public static final int CYAN = 6;
	public static final int WHITE = 7;

	/**
	 * Changes the character face color.
	 * @param foregroundBgr character face color code (blue-green-red).
	 * @param backgroundBgr character background color code (blue-green-red).
	 * @param attr character attribute, e.g. BRIGHT, ITALIC, etc. (look at the table of attributes above).
	 */
	public static void color(int foregroundBgr, int backgroundBgr, int attr) {
		assert foregroundBgr >= 0 && foregroundBgr < 7 : "bgr should be a bit mask";
		assert backgroundBgr >= 0 && backgroundBgr < 7 : "bgr should be a bit mask";
		p(esc);
		p(30 + foregroundBgr);
		p(';');
		p(40 + backgroundBgr);
		if (attr > 0) {
			p(';');
			p(attr);
		}
		p('m');
	}

	/**
	 * Usage: width(terminalWindowSize()) or height(terminalWindowSize()).
	 * @return the terminal window size encoded as (height << 16) | width.
	 */
	public static int terminalWindowSize() {
		try {
			p(esc);
			p("18t");
			int ch = System.in.read();
			assert ch == 27;
			ch = System.in.read();
			assert ch == '[';
			ch = System.in.read();
			assert ch == '8';
			ch = System.in.read();
			assert ch == ';';
			char[] buff = new char[4];
			int pos = 0;
			while (true) {
				ch = System.in.read();
				if (ch == ';') {
					break;
				}
				buff[pos++] = (char) ch;
			}
			int height = Integer.parseInt(new String(buff, 0, pos));
			pos = 0;
			while (true) {
				ch = System.in.read();
				if (ch == 't') {
					break;
				}
				buff[pos++] = (char) ch;
			}
			int width = Integer.parseInt(new String(buff, 0, pos));
			return (height << 16) | width;
		}
		catch (Exception ex) {
			// We need to catch IOException because of System.in.read() and the Java compiler insists
			// but this never happens. We also need to catch NumberFormactException because of Integer.parseInt(),
			// but, again, it never happens because the Terminal never gives non-parseable numbers.
			// So syntactically we should catch these exceptions and choose what to do with them.
			// We'll just rethrow them as unchecked exceptions, knowing that no exception will ever be thrown from here.
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Retrieves the width of the terminal window obtained from terminalWindowSize().
	 * @param scrSize something returned by terminalWindowSize().
	 * @return the terminal width in number of characters.
	 */
	public static int width(int scrSize) {
		return scrSize & 0xffff;
	}
	
	/**
	 * Retrieves the height of the terminal window obtained from terminalWindowSize().
	 * @param scrSize something returned by terminalWindowSize().
	 * @return the terminal height in number of characters.
	 */
	public static int height(int scrSize) {
		return (scrSize >> 16) & 0xffff;
	}

	/**
	 * Alternate srceen.
	 */
	public static void altScreen() {
		p(esc);
		p("?1049h");
	}

	/**
	 * Restore screen's content.
	 */
	public static void restoreScreen() {
		p(esc);
		p("?1049l");
	}
	
}
