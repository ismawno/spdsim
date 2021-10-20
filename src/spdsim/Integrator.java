package spdsim;

import java.io.Serializable;
import tensors.Float.*;

public class Integrator implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public enum METHOD{
		
		EULER,
		RK4,
		RK6,
		RKF45,
		CK45,
		DOPRI45;
	}
	
	private Environment ENV;
	private METHOD method;
	private float dt;
	private float error, cumError;
		
	private boolean computeError;
	
	public Integrator(float dt, Environment ENV) {
		method = METHOD.RK4;
		this.dt = dt;
		this.ENV = ENV;
		error = 0.0f;
		cumError = 0.0f;
		computeError = true;
	}
	
	private Integrator update(boolean forward) {
		
		int nDim;
		if (ENV.getDim() == Environment.DIMENSION.TWO)
			nDim = 4;
		else
			nDim = 6;
		
		for (Particle p : ENV.getParticles()) p.getRecord().saveIntegPos();
		if (method == METHOD.EULER)
			euler(nDim, forward);
		else if (method == METHOD.RK4)
			RK4(nDim, forward);
		else if (method == METHOD.RK6)
			RK6(nDim, forward);
		else if (method == METHOD.RKF45)
			RKF45(nDim, forward);
		else if (method == METHOD.CK45)
			CK45(nDim, forward);
		else
			DOPRI45(nDim, forward);
		return this;
	}
	
	public Integrator forward() {return update(true); }
	public Integrator backward() {return update(false);}
	
	private Integrator euler(int nDim, boolean forward) {
		
		for (Particle p : ENV.getParticles())
			if (p.isDynamic()) {
				p.setKUnsafe(p.computeK(dt, forward, new Vector(nDim)));
				if (forward) p.forward(); else p.backward();
			}
		return this;
	}
	
	private Integrator RK4(int nDim, boolean forward) {
		
		for (Particle p : ENV.getParticles())
			if (p.isDynamic()) {
				Vector k1, k2, k3, k4;
	
				k1 = p.computeK(dt, forward, new Vector(nDim));
				k2 = p.computeK(dt, forward, Vector.div(k1, 2));
				k3 = p.computeK(dt, forward, Vector.div(k2, 2));
				k4 = p.computeK(dt, forward, k3);
	
				k2.mult(2);
				k3.mult(2);
	
				k1.add(k2);
				k3.add(k4);
	
				p.setKUnsafe(Vector.add(k1, k3));
				p.getK().div(6);
				
				if (forward) p.forward(); else p.backward();
			}
		return this;
	}
	
	private Integrator RK6(int nDim, boolean forward) {
		
		for (Particle p : ENV.getParticles())
			if (p.isDynamic()) {
			    Vector k1, k2, k3, k4, k5, k6, k7;
			  
			    k1 = p.computeK(dt, forward, new Vector(nDim));
			    k2 = p.computeK(dt, forward, k1);
			  
			    Vector K = Vector.mult(k1, 3);
			    K.add(k2);
			    K.div(8);
			  
			    k3 = p.computeK(dt, forward, K);
			  
			    K = Vector.mult(k1, 8);
			    K.add(Vector.mult(k2, 2));
			    K.add(Vector.mult(k3, 8));
			    K.div(27);
			  
			    k4 = p.computeK(dt, forward, K);
			  
			    K = Vector.mult(k1, 3 * (3 * (float) Math.sqrt(21) - 7));
			    K.sub(Vector.mult(k2, 8 * (7 - (float) Math.sqrt(21))));
			    K.add(Vector.mult(k3, 48 * (7 - (float) Math.sqrt(21))));
			    K.sub(Vector.mult(k4, 3 * (21 - (float) Math.sqrt(21))));
			    K.div(392);
			  
			    k5 = p.computeK(dt, forward, K);
			  
			    K = Vector.mult(k1, - 5 * (231 + 51 * (float) Math.sqrt(21)));
			    K.sub(Vector.mult(k2, 40 * (7 + (float) Math.sqrt(21))));
			    K.sub(Vector.mult(k3, 320 * (float) Math.sqrt(21)));
			    K.add(Vector.mult(k4, 3 * (21 + 121 * (float) Math.sqrt(21))));
			    K.add(Vector.mult(k5, 392 * (6 + (float) Math.sqrt(21))));
			    K.div(1960);
			  
			    k6 = p.computeK(dt, forward, K);
			  
			    K = Vector.mult(k1, 15 * (22 + 7 * (float) Math.sqrt(21)));
			    K.add(Vector.mult(k2, 120));
			    K.add(Vector.mult(k3, 40 * (7 * (float) Math.sqrt(21) - 5)));
			    K.sub(Vector.mult(k4, 63 * (3 * (float) Math.sqrt(21) - 2)));
			    K.sub(Vector.mult(k5, 14 * (49 + 9 * (float) Math.sqrt(21))));
			    K.add(Vector.mult(k6, 70 * (7 - (float) Math.sqrt(21))));
			    K.div(180);
			  
			    k7 = p.computeK(dt, forward, K);
			  
			    p.setKUnsafe(Vector.mult(k1, 9));
			    p.getK().add(Vector.mult(k3, 64));
			    p.getK().add(Vector.mult(k5, 49));
			    p.getK().add(Vector.mult(k6, 49));
			    p.getK().add(Vector.mult(k7, 9));
			    p.getK().div(180);
			    
			    if (forward) p.forward(); else p.backward();
			}
		return this;
	}
	
	private Integrator RKF45(int nDim, boolean forward) {
		
		for (Particle p : ENV.getParticles())
			if (p.isDynamic()) {
			    Vector k1, k2, k3, k4, k5, k6;
			  
			    k1 = p.computeK(dt, forward, new Vector(nDim));
			    k2 = p.computeK(dt, forward, Vector.div(k1, 4));
			  
			    Vector K = Vector.mult(k1, 3);
			    K.add(Vector.mult(k2, 9));
			    K.div(32);
			  
			    k3 = p.computeK(dt, forward, K);
			  
			    K = Vector.mult(k1, 1932);
			    K.sub(Vector.mult(k2, 7200));
			    K.add(Vector.mult(k3, 7296));
			    K.div(2197);
			  
			    k4 = p.computeK(dt, forward, K);
			  
			    K = Vector.mult(k1, 439.0f / 216);
			    K.sub(Vector.mult(k2, 8));
			    K.add(Vector.mult(k3, 3680.0f / 513));
			    K.sub(Vector.mult(k4, 845.0f / 4104));
			  
			    k5 = p.computeK(dt, forward, K);
			  
			    K = Vector.mult(k1, - 8.0f / 27);
			    K.add(Vector.mult(k2, 2));
			    K.sub(Vector.mult(k3, 3544.0f / 2565));
			    K.add(Vector.mult(k4, 1859.0f / 4104));
			    K.sub(Vector.mult(k5, 11.0f / 40));
			  
			    k6 = p.computeK(dt, forward, K);
			  
			    p.setKUnsafe(Vector.mult(k1, 16.0f / 135));
			    p.getK().add(Vector.mult(k3, 6656.0f / 12825));
			    p.getK().add(Vector.mult(k4, 28561.0f / 56430));
			    p.getK().sub(Vector.mult(k5, 9.0f / 50));
			    p.getK().add(Vector.mult(k6, 2.0f / 55));
			    
			    if (forward) p.forward(); else p.backward();
			  
			    if (computeError) {
			  
			        K = Vector.mult(k1, 25.0f / 216);
			        K.add(Vector.mult(k3, 1408.0f / 2565));
			        K.add(Vector.mult(k4, 2197.0f / 4104));
			        K.sub(Vector.div(k5, 5));
			    
			        Vector err = Vector.sub(K, p.getK());
			        error = err.norm();
			        cumError += error;
			    }
			}
		return this;
	}
	
	private Integrator CK45(int nDim, boolean forward) {
		
		for (Particle p : ENV.getParticles())
			if (p.isDynamic()) {
			    Vector k1, k2, k3, k4, k5, k6;
			  
			    k1 = p.computeK(dt, forward, new Vector(nDim));
			    k2 = p.computeK(dt, forward, Vector.div(k1, 5));
			  
			    Vector K = Vector.mult(k1, 3);
			    K.add(Vector.mult(k2, 9));
			    K.div(40);
			  
			    k3 = p.computeK(dt, forward, K);
			  
			    K = Vector.mult(k1, 3);
			    K.sub(Vector.mult(k2, 9));
			    K.add(Vector.mult(k3, 12));
			    K.div(10);
			  
			    k4 = p.computeK(dt, forward, K);
			  
			    K = Vector.mult(k1, - 11.0f / 54);
			    K.add(Vector.mult(k2, 5.0f / 2));
			    K.sub(Vector.mult(k3, 70.0f / 27));
			    K.add(Vector.mult(k4, 35.0f / 27));
			  
			    k5 = p.computeK(dt, forward, K);
			  
			    K = Vector.mult(k1, 1631.0f / 55296);
			    K.add(Vector.mult(k2, 175.0f / 512));
			    K.add(Vector.mult(k3, 575.0f / 13824));
			    K.add(Vector.mult(k4, 44275.0f / 110592));
			    K.add(Vector.mult(k5, 253.0f / 4096));
			  
			    k6 = p.computeK(dt, forward, K);
			  
			    p.setKUnsafe(Vector.mult(k1, 37.0f / 378));
			    p.getK().add(Vector.mult(k3, 250.0f / 621));
			    p.getK().add(Vector.mult(k4, 125.0f / 594));
			    p.getK().add(Vector.mult(k6, 512.0f / 1771));
			    
			    if (forward) p.forward(); else p.backward();
			  
			    if (computeError) {
			  
			        K = Vector.mult(k1, 2825.0f / 27648);
			        K.add(Vector.mult(k3, 18575.0f / 48384));
			        K.add(Vector.mult(k4, 13525.0f / 55296));
			        K.add(Vector.mult(k5, 277.0f / 14336));
			        K.add(Vector.div(k6, 4));
			    
			        Vector err = Vector.sub(K, p.getK());
			        error = err.norm();
			        cumError += error;
			    }
			}
		return this;
	}
	
	private Integrator DOPRI45(int nDim, boolean forward) {
		
		for (Particle p : ENV.getParticles())
			if (p.isDynamic()) {
			    Vector k1, k2, k3, k4, k5, k6, k7;
			  
			    k1 = p.computeK(dt, forward, new Vector(nDim));
			    k2 = p.computeK(dt, forward, Vector.div(k1, 5));
			  
			    Vector K = Vector.mult(k1, 3);
			    K.add(Vector.mult(k2, 9));
			    K.div(40);
			  
			    k3 = p.computeK(dt, forward, K);
			  
			    K = Vector.mult(k1, 44.0f / 45);
			    K.sub(Vector.mult(k2, 56.0f / 15));
			    K.add(Vector.mult(k3, 32.0f / 9));
			  
			    k4 = p.computeK(dt, forward, K);
			  
			    K = Vector.mult(k1, 19372.0f / 6561);
			    K.sub(Vector.mult(k2, 25360.0f / 2187));
			    K.add(Vector.mult(k3, 64448.0f / 6561));
			    K.sub(Vector.mult(k4, 212.0f / 729));
			  
			    k5 = p.computeK(dt, forward, K);
			  
			    K = Vector.mult(k1, 9017.0f / 3168);
			    K.sub(Vector.mult(k2, 355.0f / 33));
			    K.add(Vector.mult(k3, 46732.0f / 5247));
			    K.add(Vector.mult(k4, 49.0f / 176));
			    K.sub(Vector.mult(k5, 5103.0f / 18656));
			  
			    k6 = p.computeK(dt, forward, K);
			  
			    K = Vector.mult(k1, 35.0f / 384);
			    K.add(Vector.mult(k3, 500.0f / 1113));
			    K.add(Vector.mult(k4, 125.0f / 192));
			    K.sub(Vector.mult(k5, 2187.0f / 6784));
			    K.add(Vector.mult(k6, 11.0f / 84));
			  
			    k7 = p.computeK(dt, forward, K);
			  
			    p.setKUnsafe(K.copy());
			    if (forward) p.forward(); else p.backward();
			  
			    if (computeError) {
			  
			        K = Vector.mult(k1, 5179.0f / 57600);
			        K.add(Vector.mult(k3, 7571.0f / 16695));
			        K.add(Vector.mult(k4, 393.0f / 640));
			        K.sub(Vector.mult(k5, 92097.0f / 339200));
			        K.add(Vector.mult(k6, 187.0f / 2100));
			        K.add(Vector.div(k7, 40));
			        
			        Vector err = Vector.sub(K, p.getK());    
			        error = err.norm();
			        cumError += error;
			    }
			}
		return this;
	}
	
	public Environment getParticles() {return ENV;}
	public METHOD[] getMethodList() {return new METHOD[] {METHOD.EULER, METHOD.RK4, METHOD.RK6, METHOD.RKF45, METHOD.CK45, METHOD.DOPRI45};}
	public METHOD getMethod() {return method;}
	public float getDt() {return dt;}
	public float getError() {return error;}
	public float getCumulativeError() {return cumError;}
	public boolean hasError() {return computeError & (method == METHOD.RKF45 || method == METHOD.CK45 || method == METHOD.DOPRI45);}
	
	public Integrator setEnvironment(Environment E) {ENV = E; return this;}
	public Integrator setMethod(METHOD m) {method = m; return this;}
	public Integrator setDt(float val) {dt = val; return this;}
	public Integrator resetError() {cumError = 0.0f; error = 0.0f; return this;}
}
