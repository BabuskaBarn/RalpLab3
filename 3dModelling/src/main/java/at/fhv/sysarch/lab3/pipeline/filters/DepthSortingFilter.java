package at.fhv.sysarch.lab3.pipeline.filters;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.data.Pair;
import at.fhv.sysarch.lab3.pipeline.pipe.Pipe;
import com.hackoeur.jglm.Vec3;
import javafx.scene.paint.Color;
import java.util.*;

public class DepthSortingFilter implements PushFilter<List<Pair<Face, Color>>, Pair<Face, Color>>, PullFilter<Pair<Face, Color>, Pair<Face, Color>> {

    private Pipe<Pair<Face, Color>> pipe;
    private final Vec3 viewPort;

    // PriorityQueue zum Sortieren nach Tiefe
    private PriorityQueue<Pair<Face, Color>> faceQueue;

    public DepthSortingFilter(Vec3 viewPort) {
        this.viewPort = viewPort;
        faceQueue = new PriorityQueue<>(
                (p1, p2) -> {
                    float z1 = averageDepth(p1.fst());
                    float z2 = averageDepth(p2.fst());
                    // Tiefster zuerst = größere Entfernung zuerst => absteigend sortieren
                    return Float.compare(z2, z1);
                }
        );
    }

    @Override
    public void setNext(Pipe<Pair<Face,Color>> next) {
        this.pipe = next;
    }

    @Override
    public void push(List<Pair<Face, Color>> input) {
        // Sortiere Faces mit Color nach Tiefe
        input.sort((p1, p2) -> Float.compare(averageDepth(p2.fst()), averageDepth(p1.fst())));

        for (Pair<Face, Color> pair : input) {
            if (pipe != null) {
                pipe.push(pair);
            }
        }
    }

    @Override
    public void setPrevious(Pipe<Pair<Face, Color>> predecessor) {
        this.pipe = predecessor;
        // PriorityQueue neu initialisieren, falls nötig
        faceQueue = new PriorityQueue<>(
                (p1, p2) -> {
                    float z1 = averageDepth(p1.fst());
                    float z2 = averageDepth(p2.fst());
                    return Float.compare(z2, z1);
                }
        );
    }

    @Override
    public Pair<Face, Color> pull() {
        // Fülle Queue mit allen verfügbaren Faces vom Predecessor
        while (true) {
            Pair<Face, javafx.scene.paint.Color> pair;
            try {
                pair = pipe.pull();
                if (pair != null) {
                    faceQueue.add(pair);
                } else {
                    break;
                }
            } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
                break;
            }
        }

        // Gibt jeweils den tiefsten Face+Color zurück
        if (!faceQueue.isEmpty()) {
            return faceQueue.poll();
        }

        return null;
    }

    private float averageDepth(Face face) {
        Vec3 v1 = face.getV1().toVec3();
        Vec3 v2 = face.getV2().toVec3();
        Vec3 v3 = face.getV3().toVec3();

        float d1 = v1.subtract(viewPort).getLength();
        float d2 = v2.subtract(viewPort).getLength();
        float d3 = v3.subtract(viewPort).getLength();

        return (d1 + d2 + d3) / 3f;
    }
}
