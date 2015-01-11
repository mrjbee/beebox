package org.monroe.team.corebox.utils;

public interface Closure <In,Out> {
    public Out execute(In arg);
}
