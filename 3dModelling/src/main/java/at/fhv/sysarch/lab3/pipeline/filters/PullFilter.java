package at.fhv.sysarch.lab3.pipeline.filters;

import at.fhv.sysarch.lab3.pipeline.pipe.Pipe;

public interface PullFilter <T,O> {
//T=I
     void setPrevious(Pipe<T> prev);

     public O pull();
}
