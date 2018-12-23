package com.nearsyh.mafia.common.fn;

@FunctionalInterface
public interface CheckedFunction<I, O, X extends Exception> {

    O apply(I input) throws X;

}
