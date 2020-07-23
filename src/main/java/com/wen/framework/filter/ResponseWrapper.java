package com.wen.framework.filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class ResponseWrapper extends HttpServletResponseWrapper {
    private PrintWriter writer;
    private ByteArrayOutputStream bos;
    
    public ResponseWrapper(HttpServletResponse response) {
        super(response);
        
        this.bos = new ByteArrayOutputStream();
    }

    @Override
    public PrintWriter getWriter() throws UnsupportedEncodingException, IOException {
    	if (writer == null) {
            writer = new PrintWriter(new OutputStreamWriter(getOutputStream(), "UTF-8"), true);
        }
        return writer;
    }
    
    @Override
	public ServletOutputStream getOutputStream() throws IOException {
		return new FilterServletOutputStream(bos);
	}

    // 获取数据
    public byte[] getData() {
        if (writer != null)
            writer.flush();
        return bos.toByteArray();
    }
}
