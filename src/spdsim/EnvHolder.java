package spdsim;

import java.util.List;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import processing.core.PVector;

public class EnvHolder implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private PVector translate, constrainDim, constrainPos;
	private float scaling;
	
	private List<Particle> particles, selected;
	private List<List<Particle>> ints, exts;
	private List<Spring> joints;
	private Map<String, List<Particle>> groups;
	private Map<String, Particle> particulars;
	
	private String lengthUnit, timeUnit, massUnit, chargeUnit;
	
	public EnvHolder() {
		particles = new ArrayList<Particle>();
		selected = new ArrayList<Particle>();
		ints = new ArrayList<List<Particle>>();
		exts = new ArrayList<List<Particle>>();
		joints = new ArrayList<Spring>();
		groups = new HashMap<String, List<Particle>>();
		particulars = new HashMap<String, Particle>();
		translate = new PVector();
		constrainDim = new PVector();
		constrainPos = new PVector();
		scaling = 1;
		lengthUnit = "m";
		timeUnit = "s";
		massUnit = "kg";
		chargeUnit = "C";
	}
	public EnvHolder smallSave(Environment ENV) {
		translate = ENV.getVisualizer().getTranslate().copy();
		constrainDim = ENV.getBoundaries().getConstrainDim().copy();
		constrainPos = ENV.getBoundaries().getConstrainPos().copy();
		scaling = ENV.getVisualizer().getScaling();
		lengthUnit = ENV.getUnits().getLengthUnit();
		timeUnit = ENV.getUnits().getTimeUnit();
		massUnit = ENV.getUnits().getMassUnit();
		chargeUnit = ENV.getUnits().getChargeUnit();
		return this;
	}
	public EnvHolder smallLoad(Environment ENV) {
		ENV.getBoundaries().setConstrainDim(constrainDim.copy()).setConstrainPos(constrainPos.copy());
		ENV.getVisualizer().setTranslate(translate.copy()).setScaling(scaling);
		return this;
	}
	private static void switchData(List<Particle> savePts, List<Particle> saveSel,
			List<List<Particle>> saveInts, List<List<Particle>> saveExts, List<Spring> saveSpr,
			Map<String, List<Particle>> saveGroups, Map<String, Particle> saveParticulars,
			List<Particle> loadPts, List<Particle> loadSel,
			List<List<Particle>> loadInts, List<List<Particle>> loadExts, List<Spring> loadSpr,
			Map<String, List<Particle>> loadGroups, Map<String, Particle> loadParticulars) {
		
		savePts.clear();
		saveSel.clear();
		saveInts.clear();
		saveExts.clear();
		saveSpr.clear();
		saveGroups.clear();
		saveParticulars.clear();
		
		for (Particle p : loadPts) savePts.add(p.copy());
		for (Particle p : loadSel) saveSel.add(p.getLastCopy().setSelected(true));
		
		for (List<Particle> pts : loadInts) {
			List<Particle> cs = new ArrayList<Particle>();
			for (Particle p : pts)
				cs.add(p.getLastCopy());
			saveInts.add(cs);
		}
		for (List<Particle> pts : loadExts) {
			List<Particle> cs = new ArrayList<Particle>();
			for (Particle p : pts)
				cs.add(p.getLastCopy());
			saveExts.add(cs);
		}
		for (Spring s : loadSpr)
			saveSpr.add(new Spring(s.getFirst().getLastCopy(), s.getSecond().getLastCopy(), s.getStiffness(), s.getLength(), s.getTerms(), s.getDecay()).setColor(s.getColor()).setDampening(s.getDampening()));
		
		for (String key : loadGroups.keySet()) {
			saveGroups.put(key, new ArrayList<Particle>());
			for (Particle p : loadGroups.get(key))
				if (loadPts.contains(p))
					saveGroups.get(key).add(p.getLastCopy());
				else
					saveGroups.get(key).add(p.copy());
		}
		for (String key : loadParticulars.keySet()) {
			Particle p = loadParticulars.get(key);
			if (loadPts.contains(p))
				saveParticulars.put(key, p.getLastCopy());
			else
				saveParticulars.put(key, p.copy());
		}
	}
	public EnvHolder bigSave(Environment ENV) {
		
		smallSave(ENV);
		List<List<Particle>> loadInts = new ArrayList<List<Particle>>();
		List<List<Particle>> loadExts = new ArrayList<List<Particle>>();
		for (Interaction inter : ENV.getInteractions())
			loadInts.add(inter.getParticles());
		for (ExternalForce ext : ENV.getExternals())
			loadExts.add(ext.getParticles());

		switchData(particles, selected, ints, exts, joints, groups, particulars, ENV.getParticles(), ENV.getSelected(),
				loadInts, loadExts, ENV.getJoints(), ENV.getGroups(), ENV.getParticulars());

		return this;
	}
	public EnvHolder bigLoad(Environment ENV) {
		
		ENV.getUnits().setLengthUnit(lengthUnit).setTimeUnit(timeUnit).setMassUnit(massUnit).setChargeUnit(chargeUnit);
		List<List<Particle>> saveInts = new ArrayList<List<Particle>>();
		List<List<Particle>> saveExts = new ArrayList<List<Particle>>();
		ENV.neglectAll();
		switchData(ENV.getParticles(), ENV.getSelected(), saveInts, saveExts, ENV.getJoints(), ENV.getGroups(), ENV.getParticulars(),
				particles, selected, ints, exts, joints, groups, particulars);
		
		int i = 0;
		for (Interaction inter : ENV.getInteractions())
			Environment.implement(saveInts.get(i++), inter);
		i = 0;
		for (ExternalForce ext : ENV.getExternals())
			Environment.implement(saveExts.get(i++), ext);
		smallLoad(ENV);
		return this;
	}
	
	public EnvHolder copy() {
		EnvHolder cMH = new EnvHolder();
		
		switchData(cMH.getParticles(), cMH.getSelected(), cMH.getInterLists(), cMH.getExtsLists(), cMH.getJoints(), cMH.getGroups(),
				cMH.getParticulars(), particles, selected, ints, exts, joints, groups, particulars);
		
		cMH.setTranslate(translate.copy()).setConstrainDim(constrainDim.copy()).setConstrainPos(constrainPos.copy()).setLengthUnit(lengthUnit).
		setTimeUnit(timeUnit).setChargeUnit(chargeUnit).setScaling(scaling);
		return cMH;
	}
	
	public List<Particle> getParticles() {return particles;}
	public List<Particle> getSelected() {return selected;}
	public List<List<Particle>> getInterLists() {return ints;}
	public List<List<Particle>>	getExtsLists() {return exts;}
	public List<Spring> getJoints() {return joints;}
	public Map<String, List<Particle>> getGroups() {return groups;}
	public Map<String, Particle> getParticulars() {return particulars;}
	public PVector getTranslate() {return translate;}
	public PVector getConstrainDim() {return constrainDim;}
	public PVector getConstrainPos() {return constrainPos;}
	public String getLengthUnit() {return lengthUnit;}
	public String getTimeunit() {return timeUnit;}
	public String getMassUnit() {return massUnit;}
	public String getChargeUnit() {return chargeUnit;}
	public float getScaling() {return scaling;}
	
	public EnvHolder setParticles(List<Particle> pts) {particles = pts; return this;}
	public EnvHolder setSelected(List<Particle> pts) {selected = pts; return this;}
	public EnvHolder setInterLists(List<List<Particle>> lists) {ints = lists; return this;}
	public EnvHolder setExtsLists(List<List<Particle>> lists) {exts = lists; return this;}
	public EnvHolder setJoints(List<Spring> jts) {joints = jts; return this;}
	public EnvHolder setGroups(Map<String, List<Particle>> gr) {groups = gr; return this;}
	public EnvHolder setParticulars(Map<String, Particle> pts) {particulars = pts; return this;}
	public EnvHolder setTranslate(PVector trans) {translate = trans; return this;}
	public EnvHolder setConstrainDim(PVector dim) {constrainDim = dim; return this;}
	public EnvHolder setConstrainPos(PVector pos) {constrainPos = pos; return this;}
	public EnvHolder setLengthUnit(String l) {lengthUnit = l; return this;}
	public EnvHolder setTimeUnit(String t) {timeUnit = t; return this;}
	public EnvHolder setMassUnit(String m) {massUnit = m; return this;}
	public EnvHolder setChargeUnit(String c) {chargeUnit = c; return this;}
	public EnvHolder setScaling(float sc) {scaling = sc; return this;}
}
