package at.fhv.sysarch.lab3.pipeline.pipe;

import at.fhv.sysarch.lab3.pipeline.filters.PushFilter;

public interface PushPipe<T> {
    void push(T input);

    PushFilter<T,?> getPushFilter();
    void setNext(PushFilter<T,?> pushFilter);
}
