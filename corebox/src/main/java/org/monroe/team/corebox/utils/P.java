package org.monroe.team.corebox.utils;

import java.io.Serializable;

public class P<TypeFirst, TypeSecond> implements Serializable {

    public final TypeFirst first;
    public final TypeSecond second;

    public P(TypeFirst first, TypeSecond second) {
        this.first = first;
        this.second = second;
    }

    public static <TypeFirst,TypeSecond> P<TypeFirst, TypeSecond> pair(TypeFirst first, TypeSecond second){
        return new P<>(first,second);
    }
}
