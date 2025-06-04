package at.fhv.sysarch.lab3.pipeline.filters.source;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.obj.Model;
import at.fhv.sysarch.lab3.pipeline.filters.PullFilter;
import at.fhv.sysarch.lab3.pipeline.filters.PushFilter;
import at.fhv.sysarch.lab3.pipeline.pipe.Pipe;

import java.util.List;

public class Source implements PushFilter<Model, List<Face>>, PullFilter<Object,Face> {
   //Batch Loading
   private Pipe<List<Face>> next;

   private Pipe<Face> prev;

    private int index = 0;

    private Model model;







    @Override
    public void setPrevious(Pipe<Object> prev) {
        //Does nothing

    }

    @Override
    public Face pull() {
        if(index<model.getFaces().size()){
            return model.getFaces().get(index++);
        }
        else{
            throw new ArrayIndexOutOfBoundsException("Index out of bound");
        }
    }

    @Override
    public void setNext(Pipe<List<Face>> next) {
        this.next=next;

    }

    @Override
    public void push(Model input) {
        this.next.push(input.getFaces());

    }

    public void setModel(Model model){
        this.model=model;
    }


}
