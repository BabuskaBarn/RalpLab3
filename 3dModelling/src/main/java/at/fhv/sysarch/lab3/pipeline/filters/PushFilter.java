package at.fhv.sysarch.lab3.pipeline.filters;

import at.fhv.sysarch.lab3.obj.Face;

import java.nio.channels.Pipe;

public interface PushFilter<T, O> {

    public void setNext(Pipe<O> next);

    public void push(T input);
}
