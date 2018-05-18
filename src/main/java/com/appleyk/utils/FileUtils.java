package com.appleyk.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.appleyk.exception.BaseException;
import com.appleyk.result.ResponseMessage;

public class FileUtils {

	/**
	 * 获取存放位置路径
	 * 
	 * @param tempFilePath
	 * @return
	 */
	public static String getFilePath(String tempFilePath) {
		File file = new File(tempFilePath);
		if (!file.exists()) {
			file.mkdir();
		}

		return tempFilePath;
	}

	/**
	 * 根据文件路径获取文件名
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFileName(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			return file.getName();
		}

		return "";
	}



	/**
	 * 读取文件
	 * 
	 * @param multipartFile
	 * @return
	 * @throws Exception
	 */
	public static byte[] readUploadFileDatas(MultipartFile multipartFile) throws IOException {

		// 获取文件流
		InputStream fis = multipartFile.getInputStream();
		// 使用available()方法获取流中可读取的数据大小
		byte[] temp = new byte[fis.available()];
		fis.read(temp);

		return temp;
	}

	/**
	 * 从文件里面读内容
	 * @param file
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public static byte[] readFileDatas(File file) throws IOException {

		// 获取文件流
		FileInputStream fis = new FileInputStream(file);
		// 使用available()方法获取流中可读取的数据大小
		byte[] temp = new byte[fis.available()];
		fis.read(temp);
		return temp;
	}
	
	/**
	 * 下载文件
	 * 
	 * @param absolutePath
	 *            文件绝对路径
	 * @param response
	 * @throws Exception
	 */
	public static boolean download(String absolutePath, String originalName, HttpServletResponse response)
			throws Exception {
		boolean result = false;

		if (absolutePath != null && !("").equals(absolutePath)) {
			File file = null;
			try {
				file = new File(absolutePath);
			} catch (Exception e) {
				throw new BaseException(ResponseMessage.FILE_NOT_FOUND);
			}
			if (file.isFile() && file.exists()) {
				String fileName = absolutePath.substring(absolutePath.lastIndexOf("/") + 1);

				response.setContentType(new MimetypesFileTypeMap().getContentType(new File(fileName)));

				if (originalName != null && !("").equals(originalName)) {
					String extension = absolutePath.substring(absolutePath.lastIndexOf("."));
					fileName = originalName + extension;
				}
				response.setHeader("Content-Disposition",
						"attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
				if (write(new FileInputStream(file), response.getOutputStream())) {
					result = true;
				} else {
					result = false;
				}
			} else {
				// result = false;
				throw new RuntimeException("文件不存在,下载失败！");
			}
		}

		return result;
	}

	/**
	 * 写入文件
	 * 
	 * @param in
	 * @param out
	 * @throws IOException
	 * @throws Exception
	 */
	private static boolean write(InputStream in, OutputStream out) throws IOException {

		boolean result = false;

		try {
			byte[] buffer = new byte[1024];
			int len = -1;
			while ((len = in.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			out.flush();
			result = true;
		} catch (IOException e) {
			result = false;
			e.printStackTrace();
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}

		return result;
	}

	/**
	 * 删除文件
	 * 
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public static boolean deleteFile(String filePath) throws Exception {
		File file = new File(filePath);
		if (file.exists()) {
			return file.delete();
		}
		return true;
	}

	/**
	 * 递归获取指定路径下所有文件（文件夹信息不返回）
	 * 
	 * @param path
	 * @param fileList
	 * @return
	 * @throws Exception
	 */
	public static List<File> getFiles(String path, List<File> fileList) throws Exception {
		if (fileList == null) {
			fileList = new ArrayList<File>();
		}
		// 目标集合fileList
		File file = new File(path);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File fileIndex : files) {
				// 如果这个文件是目录，则进行递归搜索
				if (fileIndex.isDirectory()) {
					getFiles(fileIndex.getPath(), fileList);
				} else {
					fileList.add(fileIndex);
				}
			}
		}

		return fileList;
	}

	/**
	 * 根据文件名获取文件后缀名 == 小写
	 * 
	 * @param file
	 * @return
	 */
	public static String getSuffixName(String fileName) {
		return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
	}

	/**
	 * 根据文件的length转化实际大小
	 * @param filelength
	 * @return
	 */
	public static String getFileSize(long filelength) {
		
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (filelength < 1024) {
			fileSizeString = df.format((double) filelength) + "B";
		} else if (filelength < 1048576) {
			fileSizeString = df.format((double) filelength / 1024) + "K";
		} else if (filelength < 1073741824) {
			fileSizeString = df.format((double) filelength / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) filelength / 1073741824) + "G";
		}
		return fileSizeString;
	}
}
