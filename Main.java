import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Main {
	public static final List<Point> POINTS = new ArrayList<>();
	public static final List<Beam> BEAMS = new ArrayList<>();

	public static Point load;
	public static Point supportA;
	public static Point supportB;

	private static final BufferedReader INPUT = new BufferedReader(new InputStreamReader(System.in));

	public static void main(String[] args) throws IOException {
		System.out.println("Please input the positions of your points, one at a time, each co-ord sepperated by a space each on a new line.\ne.g.\n20 3.543\n24 0\n...\nType DONE when done.");

		String line;
		int pointNo = 0;
		while(!(line = INPUT.readLine()).equals("DONE")) {
			POINTS.add(new Point(line, pointNo++));
		}

		System.out.println("Input the beams in the form POINTA POINTB TYPE");
		System.out.println("Available types are:");
		for(BeamType type : BeamType.values()) {
			System.out.println('\t' + type.name());
		}
		System.out.println("Type DONE when done.");

		int beamNo = 0;
		while(!(line = INPUT.readLine()).equals("DONE")) {
			BEAMS.add(new Beam(line, beamNo++));
		}

		System.out.println("Which point is the load?");
		load = POINTS.get(Integer.parseInt(INPUT.readLine()));

		System.out.println("Which points are the support");
		try {
			String[] split = INPUT.readLine().split(" ");
			supportA = POINTS.get(Integer.parseInt(split[0]));
			supportB = POINTS.get(Integer.parseInt(split[1]));
		} catch(Exception ex) {
			throw new IllegalArgumentException("Malformed input " + ex);
		}

		System.out.println("Calculate(c) or Optimize(o)?");
		String choice = INPUT.readLine();
		switch(choice) {
			case "o":
				System.out.println("Select an optimization type.\nAvailable types are:");
				for(Metric type : Metric.values()) {
					System.out.println('\t' + type.name());
				}
				TrussOptimizer.metric = Metric.valueOf(INPUT.readLine());
				
				System.out.println("Select a maximum weight limit in sticks, write 'NO' for no limit");
				String input = INPUT.readLine();
				if(!input.equals("NO")) {
					TrussOptimizer.maxWeight = Double.parseDouble(input);
				}
				
				System.out.println("Input the number of seconds to optimize for.");
				TrussOptimizer.optimize(Double.parseDouble(INPUT.readLine()));
			case "c":
				TrussSolver.solve();
				double[] LF = new double[BEAMS.size()];
				double maxLoadFactor = 0;
				
				for(int i = 0; i < BEAMS.size(); i++) {
					if(TrussSolver.beamLoads[i] < 0) {
						//compression
						LF[i] = TrussSolver.beamLoads[i] / -BEAMS.get(i).maxCompressionLoad;
					} else {
						//tension
						LF[i] = TrussSolver.beamLoads[i] / BEAMS.get(i).maxTensionLoad;
					}
					
					maxLoadFactor = Math.max(maxLoadFactor, LF[i]);
				}
				
				double maxLoad = 1 / maxLoadFactor;
				
				System.out.printf("Max Load %.2fN\n", maxLoad);
				
				System.out.println("Point Data:\nX   Y");
				for(Point point : POINTS) {
					System.out.printf("\t%.2f %.2f\n\n", point.x, point.y);
				}
				
				System.out.println("Beam Data:\nPointA PointB Type Length Load at 100N LoadFactor");
				
				double cost = 0;
				for(int i = 0; i < BEAMS.size(); i++) {
					Beam beam = BEAMS.get(i);
					cost += beam.cost;
					System.out.printf("\t%d %d %s %.2fmm %.2fN %d%%\n\n", beam.a.index, beam.b.index, beam.type, beam.length, TrussSolver.beamLoads[i] * 100, (int) (LF[i] * maxLoad * 100));
				}
				
				System.out.printf("\nWeight: %.2f sticks", cost);
				
				break;
			default:
				throw new IllegalArgumentException(choice + " is not 'c' or 'o'");
		}
	}
}
