package spdsim;
import java.util.List;

import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;
import java.util.ArrayList;
import processing.core.*;

public class Environment implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public enum DIMENSION {TWO, THREE;}
		
	private DIMENSION dim;
	
	private Collider collider;
	private Boundaries bounds;
	private Units units;
	private EVisualizer visual;
	private Integrator integ;
		
	private List<Particle> particles;
	private List<Particle> selected;
	private transient List<Interaction> interactions;
	private transient List<ExternalForce> externals;
	private List<Spring> joints;
	private Map<String, List<Particle>> groups;
	private Map<String, Particle> particulars;
	private Map<String, EnvHolder> backups;
				
	public Environment(PApplet parent) {
		
		particles = new ArrayList<Particle>();
		interactions = new ArrayList<Interaction>();
		externals = new ArrayList<ExternalForce>();
		joints = new ArrayList<Spring>();
		selected = new ArrayList<Particle>();
		groups = new HashMap<String, List<Particle>>();
		particulars = new HashMap<String, Particle>();
		
		visual = new EVisualizer(this, parent);
		collider = new Collider(this, 1, 1);
		bounds = new Boundaries(this, parent.width, parent.height, parent.height, 1, 1);
		units = new Units(this, "m", "s", "kg", "C");
		integ = new Integrator(0.001f, this);
		dim = DIMENSION.TWO;
		
		backups = new HashMap<String, EnvHolder>();
		backups.put("main", new EnvHolder());
		
		Units.initUnits();
	}
	
	public Particle atRandomPos(float vx, float vy, float vz, float md, float cd, float rd) {
		return new Particle(visual.getParent(), visual.getParent().random(bounds.getLeftEdge(), bounds.getRightEdge()), visual.getParent().random(bounds.getBottomEdge(), bounds.getRightEdge()),
				visual.getParent().random(bounds.getLowerEdge(), bounds.getUpperEdge()), vx, vy, vx, md, cd, rd);
	}
	public Particle atRandomPos(float vx, float vy, float md, float cd, float rd) {
		return new Particle(visual.getParent(), visual.getParent().random(bounds.getLeftEdge(), bounds.getRightEdge()), visual.getParent().random(bounds.getBottomEdge(), bounds.getRightEdge()), vx, vy, md, cd, rd);
	}
	public Particle atRandomPos(float md, float cd, float rd) {return atRandomPos(0, 0, md, cd, rd);}
	
	public Particle atRandomVel(float x, float y, float z, float md, float cd, float rd, float vMax) {
		return new Particle(visual.getParent(), x, y, z, PVector.random3D().setMag(visual.getParent().random(vMax)), md, cd, rd);
	}
	public Particle atRandomVel(float x, float y, float md, float cd, float rd, float vMax) {
		return new Particle(visual.getParent(), x, y, PVector.random2D().setMag(visual.getParent().random(vMax)), md, cd, rd);
	}
	public Particle atRandomVel(float md, float cd, float rd, float vMax) {return atRandomVel(0, 0, md, cd, rd, vMax);}
	
	public Environment saveConfig(String id) {
		for (Particle p : particles) p.saveConfig(id);
		if (backups.containsKey(id)) backups.get(id).smallSave(this);
		else backups.put(id, new EnvHolder().smallSave(this));
		return this;
	}
	public Environment saveConfig() {return saveConfig("main");}
	
	public Environment saveEnv(String id) {
		if (backups.containsKey(id))
			backups.get(id).bigSave(this);
		else
			backups.put(id, new EnvHolder().bigSave(this));
		return this;
	}
	public Environment saveEnv() {return saveEnv("main");}
	
	public Environment loadConfig(String id) {
		if (!backups.containsKey(id)) throw new RuntimeException("No id found!");
		for (Particle p : particles) p.loadConfig(id);
		backups.get(id).smallLoad(this);
		return this;
	}
	public Environment loadConfig() {return loadConfig("main");}
	public Environment loadEnv(String id) {
		if (!backups.containsKey(id)) throw new RuntimeException("No id found!");
		backups.get(id).bigLoad(this);
		return this;
	}
	public Environment loadEnv() {return loadEnv("main");}
	
	public Environment savePositions() {for (Particle p : particles) p.getRecord().savePosition(); return this;}
	public Environment saveRelPositions(List<Particle> rel) {for (Particle p : particles) p.getRecord().saveRelPosition(rel); return this;}
	public Environment saveVelocityRecords() {for (Particle p : particles) p.getRecord().saveVelocity(); return this;}
	public Environment saveRelVelocityRecords(List<Particle> rel) {for (Particle p : particles) p.getRecord().saveRelVelocity(rel); return this;}
	public Environment saveEnergyRecords() {for (Particle p : particles) p.getRecord().saveEnergy(); return this;}
	public Environment saveRelEnergyRecords(List<Particle> rel) {for (Particle p : particles) p.getRecord().saveRelEnergy(rel); return this;}
	public Environment saveRecords() {for (Particle p : particles) p.getRecord().save(); return this;}
	public Environment saveRelRecords(List<Particle> rel) {for (Particle p : particles) p.getRecord().saveRel(rel); return this;}
	
	public Environment moveOrigin(PVector center) {for (Particle p : particles) p.getPos().sub(center); bounds.getConstrainPos().sub(center); return this;}
	
	public PVector transform(float x, float y, float z) {return Physics.transform(x, y, z, visual.getTranslate(), visual.getScaling());}
	public PVector transform(float x, float y) {return Physics.transform(x, y, visual.getTranslate(), visual.getScaling());}
	public PVector transform(PVector vec) {return Physics.transform(vec.x, vec.y, vec.z, visual.getTranslate(), visual.getScaling());}
	public PVector invTransform(float x, float y, float z) {return Physics.invTransform(x, y, z, visual.getTranslate(), visual.getScaling());}
	public PVector invTransform(float x, float y) {return Physics.invTransform(x, y, visual.getTranslate(), visual.getScaling());}
	public PVector invTransform(PVector vec) {return Physics.invTransform(vec.x, vec.y, vec.z, visual.getTranslate(), visual.getScaling());}
	
	public static void implement(Particle p, Interaction inter) {p.add(inter); inter.add(p);}
	public static void implement(List<Particle> pts, Interaction inter) {for (Particle p : pts) implement(p, inter);}
	public static void implement(Particle p, ExternalForce ext) {p.add(ext); ext.add(p);}
	public static void implement(List<Particle> pts, ExternalForce ext) {for (Particle p : pts) implement(p, ext);}

	public Environment implement(Interaction inter) {implement(particles, inter); return this;}
	public Environment implement(ExternalForce ext) {implement(particles, ext); return this;}
	public Environment implementAll(Particle p) {for (Interaction inter : interactions) implement(p, inter); for (ExternalForce ext : externals) implement(p, ext); return this;}
	public Environment implementAll() {for (Interaction inter : interactions) implement(inter); for(ExternalForce ext : externals) implement(ext); return this;}
	public static void neglect(Particle p, Interaction inter) {p.remove(inter); inter.remove(p);}
	public static void neglect(List<Particle> pts, Interaction inter) {for (Particle p : pts) neglect(p, inter);}
	public static void neglect(Particle p, ExternalForce ext) {p.remove(ext); ext.remove(p);}
	public static void neglect(List<Particle> pts, ExternalForce ext) {for (Particle p : pts) neglect(p, ext);}
	public Environment neglect(Interaction inter) {neglect(particles, inter); return this;}
	public Environment neglect(ExternalForce ext) {neglect(particles, ext); return this;}
	public Environment neglectAll(Particle p) {for (Interaction inter : interactions) neglect(p, inter); for (ExternalForce ext : externals) neglect(p, ext); return this;}
	public Environment neglectAll() {for (Interaction inter : interactions) neglect(inter); for (ExternalForce ext : externals) neglect(ext); clearJoints(); return this;}
	
	public Particle getFirst() {if (isEmpty()) return null; return particles.get(0);}
	public Particle getLast() {if (isEmpty()) return null; return particles.get(particles.size() - 1);}
	public Particle getParticle(String label) {return particulars.get(label);}
	public Integrator getIntegrator() {return integ;}
	public Interaction getInteraction(String id) {for (Interaction inter : interactions) if (inter.getId().equals(id)) return inter; return null;}
	public ExternalForce getExternal(String id) {for (ExternalForce ext : externals) if (ext.getId().equals(id)) return ext; return null;}
	public List<List<PVector>> getPositionRecords() {List<List<PVector>> res = new ArrayList<List<PVector>>();
	for (Particle p : particles) res.add(p.getRecord().getPositions()); return res;}
	public List<List<PVector>> getVelocityRecords() {List<List<PVector>> res = new ArrayList<List<PVector>>();
	for (Particle p : particles) res.add(p.getRecord().getVelocities()); return res;}
	public List<List<Float>> getEnergyRecords() {List<List<Float>> res = new ArrayList<List<Float>>();
	for (Particle p : particles) res.add(p.getRecord().getEnergies()); return res;}
	public List<PVector> getPositionRecord(int index) {return particles.get(index).getRecord().getPositions();}
	public List<PVector> getVelocityRecord(int index) {return particles.get(index).getRecord().getVelocities();}
	public List<Float> getEnergyRecord(int index) {return particles.get(index).getRecord().getEnergies();}
	public List<Particle> getParticles() {return particles;}
	public List<Particle> getSelected() {return selected;}
	public List<Interaction> getInteractions() {return interactions;}
	public List<ExternalForce> getExternals() {return externals;}
	public List<Spring> getJoints() {return joints;}
	public List<Particle> getGroup(String label) {return groups.get(label);}
	public Map<String, List<Particle>> getGroups() {return groups;}
	public Map<String, Particle> getParticulars() {return particulars;}
	public Map<String, EnvHolder> getBackups() {return backups;}
	public DIMENSION getDim() {return dim;}
	public Boundaries getBoundaries() {return bounds;}
	public Collider getCollider() {return collider;}
	public Units getUnits() {return units;}
	public EnvHolder getBackup(String id) {return backups.get(id);}
	public EVisualizer getVisualizer() {return visual;}
	public boolean isEmpty() {return particles.isEmpty();}
	public boolean contains(Particle p) {return particles.contains(p);}
	public boolean contains(Interaction inter) {return interactions.contains(inter);}
	public boolean contains(ExternalForce ext) {return externals.contains(ext);}
	public boolean contains(Spring s) {return joints.contains(s);}
	public boolean isSelected(Particle p) {return selected.contains(p);}
	public boolean areAllSelected(List<Particle> pts) {for (Particle p : pts) if (!isSelected(p)) return false; return true;}
	public boolean areAllSelected() {return areAllSelected(particles);}
	public boolean hasBackup(String id) {return backups.containsKey(id);}
	public boolean overlaps(Particle p) {
		for (Particle other : particles)
			if (p != other && p.overlaps(other))
				return true;
		return false;
	}
	public Environment setIntegrator(Integrator integ) {this.integ = integ; return this;}
	public Environment setBackups(Map<String, EnvHolder> bckp) {backups = bckp; return this;}
	public Environment setParticles(List<Particle> pts) {particles = pts; return this;}
	public Environment setInteractions(List<Interaction> inters) {interactions = inters; return this;}
	public Environment setExternals(List<ExternalForce> exts) {externals = exts; return this;}
	public Environment setParticulars(Map<String, Particle> p) {particulars = p; return this;}
	public Environment setParticle(String label, Particle p) {particulars.put(label, p); return this;}
	public Environment removeBackup(String id) {for (Particle p : particles) p.removeBackup(id); backups.remove(id); return this;}
	public Environment clearBackups() {for (Particle p : particles) p.clearBackups(); backups.clear(); return this;}
	public Environment removeParticle(String label) {particulars.remove(label); return this;}
	public Environment clearParticulars() {particulars.clear(); return this;}
	public Environment setGroups(Map<String, List<Particle>> cg) {groups = cg; return this;}
	public Environment setGroup(String label, List<Particle> pts) {groups.put(label, pts); return this;}
	public Environment setGroup(String label) {return setGroup(label, new ArrayList<Particle>());}
	public Environment removeGroup(String label) {groups.remove(label); return this;}
	public Environment removeGroups() {groups.clear(); return this;}
	public Environment removeFromGroups(Particle p) {for (Map.Entry<String, List<Particle>> entry : groups.entrySet()) groups.get(entry.getKey()).remove(p); return this;}
	public Environment clearGroup(String label) {groups.get(label).clear(); return this;}
	public Environment clearGroups() {for (Map.Entry<String, List<Particle>> entry : groups.entrySet()) groups.get(entry.getKey()).clear(); return this;}
	public Environment setDim(DIMENSION dim) {this.dim = dim; for (Particle p : particles) p.setDim(dim); return this;}
	public Environment setBoundaries(Boundaries b) {bounds = b; return this;}
	public Environment setVisualizer(EVisualizer ev) {visual = ev; return this;}
	public Environment setCollider(Collider c) {collider = c; return this;}
	public Environment setUnits(Units u) {units = u; return this;}
	public Environment select(Particle p) {if (!isSelected(p)) selected.add(p); p.setSelected(true); return this;}
	public Environment select(List<Particle> pts) {for (Particle p : pts) select(p); return this;}
	public Environment select(float x, float y, float z) {for (Particle p : particles) if (p.isWithin(x, y, z)) {select(p); return this;} return this;}
	public Environment select(float x, float y) {for (Particle p : particles) if (p.isWithin(x, y)) {select(p); return this;} return this;}
	public Environment select(PVector pos) {for (Particle p : particles) if (p.isWithin(pos)) {select(p); return this;} return this;}
	public Environment unSelect(Particle p) {selected.remove(p); p.setSelected(false); return this;}
	public Environment unSelect(List<Particle> pts) {for (Particle p : pts) unSelect(p); return this;}
	public Environment unSelect(float x, float y, float z) {for (Particle p : particles) if (p.isWithin(x, y, z)) {unSelect(p); return this;} return this;}
	public Environment unSelect(float x, float y) {for (Particle p : particles) if (p.isWithin(x, y)) {unSelect(p); return this;} return this;}
	public Environment unSelect(PVector pos) {for (Particle p : particles) if (p.isWithin(pos)) {unSelect(p); return this;} return this;}
	public Environment switchSelect(Particle p) {if (isSelected(p)) selected.remove(p); else selected.add(p); p.setSelected(!p.isSelected()); return this;}
	public Environment switchSelect(List<Particle> pts) {for (Particle p : pts) switchSelect(p); return this;}
	public Environment switchSelect(float x, float y, float z) {for (Particle p : particles) if (p.isWithin(x, y, z)) {switchSelect(p); return this;} return this;}
	public Environment switchSelect(float x, float y) {for (Particle p : particles) if (p.isWithin(x, y)) {switchSelect(p); return this;} return this;}
	public Environment switchSelect(PVector pos) {for (Particle p : particles) if (p.isWithin(pos)) {switchSelect(p); return this;} return this;}
	public Environment clearSelected() {for (Particle p : selected) p.setSelected(false); selected.clear(); return this;}
	public Environment clearPositionRecords() {for (Particle p : particles) p.getRecord().clearPositions(); return this;}
	public Environment clearVelocityRecords() {for (Particle p : particles) p.getRecord().clearVelocities(); return this;}
	public Environment clearEnergyRecords() {for (Particle p : particles) p.getRecord().clearEnergies(); return this;}
	public Environment clearRecords() {for (Particle p : particles) p.getRecord().clear(); return this;}
	
	public Environment add(Particle p) {
		
		if (contains(p))
			return this;
		
		p.setDim(dim);
		for (Interaction inter : interactions)
			if (inter.includedInAddition())
				implement(p, inter);
		for (ExternalForce ext : externals)
			if (ext.includedInAddition())
				implement(p, ext);
		particles.add(p);
		p.getVisualizer().setTranslate(visual.getTranslate()).setScaling(visual.getScaling());
		return this;
	}
	
	public Environment add(Interaction inter) {if (!contains(inter)) interactions.add(inter); return this;}
	public Environment add(ExternalForce ext) {if (!contains(ext)) externals.add(ext); return this;}
	public Environment add(Spring s) {
		add(s.getFirst()).add(s.getSecond());
		if (!contains(s))
			joints.add(s.setLengthUnitFactor(units.getLengthUnitScale()));
		return this;
	}
	public Environment add(List<Particle> pts) {for (Particle p : pts) add(p); return this;}
	public Environment add(SoftBody sb) {
		add(sb.getLinealBody());
		for (Spring s : sb.getJoints())
			add(s);
		return this;
	}
	
	public Environment remove(Particle p, boolean remFG) {
		
		for (int i = p.getInteractions().size() - 1; i >= 0; i--) neglect(p, p.getInteractions().get(i));
		for (int i = p.getExternals().size() - 1; i >= 0; i--) neglect(p, p.getExternals().get(i));
		for (int i = p.getJoints().size() - 1; i >= 0; i--) remove(p.getJoints().get(i));
		unSelect(p);
		if (remFG) removeFromGroups(p);
		particles.remove(p);
		return this;
	}
	public Environment remove(Particle p) {return remove(p, true);}
	public Environment remove(int index) {return remove(particles.get(index));}
	public Environment remove(Interaction inter) {neglect(inter); interactions.remove(inter); return this;}
	public Environment remove(ExternalForce ext) {neglect(ext); externals.remove(ext); return this;}
	public Environment remove(Spring s) {joints.remove(s.detach()); return this;}
	public Environment remove(List<Particle> pts) {for (int i = pts.size() - 1; i >= 0; i--) remove(pts.get(i)); return this;}
	public Environment clear() {
		neglectAll();
		particles.clear();
		return this;
	}
	public Environment clearInteractions() {for (int i = interactions.size() - 1; i >= 0; i--) remove(interactions.get(i)); return this;}
	public Environment clearExternals() {for (int i = externals.size() - 1; i >= 0; i--) remove(externals.get(i)); return this;}
	public Environment clearJoints() {for (int i = joints.size() - 1; i >= 0; i--) remove(joints.get(i)); return this;}
}
