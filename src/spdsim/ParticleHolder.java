package spdsim;
import java.io.Serializable;

import processing.core.PVector;

public class ParticleHolder implements Serializable {
	
	private static final long serialVersionUID = 1L;
		
	private PVector pos, prevIntegPos, prevDispPos, vel, translate;
	private float radius, massDens, chargeDens, scaling;
	
	public ParticleHolder() {
		pos = new PVector();
		prevIntegPos = new PVector();
		prevDispPos = new PVector();
		vel = new PVector();
		translate = new PVector();
	}
	public ParticleHolder save(Particle p) {
		pos = p.getPos().copy();
		prevIntegPos = p.getRecord().getPrevIntegPos().copy();
		prevDispPos = p.getRecord().getPrevDispPos().copy();
		vel = p.getVel().copy();
		translate = p.getVisualizer().getTranslate().copy();
		
		radius = p.getRadius();
		massDens = p.getMassDens();
		chargeDens = p.getChargeDens();
		scaling = p.getVisualizer().getScaling();
		return this;
	}
	public ParticleHolder load(Particle p) {
		p.setPos(pos.copy()).setVel(vel.copy()).getVisualizer().setTranslate(translate.copy()).setScaling(scaling);
		p.getRecord().setPrevIntegPos(prevIntegPos.copy()).setPrevDispPos(prevDispPos.copy());
		p.setRadius(radius).setMassDens(massDens).setChargeDens(chargeDens).computeParametersDensity();
		return this;
	}
	
	public PVector getPos() {return pos;}
	public PVector getPrevIntegPos() {return prevIntegPos;}
	public PVector getPrevDispPos() {return prevIntegPos;}
	public PVector getVel() {return vel;}
	public PVector getTranslate() {return translate;}
	public float getRadius() {return radius;}
	public float getMassDens() {return massDens;}
	public float getChargeDens() {return chargeDens;}
	public float getScaling() {return scaling;}
}
