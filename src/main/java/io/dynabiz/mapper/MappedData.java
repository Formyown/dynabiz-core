/*
 * Copyright (c) 2018, Deyu Heng. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.dynabiz.mapper;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Hashtable;

public class MappedData<T extends MappedData> {

    private MappedData(){

    }

    public MappedData(Object... sources){
        for (Object s:sources
             ) {
            this.mapFrom(s);
        }
    }

    @SuppressWarnings("unchecked")
    public T mapFrom(Object source) {
        if(source == null) return (T) this;
        mapFrom(source, source.getClass(), this.getClass());

        Class superClass = this.getClass();
        while (!(superClass = superClass.getSuperclass()).equals(MappedData.class)) {
            mapFrom(source, source.getClass(), superClass);
        }

        return (T) this;
    }


    @SuppressWarnings("unchecked")
    public T mapFrom(Hashtable<String, Object> source) {
        mapTable(source, this);
        return (T) this;
    }

    public void mapTo(Object target) {
        mapTo(target, target.getClass(), this.getClass());

        Class superClass = this.getClass();
        while (!(superClass = superClass.getSuperclass()).equals(MappedData.class)) {
            mapTo(target, target.getClass(), superClass);
        }
    }
    @SuppressWarnings("unchecked")
    public void mapTable(Hashtable<String, Object> from, Object target){
        if (from == null || from.isEmpty()) {
            return;
        }
        Field[] thisFields = this.getClass().getDeclaredFields();
        for (Field thisField : thisFields) {

            String name = thisField.getName();
            Annotation annotation = thisField.getAnnotation(Mapped.class);


            if (annotation != null) {
                if(!((Mapped) annotation).name().equals("")){
                    name = ((Mapped) annotation).name();
                }
            } else {
                continue;
            }

            if (!from.containsKey(name)) return;

            thisField.setAccessible(true);
            try {
                if (((Mapped) annotation).filter().equals(void.class)) {

                        thisField.set(target, from.get(from));

                } else {
                    thisField.set(target, ((MappingFilter) (((Mapped) annotation).filter().newInstance())).set(from.get(from)));
                }
            } catch (Exception e) {
                throw new MapperException(e);
            }

        }
    }

    private static boolean checkSatisfied(Class[] targetConditions, Class targetClass){
        if(targetConditions.length != 0){
            for (Class i : targetConditions){
                while(!i.equals(targetClass)){
                    if(targetClass.equals(Object.class)) return false;
                    targetClass = targetClass.getSuperclass();
                }
                return true;
            }
            return false;
        }
        else{
            return true;
        }
    }

    private void mapTo(Object target, Class targetClass, Class sourceClass) {
        Field[] sourceFields = sourceClass.getDeclaredFields();

        Class[] fieldTargetClassesConfig = {};
        Class sourceFilterClassConfig = void.class;


        MappedConfig classAnno = (MappedConfig)sourceClass.getAnnotation(MappedConfig.class);
        if(classAnno != null){
            fieldTargetClassesConfig = classAnno.targetClass();
            sourceFilterClassConfig = classAnno.filter();
        }

        for (Field sourceField : sourceFields) {
            Mapped sourceFieldAnno = sourceField.getAnnotation(Mapped.class);
            if(sourceFieldAnno == null) continue;
            Class[] sourceFieldTargetClass = sourceFieldAnno.targetClass();
            if(sourceFieldTargetClass.length == 0){
                sourceFieldTargetClass = fieldTargetClassesConfig;
            }
            if(!checkSatisfied(sourceFieldTargetClass, targetClass)){
                continue;
            }
            Class sourceFilterClass = sourceFieldAnno.filter();
            if(sourceFilterClass.equals(void.class)){
                sourceFilterClass = sourceFilterClassConfig;
            }
            String name = sourceFieldAnno.name();
            if(name.isEmpty()){
                name = sourceField.getName();
            }

            try{
                Field targetField = targetClass.getDeclaredField(name);
                targetField.setAccessible(true);
                sourceField.setAccessible(true);

                if(sourceFilterClass.equals(void.class)){
                    targetField.set(target , sourceField.get(this));
                    continue;
                }
                targetField.set(target,  ((MappingFilter)sourceFilterClass.newInstance()).get(sourceField.get(this)));

            }
            catch(Exception e){
                throw new MapperException(e);
            }


        }
    }
    @SuppressWarnings("unchecked")
    private void mapFrom(Object source, Class sourceClass, Class targetClass) {
        Field[] targetFields = targetClass.getDeclaredFields();

        Class[] fieldTargetClassesConfig = {};
        Class targetFilterClassConfig = void.class;

        MappedConfig classAnno = (MappedConfig) targetClass.getAnnotation(MappedConfig.class);
        if (classAnno != null) {
            fieldTargetClassesConfig = classAnno.targetClass();
            targetFilterClassConfig = classAnno.filter();
        }

        for (Field targetField : targetFields) {
            Mapped targetFieldAnno = targetField.getAnnotation(Mapped.class);
            if(targetFieldAnno == null) continue;
            Class[] targetFieldTargetClass = targetFieldAnno.targetClass();
            if (targetFieldTargetClass.length == 0) {
                targetFieldTargetClass = fieldTargetClassesConfig;
            }
            if (!checkSatisfied(targetFieldTargetClass, sourceClass)) {
                continue;
            }
            Class sourceFilterClass = targetFieldAnno.filter();
            if (sourceFilterClass.equals(void.class)) {
                sourceFilterClass = targetFilterClassConfig;
            }
            String name = targetFieldAnno.name();
            if (name.isEmpty()) {
                name = targetField.getName();
            }
            //Field sourceField = sourceClass.getDeclaredField(name);
            Method sourceGetter = null;
            try{
                sourceGetter = sourceClass.getMethod("get" + upperFirst(name));

            }catch (NoSuchMethodException e){
                try {
                    sourceGetter = sourceClass.getMethod("is" + upperFirst(name));
                }
                catch (Exception ex){
                    throw new MapperException(ex);
                }
            }

            targetField.setAccessible(true);
            //sourceField.setAccessible(true);
            if (sourceFilterClass.equals(void.class)) {
                try {
                    targetField.set(this, sourceGetter.invoke(source));
                } catch (Exception e) {
                    throw new MapperException(e);
                }
                //sourceField.get(source)
                continue;
            }
            try {
                targetField.set(this, ((MappingFilter) sourceFilterClass.newInstance()).set(sourceGetter.invoke(source)));
            } catch (Exception e) {
                throw new MapperException(e);
            }
            //sourceField.get(source)
        }

    }

    private static String upperFirst(String str){
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}
