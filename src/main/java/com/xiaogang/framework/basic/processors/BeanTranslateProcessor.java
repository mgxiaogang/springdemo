package com.xiaogang.framework.basic.processors;

import com.xiaogang.framework.basic.annotations.Translate;
import com.xiaogang.framework.basic.processors.interfaces.FieldTranslater;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhuganggang
 * @Date: 2018/10/11 15:12
 * @Description:
 */
public class BeanTranslateProcessor implements ApplicationContextAware {

    private Map<Class<? extends FieldTranslater>, FieldTranslater> translaters = new HashMap<Class<? extends FieldTranslater>, FieldTranslater>();
    private ApplicationContext applicationContext;

    public void translate(Object obj) throws TranslateException {
        if (obj == null || !needDecomposed(obj.getClass()))
            return;
        if (Collection.class.isAssignableFrom(obj.getClass())) {
            for (Object item : (Collection) obj) {
                translate(item);
            }
        } else {
            List<Field> fields = ReflectUtils.getAllDeclaredFields(obj.getClass());
            for (Field field : fields) {
                if (!needDecomposed(field.getType())) {
                    Translate translate = field.getAnnotation(Translate.class);
                    if (translate == null) continue;
                    FieldTranslater translater = getFieldTranslater(translate.using());
                    if (translater == null) {
                        throw new TranslateException("BeanTranslater undefined in Spring ApplicationContext : "
                                + translate.using().getName());
                    }
                    Object value = translater.translate(obj, translate.source(), translate.additional(),
                            translate.additionalClass(), field.getType());
                    try {
                        ReflectUtils.invokeSetterMethod(obj, field.getName(), value);
                    } catch (Exception e) {
                        throw new TranslateException("Class " + obj.getClass().getName()
                                + " can't find public Setter method for field " + field.getName(), e);
                    }
                } else {
                    Object subObj = null;
                    try {
                        subObj = ReflectUtils.getValueByFieldName(obj, field.getName());
                    } catch (Exception e) {
                        throw new TranslateException(e);
                    }
                    translate(subObj);
                }
            }
        }
    }

    /**
     * 判断该类型是否需要分解(不是基本类型、不是Map、不是数组)
     *
     * @param clazz
     * @return
     */
    protected boolean needDecomposed(Class<?> clazz) {
        return !(BeanUtils.isSimpleValueType(clazz) || Map.class.isAssignableFrom(clazz) || clazz.isArray());
    }

    private FieldTranslater getFieldTranslater(Class<? extends FieldTranslater> type) {
        FieldTranslater translater = translaters.get(type);
        if (translater == null) {
            translater = applicationContext.getBean(type);
            if (translater == null) {
                return null;
            }
            translaters.put(type, translater);
        }
        return translater;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

