package com.xiaogang.framework.basic.processors.codec;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.util.*;

/**
 * @Author: zhuganggang
 * @Date: 2018/10/11 20:01
 * @Description:
 */
public class Base64HttpServletRequestWrapper extends HttpServletRequestWrapper {

    private Map<String, String[]> parameters = new HashMap<String, String[]>();
    private byte[] bytes;
    private boolean firstTime = true;
    private ObjectMapper objectMapper = new ObjectMapper();
    private String encoding = "UTF-8";

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public Base64HttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        try {
            parseParameters();
        } catch (IOException e) {
            throw new RuntimeException("IOException", e);
        }
    }

    protected void parseParameters() throws IOException {
        String method = this.getHttpServletRequest().getMethod();
        StringBuilder jsonstr = new StringBuilder();
        if (method.equalsIgnoreCase("GET") || method.equalsIgnoreCase("DELETE")) {
            jsonstr.append(this.getQueryString());
        } else {
            String line;
            BufferedReader reader = getReader();
            while ((line = reader.readLine()) != null) {
                jsonstr.append(line);
            }
        }
        if (jsonstr.length() > 0) {
            JsonNode node = objectMapper.readTree(jsonstr.toString());
            Iterator<String> fieldNames = node.getFieldNames();
            for (; fieldNames.hasNext(); ) {
                String key = fieldNames.next();
                String value = node.get(key).toString();
                if (value.length() > 2 && value.startsWith("\"")) {
                    parameters.put(key, new String[]{value.substring(1, value.length() - 1)});
                } else {
                    parameters.put(key, new String[]{value});
                }
            }
        }
    }

    private HttpServletRequest getHttpServletRequest() {
        return (HttpServletRequest) super.getRequest();
    }

    public String getParameter(String name) {
        return parameters.get(name)[0];
    }

    public String[] getParameterValues(String name) {
        String value = getParameter(name);
        if (value != null) {
            return new String[]{value};
        }
        return null;
    }

    public Map<String, String[]> getParameterMap() {
        return (Map<String, String[]>) parameters;
    }

    public Enumeration<String> getParameterNames() {
        return new IteratorEnumeration<String>(parameters.keySet());
    }

    public String getQueryString() {
        String queryString = super.getQueryString();
        try {
            if (queryString != null) {
                if (Base64.isBase64(queryString)) {
                    return new String(Base64.decodeBase64(queryString.getBytes(encoding)), encoding);
                } else {
                    return queryString;
                }
            }
            return null;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedEncodingException", e);
        }
    }

    public BufferedReader getReader() throws IOException {
        if (firstTime)
            firstTime();
        InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(bytes), encoding);
        return new BufferedReader(isr);
    }

    private void firstTime() throws IOException {
        firstTime = false;
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = this.getHttpServletRequest().getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        byte[] originBytes = buffer.toString().getBytes(encoding);
        if (Base64.isBase64(originBytes)) {
            bytes = Base64.decodeBase64(originBytes);
        } else {
            bytes = originBytes;
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (firstTime)
            firstTime();
        ServletInputStream sis = new ServletInputStream() {
            private int i;

            @Override
            public int read() throws IOException {
                byte b;
                if (bytes.length > i)
                    b = bytes[i++];
                else
                    b = -1;

                return b;
            }
        };

        return sis;
    }

    public class IteratorEnumeration<E> implements Enumeration {

        private Iterator<E> iterator;

        public IteratorEnumeration(Set<E> set) {
            this.iterator = set.iterator();
        }

        public boolean hasMoreElements() {
            return iterator.hasNext();
        }

        public E nextElement() {
            return iterator.next();
        }

    }

}
