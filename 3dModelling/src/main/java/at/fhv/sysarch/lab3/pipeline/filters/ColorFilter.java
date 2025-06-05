package at.fhv.sysarch.lab3.pipeline.filters;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.data.Pair;
import at.fhv.sysarch.lab3.pipeline.pipe.Pipe;
import javafx.scene.paint.Color;

public class ColorFilter implements PushFilter<Face, Pair<Face, Color>>, PullFilter<Face, Pair<Face, Color>> {

    private Pipe<Pair<Face, Color>> next;
    private Pipe<Face> prev;
    private Color color;

    public ColorFilter(Color color) {
        this.color = color;
    }

    @Override
    public void setNext(Pipe<Pair<Face, Color>> next) {
        this.next = next;
    }

    @Override
    public void setPrevious(Pipe<Face> prev) {
        this.prev = prev;
    }

    @Override
    public void push(Face input) {
        this.next.push(new Pair<>(input, color));
    }

    @Override
    public Pair<Face, Color> pull() {
       Face data = prev.pull();
       if(data!=null){
           return new  Pair<>(data, color);
        }
       return null;
    }
}
