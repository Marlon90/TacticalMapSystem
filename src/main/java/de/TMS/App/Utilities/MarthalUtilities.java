package de.TMS.App.Utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MarthalUtilities {

	public static String[] readFile(String path, String split) {
		File file = new File(path);
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			String maps = "";
			String line = "";
			while (line != null) {
				if (line != null || line == "") {
					maps += line.trim();
				}
				line = br.readLine();
			}
			br.close();
			return maps.split(split);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
