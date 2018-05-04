package com.common.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Map;

/**
 * @author chengwei
 * @date 2018/4/27 15:19
 */
public class SpringUtil implements ApplicationContextAware {
    private static ApplicationContext context = null;

    private SpringUtil() {
        super();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    /**
     * 根据名称获取bean
     * @param beanName
     * @return
     */
    public static Object getBean(String beanName) {
        if (context != null) {
            return context.getBean(beanName);
        }
        return null;
    }

    /**
     * 根据bean名称获取指定类型bean
     * @param beanName bean名称
     * @param clazz 返回的bean类型,若类型不匹配,将抛出异常
     */
    public static <T> T getBean(String beanName, Class<T> clazz) {
        if (context != null) {
            return context.getBean(beanName, clazz);
        }
        return null;
    }
    /**
     * 根据类型获取bean
     * @param clazz
     * @return
     */
    public static <T> T getBean(Class<T> clazz) {
        T t = null;
        if (context != null) {
            Map<String, T> map = context.getBeansOfType(clazz);
            for (Map.Entry<String, T> entry : map.entrySet()) {
                t = entry.getValue();
            }
        }
        return t;
    }

    /**
     * 是否包含bean
     * @param beanName
     * @return
     */
    public static boolean containsBean(String beanName) {
        if (context != null) {
            return context.containsBean(beanName);
        }
        return false;
    }

    /**
     * 是否是单例
     * @param beanName
     * @return
     */
    public static boolean isSingleton(String beanName) {
        if (context != null) {
            return context.isSingleton(beanName);
        }
        return false;
    }

    /**
     * bean的类型
     * @param beanName
     * @return
     */
    public static Class getType(String beanName) {
        if (context != null) {
            return context.getType(beanName);
        }
        return null;
    }

    /**
     * 注册bean
     *
     * @param beanName
     * @return
     */
    public static Object registerBean(String beanName, Class className) {
        GenericBeanDefinition definition = new GenericBeanDefinition();
        definition.setBeanClass(className);    //设置类
        definition.setScope("singleton");       //设置scope
        definition.setLazyInit(false);          //设置是否懒加载
        definition.setAutowireCandidate(true);  //设置是否可以被其他对象自动注入
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) context;
        BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) configurableApplicationContext
                .getBeanFactory();
        beanDefinitionRegistry.registerBeanDefinition(beanName, definition);
        return getBean(beanName);
    }
}




