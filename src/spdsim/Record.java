package spdsim;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import processing.core.PVector;

public class Record implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Particle p;
	private PVector prevIntegPos, prevDispPos;
	private List<PVector> posRecord, velRecord;
	private List<Float> energyRecord;
	private int memory;
	private boolean limMemory;
	
	public Record(Particle p, int memory) {
		this.p = p;
		prevIntegPos = p.getPos().copy();
		prevDispPos = p.getPos().copy();
		posRecord = new ArrayList<PVector>();
		velRecord = new ArrayList<PVector>();
		energyRecord = new ArrayList<Float>();
		this.memory = memory;
		limMemory = false;
	}
	
	public Record saveIntegPos() {prevIntegPos = p.getPos().copy(); return this;}
	public Record saveDispPos() {prevDispPos = p.getPos().copy(); return this;}
	
	public Record savePosition() {posRecord.add(p.getPos().copy()); while (posRecord.size() > memory && limMemory) posRecord.remove(0); return this;}
	public Record saveRelPosition(List<Particle> rel) {posRecord.add(p.getRelPos(rel)); while (posRecord.size() > memory && limMemory) posRecord.remove(0); return this;}
	public Record saveVelocity() {velRecord.add(p.getVel().copy()); while (velRecord.size() > memory && limMemory) velRecord.remove(0); return this;}
	public Record saveRelVelocity(List<Particle> rel) {velRecord.add(p.getRelVel(rel)); while (velRecord.size() > memory && limMemory) velRecord.remove(0); return this;}
	public Record saveEnergy() {energyRecord.add(p.getEnergy()); while (energyRecord.size() > memory && limMemory) energyRecord.remove(0); return this;}
	public Record saveRelEnergy(List<Particle> rel) {energyRecord.add(p.getRelEnergy(rel)); while (energyRecord.size() > memory && limMemory) energyRecord.remove(0); return this;}
	public Record save() {savePosition(); saveVelocity(); return saveEnergy();}
	public Record saveRel(List<Particle> rel) {saveRelPosition(rel); saveRelVelocity(rel); return saveRelEnergy(rel);}
	public Record clearPositions() {posRecord.clear(); return this;}
	public Record clearVelocities() {velRecord.clear(); return this;}
	public Record clearEnergies() {energyRecord.clear(); return this;}
	public Record clear() {clearPositions(); clearVelocities(); return clearEnergies();}
	public Record unlimitedMemory() {limMemory = false; return this;}
	public Record limitMemory(int mem) {limMemory = true; memory = mem; return this;}
	public Record limitMemory() {return limitMemory(1000);}
	
	public boolean hasUnlimitedMemory() {return !limMemory;}
	public boolean hasLimitedMemory() {return limMemory;}
	
	public Particle getParticle() {return p;}
	public List<PVector> getPositions() {return posRecord;}
	public List<PVector> getVelocities() {return velRecord;}
	public List<Float> getEnergies() {return energyRecord;}
	public PVector getPrevIntegPos() {return prevIntegPos;}
	public PVector getPrevDispPos() {return prevDispPos;}
	public int getMemory() {return memory;}
	
	public Record setParticle(Particle p) {this.p = p; return this;}
	public Record setPrevIntegPos(float x, float y) {prevIntegPos.set(x, y); return this;}
	public Record setPrevIntegPos(float x, float y, float z) {prevIntegPos.set(x, y, z); return this;}
	public Record setPrevIntegPos(PVector pos) {this.prevIntegPos = pos; return this;}
	public Record setPrevDispPos(float x, float y) {prevDispPos.set(x, y); return this;}
	public Record setPrevDispPos(float x, float y, float z) {prevDispPos.set(x, y, z); return this;}
	public Record setPrevDispPos(PVector pos) {this.prevDispPos = pos; return this;}
	public Record setMemory(int mem) {memory = mem; return this;}
}
