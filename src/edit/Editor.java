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

import static edit.Curses.*;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * The main class of the cmdline text editor.
 */
public class Editor {

	/**
	 * Supported file format (ASCII, Unicode16 small endian, Unicode16 big endian, UTF-8).
	 */
	enum FileFormat {
		ascii, smallEndian, bigEndian, utf8
	}

	/**
	 * A struct that contains the coordinates and other attributes
	 * of a file opened in the editor.
	 */
	private class OpenFile {
	
		/**
		 * The file object.
		 */
		public File f;
	
		/**
		 * Cursor coordinates within the file, they start from (1, 1).
		 */
		public int line, column;
		
		/**
		 * Up-left corner coordinates with the file, they start form (1, 1).
		 */
		public int minVisibleLine, minVisibleColumn;
		
		/**
		 * Window's coords and size (coords start from (1, 1), width & height are at least 1).
		 */
		public int x, y, width, height;
		
		/**
		 * File's content.
		 */
		public List<String> content = new ArrayList<>();
		
		/**
		 * Shows the error message if the file couldn't be opened for some reason.
		 * If no error has occurred, this field is NULL.
		 */
		public String errorOpeningFile;
		
		/**
		 * File's own text format (should be preserved upon saving).
		 */
		public TextFormat format;
		
		/**
		 * Creates an OpenFile object.
		 */
		public OpenFile(String filename) {
			this.f = new File(filename);
			if (f.exists()) {
				readContent();
			}
		}
		
		/**
		 * Reads file's content.
		 */
		private void readContent() {
			// We should recognize the format here.
			// Here's some default content reading.
			try {
				BufferedReader inp = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
				while (true) {
					String line = inp.readLine();
					if (line == null) {
						break;
					}
					content.add(line);
				}
				inp.close();
			}
			catch (IOException ex) {
				this.errorOpeningFile = ex.getMessage();
			}
		}
	
	}

	/**
	 * The list of currently open files.
	 */
	private List<OpenFile> files = new ArrayList<>();
	
	/**
	 * Current screen width & height.
	 */
	private int scrWidth, scrHeight;
	
	/**
	 * Current file #.
	 */
	private int currentFile;

	/**
	 * Creates an editor and initializes it with the file names given
	 * through the command line.
	 */
	public Editor(String... filenames) {
		for (String s : filenames) {
			this.files.add(new OpenFile(s));
		}
		if (this.files.size() == 0) {
			newFile();
		}
	}
	
	/**
	 * Starts the editor.
	 */
	public void run() throws Exception {
		emptyKybdBuffer();
		int scrSize = terminalWindowSize();
		up(height(scrSize));
		p(width(scrSize) + "x"+ height(scrSize) + "\n\r");
		color(1, 7, BRIGHT);
		right(5);
		p('A');
		left(5);
		color(7, 0, DOUBLE_UNDERLINE);
		p('B');
		resetColor();
		int n = System.in.read();
		pl(n);
		down();
	}
	
	/**
	 * Draws
	 */
	private void drawFrame() {
	}

	/**
	 * Creates a new file and adds it to the editor's list.
	 */
	private void newFile() {
		files.add(new OpenFile(uniqueName()));
	}
	
	/**
	 * Chooses a name for a newly created file (should denote a non-exdisting file).
	 */
	private String uniqueName() {
		char[] num = new char[3];
		int maxFile = 10;
		for (int i = 0; i < num.length; i++) {
			maxFile *= 10;
		}
		for (int i = 0; i < maxFile; i++) {
			int k = i;
			for (int j = num.length - 1; j >= 0; j--) {
				num[j] = (char) ('0' + (k % 10));
				k /= 10;
			}
			String proposedFileName = "noname" + new String(num) + ".txt";
			if (!new File(proposedFileName).exists()) { // Checks in the current directory.
				return proposedFileName;
			}
		}
		throw new RuntimeException("Too many new files");
	}

	/**
	 * Empties the keyboard buffer if there are characters available.
	 */	
	private void emptyKybdBuffer() {
		while (System.in.available() > 0) {
			System.in.read();
		}
	}

	/**
	 * The main() method.
	 */	
	public static void main(String[] args) throws Exception {
		Editor edit = new Editor(args);
		edit.run();
	}

}
