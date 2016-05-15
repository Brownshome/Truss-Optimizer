
public enum BeamType {
	TWO(48, 2, 2),
	TWO_PLUS(156, 2, 2.25),
	TWO_PLUS_PLUS(243, 2, 2.5),
	SINGLE(6, 1, 1),
	I_BEAM(249, 2, 3),
	BOX_BEAM(533, 2, 4),
	THREE(162, 3, 3),
	THREE_PLUS(365, 3, 3.5);
	
	double tensionStrength;
	double compressionStrength;
	double cost;
	
	BeamType(double momentOfArea, double tensionMult, double cost) {
		compressionStrength = Beam.COMPRESSION_PER_MM2 * momentOfArea;
		tensionStrength = Beam.TENSION_SINGLE * tensionMult;
		this.cost = cost / Beam.LENGTH;
	}
}