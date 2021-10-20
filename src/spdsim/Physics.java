package spdsim;

import java.util.List;

import processing.core.PVector;
import java.io.Serializable;

public abstract class Physics implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static float getMass(List<Particle> pts) {
		float M = 0.0f;
		for (Particle p : pts) M += p.getMass();
		return M;
	}
	
	public static PVector transform(float x, float y, float z, PVector trans, float scaling) {return new PVector(x - trans.x, trans.y - y, z - trans.z).div(scaling);}
	public static PVector transform(float x, float y, PVector trans, float scaling) {return new PVector(x - trans.x, trans.y - y).div(scaling);}
	public static PVector transform(PVector vec, PVector trans, float scaling) {return transform(vec.x, vec.y, vec.z, trans, scaling);}
	public static PVector invTransform(float x, float y, float z, PVector trans, float scaling) {return new PVector(x * scaling + trans.x, trans.y - scaling * y, z * scaling + trans.z);}
	public static PVector invTransform(float x, float y, PVector trans, float scaling) {return new PVector(x * scaling + trans.x, trans.y - scaling * y);}
	public static PVector invTransform(PVector vec, PVector trans, float scaling) {return invTransform(vec.x, vec.y, vec.z, trans, scaling);}
	
	public static PVector getAccelField(List<Particle> pts, float x, float y, float z) {PVector result = new PVector(0, 0, 0); for (Particle p : pts) result.add(p.getAccelField(x, y, z)); return result;}
	public static PVector getAccelField(List<Particle> pts, float x, float y) {return getAccelField(pts, x, y, 0);}
	public static PVector getAccelField(List<Particle> pts, PVector pos) {return getAccelField(pts, pos.x, pos.y, pos.z);}
	
	public static float getPotField(List<Particle> pts, float x, float y, float z) {
		float result = 0; for (Particle p : pts) result += p.getPotField(x, y, z);
		return result;
	}
	public static float getPotField(List<Particle> pts, float x, float y) {return getPotField(pts, x, y, 0);}
	public static float getPotField(List<Particle> pts, PVector pos) {return getPotField(pts, pos.x, pos.y, pos.z);}
	
	public static PVector getCMPos(List<Particle> pts) {
		
		PVector cm = new PVector(0, 0, 0);
		for (Particle p : pts) cm.add(PVector.mult(p.getPos(), p.getMass()));
		return cm.div(getMass(pts));
	}
	
	public static PVector getCMVel(List<Particle> pts) {return getMomentum(pts).div(getMass(pts));}	
	public static PVector getMomentum(List<Particle> pts) {
		PVector cm = new PVector(0, 0, 0);
		for (Particle p : pts) cm.add(p.getMomentum());
		return cm;
	}
	public static PVector getRelMomentum(List<Particle> pts, List<Particle> rel) {
		PVector mom = new PVector(0, 0, 0);
		for (Particle p : pts) mom.add(p.getRelMomentum(rel));
		return mom;
	}	
	public static PVector getAngularMomentum(List<Particle> pts) {
		PVector result = new PVector(0, 0, 0);
		for (Particle p : pts) result.add(p.getAngularMomentum());
		return result;
	}
	public static PVector getRelAngularMomentum(List<Particle> pts, List<Particle> rel) {
		PVector result = new PVector(0, 0, 0);
		for (Particle p : pts) result.add(p.getRelAngularMomentum(rel));
		return result;
	}
	public static PVector getCMAngularMomentum(List<Particle> pts) {return getCMPos(pts).cross(getMomentum(pts));}
	public static float getMomentumMagnitude(List<Particle> pts) {
		float mag = 0;
		for (Particle p : pts) mag += p.getMomentumMagnitude();
		return mag;
	}
	public static float getRelMomentumMagnitude(List<Particle> pts, List<Particle> rel) {
		float mag = 0;
		for (Particle p : pts) mag += p.getRelMomentumMagnitude(rel);
		return mag;
	}
	public static float getAngularMomentumMagnitude(List<Particle> pts) {
		float mag = 0;
		for (Particle p : pts) mag += p.getAngularMomentumMagnitude();
		return mag;
	}
	public static float getRelAngularMomentumMagnitude(List<Particle> pts, List<Particle> rel) {
		float mag = 0;
		for (Particle p : pts) mag += p.getRelAngularMomentumMagnitude(rel);
		return mag;
	}	
	public static float getKineticEnergy(List<Particle> pts) {
		float result = 0;
		for (Particle p : pts) result += p.getKineticEnergy();
		return result;
	}
	public static float getRelKineticEnergy(List<Particle> pts, List<Particle> rel) {
		float result = 0;
		for (Particle p : pts) result += p.getRelKineticEnergy(rel);
		return result;
	}
	public static float getCMKineticEnergy(List<Particle> pts) {return getMass(pts) * getCMVel(pts).magSq() / 2;}	
	public static float getInteractivePotentialEnergy(Environment ENV, List<Particle> pts) {
		float result = 0;
		for (Interaction inter : ENV.getInteractions()) result += inter.computePotentialEnergyPairs(pts);
		return result;
	}
	public static float getInteractivePotentialEnergy(Environment ENV) {
		float result = 0;
		for (Interaction inter : ENV.getInteractions()) result += inter.computePotentialEnergyPairs();
		return result;
	}
	public static float getExternalPotentialEnergy(List<Particle> pts) {
		float result = 0;
		for (Particle p : pts) result += p.getExternalPotentialEnergy();
		return result;
	}
	public static float getElasticPotentialEnergy(Environment ENV, List<Particle> pts) {
		return Spring.potentialEnergyPairs(pts);
	}
	public static float getPotentialEnergy(Environment ENV, List<Particle> pts) {
		return getInteractivePotentialEnergy(ENV, pts) + getExternalPotentialEnergy(pts) + getElasticPotentialEnergy(ENV, pts);
	}	
	public static float getEnergy(Environment ENV, List<Particle> pts) {return getKineticEnergy(pts) + getPotentialEnergy(ENV, pts);}
	public static float getRelEnergy(Environment ENV, List<Particle> pts, List<Particle> rel) {return getRelKineticEnergy(pts, rel) + getPotentialEnergy(ENV, pts);}
	
	public static PVector[][] getAccelField2D(List<Particle> pts, Boundaries bounds, int detail) {
		
		if (detail < 2) throw new RuntimeException("Detail must be greater than 1");
		float left = bounds.getLeftEdge(), right = bounds.getRightEdge(), bottom = bounds.getBottomEdge(), top = bounds.getTopEdge();
		
		float dx = (right - left) / (detail - 1);
		float dy = (top - bottom) / (detail - 1);
		PVector[][] result = new PVector[detail][detail];
		for (int i = 0; i < detail; i++) {
			
			float x = left + dx * i;
			for (int j = 0; j < detail; j++) {
				
				float y = bottom + dy * j;
				result[i][j] = getAccelField(pts, x, y);
			}
		}
		return result;
	}
	public static PVector[][][] getAccelField3D(List<Particle> pts, Boundaries bounds, int detail) {
		
		if (detail < 2) throw new RuntimeException("Detail must be greater than 1");
		float left = bounds.getLeftEdge(), right = bounds.getRightEdge(), bottom = bounds.getBottomEdge(), top = bounds.getTopEdge();
		float lower = bounds.getLowerEdge(), upper = bounds.getUpperEdge();
		
		float dx = (right - left) / (detail - 1);
		float dy = (top - bottom) / (detail - 1);
		float dz = (upper - lower) / (detail - 1);
		PVector[][][] result = new PVector[detail][detail][detail];
		for (int i = 0; i < detail; i++) {
			
			float x = left + dx * i;
			for (int j = 0; j < detail; j++) {
				
				float y = bottom + dy * j;
				for (int k = 0; k < detail; k++) {
					
					float z = lower + dz * k;
					result[i][j][k] = getAccelField(pts, x, y, z);
				}
			}
		}
		return result;
	}
	public static float[][] getPotField2D(List<Particle> pts, Boundaries bounds, int detail) {
		
		if (detail < 2) throw new RuntimeException("Detail must be greater than 1");
		float left = bounds.getLeftEdge(), right = bounds.getRightEdge(), bottom = bounds.getBottomEdge(), top = bounds.getTopEdge();
		
		float dx = (right - left) / (detail - 1);
		float dy = (top - bottom) / (detail - 1);
		float[][] result = new float[detail][detail];
		for (int i = 0; i < detail; i++) {
			
			float x = left + dx * i;
			for (int j = 0; j < detail; j++) {
				
				float y = bottom + dy * j;
				result[i][j] = getPotField(pts, x, y);
			}
		}
		return result;
	}	
	public static float[][][] getPotField3D(List<Particle> pts, Boundaries bounds, int detail) {
		
		if (detail < 2) throw new RuntimeException("Detail must be greater than 1");
		float left = bounds.getLeftEdge(), right = bounds.getRightEdge(), bottom = bounds.getBottomEdge(), top = bounds.getTopEdge();
		float lower = bounds.getLowerEdge(), upper = bounds.getUpperEdge();
		
		float dx = (right - left) / (detail - 1);
		float dy = (top - bottom) / (detail - 1);
		float dz = (upper - lower) / (detail - 1);
		float[][][] result = new float[detail][detail][detail];
		for (int i = 0; i < detail; i++) {
			
			float x = left + dx * i;
			for (int j = 0; j < detail; j++) {
				
				float y = bottom + dy * j;
				for (int k = 0; k < detail; k++) {
					
					float z = lower + dz * k;
					result[i][j][k] = getPotField(pts, x, y, z);
				}
			}
		}
		return result;
		
	}
}
