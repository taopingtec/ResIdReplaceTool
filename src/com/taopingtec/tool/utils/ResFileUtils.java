package com.taopingtec.tool.utils;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.taopingtec.tool.Constants;
import com.taopingtec.tool.ResInfo;

public class ResFileUtils {
	public static ArrayList<ResInfo> getResInfos(String strResFilePath) throws IOException {
		if (null == strResFilePath) {
			return null;
		}

		if (strResFilePath.endsWith(Constants.R_JAVA_SUFFIX)) {
			return getResInfosFromRJava(strResFilePath);
		} else if (strResFilePath.endsWith(Constants.PUBLIC_XML_SUFFIX)) {
			return getResInfosFromPublicXml(strResFilePath);
		}

		return null;
	}

	private static ArrayList<ResInfo> getResInfosFromPublicXml(String strResFilePath) throws IOException {
		ArrayList<ResInfo> resInfoList = new ArrayList<ResInfo>();

		InputStreamReader read = new InputStreamReader(new FileInputStream(new File(strResFilePath)));
		BufferedReader bufferedReader = new BufferedReader(read);
		String strLine = null;
		while ((strLine = bufferedReader.readLine()) != null) {
			ResInfo resInfo = getResInfoFromPublicXml(strLine);
			if (null != resInfo)
				resInfoList.add(resInfo);
		}
		read.close();

		return resInfoList;
	}

	private static ArrayList<ResInfo> getResInfosFromRJava(String strResFilePath) throws IOException {
		ArrayList<ResInfo> resInfoList = new ArrayList<ResInfo>();

		InputStreamReader read = new InputStreamReader(new FileInputStream(new File(strResFilePath)));
		BufferedReader bufferedReader = new BufferedReader(read);
		String lineTxt = null;
		String strResClass = null;
		while ((lineTxt = bufferedReader.readLine()) != null) {
			if (lineTxt.contains(Constants.CLASS_TAG_IN_R_JAVA)) {
				if (null != getValidClass(lineTxt)) {
					strResClass = "R." + getValidClass(lineTxt) + ".";
					// System.out.println("Process " + strResClass);
				} else {
					strResClass = null;
					// System.out
					// .println("\""
					// + lineTxt
					// + "\"   does not contains valid res class, SKIPPED!");
				}

				continue;
			}

			if (null == strResClass || !lineTxt.contains(Constants.RES_TAG_PREFIX_IN_R_JAVA))
				continue;

			ResInfo resInfo = getResInfoFromRJava(strResClass, lineTxt);
			if (null != resInfo)
				resInfoList.add(resInfo);
		}
		read.close();

		return resInfoList;
	}

	private static String getValidClass(String classInfo) {
		for (String str : Constants.VALID_CLASSES) {
			if (classInfo.contains(str))
				return str;
		}
		return null;
	}

	private static boolean isValidLineInPublicXml(String strLine) {
		if (null == strLine)
			return false;

		return strLine.contains(Constants.CLASS_TAG_IN_RUBLIC_XML)
				&& strLine.contains(Constants.NAME_TAG_IN_RUBLIC_XML)
				&& strLine.contains(Constants.ID_TAG_IN_RUBLIC_XML);
	}

	public static ResInfo getResInfoFromPublicXml(String strLine) {
		if (!isValidLineInPublicXml(strLine))
			return null;

		strLine = strLine.trim();
		String[] subStrs = strLine.split(Constants.SEG_TAG_IN_RUBLIC_XML);

		if (null == subStrs || 5 != subStrs.length)
			return null;

		String strType = getValidClass(subStrs[1]);
		if (null == strType)
			return null;

		String strName = subStrs[2].replace(Constants.NAME_TAG_IN_RUBLIC_XML, "");
		strName = strName.replace("\"", "");
		if (null == strName)
			return null;

		String strId = subStrs[3].replace(Constants.ID_TAG_IN_RUBLIC_XML, "");
		strId = strId.replace("\"", "");
		strId = getStrDecId(strId);
		if (null == strId)
			return null;

		ResInfo resInfo = new ResInfo();
		resInfo.strResName = "R." + strType + "." + strName;
		resInfo.strResId = strId;
		System.out.println("resName:" + resInfo.strResName);
		System.out.println("resId:" + resInfo.strResId);
		return resInfo;
	}

	private static String getStrDecId(String strId) {
		if (!strId.contains(Constants.HEX_PRE_FIX_UPPER) && !strId.contains(Constants.HEX_PRE_FIX_LOWER))
			return strId;

		strId = strId.replace("0x", "");
		strId = strId.replace("0X", "");
		long lId = Long.valueOf(strId, 16);

		return Long.toString(lId);
	}

	private static ResInfo getResInfoFromRJava(String resClass, String lineTxt) {
		String[] subStrs = lineTxt.split(Constants.RES_TAG_PREFIX_IN_R_JAVA);

		if (null == subStrs || 2 != subStrs.length)
			return null;

		String[] resStrs = subStrs[1].split(Constants.RES_TAG_SEG);
		if (null == resStrs || 2 != resStrs.length)
			return null;

		ResInfo resInfo = new ResInfo();
		resInfo.strResName = resClass + resStrs[0].trim();
		resInfo.strResId = resStrs[1].trim().replace(Constants.RES_TAG_END_IN_R_JAVA, "");
		// System.out.println("resName:" + resInfo.strResName);
		// System.out.println("resId:" + resInfo.strResId);

		return resInfo;
	}

	public static void replaceResIdByFile(ArrayList<ResInfo> resInfoList, File file) throws IOException {
		if (null == resInfoList || resInfoList.size() <= 0 || null == file || !file.exists()
				|| !file.isFile())
			return;

		InputStreamReader read = new InputStreamReader(new FileInputStream(file));
		BufferedReader bufferedReader = new BufferedReader(read);
		CharArrayWriter tempStream = new CharArrayWriter();
		String lineTxt = null;
		String newLineTxt = null;
		while ((lineTxt = bufferedReader.readLine()) != null) {
			newLineTxt = lineTxt;

			for (ResInfo resInfo : resInfoList) {
				if (null == resInfo || null == resInfo.strResId || null == resInfo.strResName)
					continue;

				newLineTxt = newLineTxt.replace(resInfo.strResId, resInfo.strResName);
			}

			tempStream.append(newLineTxt);
			tempStream.append(System.getProperty("line.separator"));
		}

		bufferedReader.close();
		FileWriter out = new FileWriter(file);
		tempStream.writeTo(out);
		out.close();
	}

}
