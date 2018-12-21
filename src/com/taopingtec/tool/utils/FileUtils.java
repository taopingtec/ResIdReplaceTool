package com.taopingtec.tool.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class FileUtils {

	public static String readToString(String fileName) {
		String encoding = "UTF-8";
		File file = new File(fileName);
		Long filelength = file.length();
		byte[] filecontent = new byte[filelength.intValue()];
		try {
			FileInputStream in = new FileInputStream(file);
			in.read(filecontent);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			return new String(filecontent, encoding);
		} catch (UnsupportedEncodingException e) {
			System.err.println("The OS does not support " + encoding);
			e.printStackTrace();
			return null;
		}
	}

	public static ArrayList<File> getFileList(String strPath) {
		ArrayList<File> fileList = new ArrayList<File>();

		File inputFile = new File(strPath);
		if (!inputFile.exists())
			return null;

		if (inputFile.isFile()) {
			fileList.add(inputFile);
			return fileList;
		}

		File[] files = inputFile.listFiles();
		if (null == files || files.length <= 0)
			return null;

		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				ArrayList<File> subFileList = getFileList(files[i].getAbsolutePath());
				addSubList(fileList, subFileList);
				continue;
			}
			fileList.add(files[i]);
		}

		return fileList;
	}

	public static void addSubList(ArrayList<File> fileList, ArrayList<File> subFileList) {
		if (null == subFileList || subFileList.size() <= 0)
			return;

		for (int i = 0; i < subFileList.size(); ++i) {
			if (null != subFileList.get(i))
				fileList.add(subFileList.get(i));
		}
	}
}
