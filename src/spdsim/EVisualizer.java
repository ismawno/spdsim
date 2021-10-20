package spdsim;

import processing.core.PApplet;

import processing.core.PVector;
import java.io.Serializable;
import java.util.List;

public class EVisualizer implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Environment ENV;
	private transient PApplet parent;
	private PVector translate;
	private float scaling;
	
	public EVisualizer(Environment ENV, PApplet parent) {
		this.ENV = ENV;
		this.parent = parent;
		setTranslate(new PVector(0, 0));
		setScaling(1);
	}
	
	public EVisualizer show(PApplet pa, List<Particle> pts) {for (Particle p : pts) p.getVisualizer().show(pa); return this;}
	public EVisualizer show(List<Particle> pts) {return show(parent, pts);}
	public EVisualizer show(PApplet pa) {return show(pa, ENV.getParticles());}
	public EVisualizer show() {return show(parent);}
	public EVisualizer showJoints(PApplet pa, List<Spring> jts) {for (Spring s : jts) showJoint(pa, s); return this;}
	public EVisualizer showJoints(List<Spring> jts) {return showJoints(parent, jts);}
	public EVisualizer showJoints(PApplet pa) {return showJoints(pa, ENV.getJoints());}
	public EVisualizer showJoints() {return showJoints(parent);}
	
	
	private EVisualizer showJoint(PApplet pa, Spring s) {
		Environment.DIMENSION dim = ENV.getDim();
		
		pa.push();
		pa.stroke(s.getColor());
		pa.strokeWeight(s.getThickness());
		
		PVector pos1 = s.getFirst().getPos(), pos2 = s.getSecond().getPos();
		if (dim == Environment.DIMENSION.TWO) {
			pa.translate(translate.x, translate.y);
			pa.scale(scaling, - scaling);
			pa.line(pos1.x, pos1.y, pos2.x, pos2.y);
		} else {
			pa.translate(translate.x, translate.y, translate.z);
			pa.scale(scaling, - scaling, scaling);
			pa.line(pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z);
		}
		pa.pop();
		return this;
	}
	
	public PApplet getParent() {return parent;}
	public Environment getEnvironment() {return ENV;}
	public PVector getTranslate() {return translate;}
	public float getScaling() {return scaling;}
	
	public EVisualizer setParent(PApplet pa) {parent = pa; return this;}
	public EVisualizer setEnvironment(Environment E) {ENV = E; return this;}
	public EVisualizer setTranslate(PVector trans) {translate = trans; for (Particle p : ENV.getParticles()) p.getVisualizer().setTranslate(trans); return this;}
	public EVisualizer setTranslate(float x, float y, float z) {setTranslate(new PVector(x, y, z)); return this;}
	public EVisualizer setTranslate(float x, float y) {setTranslate(new PVector(x, y)); return this;}
	public EVisualizer setScaling(float val) {scaling = val; for (Particle p : ENV.getParticles()) p.getVisualizer().setScaling(val); return this;}
	public EVisualizer trails(List<Particle> pts) {for (Particle p : pts) p.getVisualizer().trail(); return this;}
	public EVisualizer trails() {return trails(ENV.getParticles());}
	public EVisualizer noTrails(List<Particle> pts) {for (Particle p : pts) p.getVisualizer().noTrail(); return this;}
	public EVisualizer noTrails() {return noTrails(ENV.getParticles());}
	public EVisualizer deleteTrails(List<Particle> pts) {for (Particle p : pts) p.getVisualizer().deleteTrails(); return this;}
	public EVisualizer deleteTrails() {return deleteTrails(ENV.getParticles());}
	public EVisualizer setTrailCapacity(int cap) {for (Particle p : ENV.getParticles()) p.getVisualizer().setTrailCapacity(cap); return this;}
	public EVisualizer setTrailLength(int len) {for (Particle p : ENV.getParticles()) p.getVisualizer().setTrailLength(len); return this;}
}
