import java.util.ArrayList;
import java.util.List;

public class TrussState {
	List<Point> points = new ArrayList<>();
	double maxWeight;
	double weight;
	double score;
	
	public TrussState() {
		for(int i = 0; i < Main.POINTS.size(); i++) {
			points.add(new Point(Main.POINTS.get(i)));
		}
	}
	
	public void set() {
		for(int i = 0; i < points.size(); i++) {
			points.get(i).set(Main.POINTS.get(i));
		}
	}
	
	public void reset() {
		for(int i = 0; i < points.size(); i++) {
			Main.POINTS.get(i).set(points.get(i));
		}
	}
}
