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



import java.util.ArrayList;
import java.util.List;

/**
 * Created by Deyu Heng on 2017/09/30.
 */
public class Mapper {

    @SuppressWarnings("unchecked")
    public static <RT extends MappedData> RT mapFrom(Class targetType, Object sources){
        if(sources == null) return null;
        RT result;
        try{
            result = (RT)targetType.newInstance();
        }
        catch (Exception ex){
            throw new MapperException(ex);
        }

        result.mapFrom(sources);
        return result;
    }


    @SuppressWarnings("unchecked")
    public static <ST,RT extends MappedData> RT mapFrom(Class targetType, ST sources, Mapping<ST, RT> mapping){
        if(sources == null) return null;
        RT result;
        try{
            result = (RT)targetType.newInstance();
        }
        catch (Exception ex){
            throw new MapperException(ex);
        }
        result.mapFrom(sources);
        mapping.mapTo(sources, result);
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <RT extends MappedData> Iterable<RT> mapFrom(Class targetType, Iterable sources) {
        List<RT> resultList = new ArrayList<RT>();
        try {
            for (Object s:sources) {
                RT i = (RT)targetType.newInstance();
                i.mapFrom(s);
                resultList.add(i);
            }
        }
        catch (Exception ex){
            throw new MapperException(ex);
        }
        return resultList;
    }

    @SuppressWarnings("unchecked")
    public static <RT extends MappedData, ST> Iterable<RT> mapFrom(Class targetType, Iterable<ST> sources, Mapping<ST, RT> mapping) {
        List<RT> resultList = new ArrayList<RT>();
        try {
            for (ST s : sources) {
                RT i = (RT) targetType.newInstance();
                i.mapFrom(s);
                mapping.mapTo(s, i);
                resultList.add(i);
            }
        }
        catch (Exception ex){
            throw new MapperException(ex);
        }
        return resultList;
    }

}
