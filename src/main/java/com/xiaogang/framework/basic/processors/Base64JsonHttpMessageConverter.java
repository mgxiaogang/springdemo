package com.xiaogang.framework.basic.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.ser.FilterProvider;
import org.codehaus.jackson.map.ser.impl.SimpleBeanPropertyFilter;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: zhuganggang
 * @Date: 2018/10/11 20:30
 * @Description:
 */
public class Base64JsonHttpMessageConverter extends MappingJackson2HttpMessageConverter {

    private static final Logger log = LoggerFactory.getLogger(Base64JsonHttpMessageConverter.class);

    public Base64JsonHttpMessageConverter() {
        super();
//        ObjectMapper mapper = getObjectMapper();
//        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
//                .configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false)
//                .configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false).getSerializationConfig()
//                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
//        Set<String> fliterSet = new HashSet<String>();
//        FilterProvider filterProvider = new SimpleFilterProvider().addFilter("filter",
//                SimpleBeanPropertyFilter.serializeAllExcept(fliterSet));
//        mapper.setFilters(filterProvider);
    }

    protected void writeInternal(Object object, HttpOutputMessage outputMessage) throws IOException,
            HttpMessageNotWritableException {

        try {
            byte[] bytes = getObjectMapper().writeValueAsBytes(object);
            FileCopyUtils.copy(Base64.encodeBase64(bytes), outputMessage.getBody());
        } catch (JsonProcessingException ex) {
            log.error("Could not write JSON: " + ex.getMessage(), ex);
            throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
        }
    }

}