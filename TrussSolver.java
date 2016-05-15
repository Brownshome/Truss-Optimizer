public class TrussSolver {
	/*static double forceA;
	static double forceB;*/

	static double[] beamLoads;
	
	static double[][] matrix;

	//solves using Gausian ellimination
	public static void solve() throws InvalidTrussException {
		/*double da = Main.load.x - Main.supportA.x;
		double db = Main.supportB.x - Main.load.x;
		double ds = Main.supportB.x - Main.supportA.x;

		forceA = db / ds;
		forceB = da / ds;

		//sanity checks
		if(!Double.isFinite(forceA) || !Double.isFinite(forceB) || forceA < 0 || forceB < 0)
			throw new InvalidTrussException("Support force out of bounds");*/

		//form matrix [row][column]
		int height = Main.POINTS.size() * 2;
		int width = Main.BEAMS.size() + 2; //not including the equals column

		matrix = new double[height][width + 1];
		int equals = width;
		int suppB = width - 1;
		int suppA = width - 2;

		for(int i = 0; i < Main.POINTS.size(); i++) {
			Point point = Main.POINTS.get(i);

			for(Beam beam : point.beams) {
				if(point == beam.a) {
					matrix[i * 2][beam.index] = beam.cos;
					matrix[i * 2 + 1][beam.index] = beam.sin;
				} else {
					matrix[i * 2][beam.index] = -beam.cos;
					matrix[i * 2 + 1][beam.index] = -beam.sin;
				}
			}

			if(point == Main.load)
				matrix[i * 2 + 1][equals] = 1;
			else if(point == Main.supportA)
				matrix[i * 2 + 1][suppA] = 1;
			else if(point == Main.supportB)
				matrix[i * 2 + 1][suppB] = 1;
		}

		try {
			for(int column = 0; column < width; column++) {
				//1. choose the best pivot in the column
				double max = 0;
				int row = -1;

				for(int r = column; r < height; r++) {
					if(Math.abs(matrix[r][column]) > max) {
						max = Math.abs(matrix[r][column]);
						row = r;
					}
				}

				//2. move it into position
				double[] tmp = matrix[row];
				matrix[row] = matrix[column];
				matrix[column] = tmp;
				
				//3. divide row
				double scalar = matrix[column][column];
				for(int x = 0; x < width + 1; x++)
					matrix[column][x] /= scalar;
				
				//4. zero other rows
				for(int y = 0; y < height; y++) {
					if(y == column)
						continue;
					
					scalar = -matrix[y][column];
					
					for(int x = 0; x < width + 1; x++)
						matrix[y][x] += matrix[column][x] * scalar;
				}
			}
		} catch(Exception e) {
			throw new InvalidTrussException("Failed to solve truss");
		}

		//the matrix should be in the correct form now
		
		beamLoads = new double[Main.BEAMS.size()];
		for(int i = 0; i < beamLoads.length; i++) {
			beamLoads[i] = matrix[i][equals];
		}
	}
}
