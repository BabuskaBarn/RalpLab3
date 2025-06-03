package at.fhv.sysarch.lab3.pipeline.pipe;

import at.fhv.sysarch.lab3.pipeline.filters.PullFilter;

public interface PullPipe<T> {
    T pull();

    PullFilter<?,T> getPullFilter();
    void setPrevious(PullFilter<?,T> pullFilter);

}
