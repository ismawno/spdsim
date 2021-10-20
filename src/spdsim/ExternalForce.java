package spdsim;

import java.util.ArrayList;
import java.util.List;
import processing.core.*;

public abstract class ExternalForce {
	
	private List<Particle> particles;
	private boolean includeAdded;
	private String id;
	
	private final Particle unit = Particle.unit(null, 1);
		
	public ExternalForce(String id) {
		
		particles = new ArrayList<Particle>();
		includeAdded = false;
		this.id = id;
	}
	public ExternalForce(String id, List<Particle> pts) {
		
		this(id);
		Environment.implement(pts, this);
	}
	public ExternalForce() {this("");}
	public ExternalForce(List<Particle> pts) {this("", pts);}
	
	PVector accelField(float x, float y, float z) {
		
		unit.setPos(x, y, z);
		return acceleration(unit);
	}
	
	float potField(float x, float y, float z) {
		
		unit.setPos(x, y, z);
		return potentialEnergy(unit);
	}
	
	public ExternalForce prepareRemoval() {for (Particle p : particles) p.remove(this); return this;}
	
	PVector accelField(PVector pos) {return accelField(pos.x, pos.y, pos.z);}
	float potField(PVector pos) {return potField(pos.x, pos.y, pos.z);}
	
	public abstract PVector acceleration(Particle p);
	public float potentialEnergy(Particle p) {return 0;}
	
	public List<Particle> getParticles() {return particles;}
	public String getId() {return id;}
	public boolean includedInAddition() {return includeAdded;}
	public boolean contains(Particle p) {return particles.contains(p);}
	public final Particle getUnit() {return unit;}
	
	public ExternalForce add(Particle p) {if (!contains(p)) particles.add(p); return this;}
	public ExternalForce add(List<Particle> pts) {for (Particle p : pts) add(p); return this;}
	public ExternalForce setParticles(List<Particle> pts) {particles.clear(); particles.addAll(pts); return this;}
	public ExternalForce remove(Particle p) {particles.remove(p); return this;}
	public ExternalForce remove(List<Particle> pts) {for (Particle p : pts) remove(p); return this;}
	public ExternalForce remove(int index) {particles.remove(index); return this;}
	public ExternalForce clear() {particles.clear(); return this;}
	public ExternalForce includeInAddition(boolean val) {includeAdded = val; return this;}
}
