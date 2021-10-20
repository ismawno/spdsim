package spdsim;

import java.util.List;

import java.util.ArrayList;
import processing.core.PVector;

public abstract class Interaction {
		
	private List<Particle> particles;
	private boolean includeAdded;
	private String id;
	
	private final Particle unit = Particle.unit(null, 1);
	
	public Interaction(String id) {
		
		particles = new ArrayList<Particle>();
		includeAdded = false;
		this.id = id;
	}
	public Interaction(String id, List<Particle> pts) {
		
		this(id);
		Environment.implement(pts, this);
	}
	public Interaction() {this("");}
	public Interaction(List<Particle> pts) {this("", pts);}
	
	public PVector accelField(Particle p, float x, float y, float z) {
		
		unit.setPos(x, y, z);
		return acceleration(p, unit);
	}
	public PVector accelField(Particle p, float x, float y) {return accelField(p, x, y, 0.0f);}
	public PVector accelField(Particle p, PVector pos) {return accelField(p, pos.x, pos.y, pos.z);}
	
	public float potField(Particle p, float x, float y, float z) {
		
		unit.setPos(x, y, z);
		return potentialEnergy(p, unit);
	}
	public float potField(Particle p, float x, float y) {return potField(p, x, y, 0);}
	public float potField(Particle p, PVector pos) {return potField(p, pos.x, pos.y, pos.z);}
	
	public float computeRawPotentialEnergyPairs(List<Particle> pts) {
		
		float result = 0;
		for (int i = 0; i < pts.size(); i++) {
			
			Particle p1 = pts.get(i);
			for (int j = i + 1; j < pts.size(); j++) {
				
				Particle p2 = pts.get(j);
				if (p1.isActive(p2))
					result += potentialEnergy(p1, p2);
			}
		}
		
		return result;
	}
	
	public float computePotentialEnergyPairs() {
		return computeRawPotentialEnergyPairs(particles);
	}
	
	public float computePotentialEnergyPairs(List<Particle> toCompute) {
		
		List<Particle> areInInter = new ArrayList<Particle>();
		for (Particle p : toCompute) if (p.isIn(this)) areInInter.add(p);
		if (areInInter.isEmpty()) return 0;
		
		float result = computeRawPotentialEnergyPairs(areInInter);
		for (Particle p1 : areInInter)
			for (Particle p2 : particles)
				if (!areInInter.contains(p2) && p1.isActive(p2))
					result += potentialEnergy(p1, p2);
		
		return result;
	}
	
	public Interaction prepareRemoval() {for (Particle p : particles) p.remove(this); return this;}
		
	public abstract PVector acceleration(Particle p1, Particle p2);
	public float potentialEnergy(Particle p1, Particle p2) {return 0;};
	
	public List<Particle> getParticles() {return particles;}
	public String getId() {return id;}
	public boolean includedInAddition() {return includeAdded;}
	public boolean contains(Particle p) {return particles.contains(p);}
	public final Particle getUnit() {return unit;}
	
	public Interaction add(Particle p) {if (!contains(p)) particles.add(p); return this;} //HAY QUE IMPLEMENTR UN ANADIR INTERACCION
	public Interaction add(List<Particle> pts) {for (Particle p : pts) add(p); return this;}
	public Interaction setParticles(List<Particle> pts) {particles.clear(); particles.addAll(pts); return this;}
	public Interaction remove(Particle p) {particles.remove(p); return this;}
	public Interaction remove(List<Particle> pts) {for (Particle p : pts) remove(p); return this;}
	public Interaction remove(int index) {particles.remove(index); return this;}
	public Interaction clear() {particles.clear(); return this;}
	public Interaction includeInAddition(boolean val) {includeAdded = val; return this;}
	
}
