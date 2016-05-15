
public class Beam {
	public static final double WIDTH = 9;
	public static final double DEPTH = 2;
	public static final double LENGTH = 175;
	
	public static final double TENSION_SINGLE = 260;
	public static final double COMPRESSION_PER_MM2 = 123333;

	public final Point a;
	public final Point b;
	public final int index;
	public final BeamType type;
	
	public double cos;
	public double sin;
	public double length;
	public double maxCompressionLoad;
	public double maxTensionLoad;
	public double cost;
	
	public Beam(String line, int index) throws IllegalArgumentException {
		String[] split = line.split(" ");
		this.index = index;
		
		try {
			a = Main.POINTS.get(Integer.parseInt(split[0]));
			b = Main.POINTS.get(Integer.parseInt(split[1]));
			a.beams.add(this);
			b.beams.add(this);
			type = BeamType.valueOf(split[2]);
		} catch(Exception ex) {
			throw new IllegalArgumentException("Malformed beam input " + ex);
		}
		
		recalc();
	}

	public void recalc() {
		double dx = b.x - a.x;
		double dy = b.y - a.y;
		length = Math.sqrt(dx * dx + dy * dy);
		
		cos = dx / length;
		sin = dy / length;
		
		maxCompressionLoad = type.compressionStrength / (length * length);
		maxTensionLoad = type.tensionStrength;
		cost = type.cost * length;
	}
}
