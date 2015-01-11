package org.monroe.team.corebox.utils;

public class P<TypeFirst, TypeSecond> {

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
