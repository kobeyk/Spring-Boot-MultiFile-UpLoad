package com.appleyk.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.appleyk.file.FileUploadState;

@Component
public class FileWriteUtils {

	/**
	 * 异步写入文件数据
	 * 
	 * @param fileUploadState
	 * @throws Exception
	 */
	@Async
	public void writeData(FileUploadState fileUploadState) throws Exception {

		File file = new File(fileUploadState.LocalPathAndFileName());
		FileOutputStream fileOutputStream = null;
		fileOutputStream = new FileOutputStream(file, true);
		byte[] data = fileUploadState.getData();

		/**
		 * 分批写 一次性写入1024个字节
		 */
		InputStream is = new ByteArrayInputStream(data);
		byte[] buffer = new byte[1024];

		int len = 0;
		/**
		 * 当前写入的进度
		 */
		int currentCount = 0;
		/**
		 * 每次读将buffer填满，如果读完，返回-1
		 */
		while ((len = is.read(buffer)) != -1) {
			fileOutputStream.write(buffer, 0, len);
			currentCount += len;
			fileUploadState.setCurrentCount(currentCount);
		}

		is.close();
		fileOutputStream.close();	
		
		// 记录最后修改时间
		fileUploadState.setLastModifyDate(new Date());
		fileUploadState.setStatus("done");
	}
	
}
