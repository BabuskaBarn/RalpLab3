package at.fhv.sysarch.lab3.pipeline.pipe;

import at.fhv.sysarch.lab3.pipeline.filters.PushFilter;
import at.fhv.sysarch.lab3.pipeline.filters.PullFilter;
public class Pipe<T> implements PushPipe<T>, PullPipe<T> {

    private PushFilter<T, ?> pushFilter;
    private PullFilter<?, T> pullFilter;

    public Pipe(){};
    public Pipe(PullFilter<?,T> pullFilter){this.pullFilter=pullFilter;}
    public Pipe(PushFilter<T,?> pushFilter){
        this.pushFilter=pushFilter;
    }



    @Override
    public void setPrevious(PullFilter<?,T> prev) {
        this.pullFilter=prev;

    }

    @Override
    public T pull() {
        return this.pullFilter.pull();
    }



    @Override
    public void setNext( PushFilter<T,?> next) {
        this.pushFilter=next;

    }

    @Override
    public void push(T input) {
        pushFilter.push(input);

    }

    @Override
    public PushFilter<T, ?> getPushFilter() {
        return this.pushFilter;
    }

    @Override
    public PullFilter<?, T> getPullFilter() {
        return this.pullFilter;
    }
}

