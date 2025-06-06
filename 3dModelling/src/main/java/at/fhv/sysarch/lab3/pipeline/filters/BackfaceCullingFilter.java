package at.fhv.sysarch.lab3.pipeline.filters;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.pipe.Pipe;
import com.hackoeur.jglm.Vec3;
import com.hackoeur.jglm.Vec4;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BackfaceCullingFilter implements PushFilter <List<Face>, List<Face>>, PullFilter<Face, Face> {

    // Batch loading


    private Pipe<List<Face>> next;

    private Pipe<Face> prev;


    @Override
    public void setNext(Pipe<List<Face>> next) {
        this.next = next;

    }

    @Override
    public void push(List<Face> input) {
        List<Face> output = new LinkedList<>();

        for (Face face : input) {
            //Wenn das skalar Produkt vom Vektor und NormV Pos dann zeigt der polygon zum kamera
            if(face.getV1().dot(face.getN1()) <= 0) {
                output.add(face);
            }
        }
        next.push(output);
    }




    @Override
    public void setPrevious(Pipe<Face> prev) {
        this.prev = prev;

    }

    @Override
    public Face pull() {
        Face data = prev.pull();
        if (data != null) {
            if (data.getV1().dot(data.getN1()) <= 0) {
                return data;
            }
            throw new IllegalArgumentException("not visible");
        }
        return data;
    }
}




/*  checkt ob das object nach hinten oder vorne gedreht ist
    Wir rendern nur die vorderseite des Objekts
    wir werden den output im psuh verarbeiten
 */
