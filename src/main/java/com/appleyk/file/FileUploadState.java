package com.appleyk.file;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import com.appleyk.utils.FileUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 文件上传状态类  == 便于前后端交互
 * @blob   http://blog.csdn.net/appleyk
 * @date   2018年5月18日-下午2:48:47
 */
public class FileUploadState {

	private String fileUUID;
	
	private String fileName;
	
	private String path;
	
	/**
	 * 文件的绝对url
	 */
	private String fileUrl;
	
	private String ext;
	
	@JsonIgnore
	private byte[] data;
		
	/**
	 * 文件的总字节数
	 */
	private Integer  totalCount;
	
	/**
	 * 当前文件上传的字节数  和 totalCount组成 进度条
	 */
	private Integer  currentCount;
	
	/**
	 * 文件上传的状态值
	 * 1.stop  == 初始状态
	 * 2.start == 进行状态
	 * 3.done  == 完成状态
	 */
	private String   status;
	
	/**
	 * 创建时间
	 */
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date createDate;
	
	/**
	 * 最后修改时间
	 */
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date lastModifyDate;
	
	/**
	 * 文件的大小【容量】
	 */
    private String size;
	
	public static FileUploadState createFileUploadState(String fileName,String ext,String fileDir,byte[] data) {
		
		FileUploadState result = new FileUploadState();		
		result.fileName = getFileName(fileName);
		result.fileUUID = java.util.UUID.randomUUID().toString();
		result.createDate = new Date();
		result.lastModifyDate = new Date();
		result.path = getDirPath(fileName, fileDir);	
		result.ext = ext;
		result.data = data;
		result.status = "stop";
		result.size = FileUtils.getFileSize(data.length);	
		result.totalCount = data.length;
		result.currentCount = 0;
		return result;
	}
	
	public FileUploadState() {	
		this.lastModifyDate = new Date();
	}
	
	/**
	 *  1. 获取文件名称的hashCode：int hCode = name.hashCode();； 
		2. 获取hCode的低4位，然后转换成16进制字符； 
		3. 获取hCode的5~8位，然后转换成16进制字符； 
		4. 使用这两个16进制的字符生成目录链。例如低4位字符为“5”
		采用hash算法来打散目录，防止一个目录上传的文件过多！
	 * @return
	 */
	public static String getDirPath(String fileName,String fileDir){	
	
		// 1.获取文件名的hashCode
		int hCode = fileName.hashCode();
		// 2.获取hCode的低4位，并转换成16进制字符串
		String dir1 = Integer.toHexString(hCode & 0xF);
		// 3.获取hCode的低5~8位，并转换成16进制字符串
		String dir2 = Integer.toHexString(hCode >>> 4 & 0xF);
		// 4.与文件保存目录连接成完整路径
		String path = fileDir + dir1 + "/" + dir2+"/";
		// 5.防止目录不存在，创建
		new File(path).mkdirs();
		return path;
	}
	
	public String getFileUUID() {
		return fileUUID;
	}

	public void setFileUUID(String fileUUID) {
		this.fileUUID = fileUUID;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFileUrl() {	
		fileUrl = LocalPathAndFileName();
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public Integer getCurrentCount() {
		return currentCount;
	}

	public void setCurrentCount(Integer currentCount) {
		this.currentCount = currentCount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getLastModifyDate() {
		return lastModifyDate;
	}

	public void setLastModifyDate(Date lastModifyDate) {
		this.lastModifyDate = lastModifyDate;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	
    public String UUIDFileName() {
        return fileUUID+ '_' +fileName;
    }
    
    /**
     * 不要后缀
     * @param fileName
     * @return
     */
    public static String getFileName(String fileName){
    	return fileName.substring(0, fileName.indexOf("."));
    }

    /**
     * 为防止同名文件上传出现覆盖的问题，上传的文件名采用 UUID+_FileName
     * @return
     */
    public String LocalPathAndFileName() {
        return path + fileUUID+ '_' +fileName+"."+ext;
    }

    // 获取解压缩文件路径
    public String LocalUnCompressPath() {
        File file = new File(path + UUIDFileName());
        if (!file.exists()) {
            file.mkdir();
        }
        return file.getPath();
    }

    /**
     * 创建文件  == 先占个位置
     *
     * @return
     */
    public boolean createFile() {
    	
        File file = new File(LocalPathAndFileName());      
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (IOException e) {
            return false;
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {

                }
            }
        }
        return true;
    }

    /**
     * 删除文件
     *
     * @return
     */
    public boolean deleteFile() {
        File file = new File(LocalPathAndFileName());
        return file.delete();
    }

    /**
     * 文件是否过期
     *
     * @return
     */
    public boolean isExpire() {
    	
        Date currentDate = new Date();
        long diff = currentDate.getTime() - lastModifyDate.getTime();
        long seconds = diff / 1000;
        //过期秒
        return seconds > 60;
    }

   
}
