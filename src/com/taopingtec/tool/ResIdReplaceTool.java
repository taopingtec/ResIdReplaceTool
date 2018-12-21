package com.taopingtec.tool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.taopingtec.tool.utils.FileUtils;
import com.taopingtec.tool.utils.ResFileUtils;

public class ResIdReplaceTool {

	public static void main(String[] args) throws IOException {
		if (null == args || 2 != args.length) {
			System.out
					.println("Please use like this: java -jar ResIdReplaceTool.jar resFile ./replaceDir");
			System.out
					.println("ResFile can be R.java or public.xml. This tool judge the file type by suffix");
			return;
		}

		replaceResId(args[0], args[1]);

		System.out.println("Finished");

	}

	public static void replaceResId(String strResFilePath, String strReplaceDir) throws IOException {
		ArrayList<ResInfo> resInfoList = ResFileUtils.getResInfos(strResFilePath);

		if (null == resInfoList || resInfoList.size() <= 0) {
			System.out.println("resInfoList is Empty, Please Check the file:" + strResFilePath);
		}

		// 获取文件列表
		ArrayList<File> fileList = FileUtils.getFileList(strReplaceDir);
		System.out.println("File Count: " + fileList.size());
		for (File file : fileList) {
			System.out.println(file.getPath());
			ResFileUtils.replaceResIdByFile(resInfoList, file);
		}
	}

}
