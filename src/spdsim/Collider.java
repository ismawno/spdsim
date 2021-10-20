package spdsim;

import processing.core.PVector;

import java.io.Serializable;
import spdsim.Environment.DIMENSION;

public class Collider implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public enum COLLISIONMODE {BOUNCE, MERGE;}
	private COLLISIONMODE cMode;
	private Environment ENV;
	private float elasticity, glide;
	
	public Collider(Environment ENV, float elasticity, float glide) {
		cMode = COLLISIONMODE.BOUNCE;
		this.ENV = ENV;
	}
	public Collider merge(Particle p1, Particle p2) {
		if (p1.getRadius() > p2.getRadius()) {
			p1.merge(p2);
			ENV.remove(p2);
		} else {
			p2.merge(p1);
			ENV.remove(p1);
		}
		return this;
	}
	public PVector collide2D(PVector V, PVector R) {return collide2D(V, R, elasticity, glide);}
	public PVector collide3D(PVector V, PVector R) {return collide3D(V, R, elasticity, glide);}
	public static PVector collide2D(PVector V, PVector R, float elasticity, float glide) {
		
		float e = elasticity;
		float g = glide;
		PVector result = new PVector();
		result.x = (V.x * (g * R.y * R.y - e * R.x * R.x) - (e + g) * R.x * R.y * V.y);
		result.y = (V.y * (g * R.x * R.x - e * R.y * R.y) - (e + g) * R.x * R.y * V.x);
		
		return result.div(R.magSq());
	}
	public static PVector collide3D(PVector V, PVector R, float elasticity, float glide) {
		
		float e = elasticity;
		float g = glide;
		PVector result = new PVector();
		
		result.x = V.x * (g * (R.y * R.y + R.z * R.z) - e * R.x * R.x)
				 - (V.y * R.y + V.z * R.z) * (g + e) * R.x;
		result.y = V.y * (g * (R.x * R.x + R.z * R.z) - e * R.y * R.y)
				 - (V.x * R.x + V.z * R.z) * (g + e) * R.y;
		result.z = V.z * (g * (R.x * R.x + R.y * R.y) - e * R.z * R.z)
				 - (V.x * R.x + V.y * R.y) * (g + e) * R.z;
		
		return result.div(R.magSq());
	}
	private float interpolate(PVector Pfi, PVector deltaP, float r1, float r2) {
		float sign = Math.signum(Pfi.dot(deltaP));
		float a = Pfi.magSq();
		float b = 2 * Pfi.dot(deltaP);
		float c = deltaP.magSq() - (r1 + r2) * (r1 + r2);
		return ( - b + sign * (float) Math.sqrt(b * b - 4 * a * c)) / (2 * a);
	}
	private Collider interpolateOrdered(Particle p1, Particle p2) {
		PVector Pfi1 = PVector.sub(p1.getPos(), p1.getRecord().getPrevIntegPos());
		PVector Pfi2 = PVector.sub(p2.getPos(), p2.getRecord().getPrevIntegPos());
		float m1 = Pfi1.magSq(), m2 = Pfi2.magSq();
		if (m1 < 0.001f && m2 < 0.001f) {p1.overrideOverlap(p2); return this;}
		if (m1 < 0.001f) {p2.getPos().sub(Pfi2.mult(interpolate(Pfi2, PVector.sub(p1.getPos(), p2.getPos()), p1.getRadius(), p2.getRadius()))); return this;}
		p1.getPos().sub(Pfi1.mult(interpolate(Pfi1, PVector.sub(p2.getPos(), p1.getPos()), p1.getRadius(), p2.getRadius())));
		return this;
	}
	public Collider collide(int id1, int id2, int[] numCol, float elasticity, float glide, boolean inter) {
		
		Particle p1 = numCol[id1] > numCol[id2] ? ENV.getParticles().get(id2) : ENV.getParticles().get(id1);
		Particle p2 = numCol[id1] > numCol[id2] ? ENV.getParticles().get(id1) : ENV.getParticles().get(id2);
		if (inter) interpolateOrdered(p1, p2);
		else p1.overrideOverlap(p2);
		
		PVector relV = PVector.sub(p2.getVel(), p1.getVel());
		PVector relPos = PVector.sub(p2.getPos(), p1.getPos());
		
		boolean two = ENV.getDim() == DIMENSION.TWO;
		PVector newRelV = PVector.sub(two ? collide2D(relV, relPos, elasticity, glide) : collide3D(relV, relPos, elasticity, glide), relV);
		
		if (!p1.isDynamic())
		    p2.getVel().add(newRelV);
		  else if (!p2.isDynamic())
		    p1.getVel().sub(newRelV);
		  else {

		    p2.getVel().add(PVector.mult(newRelV, p1.getMass() / (p1.getMass() + p2.getMass())));
		    p1.getVel().sub(PVector.mult(newRelV, p2.getMass() / (p1.getMass() + p2.getMass())));
		  }
		p1.onCollisionEnter(p2);
		p2.onCollisionEnter(p1);
		return this;
	}
	public Collider runCollisions(float elasticity, float glide, boolean inter) {
		int[] numCol = new int[ENV.getParticles().size()];
		if (cMode == COLLISIONMODE.BOUNCE)
			for (int i = 0; i < numCol.length; i++) {
				Particle p1 = ENV.getParticles().get(i);
				for (int j = i + 1; j < numCol.length; j++) {
					Particle p2 = ENV.getParticles().get(j);
					if (p1.overlaps(p2)) {
						if (p1.isDynamic()) numCol[i]++;
						else numCol[i] = Integer.MAX_VALUE;
						if (p2.isDynamic()) numCol[j]++;
						else numCol[j] = Integer.MAX_VALUE;
					}
				}
			}
		for (int i = ENV.getParticles().size() - 1; i >= 0; i--) {
			Particle p1 = ENV.getParticles().get(i);
			for (int j = i - 1; j >= 0; j--) {
				Particle p2 = ENV.getParticles().get(j);
				if (p1.overlaps(p2))
					if (cMode == COLLISIONMODE.MERGE) {
						merge(p1, p2);
						break;
					} else
						collide(p1.getMass() > p2.getMass() ? j : i, p1.getMass() > p2.getMass() ? i : j, numCol, elasticity, glide, inter);
			}
		}
		return this;
	}
	public Collider runCollisions() {return runCollisions(1, 1, false);}
	public Collider runCollisions(float elasticity, float glide) {return runCollisions(elasticity, glide, false);}
	public Collider runCollisions(boolean inter) {return runCollisions(elasticity, glide, inter);}
	
	public COLLISIONMODE getCollisionMode() {return cMode;}
	public Environment getEnvironmet() {return ENV;}
	public float getElasticity() {return elasticity;}
	public float getGlide() {return glide;}
	
	public Collider setCollisionMode(COLLISIONMODE c) {cMode = c; return this;}
	public Collider setEnvironment(Environment E) {ENV = E; return this;}
	public Collider setElasticity(float e) {elasticity = e; return this;}
	public Collider setGlide(float g) {glide = g; return this;}
}
