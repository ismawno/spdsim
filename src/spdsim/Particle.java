package spdsim;
import processing.core.*;

import tensors.Float.*;

import java.util.List;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Particle implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private List<Particle> copies;
	
	private transient List<Interaction> myInteractions;
	private transient List<ExternalForce> myExternals;
	private transient List<Spring> myJoints;

	
	private PVisualizer visual;
	private Record record;
	private PVector pos, vel;
	private String activeLabel;
	private float radius, mass, charge, massDens, chargeDens;
	Map<String, ParticleHolder> backups;
	
	private Vector k;
	
	private int color, strokeColor;
	private float strokeWeight;
	private boolean dynamic, selected, active, stroke;
	
	private Environment.DIMENSION dim;
	
	public Particle(PApplet parent, PVector pos, PVector vel, float massDens, float chargeDens, float radius) {
				
		myInteractions = new ArrayList<Interaction>();
		myExternals = new ArrayList<ExternalForce>();
		myJoints = new ArrayList<Spring>();
		copies = new ArrayList<Particle>();
		
		this.pos = pos;
		this.vel = vel;
		
		this.massDens = massDens;
		this.chargeDens = chargeDens;
		this.radius = radius;
		
		visual = new PVisualizer(this, parent);
		
		color = - 1;
		strokeColor = - 1;
		strokeWeight = 2;
		
		dynamic = true;
		selected = false;
		active = true;
		computeParametersDensity();
		
		record = new Record(this, 1000);
		setDim(Environment.DIMENSION.THREE);
		backups = new HashMap<String, ParticleHolder>();
		backups.put("main", new ParticleHolder().save(this));
	}
	public Particle(PApplet parent, PVector pos, float vx, float vy, float vz, float massDens, float chargeDens, float radius) {
		this(parent, pos, new PVector(vx, vy, vz), massDens, chargeDens, radius);
		setDim(Environment.DIMENSION.THREE);
	}
	public Particle(PApplet parent, PVector pos, float vx, float vy, float massDens, float chargeDens, float radius) {
		this(parent, pos, new PVector(vx, vy), massDens, chargeDens, radius);
		setDim(Environment.DIMENSION.TWO);
	}
	public Particle(PApplet parent, float x, float y, float z, PVector vel, float massDens, float chargeDens, float radius) {
		this(parent, new PVector(x, y, z), vel, massDens, chargeDens, radius);
		setDim(Environment.DIMENSION.THREE);
	}
	public Particle(PApplet parent, float x, float y, PVector vel, float massDens, float chargeDens, float radius) {
		this(parent, new PVector(x, y), vel, massDens, chargeDens, radius);
		setDim(Environment.DIMENSION.TWO);
	}
	public Particle(PApplet parent, float x, float y, float z, float vx, float vy, float vz, float massDens, float chargeDens, float radius) {
		this(parent, new PVector(x, y, z), new PVector(vx, vy, vz), massDens, chargeDens, radius);
		setDim(Environment.DIMENSION.THREE);
	}
	public Particle(PApplet parent, float x, float y, float vx, float vy, float massDens, float chargeDens, float radius) {
		this(parent, x, y, 0, vx, vy, 0, massDens, chargeDens, radius);
		setDim(Environment.DIMENSION.TWO);
	}
	
	public static Particle atZero(PApplet parent, float vx, float vy, float vz, float md, float cd, float rd) {
		return new Particle(parent, 0, 0, 0, vx, vy, vz, md, cd, rd);
	}
	public static Particle atZero(PApplet parent, float vx, float vy, float md, float cd, float rd) {
		return new Particle(parent, 0, 0, vx, vy, md, cd, rd);
	}
	public static Particle still(PApplet parent, float x, float y, float z, float md, float cd, float rd) {
		return new Particle(parent, x, y, z, 0, 0, 0, md, cd, rd);
	}
	public static Particle still(PApplet parent, float x, float y, float md, float cd, float rd) {
		return new Particle(parent, x, y, 0, 0, md, cd, rd);
	}
	public static Particle atZeroStill(PApplet parent, float md, float cd, float rd) {
		return new Particle(parent, 0, 0, 0, 0, md, cd, rd);
	}
	
	public Particle computeParametersDensity() {
		float volume = getVolume();
		mass =  massDens * volume;
	    charge = chargeDens * volume;
	    return this;
	}
	public Particle computeParametersValue() {
		float volume = getVolume();
		massDens = mass / volume;
		chargeDens = charge / volume;
		return this;
	}
	public static Particle unit(PApplet parent, float rd) {return new Particle(parent, 0, 0, 0, 0, 0, 0, 0).unit(rd);}
	public Particle unit(float rd) {
		mass = 1;
		charge = 1;
		radius = rd;
		return computeParametersValue();
	}
	
	public boolean overlaps(Particle p) {return pos.dist(p.getPos()) < radius + p.getRadius();}
	protected void onCollisionEnter(Particle p) {return;}
	protected void onBoundaryEnter() {return;}
	public boolean isWithin(float x, float y, float z) {return PApplet.dist(pos.x, pos.y, pos.z, x, y, z) < radius;}
	public boolean isWithin(float x, float y) {return PApplet.dist(pos.x, pos.y, x, y) < radius;}
	public boolean isWithin(PVector other) {return pos.dist(other) < radius;}
	
	public Particle overrideOverlap(Particle p) {
		
		PVector dist = PVector.sub(pos, p.getPos());
		float corrMag = radius + p.getRadius() - dist.mag();
		
		dist.setMag(corrMag);
		pos.add(dist);
		return this;
	}
	
	public Particle merge(Particle p) {
		
		if (dynamic) {
			List<Particle> both = new ArrayList<Particle>(2);
			both.add(this);
			both.add(p);
			vel = Physics.getCMVel(both);
		}
		
		float volume = getVolume();
		massDens = (mass + p.getMass()) / (volume + p.getVolume());
	    chargeDens = (charge + p.getCharge()) / (volume + p.getVolume());
	    radius = (float) Math.pow(radius * radius * radius + p.getRadius() * p.getRadius() * p.getRadius(), 1.0f / 3.0f);
	    return computeParametersDensity();
	}
	
	public Vector computeK(float dt, boolean forward, Vector K) {
		
		if (forward) forward(K);
		else backward(K);
		
		PVector accel = new PVector();
		for (Interaction inter : myInteractions)
			for (Particle p : inter.getParticles())
				if (p != this && isActive(p))
					accel.add(inter.acceleration(p, this));
		
		for (ExternalForce ext : myExternals)
			accel.add(ext.acceleration(this));
		
		for (Spring s : myJoints)
			accel.add(s.acceleration(this));
			
		Vector vec;
		if (dim == Environment.DIMENSION.TWO)
			vec = new Vector(new float[] {vel.x, vel.y, accel.x, accel.y});
		else
			vec = new Vector(new float[] {vel.x, vel.y, vel.z, accel.x, accel.y, accel.z});
		vec.mult(dt);
		
		if (forward) backward(K);
		else forward(K);
		return vec;
	}
	private Particle forward2D(Vector K) {
		
		pos.x += K.get(0);
		pos.y += K.get(1);
		vel.x += K.get(2);
		vel.y += K.get(3);
		return this;
	}
	
	private Particle forward3D(Vector K) {
		
		pos.x += K.get(0);
		pos.y += K.get(1);
		pos.z += K.get(2);
		vel.x += K.get(3);
		vel.y += K.get(4);
		vel.z += K.get(5);
		return this;
	}
	
	private Particle backward2D(Vector K) {
		
		pos.x -= K.get(0);
		pos.y -= K.get(1);
		vel.x -= K.get(2);
		vel.y -= K.get(3);
		return this;
	}
	
	private Particle backward3D(Vector K) {
		
		pos.x -= K.get(0);
		pos.y -= K.get(1);
		pos.z -= K.get(2);
		vel.x -= K.get(3);
		vel.y -= K.get(4);
		vel.z -= K.get(5);
		return this;
	}
	
	public Particle forward(Vector K) {if (dim == Environment.DIMENSION.TWO) return forward2D(K); return forward3D(K);}
	public Particle backward(Vector K) {if (dim == Environment.DIMENSION.TWO) return backward2D(K); return backward3D(K);}
	public Particle forward() {if (dynamic) return forward(k); return this;}
	public Particle backward() {if (dynamic) return backward(k); return this;}
	
	public Particle saveConfig(String id) {if (backups.containsKey(id)) backups.get(id).save(this); else backups.put(id, new ParticleHolder().save(this)); return this;}
	public Particle saveConfig() {return saveConfig("main");}
	public Particle loadConfig(String id) {if (!backups.containsKey(id)) throw new RuntimeException("No id found!"); backups.get(id).load(this); return this;}
	public Particle loadConfig() {return loadConfig("main");}
	
	public PVector getAccelField(float x, float y, float z) {
		PVector result = new PVector(0, 0, 0); for (Interaction inter : myInteractions) result.add(inter.accelField(this, x, y, z));
		return result;
	}
	public PVector getAccelField(float x, float y) {return getAccelField(x, y, 0);}
	public PVector getAccelField(PVector pos) {return getAccelField(pos.x, pos.y, pos.z);}
	public PVector getRelPos(List<Particle> pts) {return PVector.sub(pos, Physics.getCMPos(pts));}
	public PVector getRelVel(List<Particle> pts) {return PVector.sub(vel, Physics.getCMVel(pts));}
	public PVector getMomentum() {return PVector.mult(vel, mass);}
	public PVector getRelMomentum(List<Particle> pts) {return getRelVel(pts).mult(mass);}
	public PVector getAngularMomentum() {return pos.cross(getMomentum());}
	public PVector getRelAngularMomentum(List<Particle> pts) {return getRelPos(pts).cross(getRelMomentum(pts));}
	public float getMomentumMagnitude() {return mass * vel.mag();}
	public float getRelMomentumMagnitude(List<Particle> pts) {return mass * getRelVel(pts).mag();}
	public float getAngularMomentumMagnitude() {return pos.cross(vel).mag() * mass;}
	public float getRelAngularMomentumMagnitude(List<Particle> pts) {return getRelPos(pts).cross(getRelVel(pts)).mag() * mass;}
	public float getPotField(float x, float y, float z) {
		float result = 0; for (Interaction inter : myInteractions) result += inter.potField(this, x, y, z);
		return result;
	}
	public float getPotField(float x, float y) {return getPotField(x, y, 0);}
	public float getPotField(PVector pos) {return getPotField(pos.x, pos.y, pos.z);}
	public float getKineticEnergy() {return vel.magSq() * mass / 2;}
	public float getRelKineticEnergy(List<Particle> pts) {return getRelVel(pts).magSq() * mass / 2;}
	public float getInteractivePotentialEnergy() {
		float result = 0.0f;
		for (Interaction inter : myInteractions)
			for (Particle p : inter.getParticles())
				if (p != this) result += inter.potentialEnergy(p, this);
		return result;
	}
	public float getExternalPotentialEnergy() {
		float result = 0.0f;
		for (ExternalForce ext : myExternals) result += ext.potentialEnergy(this);
		return result;
	}
	public float getElasticPotentialEnergy() {
		float result = 0.0f;
		for (Spring s : myJoints) result += s.potentialEnergy();
		return result;
	}
	public float getPotentialEnergy() {return getInteractivePotentialEnergy() + getExternalPotentialEnergy() + getElasticPotentialEnergy();}
	public float getInteractiveEnergy() {return getInteractivePotentialEnergy() + getKineticEnergy();}
	public float getRelInteractiveEnergy(List<Particle> pts) {return getInteractivePotentialEnergy() + getRelKineticEnergy(pts);}
	public float getEnergy() {return getPotentialEnergy() + getKineticEnergy();}
	public float getRelEnergy(List<Particle> pts) {return getPotentialEnergy() + getRelKineticEnergy(pts);}
	
	public Particle blindCopy() {
		Particle p = new Particle(visual.getParent(), pos.copy(), vel.copy(), massDens, chargeDens, radius);
		p.getVisualizer().setTranslate(visual.getTranslate()).setScaling(visual.getScaling());
		for (PVector pos : record.getPositions()) p.getRecord().getPositions().add(pos.copy());
		for (PVector vel : record.getVelocities()) p.getRecord().getVelocities().add(vel.copy());
		for (Float e : record.getEnergies()) p.getRecord().getEnergies().add(e.floatValue());
		if (record.hasLimitedMemory()) p.getRecord().limitMemory(record.getMemory());
		p.getVisualizer().setTrail(visual.hasTrail());
		return p.setDim(dim).setDynamic(dynamic).setActive(active).setColor(color).computeParametersDensity();
	}
	public Particle copy() {
		Particle p = blindCopy().addCopy(this);
		addCopy(p);
		return p;
	}
	
	public List<Interaction> getInteractions() {return myInteractions;}
	public List<ExternalForce> getExternals() {return myExternals;}
	public List<Spring> getJoints() {return myJoints;}
	public List<Particle> getCopies() {return copies;}
	public Map<String, ParticleHolder> getBackups() {return backups;}
	public Record getRecord() {return record;}
	public PVisualizer getVisualizer() {return visual;}
	public PVector getPos() {return pos;}
	public PVector getVel() {return vel;}
	public String getActiveLabel() {return activeLabel;}
	public Particle getLastCopy() {if (!hasCopies()) throw new RuntimeException("No copies!"); return copies.get(copies.size() - 1);}
	public Vector getK() {return k;}
	public ParticleHolder getBackup(String id) {return backups.get(id);}
	public Environment.DIMENSION getDim() {return dim;}
	public int getColor() {return color;}
	public int getStrokeColor() {return strokeColor;}
	public float getStrokeWeight() {return strokeWeight;}
	public float getMass() {return mass;}
	public float getCharge() {return charge;}
	public float getMassDens() {return massDens;}
	public float getChargeDens() {return chargeDens;}
	public float getRadius() {return radius;}
	public float getVolume() {return 4 * (float) Math.PI * radius * radius * radius / 3.0f;}
	public boolean isDynamic() {return dynamic;}
	public boolean isSelected() {return selected;}
	public boolean isActive() {return active;}
	public boolean isActive(Particle p) {if (active || p.isActive()) return true; return !p.getActiveLabel().equals(activeLabel);}
	public boolean isIn(Interaction inter) {return myInteractions.contains(inter);}
	public boolean isIn(ExternalForce ext) {return myExternals.contains(ext);}
	public boolean isIn(Spring s) {return myJoints.contains(s);}
	public boolean isACopyOf(Particle p) {return copies.contains(p);}
	public boolean hasCopies() {return !copies.isEmpty();}
	public boolean hasBackup(String id) {return backups.containsKey(id);}
	public boolean hasStroke() {return stroke;}
	
	public Particle setBackups(Map<String, ParticleHolder> bckp) {backups = bckp; return this;}
	public Particle setRecord(Record r) {record = r; return this;}
	public Particle setVisualizer(PVisualizer pv) {visual = pv; return this;}
	public Particle setPos(float x, float y) {pos.set(x, y); return this;}
	public Particle setPos(float x, float y, float z) {pos.set(x, y, z); return this;}
	public Particle setPos(PVector pos) {this.pos = pos; return this;}
	public Particle setVel(float vx, float vy) {vel.set(vx, vy); return this;}
	public Particle setVel(float vx, float vy, float vz) {vel.set(vx, vy, vz); return this;}
	public Particle setVel(PVector vel) {this.vel = vel; return this;}
	public Particle setK(Vector K) {
		if ((K.getLength() != 4 && K.getLength() != 6) || 
			(K.getLength() == 4 && dim != Environment.DIMENSION.TWO) || 
			(K.getLength() == 6 && dim != Environment.DIMENSION.THREE)) throw new RuntimeException("Wrong k dimension");
		k = K;
		return this;
	}
	public Particle setDim(Environment.DIMENSION nDim) {dim = nDim; if (dim == Environment.DIMENSION.THREE) k = new Vector(6); else k = new Vector(4); return this;}
	public Particle setK(float val) {k.setAll(val); return this;}
	public Particle setKUnsafe(Vector K) {k = K; return this;}
	public Particle setColor(int col) {color = col; return this;}
	public Particle setMass(float mass) {this.mass = mass; computeParametersValue(); return this;}
	public Particle setCharge(float charge) {this.charge = charge; computeParametersValue(); return this;}
	public Particle setMassDens(float massDens) {this.massDens = massDens; computeParametersDensity(); return this;}
	public Particle setChargeDens(float chargeDens) {this.chargeDens = chargeDens; computeParametersDensity(); return this;}
	public Particle setRadius(float radius) {this.radius = radius; computeParametersDensity(); return this;}
	public Particle setRadiusRaw(float radius) {this.radius = radius; return this;}
	public Particle setDynamic(boolean val) {dynamic = val; if (!val) setVel(0, 0, 0); return this;}
	public Particle setSelected(boolean val) {selected = val; return this;}
	public Particle setActive(boolean val) {active = val; return this;}
	public Particle add(Interaction inter) {if (!isIn(inter)) myInteractions.add(inter); return this;}
	public Particle add(ExternalForce ext) {if (!isIn(ext)) myExternals.add(ext); return this;}
	public Particle add(Spring s) {if (!isIn(s)) myJoints.add(s); return this;}
	public Particle remove(Interaction inter) {myInteractions.remove(inter); return this;}
	public Particle remove(ExternalForce ext) {myExternals.remove(ext); return this;}
	public Particle remove(Spring s) {myJoints.remove(s); return this;}
	public Particle removeBackup(String id) {backups.remove(id); return this;}
	public Particle clearBackups() {backups.clear(); return this;}
	public Particle addCopy(Particle p) {if (!copies.contains(p)) copies.add(p); return this;}
	public Particle removeCopy(Particle p) {copies.remove(p); return this;}
	public Particle removeCopies() {copies.clear(); return this;}
	public Particle setActiveLabel(String a) {activeLabel = a; return this;}
	public Particle stroke(int col) {stroke = true; strokeColor = col; return this;}
	public Particle strokeWeight(float w) {strokeWeight = w; return this;}
	public Particle noStroke() {stroke = false; return this;}
}

