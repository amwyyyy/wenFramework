package com.wen.framework.common;

import java.util.HashMap;

/**
 * 返回前端的Map包装类对象
 * @author huangwg
 *
 */
public class ResultObject extends HashMap<String, Object> {
	private static final long serialVersionUID = 385654030713612995L;

	private static final String PARAM_ERRCODE = "errCode";
	private static final String PARAM_MSG = "msg";
	private static final String PARAM_DATA = "data";
	
	public static final String ERRCODE_OK = "0";
	public static final String ERRCODE_EXCEPTION = "9999";
	
	public ResultObject(){
		setErrCode(ERRCODE_OK);
	}

	public String getErrCode() {
		return (String) this.get(PARAM_ERRCODE);
	}

	public void setErrCode(String errCode) {
		this.put(PARAM_ERRCODE, errCode);
	}

	public String getMsg() {
		return (String) this.get(PARAM_MSG);
	}

	public void setMsg(String msg) {
		this.put(PARAM_MSG, msg);
	}

	public Object getData() {
		return this.get(PARAM_DATA);
	}

	public void setData(Object data) {
		this.put(PARAM_DATA, data);
	}
	
	public boolean isOk(){
		return ERRCODE_OK.equals(this.getErrCode());
	}
}
