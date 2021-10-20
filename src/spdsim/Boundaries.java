package spdsim;

import java.io.Serializable;
import processing.core.*;
import spdsim.Environment.DIMENSION;

public class Boundaries implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Environment ENV;
	private PVector constrainDim, constrainPos;
	private float elasticity, glide;
	
	public Boundaries(Environment ENV, float width, float height, float depth, float elasticity, float glide) {
		this.ENV = ENV;
		this.elasticity = elasticity;
		this.glide = glide;
		constrainDim = new PVector(width, height, depth);
		constrainPos = new PVector(0, 0);
	}
	
	public float interpolFactor(float q0, float q1, float qL, float r) {
		float dL = qL + r;
		return (dL - q0) / (q1 - q0);
	}
	public PVector interpolate(PVector prev, PVector current, float factor) {
		return PVector.mult(current, factor).add(PVector.mult(prev, 1 - factor));
	}
	private Boundaries checkBoundaries2D(Particle p, float elasticity, float glide, boolean inter) {
		PVector pos = p.getPos(),  vel = p.getVel(), prevPos = p.getRecord().getPrevIntegPos();
		float left = getLeftEdge(), right = getRightEdge(), bottom = getBottomEdge(), top = getTopEdge(), radius = p.getRadius();
		
		if (pos.x > right - radius) {			
		    if (inter) pos = interpolate(prevPos, pos, interpolFactor(prevPos.x, pos.x, right, - radius));
		    else pos.x = right - radius;
		    vel.x *= - elasticity;
		    vel.y *= glide;
		    vel.z *= glide;
		    p.onBoundaryEnter();
	    } else if (pos.x < left + radius) {
	    	if (inter) pos = interpolate(prevPos, pos, interpolFactor(prevPos.x, pos.x, left, radius));
	    	else pos.x = left + radius;
	        vel.x *= - elasticity;
	        vel.y *= glide;
	        vel.z *= glide;
	        p.onBoundaryEnter();
	    }
	    if (pos.y > top - radius) {
	    	if (inter) pos = interpolate(prevPos, pos, interpolFactor(prevPos.y, pos.y, top, - radius));
	    	else pos.y = top - radius;
	        vel.y *= - elasticity;
	        vel.x *= glide;
	        vel.z *= glide;
	        p.onBoundaryEnter();
	    } else if (pos.y < bottom + radius) {
	    	if (inter) pos = interpolate(prevPos, pos, interpolFactor(prevPos.y, pos.y, bottom, radius));
	    	else pos.y = bottom + radius;
	        vel.y *= - elasticity;
	        vel.x *= glide;
	        vel.z *= glide;
	        p.onBoundaryEnter();
	    }
	    p.setPos(pos);
		return this;
	}
	private Boundaries checkBoundaries3D(Particle p, float elasticity, float glide, boolean inter) {
		checkBoundaries2D(p, elasticity, glide, inter);
		PVector pos = p.getPos(),  vel = p.getVel(), prevPos = p.getRecord().getPrevIntegPos();
		float lower = getLowerEdge(), upper = getUpperEdge(), radius = p.getRadius();
		
		if (pos.z > upper - radius) {
			if (inter) pos = interpolate(prevPos, pos, interpolFactor(prevPos.z, pos.z, upper, - radius));
			else pos.z = upper - radius;
			vel.z *= - elasticity;
			vel.x *= glide;
			vel.y *= glide;
			p.onBoundaryEnter();
		} else if (pos.z < lower + radius) {
			if (inter) pos = interpolate(prevPos, pos, interpolFactor(prevPos.z, pos.z, lower, radius));
			else pos.z = lower + radius;
			vel.z *= - elasticity;
			vel.x *= glide;
			vel.y *= glide;
			p.onBoundaryEnter();
		}
		p.setPos(pos);
		return this;
	}
	public Boundaries constrain(float elasticity, float glide, boolean inter) {
		if (ENV.getDim() == Environment.DIMENSION.TWO)
			for (Particle p : ENV.getParticles()) 
				checkBoundaries2D(p, elasticity, glide, inter);
		else
			for (Particle p : ENV.getParticles()) 
				checkBoundaries3D(p, elasticity, glide, inter);
		return this;
	}
	public Boundaries constrain() {return constrain(elasticity, glide, true);}
	public Boundaries constrain(float elasticity, float glide) {return constrain(elasticity, glide, true);}
	public Boundaries constrain(boolean inter) {return constrain(elasticity, glide, inter);}
	
	public Boundaries show(PApplet pa, int color) {
		
		if (ENV.getDim() == DIMENSION.TWO) {
			pa.push();
			
			pa.translate(ENV.getVisualizer().getTranslate().x, ENV.getVisualizer().getTranslate().y);
			pa.scale(ENV.getVisualizer().getScaling(), - ENV.getVisualizer().getScaling());
			pa.stroke(color);
			pa.strokeWeight(4);
			pa.noFill();
			pa.rect(getLeftEdge(), getBottomEdge(), getConstrainDim().x, getConstrainDim().y);
			
			pa.pop();
			return this;
		}
		pa.push();
		
		pa.translate(ENV.getVisualizer().getTranslate().x, ENV.getVisualizer().getTranslate().y, ENV.getVisualizer().getTranslate().z);
		pa.scale(ENV.getVisualizer().getScaling(), - ENV.getVisualizer().getScaling(), ENV.getVisualizer().getScaling());
		pa.fill(color);
		
		pa.translate(getRightEdge() - getConstrainDim().x / 2, getRightEdge() - getConstrainDim().y / 2, getUpperEdge() - getConstrainDim().z / 2);
		pa.box(getConstrainDim().x, getConstrainDim().y, getConstrainDim().z);
		
		pa.pop();
		return this;
	}
	public Boundaries show(int color) {return show(ENV.getVisualizer().getParent(), color);}
	public Boundaries show(PApplet pa) {return show(pa, - 1);}
	public Boundaries show() {return show(ENV.getVisualizer().getParent());}
	
	public Environment getEnvironment() {return ENV;}
	public PVector getConstrainDim() {return constrainDim;}
	public PVector getConstrainPos() {return constrainPos;}
	public float getLeftEdge() {return - constrainDim.x / 2 + constrainPos.x;}
	public float getRightEdge() {return getLeftEdge() + constrainDim.x;}
	public float getBottomEdge() {return - constrainDim.y / 2 + constrainPos.y;}
	public float getTopEdge() {return getBottomEdge() + constrainDim.y;}
	public float getLowerEdge() {return - constrainDim.z / 2 + constrainPos.z;}
	public float getUpperEdge() {return getLowerEdge() + constrainDim.z;}
	public float getElasticity() {return elasticity;}
	public float getGlide() {return glide;}
	
	public Boundaries setEnvironment(Environment E) {ENV = E; return this;}
	public Boundaries setConstrainDim(PVector dim) {constrainDim = dim; return this;}
	public Boundaries setConstrainDim(float w, float h, float d) {constrainDim.set(w, h, d); return this;}
	public Boundaries setConstrainDim(float w, float h) {constrainDim.set(w, h); return this;}
	public Boundaries setConstrainPos(PVector pos) {constrainPos = pos; return this;}
	public Boundaries setConstrainPos(float x, float y, float z) {constrainPos.set(x, y, z); return this;}
	public Boundaries setConstrainPos(float x, float y) {constrainPos.set(x, y); return this;}
	public Boundaries setElasticity(float e) {elasticity = e; return this;}
	public Boundaries setGlide(float g) {glide = g; return this;}
}
