package spdsim;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import processing.core.*;

public class Spring implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Particle p1, p2;
	private float stiffness, length, dampening, lengthUnitFactor, thickness;
	private int decay, terms, color;
	
	public Spring(Particle p1, Particle p2, float stiffness, float length, int terms, int decay) {
		this.p1 = p1;
		this.p2 = p2;
		
		if (p1 != null) p1.add(this);
		if (p2 != null) p2.add(this);
		
		this.stiffness = stiffness;
		this.length = length;
		this.decay = decay;
		this.terms = terms;
		this.dampening = 0;
		this.color = - 1;
		this.lengthUnitFactor = 1;
		this.thickness = 2;
	}
	public Spring(Particle p1, Particle p2, float stiffness, int terms, int decay) {
		this(p1, p2, stiffness, 0, terms, decay);
	}
	public Spring(float stiffness, int terms, int decay) {
		this(null, null, stiffness, 0, terms, decay);
	}
	public Spring(float stiffness, float length, int terms, int decay) {
		this(null, null, stiffness, length, terms, decay);
	}
	public Spring(Particle p1, Particle p2) {
		this(p1, p2, 1, 0, 1, 1);
	}
	public Spring() {
		this(null, null);
	}
	
	public PVector acceleration(Particle p) {
		if (!isReady())
			throw new RuntimeException("Spring has missing or no particles attached");
		if (p != p1 && p != p2)
			throw new RuntimeException("Particle does not belong to spring");
		
		Particle chosen = p == p1 ? p1 : p2;
		Particle other = p == p1 ? p2 : p1;
		
		float factor = 1, dist = other.getPos().dist(chosen.getPos()) - length;
		for (int i = 1; i < terms; i++)
			factor += (float) Math.pow(dist / lengthUnitFactor, 2 * i) / (float) Math.pow(2 * i, decay);
		
		factor *= stiffness / chosen.getMass();
		
		PVector diff = PVector.sub(other.getPos(), chosen.getPos());
		PVector lenDir = diff.copy().setMag(length);
		
		if (dampening > 0) {
			PVector dir = diff.copy().normalize();
			PVector relVelProj = dir.mult(PVector.sub(other.getVel(), chosen.getVel()).dot(dir) * dampening / chosen.getMass());
			diff.add(relVelProj);
		}
		
		return diff.sub(lenDir).mult(factor);
	}
	public PVector acceleration(int index) {
		if (index != 0 && index != 1)
			throw new RuntimeException("Index must be 0 or 1");
		
		Particle chosen = index == 0 ? p1 : p2;
		return acceleration(chosen);
	}
	public float potentialEnergy() {
		if (!isReady())
			throw new RuntimeException("Spring has missing or no particles attached");
		
		PVector diff = PVector.sub(p1.getPos(), p2.getPos());
		PVector lenDir = diff.copy().setMag(length);
		float dist = diff.sub(lenDir).mag();
		
		float result = dist * dist / 2;
		for (int i = 1; i < terms; i++)
			result += (float) Math.pow(dist / lengthUnitFactor, 2 * (i + 1)) / (2 * (i + 1) * Math.pow(2 * i, decay));
		
		result *= stiffness;
		return result;
	}
	public static float potentialEnergyPairs(List<Particle> pts) {
		List<Spring> springs = new ArrayList<Spring>();
		for (Particle p : pts)
			for (Spring s : p.getJoints())
				if (!springs.contains(s))
					springs.add(s);
		
		float result = 0;
		for (Spring s : springs)
			result += s.potentialEnergy();
		
		return result;
	}
	
	public Particle get(int index) {return index == 0 ? p1 : (index == 1 ? p2 : null);}
	public Particle getFirst() {return p1;}
	public Particle getSecond() {return p2;}
	public Particle getOther(Particle p) {return p == p1 ? p2 : (p == p2 ? p1 : null);}
	public float getStiffness() {return stiffness;}
	public float getLength() {return length;}
	public float getDampening() {return dampening;}
	public float getLengthUnitFactor() {return lengthUnitFactor;}
	public float getThickness() {return thickness;}
	public int getTerms() {return terms;}
	public int getDecay() {return decay;}
	public int getIndex(Particle p) {return p == p1 ? 0 : (p == p2 ? 1 : - 1);}
	public int getColor() {return color;}
	public boolean isReady() {return p1 != null && p2 != null;}
	public boolean contains(Particle p) {return p == p1 || p == p2;}
	public boolean contains(Particle p1, Particle p2) {return contains(p1) && contains(p2);}
	
	public Spring detach() {p1.remove(this); p2.remove(this); p1 = null; p2 = null; return this;}
	public Spring set(Particle p, int index) {
		if (index != 0 && index != 1)
			throw new RuntimeException("Index must be 0 or 1");
		if (index == 0) {p1.remove(this); p1 = p.add(this);}
		else {p2.remove(this); p2 = p.add(this);}
		return this;
	}
	public Spring attach(Particle p1, Particle p2) {detach(); this.p1 = p1.add(this); this.p2 = p2.add(this); return this;}
	public Spring setStiffness(float s) {stiffness = s; return this;}
	public Spring setLength(float l) {length = l; return this;}
	public Spring setDecay(int d) {decay = d; return this;}
	public Spring setTerms(int t) {terms = t; return this;}
	public Spring setDampening(float damp) {dampening = damp; return this;}
	public Spring setLengthUnitFactor(float fact) {lengthUnitFactor = fact; return this;}
	public Spring setThickness(float t) {thickness = t; return this;}
	public Spring setColor(int col) {color = col; return this;}
}
