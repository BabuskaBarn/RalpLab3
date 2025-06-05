package at.fhv.sysarch.lab3.pipeline.filters;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.data.Pair;
import at.fhv.sysarch.lab3.pipeline.pipe.Pipe;
import com.hackoeur.jglm.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class DepthSortingFilter implements PushFilter<List<Face>, Face>, PullFilter<Face, Face> {

    private Pipe<Face> pipe;
    private final Vec3 viewPort;

    // PriorityQueue zum Sortieren nach Tiefe
    private PriorityQueue<Face> faceQueue;

    public DepthSortingFilter(Vec3 viewPort) {
        this.viewPort = viewPort;
        faceQueue = new PriorityQueue<>(
                (f1, f2) -> {
                    Vec3 vertex1f1 = f1.getV1().toVec3();
                    Vec3 vertex2f1 = f1.getV2().toVec3();
                    Vec3 vertex3f1 = f1.getV3().toVec3();

                    float z1 = vertex1f1.subtract(viewPort).getLength();
                    float z2 = vertex2f1.subtract(viewPort).getLength();
                    float z3 = vertex3f1.subtract(viewPort).getLength();

                    float zf1 = (z1 + z2 + z3) / 3;


                    z1 = vertex1f1.subtract(viewPort).getLength();
                    z2 = vertex2f1.subtract(viewPort).getLength();
                    z3 = vertex3f1.subtract(viewPort).getLength();



                    float zf2 = (z1 + z2 + z3) / 3;

                    return Float.compare(zf2, zf1);
                }
        );
    }

    @Override
    public void setNext(Pipe<Face> next) {
        this.pipe = next;
    }

    @Override
    public void push(List<Face> input) {
        List<Pair<Float,Face>> faces = new ArrayList<>();

        for (Face face : input) {
            Vec3 vertex1 = face.getV1().toVec3();
            Vec3 vertex2 = face.getV2().toVec3();
            Vec3 vertex3 = face.getV3().toVec3();

            float z1 = vertex1.subtract(viewPort).getLength();
            float z2 = vertex2.subtract(viewPort).getLength();
            float z3 = vertex3.subtract(viewPort).getLength();

            float z = (z1 + z2 + z3) / 3;

            faces.add(new Pair<>(z, face));
        }

        faces.sort((pair1,pair2) -> -pair1.fst().compareTo(pair2.fst()));

        for (Pair<Float,Face> pair : faces) {
            pipe.push(pair.snd());
        }}

    @Override
    public void setPrevious(Pipe<Face> predecessor) {
        pipe = predecessor;
        // PriorityQueue neu initialisieren, falls nötig
        faceQueue = new PriorityQueue<>(
                (f1, f2) -> {
                    Vec3 vertex1f1 = f1.getV1().toVec3();
                    Vec3 vertex2f1 = f1.getV2().toVec3();
                    Vec3 vertex3f1 = f1.getV3().toVec3();

                    float z1 = vertex1f1.subtract(viewPort).getLength();
                    float z2 = vertex2f1.subtract(viewPort).getLength();
                    float z3 = vertex3f1.subtract(viewPort).getLength();

                    float zf1 = (z1 + z2 + z3) / 3;



                    float zf2 = (z1 + z2 + z3) / 3;

                    return Float.compare(zf2, zf1);
                }
        );
    }




@Override
public Face pull() {
    while (true){
        Face face;
        try {
            face = pipe.pull();
            if (face != null) {
                faceQueue.add(face);
            } else {

                break;
            }
        } catch (IllegalArgumentException ignored) {

        } catch (ArrayIndexOutOfBoundsException ignored) {
            break;
        }

    }
    while (!faceQueue.isEmpty()) {
        return faceQueue.poll();
    }


    return null;
}
}
