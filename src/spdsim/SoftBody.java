package spdsim;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.io.Serializable;
import java.nio.charset.Charset;
import processing.core.*;

public class SoftBody implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String[] validVertices = new String[] {"LBD", "RBD", "LTD", "RTD", "LBU", "RBU", "LTU", "RTU"};
	
	private transient PApplet parent;
	//private Environment.DIMENSION dim;
	
	private Particle[][][] body;
	private List<Particle> linealBody;
	private List<Spring> joints;
	private Map<String, Particle> vertices;
	private String actLabel;
	
	private float massDens, chargeDens, radius;
	private int width, height, depth;
	private int color;
	private boolean crossJoints, init;
	
	public SoftBody(PApplet parent, int width, int height, int depth, float massDens, float chargeDens, float radius) {
		this.parent = parent;
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.massDens = massDens;
		this.chargeDens = chargeDens;
		this.radius = radius;
		this.color = -1;
		
		if (width < 2)
			throw new RuntimeException("Cannot create a Soft Body with width < 2.");
		
		crossJoints = false;
		
		linealBody = new ArrayList<Particle>(getSize());
		joints = new ArrayList<Spring>();
		vertices = new HashMap<String, Particle>();
		
		byte[] array = new byte[10];
		new Random().nextBytes(array);
		actLabel = new String(array, Charset.forName("UTF-8"));
		
		//setDim(Environment.DIMENSION.THREE);
	}
	public SoftBody(PApplet parent, int width, int height, float massDens, float chargeDens, float radius) {
		this(parent, width, height, 1, massDens, chargeDens, radius);
		//setDim(Environment.DIMENSION.TWO);
	}
	public SoftBody(PApplet parent, int width, float massDens, float chargeDens, float radius) {
		this(parent, width, 1, massDens, chargeDens, radius);
	}
	
	public SoftBody init() {
		
		if (!isReady3D()) depth = 1;
		if (!isReady2D()) height = 1;
		if (!isReady1D()) throw new RuntimeException("Soft Body is not ready. Set the vertices first.");
		body = new Particle[depth][height][width];
		
		for (int k = 0; k < depth; k++)
			for (int j = 0; j < height; j++)
				for (int i = 0; i < width; i++)
					body[k][j][i] = Particle.atZeroStill(parent, massDens, chargeDens, radius).setColor(color).setActive(false).setActiveLabel(actLabel);
		
		if (isReady3D()) {
			body[depth - 1][0][0] = vertices.get("LBU");
			body[depth - 1][0][width - 1] = vertices.get("RBU");
			body[depth - 1][height - 1][0] = vertices.get("LTU");
			body[depth - 1][height - 1][width - 1] = vertices.get("RTU");
		}
		if (isReady2D()) {
			body[0][height - 1][0] = vertices.get("LTD");
			body[0][height - 1][width - 1] = vertices.get("RTD");
		}
		body[0][0][0] = vertices.get("LBD");
		body[0][0][width - 1] = vertices.get("RBD");
		
		linealBody.clear();
		joints.clear();
		
		initJointsAndLinealBody();
		init = true;
		return this;
	}
	private void initJointsAndLinealBody() {
		for (int k = 0; k < depth; k++)
			for (int j = 0; j < height; j++)
				for (int i = 0; i < width; i++) {
					Particle center = body[k][j][i];
					Particle left = i - 1 >= 0 ? body[k][j][i - 1] : null;
					Particle right = i + 1 < width ? body[k][j][i + 1] : null;
					Particle bottom = j - 1 >= 0 ? body[k][j - 1][i] : null;
					Particle top = j + 1 < height ? body[k][j + 1][i] : null;
					Particle down = k - 1 >= 0 ? body[k - 1][j][i] : null;
					Particle up = k + 1 < depth ? body[k + 1][j][i] : null;
					
					if (left != null && !hasJoint(left, center)) joints.add(new Spring(left, center));
					if (right != null && !hasJoint(right, center)) joints.add(new Spring(center, right));
					if (bottom != null && !hasJoint(bottom, center)) joints.add(new Spring(bottom, center));
					if (top != null && !hasJoint(top, center)) joints.add(new Spring(center, top));
					if (down != null && !hasJoint(down, center)) joints.add(new Spring(down, center));
					if (up != null && !hasJoint(up, center)) joints.add(new Spring(center, up));
					
					linealBody.add(center);
					if (crossJoints) {
						Particle leftBottom = (i - 1 >= 0 && j - 1 >= 0) ? body[k][j - 1][i - 1] : null;
						Particle rightBottom = (i + 1 < width && j - 1 >= 0) ? body[k][j - 1][i + 1] : null;
						Particle leftTop = (i - 1 >= 0 && j + 1 < height) ? body[k][j + 1][i - 1] : null;
						Particle rightTop = (i + 1 < width && j + 1 < height) ? body[k][j + 1][i + 1] : null;
						
						Particle leftDown = (i - 1 >= 0 && k - 1 >= 0) ? body[k - 1][j][i - 1] : null;
						Particle rightDown = (i + 1 < width && k - 1 >= 0) ? body[k - 1][j][i + 1] : null;
						Particle leftUp = (i - 1 >= 0 && k + 1 < depth) ? body[k + 1][j][i - 1] : null;
						Particle rightUp = (i + 1 < width && k + 1 < depth) ? body[k + 1][j][i + 1] : null;
						
						Particle bottomDown = (j - 1 >= 0 && k - 1 >= 0) ? body[k - 1][j - 1][i] : null;
						Particle topDown = (j + 1 < height && k - 1 >= 0) ? body[k - 1][j + 1][i] : null;
						Particle bottomUp = (j - 1 >= 0 && k + 1 < depth) ? body[k + 1][j - 1][i] : null;
						Particle topUp = (j + 1 < height && k + 1 < depth) ? body[k + 1][j + 1][i] : null;
						
						if (leftBottom != null && !hasJoint(leftBottom, center)) joints.add(new Spring(leftBottom, center));
						if (rightBottom != null && !hasJoint(rightBottom, center)) joints.add(new Spring(center, rightBottom));
						if (leftTop != null && !hasJoint(leftTop, center)) joints.add(new Spring(leftTop, center));
						if (rightTop != null && !hasJoint(rightTop, center)) joints.add(new Spring(center, rightTop));
						
						if (leftDown != null && !hasJoint(leftDown, center)) joints.add(new Spring(leftDown, center));
						if (rightDown != null && !hasJoint(rightDown, center)) joints.add(new Spring(center, rightDown));
						if (leftUp != null && !hasJoint(leftUp, center)) joints.add(new Spring(leftUp, center));
						if (rightUp != null && !hasJoint(rightUp, center)) joints.add(new Spring(center, rightUp));
						
						if (bottomDown != null && !hasJoint(bottomDown, center)) joints.add(new Spring(bottomDown, center));
						if (topDown != null && !hasJoint(topDown, center)) joints.add(new Spring(center, topDown));
						if (bottomUp != null && !hasJoint(bottomUp, center)) joints.add(new Spring(bottomUp, center));
						if (topUp != null && !hasJoint(topUp, center)) joints.add(new Spring(center, topUp));
					}
				}
		for (Spring s : joints) s.setColor(color);
	}
	
	public SoftBody locateAsVertices() {
		if (!init) throw new RuntimeException("Soft Body is not initialized");
		
		Particle LBD = vertices.get("LBD"), RBD = vertices.get("RBD"), LTD = vertices.get("LTD"), RTD = vertices.get("RTD");
		Particle LBU = vertices.get("LBU"), RBU = vertices.get("RBU"), LTU = vertices.get("LTU"), RTU = vertices.get("RTU");
		
		PVector drLBDU = isReady3D() ? PVector.sub(LBU.getPos(), LBD.getPos()).div(depth - 1) : null;
		PVector drRBDU = isReady3D() ? PVector.sub(RBU.getPos(), RBD.getPos()).div(depth - 1) : null;
		PVector drLTDU = isReady3D() ? PVector.sub(LTU.getPos(), LTD.getPos()).div(depth - 1) : null;
		PVector drRTDU = isReady3D() ? PVector.sub(RTU.getPos(), RTD.getPos()).div(depth - 1) : null;
		
		for (int k = 0; k < depth; k++) {
			Particle LB = body[k][0][0], RB = body[k][0][width - 1];
			Particle LT = body[k][height - 1][0], RT = body[k][height - 1][width - 1];
			if (drLBDU != null) LB.setPos(PVector.add(LBD.getPos(), PVector.mult(drLBDU, k)));
			if (drRBDU != null) RB.setPos(PVector.add(RBD.getPos(), PVector.mult(drRBDU, k)));
			if (drLTDU != null) LT.setPos(PVector.add(LTD.getPos(), PVector.mult(drLTDU, k)));
			if (drRTDU != null) RT.setPos(PVector.add(RTD.getPos(), PVector.mult(drRTDU, k)));
			
			PVector drLBT = isReady2D() ? PVector.sub(LT.getPos(), LB.getPos()).div(height - 1) : null;
			PVector drRBT = isReady2D() ? PVector.sub(RT.getPos(), RB.getPos()).div(height - 1) : null;
			for (int j = 0; j < height; j++) {
				Particle L = body[k][j][0], R = body[k][j][width - 1];
				if (drLBT != null) L.setPos(PVector.add(LB.getPos(), PVector.mult(drLBT, j)));
				if (drRBT != null) R.setPos(PVector.add(RB.getPos(), PVector.mult(drRBT, j)));
				
				PVector drLR = PVector.sub(R.getPos(), L.getPos()).div(width - 1);
				for (int i = 1; i < width - 1; i++)
					body[k][j][i].setPos(PVector.add(L.getPos(), PVector.mult(drLR, i))).setVel(0, 0, 0);
				
			}
		}
		adjustEquilibrium();
		return this;
	}
	
	public PApplet getParent() {return parent;}
	//public Environment.DIMENSION getDim() {return dim;}
	public Particle[][][] getBody() {return body;}
	public List<Particle> getLinealBody() {return linealBody;}
	public List<Spring> getJoints() {return joints;}
	public Particle getVertex(String vertex) {return vertices.get(vertex);}
	public Particle getParticle(int i, int j, int k) {return body[k][j][i];}
	public Particle getParticle(int i, int j) {return getParticle(i, j, 0);}
	public Spring getJoint(int index) {return joints.get(index);}
	public static String[] getValidVertices() {return validVertices;}
	public float getMassDens() {return massDens;}
	public float getMass() {return massDens * getNMass() * getVolume();}
	public float getChargeDens() {return chargeDens;}
	public float getCharge() {return chargeDens * getNMass() * getVolume();}
	public float getRadius() {return radius;}
	public float getVolume() {return 4 * (float) Math.PI * radius * radius * radius / 3.0f;}
	public int getNMass() {return linealBody.size();}
	public int getNJoints() {return joints.size();}
	public int getWidth() {return width;}
	public int getHeight() {return height;}
	public int getDepth() {return depth;}
	public int getSize() {return width * height * depth;}
	public int getColor() {return color;}
	private int getVertexPlacement(String vertex) {for (int i = 0; i < validVertices.length; i++) if (validVertices[i].equals(vertex)) return i; throw new RuntimeException("Vertex" + vertex + "is not a valid vertex");}
	private boolean isVertexSorted(String vertex) {return vertices.size() == getVertexPlacement(vertex);}
	private boolean hasCapacity(String vertex) {
		if (vertex.equals("LBD") || vertex.equals("RBD")) return true;
		if (vertex.equals("LTD") || vertex.equals("RTD")) return width > 1 && height > 1;
		if (vertex.equals("LBU") || vertex.equals("RBU") || vertex.equals("LTU") || vertex.equals("RTU")) return width > 1 && height > 1 && depth > 1;
		return false;
	}
	public boolean hasJoint(Particle p1, Particle p2) {for (Spring s : joints) if (s.contains(p1, p2)) return true; return false;}
	public boolean hasVertex(String vertex) {return vertices.containsKey(vertex);}
	public boolean isVertexValid(String vertex) {for (String v : validVertices) if (v.equals(vertex)) return true; return false;}
	public boolean isReady1D() {return hasVertex("LBD") && hasVertex("RBD");}
	public boolean isReady2D() {return isReady1D() && hasVertex("LTD") && hasVertex("RTD");}
	public boolean isReady3D() {return isReady2D() && hasVertex("LBU") && hasVertex("RBU") && hasVertex("LTU") && hasVertex("RTU");}
	public boolean hasCrossJoints() {return crossJoints;}
	public boolean hasInitialized() {return init;}
	
	public SoftBody setVertex(String vertex, Particle p) {
		if (!isVertexValid(vertex)) throw new RuntimeException("Vertex " + vertex + " is not a valid vertex. Call SoftBody.validVertices() to get the sorted list of valid vertices");
		if (!isVertexSorted(vertex)) throw new RuntimeException("Vertex " + vertex + " is not sorted. Call SoftBody.validVertices() to get the sorted list of valid vertices");
		if (!hasCapacity(vertex)) throw new RuntimeException("The Soft Body dimensions dont allow a " + vertex + "vertex");
		vertices.put(vertex, p.setColor(color).setActive(false).setActiveLabel(actLabel));
		return this;
	}
	public SoftBody setVertex(String vertex, float x, float y, float z) {
		if (!vertices.containsKey(vertex)) return setVertex(vertex, Particle.still(parent, x, y, z, massDens, chargeDens, radius));
		vertices.get(vertex).setPos(x, y, z);
		return this;
	}
	public SoftBody setVertex(String vertex, float x, float y) {
		if (!vertices.containsKey(vertex)) return setVertex(vertex, Particle.still(parent, x, y, massDens, chargeDens, radius));
		vertices.get(vertex).setPos(x, y);
		return this;
	}
	public SoftBody setVertex(String vertex, PVector pos) {
		if (!vertices.containsKey(vertex)) return setVertex(vertex, Particle.still(parent, 0, 0, 0, massDens, chargeDens, radius).setPos(pos));
		vertices.get(vertex).setPos(pos);
		return this;
	}
	
	public SoftBody fixVertices() {for (String name : vertices.keySet()) vertices.get(name).setDynamic(false); return this;}
	public SoftBody releaseVertices() {for (String name : vertices.keySet()) vertices.get(name).setDynamic(true); return this;}
	public SoftBody setParent(PApplet p) {parent = p; return this;}
	public SoftBody setMassDens(float md) {massDens = md; for (Particle p : linealBody) p.setMassDens(md); return this;}
	public SoftBody setMass(float m) {massDens = m / (getSize() * getVolume()); for (Particle p : linealBody) p.setMass(m / getSize()); return this;}
	public SoftBody setChargeDens(float cd) {chargeDens = cd; for (Particle p : linealBody) p.setChargeDens(cd); return this;}
	public SoftBody setCharge(float c) {chargeDens = c / (getSize() * getVolume()); for (Particle p : linealBody) p.setCharge(c / getSize()); return this;}
	public SoftBody setRadius(float r) {radius = r; for (Particle p : linealBody) p.setRadius(r); return this;}
	public SoftBody setColor(int col) {color = col; for (Particle p : linealBody) p.setColor(col); for (Spring s : joints) s.setColor(col); return this;}
	public SoftBody setStiffness(float stf) {for (Spring s : joints) s.setStiffness(stf); return this;}
	public SoftBody setLength(float len) {for (Spring s : joints) s.setLength(len); return this;}
	public SoftBody setDampening(float damp) {for(Spring s : joints) s.setDampening(damp); return this;}
	public SoftBody setLengthUnitFactor(float fact) {for (Spring s : joints) s.setLengthUnitFactor(fact); return this;}
	public SoftBody setThickness(float t) {for (Spring s : joints) s.setThickness(t); return this;}
	public SoftBody setTerms(int trm) {for (Spring s : joints) s.setTerms(trm); return this;}
	public SoftBody setDecay(int dcy) {for (Spring s : joints) s.setDecay(dcy); return this;}
	public SoftBody setCrossJoints(boolean val) {crossJoints = val; return this;}
	private void adjustEquilibrium() {for (Spring s : joints) s.setLength(s.getFirst().getPos().dist(s.getSecond().getPos()));}
	
}
