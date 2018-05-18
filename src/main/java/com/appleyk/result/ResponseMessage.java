package com.appleyk.result;


/**
 * @blob   http://blog.csdn.net/appleyk
 * @date   2018年5月18日-下午3:00:10
 */
public enum ResponseMessage {

	/**
	 * 成功
	 */
	OK(200,"成功"),
	
	/**
	 * 错误的请求
	 */
	BAD_REQUEST(400,"错误的请求"),
	

	/**
	 * 文件资源找不到
	 */
	FILE_NOT_FOUND(404, "文件未找到！");
	
	private final int status;
	
	private final String message;
	
	ResponseMessage(int status, String message){
		this.status = status;
		this.message = message;
	}
	
	public int getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}
	
}
