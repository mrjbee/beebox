package org.monroe.team.corebox.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


public class Lists {
    public static <DataType> void each(Collection<DataType> collection, Closure<DataType,Void> closure){
        for (DataType dataType : collection) {
            closure.execute(dataType);
        }
    }

    public static <ToData,FromData> List<ToData> collect(Collection<FromData> collection, Closure<FromData,ToData> closure){
        List<ToData> answerList = new ArrayList<ToData>(collection.size());
        for (FromData origin : collection) {
           ToData transformed = closure.execute(origin);
           if (transformed != null) answerList.add(answerList.size(), transformed);
        }
        return answerList;
    }

    public static <DataType> void iterateAndRemove(Collection<DataType> list, Closure<Iterator<DataType>, Boolean> closure) {
        Iterator<DataType> iterator = list.iterator();
        while (iterator.hasNext()){
            if(Boolean.TRUE.equals(closure.execute(iterator))){
                return;
            }
        }
    }

    public static <ElemType> ElemType getLast(List<ElemType> list) {
        return list.get(list.size() - 1);
    }

    public static <ElemType> ElemType getLast(ElemType... list) {
        return list[list.length-1];
    }
    public static <Type1,Type2>  boolean in(Collection<Type1> collection, Type2 item, Closure<P<Type1,Type2>,Boolean> compare) {
        for (Type1 type1 : collection) {
            if (compare.execute(new P<Type1, Type2>(type1,item))) return true;
        }
        return false;
    }

    public static <ElemType> ElemType find(Collection<ElemType> collection, Closure<ElemType, Boolean> comapre) {
        for (ElemType elem:collection){
            if (comapre.execute(elem)){
                return elem;
            }
        }
        return null;
    }

    public static int getLastIndex(List<?> list) {
        return list.size()-1;
    }

    public static int getLastIndex(Object... array) {
        return array.length -1;
    }
}
