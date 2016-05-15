
public class TrussOptimizer {
	public static final double MAX_DEPTH = 140;
	public static final double MAX_LENGTH = 160;
	public static final double INITIAL_VARY = 300;

	public static Metric metric;
	
	static TrussState optimal;
	static double shelf, max, min;
	static double maxWeight = Double.POSITIVE_INFINITY;

	public static void optimize(double timeToRun) {
		double start = System.currentTimeMillis() / 1000.0;
		
		optimal = new TrussState();
		
		shelf = Main.supportA.y;

		if(Main.supportA.x < Main.supportB.x) {
			min = Main.supportA.x;
			max = Main.supportB.x;
		} else {
			max = Main.supportA.x;
			min = Main.supportB.x;
		}
		
		//check and populate starting truss
		if(!isValid())
			throw new InvalidTrussException("Starting truss is not valid");
		
		optimal.weight = tmpWeight;
		
		TrussSolver.solve();
		double[] LF = new double[Main.BEAMS.size()];
		double maxLoadFactor = 0;
		
		for(int i = 0; i < Main.BEAMS.size(); i++) {
			if(TrussSolver.beamLoads[i] < 0) {
				//compression
				LF[i] = TrussSolver.beamLoads[i] / -Main.BEAMS.get(i).maxCompressionLoad;
			} else {
				//tension
				LF[i] = TrussSolver.beamLoads[i] / Main.BEAMS.get(i).maxTensionLoad;
			}
			
			maxLoadFactor = Math.max(maxLoadFactor, LF[i]);
		}
		
		optimal.maxWeight = 1 / maxLoadFactor;
		optimal.score = calculateScore(optimal.weight, optimal.maxWeight);
		
		System.out.printf("Initial score %.4f\n", optimal.score);
		
		double vary = INITIAL_VARY;
		boolean progress = false;
		while(System.currentTimeMillis() / 1000.0 - start < timeToRun) {
			//set the POINTS to optimal varied a bit, test, if better set optimal and start again
			
			if(progress) {
				progress = false;
				vary = INITIAL_VARY;
			} else {
				vary *= 0.75;
			}
			
			if(vary < 0.1)
				vary = 0.1;
			
			for(int i = 0; i < Main.POINTS.size(); i++) {
				optimal.reset();
				
				Point p = Main.POINTS.get(i);
				
				if(p == Main.load || p == Main.supportA || p == Main.supportB)
					continue;
				
				p.x += (-1 + Math.random() * 2) * vary;
				p.y += (-1 + Math.random() * 2) * vary;
				
				if(!isValid())
					continue;
				
				TrussSolver.solve();
				LF = new double[Main.BEAMS.size()];
				maxLoadFactor = 0;
				
				for(int j = 0; j < Main.BEAMS.size(); j++) {
					if(TrussSolver.beamLoads[j] < 0) {
						//compression
						LF[j] = TrussSolver.beamLoads[j] / -Main.BEAMS.get(j).maxCompressionLoad;
					} else {
						//tension
						LF[j] = TrussSolver.beamLoads[j] / Main.BEAMS.get(j).maxTensionLoad;
					}
					
					maxLoadFactor = Math.max(maxLoadFactor, LF[j]);
				}
				
				double loadTmp = 1 / maxLoadFactor;
				double scoreTmp = calculateScore(tmpWeight, loadTmp);
				
				if(scoreTmp > optimal.score) {
					optimal.set();
					optimal.score = scoreTmp;
					optimal.maxWeight = loadTmp;
					optimal.weight = tmpWeight;
					System.out.println("New score: " + scoreTmp);
					progress = true;
				}
			}
		}
		
		optimal.reset();
	}

	private static void initialCalc() {
		for(Beam beam : Main.BEAMS)
			beam.recalc();
	}

	static double tmpWeight;
	private static boolean isValid() {
		for(Point point : Main.POINTS) {
			if(point.y < shelf - TrussOptimizer.MAX_DEPTH || (point.y < shelf && (point.x < min || point.x > max)))
				return false;
		}

		initialCalc();

		double cost = 0;
		for(Beam beam : Main.BEAMS) {
			if(beam.length > MAX_LENGTH)
				return false;
			
			cost += beam.cost;
		}
		
		if(cost > maxWeight)
			return false;
		
		tmpWeight = cost;
		
		return true;
	}
	
	private static double calculateScore(double weight, double load) {
		switch(metric) {
			case EFFICIENCY:
				return load / weight;
			case LOAD_CARRIED:
				return load;
			case WEIGHT_OF_TRUSS:
				return 1 / weight;
		}
		
		System.exit(-42);
		return 0;
	}
}