package com.xiaogang.framework.basic.processors;

import com.xiaogang.framework.basic.processors.interfaces.Count;
import org.springframework.core.MethodParameter;

import java.util.List;

/**
 * @Author: zhuganggang
 * @Date: 2018/10/11 15:26
 * @Description:
 */
public class ListWrapper extends AbstractBeanWrapper {

    public boolean supports(MethodParameter returnType) {
        return List.class.isAssignableFrom(returnType.getParameterType());
    }

    public Object wrap(Object bean) {
        List<?> list = (List<?>) bean;
        Rows rows = new Rows();
        if (bean instanceof Count) {
            rows.setCount(((Count) bean).count());
        } else {
            rows.setCount(list.size());
        }
        rows.setRows(list);
        return new SuccessData(rows);
    }

    // public Integer getPriority() {
    // return 10;
    // }

    private class Rows {

        private Integer count;

        private List rows;

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public List getRows() {
            return rows;
        }

        public void setRows(List rows) {
            this.rows = rows;
        }

    }

}

