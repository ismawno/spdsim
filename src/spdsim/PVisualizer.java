package spdsim;
import processing.core.*;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class PVisualizer implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private transient PApplet parent;
	private Particle p;
	private PVector translate;
	private float scaling;
	private boolean trails;
	private List<PVector> trailRecord;
	private int trailCap, trailLen, counter;
	
	public PVisualizer(Particle p, PApplet parent) {
		this.p = p;
		this.parent = parent;
		trailRecord = new ArrayList<PVector>();
		trailCap = 100;
		trailLen = 1;
		counter = 0;
		
		scaling = 1.0f;
		if (parent != null)
			translate = new PVector(parent.width / 2, parent.height / 2);
		else
			translate = new PVector();
	}
	
	private PVisualizer show2D(PApplet pa, boolean trails) {
		pa.push();
		
		pa.translate(translate.x, translate.y);
		pa.scale(scaling, - scaling);
		
		if (trails) showTrail2D(pa);
		
		if (p.isSelected()) {
			pa.strokeWeight(6.0f / scaling);
			pa.stroke(0, 200, 255, PApplet.map((float) Math.sin(pa.frameCount * 0.1), - 1, 1, 0, 255));
		} else if (p.hasStroke()) {
			pa.stroke(p.getStrokeColor());
			pa.strokeWeight(p.getStrokeWeight());
		} else
			pa.noStroke();
		
		pa.fill(p.getColor());
		pa.circle(p.getPos().x, p.getPos().y, 2 * p.getRadius());
		
		pa.pop();
		p.getRecord().saveDispPos();
		
		return this;
	}
	
	private PVisualizer show3D(PApplet pa, boolean trails) {
		
		if (trails) showTrail3D(pa);
		
		pa.push();
		pa.scale(scaling);
		
		if (p.isSelected()) {
			pa.strokeWeight(6.0f / scaling);
			pa.stroke(0, 200, 255, PApplet.map((float) Math.sin(pa.frameCount * 0.1), - 1, 1, 0, 255));
		} else if (p.hasStroke()) {
			pa.stroke(p.getStrokeColor());
			pa.strokeWeight(p.getStrokeWeight());
		} else
			pa.noStroke();
		
		pa.fill(p.getColor());
		PVector tPos = Physics.invTransform(p.getPos(), translate, scaling);
		
		pa.translate(tPos.x, tPos.y, tPos.z);
		pa.sphere(p.getRadius());
		
		pa.pop();
		p.getRecord().saveDispPos();
		return this;
	}
	public PVisualizer show(PApplet pa, boolean trails) {if (p.getDim() == Environment.DIMENSION.TWO) return show2D(pa, trails); return show3D(pa, trails);}
	public PVisualizer show(boolean trails) {return show(parent, trails);}
	public PVisualizer show(PApplet pa) {return show(pa, trails);}
	public PVisualizer show() {return show(parent, trails);}
	
	private PVisualizer showTrail2D(PApplet pa) {
		
		int col = p.getColor();
		pa.strokeWeight(3);
		if (!trailRecord.isEmpty()) trailRecord.set(trailRecord.size() - 1, p.getPos().copy());
		for (int i = 1; i < trailRecord.size(); i++) {
			PVector current = trailRecord.get(i), prev = trailRecord.get(i - 1);
			pa.stroke(pa.red(col), pa.green(col), pa.blue(col), 255 * (float) i / trailRecord.size());
			pa.line(prev.x, prev.y, current.x, current.y);
		}
		if (--counter < 1) saveTrailPos();
	    return this;
	}
	
	private PVisualizer showTrail3D(PApplet pa) {
		
		pa.push();
		pa.translate(translate.x, translate.y, translate.z);
		pa.scale(scaling, - scaling, scaling);
		pa.strokeWeight(3);
		int col = p.getColor();
		if (!trailRecord.isEmpty()) trailRecord.set(trailRecord.size() - 1, p.getPos().copy());
		for (int i = 1; i < trailRecord.size(); i++) {
			PVector current = trailRecord.get(i), prev = trailRecord.get(i - 1);
			pa.stroke(pa.red(col), pa.green(col), pa.blue(col), 255 * (float) i / trailRecord.size());
			pa.line(prev.x, prev.y, prev.z, current.x, current.y, current.z);
		}
		
		pa.pop();
		if (--counter < 1) saveTrailPos();
	    return this;
	}
	private PVisualizer saveTrailPos() {
		counter = trailLen;
		trailRecord.add(p.getPos().copy());
		while (trailRecord.size() > trailCap)
			trailRecord.remove(0);
		return this;
	}
	
	public PApplet getParent() {return parent;}
	public Particle getParticle() {return p;}
	public PVector getTranslate() {return translate;}
	public float getScaling() {return scaling;}
	public float getStrokeWeight() {return p.getStrokeWeight();}
	public int getTrailCapacity() {return trailCap;}
	public int getTrailLength() {return trailLen;}
	public int getStrokeColor() {return p.getStrokeColor();}
	public int getColor() {return p.getColor();}
	public boolean hasTrail() {return trails;}
	public boolean hasStroke() {return p.hasStroke();}
	
	public PVisualizer setParent(PApplet theParent) {parent = theParent;
	if (translate == null) translate = new PVector(parent.width / 2, parent.height / 2); return this;}
	public PVisualizer setParticle(Particle p) {this.p = p; return this;}
	public PVisualizer setTranslate(float x, float y, float z) {translate.set(x, y, z); return this;}
	public PVisualizer setTranslate(float x, float y) {return setTranslate(x, y, 0);}
	public PVisualizer setTranslate(PVector trans) {translate = trans; return this;}
	public PVisualizer setScaling(float val) {scaling = val; return this;}
	public PVisualizer trail() {trails = true; return this;}
	public PVisualizer noTrail() {trails = false; return this;}
	public PVisualizer switchTrail() {trails = !trails; return this;}
	public PVisualizer setTrail(boolean t) {trails = t; return this;}
	public PVisualizer deleteTrails() {trailRecord.clear(); return this;}
	public PVisualizer setTrailCapacity(int cap) {trailCap = cap; return this;}
	public PVisualizer setTrailLength(int len) {trailLen = len; return this;}
	public PVisualizer setColor(int col) {p.setColor(col); return this;}
	public PVisualizer stroke(int col) {p.stroke(col); return this;}
	public PVisualizer strokeWeight(float w) {p.strokeWeight(w); return this;}
	public PVisualizer noStroke() {p.noStroke(); return this;}
}
