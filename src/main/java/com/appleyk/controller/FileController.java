package com.appleyk.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.appleyk.file.FileUploadState;
import com.appleyk.result.ResponseMessage;
import com.appleyk.result.ResponseResult;
import com.appleyk.utils.FileUtils;
import com.appleyk.utils.FileWriteUtils;

@RestController
@RequestMapping("/appleyk/file")
public class FileController {

	@Value("${file.imageDir}")
	private String imageDir;

	@Value("${file.fileDir}")
	private String fileDir;

	@Value("${file.zipFileDir}")
	private String zipDir;
	
	@Autowired
	private CacheManager cacheFile;
	
	@Autowired
	private FileWriteUtils fileWriteUtils; 

	/**
	 * 单文件的创建
	 * @param multiReq
	 * @return
	 */
	@RequestMapping("/createfile")
	public ResponseResult createFile(MultipartHttpServletRequest multiReq){

		MultipartFile multipartFile = multiReq.getFile("file");
		FileUploadState fileState = createFileStateCache(multipartFile);		
		if(multipartFile.getOriginalFilename().equals("")){
			return new ResponseResult(ResponseMessage.OK,new FileUploadState());
		}		
		return new ResponseResult(ResponseMessage.OK,fileState);
	}
	
	/**
	 * 文件上传具体实现方法;
	 *
	 * @param file
	 * @return
	 */
	@RequestMapping("/upload")
	public ResponseResult upload(String fileUUID) throws Exception {

		Cache<String, FileUploadState> fileStateCache = cacheFile.getCache("fileState", String.class,
				FileUploadState.class);
		FileUploadState fileUploadState = fileStateCache.get(fileUUID);
		
		if(fileUploadState==null){
			return new ResponseResult(ResponseMessage.OK,"文件不存在或者文件已经完成上传,请勿重复上传！");
		}
		
		/**
		 * 保证文件数据的写入只异步执行一次，而不是多次请求都触发writeData
		 */
		if(fileUploadState.getStatus().equals("stop")){
			
			fileUploadState.setStatus("start");
			
			/**
			 * 否则开始异步上传
			 */
			fileWriteUtils.writeData(fileUploadState);
			return new ResponseResult(ResponseMessage.OK,fileUploadState); 
		
		}else if(fileUploadState.getStatus().equals("start")){
			return new ResponseResult(ResponseMessage.OK,fileUploadState);
		}else{
			
			/*
			 * 文件上传完成后，重命名清除缓存
			 */
			
			fileStateCache.remove(fileUUID);
			return new ResponseResult(ResponseMessage.OK,fileUploadState); 
		}	
		
	}
	
	/**
	 *多文件的创建
	 * @param multiReq
	 * @return
	 */
	@RequestMapping("/createmultifile")
	public ResponseResult createMultiFile(HttpServletRequest request){
		
		List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
	    MultipartFile multipartFile = null;	   
	    List<FileUploadState> fileUploadStates = new ArrayList<>();
	    for (int i = 0; i < files.size(); ++i) {
	    	multipartFile = files.get(i);
	    	if(!multipartFile.getOriginalFilename().equals("")){
	    		fileUploadStates.add(createFileStateCache(multipartFile));
	    	}   	
	    }
		    
	    if(fileUploadStates.size()>0){
	    	return new ResponseResult(ResponseMessage.OK,fileUploadStates);
	    }
	    
	    return new ResponseResult(ResponseMessage.OK);
	}
	
	
	
	/**
	 * 创建文件【先占位，看是否符合文件上传格式要求】+并加入缓存，前端根据返回的fileUUID进行异步请求 == 文件数据写入
	 * @param multipartFile
	 * @return
	 */
	public FileUploadState createFileStateCache(MultipartFile multipartFile){
		
		String fileName = multipartFile.getOriginalFilename();				
		String ext  = FileUtils.getSuffixName(fileName);
		String pathDir = "";
		if(ext.equals("jpg") || ext.equals("png") || ext.equals("gif")){
			pathDir = imageDir;
		}else if(ext.equals("rar") || ext.equals("zip") || ext.equals("gz")){
			pathDir = zipDir;
		}else if(ext.equals("osm") || ext.equals("sto") || ext.equals("pbf")){
			pathDir = fileDir;
		}
					
		byte[] data;
		
		try {
			data = FileUtils.readUploadFileDatas(multipartFile);
		} catch (IOException e) {
			return null;
		}
		Cache<String, FileUploadState> fileStateCache = cacheFile.getCache("fileState", String.class,
				FileUploadState.class);
		FileUploadState fileState = FileUploadState.createFileUploadState(fileName, ext, pathDir, data);	
		fileStateCache.put(fileState.getFileUUID(), fileState);	
		
		/**
		 * 开始创建文件路径
		 */
		
		 if(fileState.createFile()){
			 System.out.println("创建文件成功");
			 return fileState;
		 }
		 
		 return null;
	}
	
	

}
