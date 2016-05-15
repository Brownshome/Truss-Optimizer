import java.util.ArrayList;
import java.util.List;

public class Point {
	public double x;
	public double y;
	public final int index;
	public final List<Beam> beams;

	/** Reads a string into two space sepperated values */
	public Point(String line, int index) throws IllegalArgumentException {
		String[] split = line.split(" ");
		
		try {
			this.index = index;
			x = Double.parseDouble(split[0]);
			y = Double.parseDouble(split[1]);
		} catch(Exception ex) {
			throw new IllegalArgumentException("Malformed point input " + ex);
		}
		
		beams = new ArrayList<>();
	}

	public Point(Point point) {
		x = point.x;
		y = point.y;
		index = point.index;
		beams = point.beams;
	}

	public void set(Point point) {
		x = point.x;
		y = point.y;
	}
}