package spdsim;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.io.Serializable;

public class Units implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Environment ENV;
	private static Map<String, Float> lengths = new LinkedHashMap<String, Float>();
	private static Map<String, Float> times = new LinkedHashMap<String, Float>();
	private static Map<String, Float> masss = new LinkedHashMap<String, Float>(); 
	private static Map<String, Float> charges = new LinkedHashMap<String, Float>();
	private String length, time, mass, charge;
	
	public Units(Environment ENV, String length, String time, String mass, String charge) {
		this.ENV = ENV;
		this.length = length;
		this.time = time;
		this.mass = mass;
		this.charge = charge;
	}
	public static void initUnits() {
		
		lengths.put("m", 1.0f);
		lengths.put("fm", 1e-15f);
		lengths.put("pm", 1e-12f);
		lengths.put("A", 1e-10f);
		lengths.put("nm", 1e-9f);
		lengths.put("um", 1e-6f);
		lengths.put("mm", 1e-3f);
		lengths.put("cm", 1e-2f);
		lengths.put("km", 1e3f);
		lengths.put("Mm", 1e6f);
		lengths.put("MR", 1737100.0f);
		lengths.put("ER", 6371000.0f);
		lengths.put("JR", 69911000.0f);
		lengths.put("SR", 696340000.0f);
		lengths.put("Ls", 2.998e8f);
		lengths.put("UA", 1.496e11f);
		lengths.put("LY", 9.461e15f);
		lengths.put("Pc", 3.086e16f);
		
		times.put("s", 1.0f);
		times.put("fs", 1e-15f);
		times.put("ps", 1e-12f);
		times.put("ns", 1e-9f);
		times.put("us", 1e-6f);
		times.put("ms", 1e-3f);
		times.put("min", 60.0f);
		times.put("h", 3600.0f);
		times.put("d", 86400.0f);
		times.put("y", 31536000.0f);
		times.put("cent", 3153600000.0f);
		
		masss.put("kg", 1.0f);
		masss.put("eV/c^2", 1.783e-36f);
		masss.put("keV/c^2", 1.783e-33f);
		masss.put("MeV/c^2", 1.783e-30f);
		masss.put("GeV/c^2", 1.783e-27f);
		masss.put("TeV/c^2", 1.783e-24f);
		masss.put("fg", 1e-18f);
		masss.put("pg", 1e-15f);
		masss.put("ng", 1e-12f);
		masss.put("ug", 1e-9f);
		masss.put("mg", 1e-6f);
		masss.put("g", 1e-3f);
		masss.put("T", 1e3f);
		masss.put("MM", 7.347e22f);
		masss.put("EM", 5.972e24f);
		masss.put("JM", 1.898e27f);
		masss.put("SM", 1.989e30f);
		
		charges.put("C", 1.0f);
		charges.put("e", 1.602e-19f);
		charges.put("fC", 1e-15f);
		charges.put("pC", 1e-12f);
		charges.put("nC", 1e-9f);
		charges.put("uC", 1e-6f);
		charges.put("mC", 1e-3f);
		charges.put("kC", 1e3f);
		charges.put("MC", 1e6f);
		charges.put("GC", 1e9f);
		charges.put("TC", 1e12f);
	}
	
	public String[] getUnits() {return new String[] {length, time, mass, charge};}
	public static List<String> getLengthUnits() {return new ArrayList<String>(lengths.keySet());}
	public static List<String> getTimeUnits() {return new ArrayList<String>(times.keySet());}
	public static List<String> getMassUnits() {return new ArrayList<String>(masss.keySet());}
	public static List<String> getChargeUnits() {return new ArrayList<String>(charges.keySet());}
	public String getLengthUnit() {return length;}
	public String getTimeUnit() {return time;}
	public String getMassUnit() {return mass;}
	public String getChargeUnit() {return charge;}
	public String getVelUnit() {return length + "/" + time;}
	public String getEnergyUnit() {return mass + "*" + length + "^2/" + time + "^2";}
	public String getMomentumUnit() {return mass + "*" + getVelUnit();}
	public String getAngularMomentumUnit() {return length + "*" + getMomentumUnit();}
	public float getLengthFactor(String to) {return getLengthFactor(length, to);}
	public static float getLengthFactor(String from, String to) {return lengths.get(from) / lengths.get(to);}
	public float getTimeFactor(String to) {return getTimeFactor(time, to);}
	public static float getTimeFactor(String from, String to) {return times.get(from) / times.get(to);}
	public float getMassFactor(String to) {return getMassFactor(mass, to);}
	public static float getMassFactor(String from, String to) {return masss.get(from) / masss.get(to);}
	public float getChargeFactor(String to) {return getChargeFactor(charge, to);}
	public static float getChargeFactor(String from, String to) {return charges.get(from) / charges.get(to);}
	public float getVelFactor(String toL, String toT) {return getVelFactor(length, time, toL, toT);} 
	public static float getVelFactor(String fromL, String fromT, String toL, String toT) {return getLengthFactor(fromL, toL) / getTimeFactor(fromT, toT);}
	public float getEnergyFactor(String toL, String toT, String toM) {return getEnergyFactor(length, time, mass, toL, toT, toM);}
	public static float getEnergyFactor(String fromL, String fromT, String fromM, String toL, String toT, String toM) {
		return getMassFactor(fromM, toM) * getVelFactor(fromL, fromT, toL, toT) * getVelFactor(fromL, fromT, toL, toT);
	}
	public float getMomentumFactor(String toL, String toT, String toM) {return getMomentumFactor(length, time, mass, toL, toT, toM);}
	public static float getMomentumFactor(String fromL, String fromT, String fromM, String toL, String toT, String toM) {
		return getMassFactor(fromM, toM) * getVelFactor(fromL, fromT, toL, toT);
	}
	public float getAngularMomentumFactor(String toL, String toT, String toM) {return getAngularMomentumFactor(length, time, mass, toL, toT, toM);}
	public static float getAngularMomentumFactor(String fromL, String fromT, String fromM, String toL, String toT, String toM) {
		return getEnergyFactor(fromL, fromT, fromM, toL, toT, toM) * getTimeFactor(fromT, toT);
	}
	public float getLengthUnitScale() {return getLengthUnitScale(length);}
	public static float getLengthUnitScale(String unit) {return lengths.get(unit);}
	public float getTimeUnitScale() {return getTimeUnitScale(time);}
	public static float getTimeUnitScale(String unit) {return times.get(unit);}
	public float getMassUnitScale() {return getMassUnitScale(mass);}
	public static float getMassUnitScale(String unit) {return masss.get(unit);}
	public float getChargeUnitScale() {return getChargeUnitScale(charge);}
	public static float getChargeUnitScale(String unit) {return charges.get(unit);}
	public float getVelUnitScale() {return getVelUnitScale(length, time);}
	public static float getVelUnitScale(String length, String time) {return lengths.get(length) / times.get(time);}
	public float getEnergyUnitScale() {return getEnergyUnitScale(length, time, mass);}
	public static float getEnergyUnitScale(String length, String time, String mass) {
		return getMassUnitScale(mass) * getVelUnitScale(length, time) * getVelUnitScale(length, time);
	}
	public float getMomentumUnitScale() {return getMomentumUnitScale(length, time, mass);}
	public static float getMomentumUnitScale(String length, String time, String mass) {
		return getMassUnitScale(mass) * getVelUnitScale(length, time);
	}
	public float getAngularMomentumUnitScale() {return getAngularMomentumUnitScale(length, time, mass);}
	public static float getAngularMomentumUnitScale(String length, String time, String mass) {
		return getEnergyUnitScale(length, time, mass) * getTimeUnitScale(time);
	}
	
	public static void setParticlesLengthUnit(List<Particle> pts, String from, String to) {
		float factor = getLengthFactor(from, to);
		for (Particle p : pts) {
			p.getPos().mult(factor); 
			p.getVel().mult(factor);
			p.setRadiusRaw(p.getRadius() * factor).computeParametersValue();
		}
	}
	public static void setSpringsLengthUnit(List<Spring> spr, String from, String to) {
		float factor = getLengthFactor(from, to);
		for (Spring s : spr)
			s.setLength(s.getLength() * factor).setLengthUnitFactor(s.getLengthUnitFactor() * factor).setThickness(s.getThickness() * factor);
	}
	public Units setLengthUnitLabel(String unit) {length = unit; return this;}
	public Units setLengthUnit(String unit) {
		setParticlesLengthUnit(ENV.getParticles(), length, unit);
		setSpringsLengthUnit(ENV.getJoints(), length, unit);
		for (Interaction inter : ENV.getInteractions()) {
			inter.getUnit().getPos().mult(getLengthFactor(length, unit));
			inter.getUnit().getVel().mult(getLengthFactor(length, unit));
			inter.getUnit().setRadius(inter.getUnit().getRadius() * getLengthFactor(length, unit));
		}
		for (ExternalForce ext : ENV.getExternals()) {
			ext.getUnit().getPos().mult(getLengthFactor(length, unit));
			ext.getUnit().getVel().mult(getLengthFactor(length, unit));
			ext.getUnit().setRadius(ext.getUnit().getRadius() * getLengthFactor(length, unit));
		}
		length = unit;
		return this;
	}
	
	public static void setParticlesTimeUnit(List<Particle> pts, String from, String to) {
		float factor = getTimeFactor(from, to);
		for (Particle p : pts)
			p.getVel().div(factor);
	}
	public static void setSpringsTimeUnit(List<Spring> spr, String from, String to) {
		float factor = getTimeFactor(from, to);
		for (Spring s : spr)
			s.setStiffness(s.getStiffness() / (factor * factor)).setDampening(s.getDampening() / factor);
	}
	public Units setTimeUnitLabel(String unit) {time = unit; return this;}
	public Units setTimeUnit(String unit) {
		setParticlesTimeUnit(ENV.getParticles(), time, unit);
		setSpringsTimeUnit(ENV.getJoints(), time, unit);
		for (Interaction inter : ENV.getInteractions())
			inter.getUnit().getVel().div(getTimeFactor(time, unit));
		for (ExternalForce ext : ENV.getExternals())
			ext.getUnit().getVel().div(getTimeFactor(time, unit));
		time = unit;
		return this;
	}
	
	public static void setParticlesMassUnit(List<Particle> pts, String from, String to) {
		float factor = getMassFactor(from, to);
		for (Particle p : pts)
			p.setMass(p.getMass() * factor);
	}
	public static void setSpringsMassUnit(List<Spring> spr, String from, String to) {
		float factor = getMassFactor(from, to);
		for (Spring s : spr)
			s.setStiffness(s.getStiffness() * factor).setDampening(s.getDampening() * factor);
	}
	public Units setMassUnitLabel(String unit) {mass = unit; return this;}
	public Units setMassUnit(String unit) {
		setParticlesMassUnit(ENV.getParticles(), mass, unit);
		setSpringsMassUnit(ENV.getJoints(), mass, unit);
		for (Interaction inter : ENV.getInteractions())
			inter.getUnit().setMass(inter.getUnit().getMass() * getMassFactor(mass, unit));
		for (ExternalForce ext : ENV.getExternals())
			ext.getUnit().setMass(ext.getUnit().getMass() * getMassFactor(mass, unit));
		mass = unit;
		return this;
	}
	
	public static void setParticlesChargeUnit(List<Particle> pts, String from, String to) {
		float factor = getChargeFactor(from, to);
		for (Particle p : pts)
			p.setCharge(p.getCharge() * factor);
	}
	public Units setChargeUnitLabel(String unit) {charge = unit; return this;}
	public Units setChargeUnit(String unit) {
		setParticlesChargeUnit(ENV.getParticles(), charge, unit);
		for (Interaction inter : ENV.getInteractions())
			inter.getUnit().setCharge(inter.getUnit().getCharge() * getChargeFactor(charge, unit));
		for (ExternalForce ext : ENV.getExternals())
			ext.getUnit().setCharge(ext.getUnit().getCharge() * getChargeFactor(charge, unit));
		charge = unit;
		return this;
	}
}
