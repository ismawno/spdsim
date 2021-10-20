package spdsim;
/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

/*
  Part of the Processing project - http://processing.org
  Copyright (c) 2012-16 The Processing Foundation
  Copyright (c) 2008-12 Ben Fry and Casey Reas
  Copyright (c) 2008 Dan Shiffman
  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License version 2.1 as published by the Free Software Foundation.
  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.
  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
 */

import processing.core.PApplet;
import processing.core.PConstants;

/**
 * ( begin auto-generated from PVectorD.xml )
 *
 * A class to describe a two or three dimensional vector. This datatype
 * stores two or three variables that are commonly used as a position,
 * velocity, and/or acceleration. Technically, <em>position</em> is a point
 * and <em>velocity</em> and <em>acceleration</em> are vectors, but this is
 * often simplified to consider all three as vectors. For example, if you
 * consider a rectangle moving across the screen, at any given instant it
 * has a position (the object's location, expressed as a point.), a
 * velocity (the rate at which the object's position changes per time unit,
 * expressed as a vector), and acceleration (the rate at which the object's
 * velocity changes per time unit, expressed as a vector). Since vectors
 * represent groupings of values, we cannot simply use traditional
 * addition/multiplication/etc. Instead, we'll need to do some "vector"
 * math, which is made easy by the methods inside the <b>PVectorD</b>
 * class.<br />
 * <br />
 * The methods for this class are extensive. For a complete list, visit the
 * <a
 * href="http://processing.googlecode.com/svn/trunk/processing/build/javadoc/core/">developer's reference.</a>
 *
 * ( end auto-generated )
 *
 * A class to describe a two or three dimensional vector.
 * <p>
 * The result of all functions are applied to the vector itself, with the
 * exception of cross(), which returns a new PVectorD (or writes to a specified
 * 'target' PVectorD). That is, add() will add the contents of one vector to
 * this one. Using add() with additional parameters allows you to put the
 * result into a new PVectorD. Functions that act on multiple vectors also
 * include static versions. Because creating new objects can be computationally
 * expensive, most functions include an optional 'target' PVectorD, so that a
 * new PVectorD object is not created with each operation.
 * <p>
 * Initially based on the Vector3D class by <a href="http://www.shiffman.net">Dan Shiffman</a>.
 *
 * @webref math
 */
public class PVectorD {
  /**
   * ( begin auto-generated from PVectorD_x.xml )
   *
   * The x component of the vector. This field (variable) can be used to both
   * get and set the value (see above example.)
   *
   * ( end auto-generated )
   *
   * @webref pvector:field
   * @usage web_application
   * @brief The x component of the vector
   */
  public double x;

  /**
   * ( begin auto-generated from PVectorD_y.xml )
   *
   * The y component of the vector. This field (variable) can be used to both
   * get and set the value (see above example.)
   *
   * ( end auto-generated )
   *
   * @webref pvector:field
   * @usage web_application
   * @brief The y component of the vector
   */
  public double y;

  /**
   * ( begin auto-generated from PVectorD_z.xml )
   *
   * The z component of the vector. This field (variable) can be used to both
   * get and set the value (see above example.)
   *
   * ( end auto-generated )
   *
   * @webref pvector:field
   * @usage web_application
   * @brief The z component of the vector
   */
  public double z;

  /** Array so that this can be temporarily used in an array context */
  transient protected double[] array;


  /**
   * Constructor for an empty vector: x, y, and z are set to 0.
   */
  public PVectorD() {
  }


  /**
   * Constructor for a 3D vector.
   *
   * @param  x the x coordinate.
   * @param  y the y coordinate.
   * @param  z the z coordinate.
   */
  public PVectorD(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }


  /**
   * Constructor for a 2D vector: z coordinate is set to 0.
   */
  public PVectorD(double x, double y) {
    this.x = x;
    this.y = y;
    this.z = 0;
  }


  /**
   * ( begin auto-generated from PVectorD_set.xml )
   *
   * Sets the x, y, and z component of the vector using two or three separate
   * variables, the data from a PVectorD, or the values from a double array.
   *
   * ( end auto-generated )
   *
   * @webref pvector:method
   * @param x the x component of the vector
   * @param y the y component of the vector
   * @param z the z component of the vector
   * @brief Set the components of the vector
   */
  public PVectorD set(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
    return this;
  }


  /**
   * @param x the x component of the vector
   * @param y the y component of the vector
   */
  public PVectorD set(double x, double y) {
    this.x = x;
    this.y = y;
    return this;
  }


  /**
   * @param v any variable of type PVectorD
   */
  public PVectorD set(PVectorD v) {
    x = v.x;
    y = v.y;
    z = v.z;
    return this;
  }


  /**
   * Set the x, y (and maybe z) coordinates using a double[] array as the source.
   * @param source array to copy from
   */
  public PVectorD set(double[] source) {
    if (source.length >= 2) {
      x = source[0];
      y = source[1];
    }
    if (source.length >= 3) {
      z = source[2];
    }
    return this;
  }


  /**
   * ( begin auto-generated from PVectorD_random2D.xml )
   *
   * Make a new 2D unit vector with a random direction.  If you pass in "this"
   * as an argument, it will use the PApplet's random number generator.  You can
   * also pass in a target PVectorD to fill.
   *
   * @webref pvector:method
   * @usage web_application
   * @return the random PVectorD
   * @brief Make a new 2D unit vector with a random direction.
   * @see PVectorD#random3D()
   */
  static public PVectorD random2D() {
    return random2D(null, null);
  }


  /**
   * Make a new 2D unit vector with a random direction
   * using Processing's current random number generator
   * @param parent current PApplet instance
   * @return the random PVectorD
   */
  static public PVectorD random2D(PApplet parent) {
    return random2D(null, parent);
  }

  /**
   * Set a 2D vector to a random unit vector with a random direction
   * @param target the target vector (if null, a new vector will be created)
   * @return the random PVectorD
   */
  static public PVectorD random2D(PVectorD target) {
    return random2D(target, null);
  }


  /**
   * Make a new 2D unit vector with a random direction. Pass in the parent
   * PApplet if you want randomSeed() to work (and be predictable). Or leave
   * it null and be... random.
   * @return the random PVectorD
   */
  static public PVectorD random2D(PVectorD target, PApplet parent) {
    return (parent == null) ?
      fromAngle((double) (Math.random() * Math.PI*2), target) :
      fromAngle(parent.random(PConstants.TAU), target);
  }


  /**
   * ( begin auto-generated from PVectorD_random3D.xml )
   *
   * Make a new 3D unit vector with a random direction.  If you pass in "this"
   * as an argument, it will use the PApplet's random number generator.  You can
   * also pass in a target PVectorD to fill.
   *
   * @webref pvector:method
   * @usage web_application
   * @return the random PVectorD
   * @brief Make a new 3D unit vector with a random direction.
   * @see PVectorD#random2D()
   */
  static public PVectorD random3D() {
    return random3D(null, null);
  }


  /**
   * Make a new 3D unit vector with a random direction
   * using Processing's current random number generator
   * @param parent current PApplet instance
   * @return the random PVectorD
   */
  static public PVectorD random3D(PApplet parent) {
    return random3D(null, parent);
  }


  /**
   * Set a 3D vector to a random unit vector with a random direction
   * @param target the target vector (if null, a new vector will be created)
   * @return the random PVectorD
   */
  static public PVectorD random3D(PVectorD target) {
    return random3D(target, null);
  }


  /**
   * Make a new 3D unit vector with a random direction
   * @return the random PVectorD
   */
  static public PVectorD random3D(PVectorD target, PApplet parent) {
    double angle;
    double vz;
    if (parent == null) {
      angle = (Math.random()*Math.PI*2);
      vz    = (Math.random()*2-1);
    } else {
      angle = parent.random(PConstants.TWO_PI);
      vz    = parent.random(-1,1);
    }
    double vx = (Math.sqrt(1-vz*vz)*Math.cos(angle));
    double vy = (Math.sqrt(1-vz*vz)*Math.sin(angle));
    if (target == null) {
      target = new PVectorD(vx, vy, vz);
      //target.normalize(); // Should be unnecessary
    } else {
      target.set(vx,vy,vz);
    }
    return target;
  }


  /**
   * ( begin auto-generated from PVectorD_sub.xml )
   *
   * Make a new 2D unit vector from an angle.
   *
   * ( end auto-generated )
   *
   * @webref pvector:method
   * @usage web_application
   * @brief Make a new 2D unit vector from an angle
   * @param angle the angle in radians
   * @return the new unit PVectorD
   */
  static public PVectorD fromAngle(double angle) {
    return fromAngle(angle,null);
  }


  /**
   * Make a new 2D unit vector from an angle
   *
   * @param target the target vector (if null, a new vector will be created)
   * @return the PVectorD
   */
  static public PVectorD fromAngle(double angle, PVectorD target) {
    if (target == null) {
      target = new PVectorD((double)Math.cos(angle),(double)Math.sin(angle),0);
    } else {
      target.set((double)Math.cos(angle),(double)Math.sin(angle),0);
    }
    return target;
  }


  /**
   * ( begin auto-generated from PVectorD_copy.xml )
   *
   * Gets a copy of the vector, returns a PVectorD object.
   *
   * ( end auto-generated )
   *
   * @webref pvector:method
   * @usage web_application
   * @brief Get a copy of the vector
   */
  public PVectorD copy() {
    return new PVectorD(x, y, z);
  }


  @Deprecated
  public PVectorD get() {
    return copy();
  }


  /**
   * @param target
   */
  public double[] get(double[] target) {
    if (target == null) {
      return new double[] { x, y, z };
    }
    if (target.length >= 2) {
      target[0] = x;
      target[1] = y;
    }
    if (target.length >= 3) {
      target[2] = z;
    }
    return target;
  }


  /**
   * ( begin auto-generated from PVectorD_mag.xml )
   *
   * Calculates the magnitude (length) of the vector and returns the result
   * as a double (this is simply the equation <em>sqrt(x*x + y*y + z*z)</em>.)
   *
   * ( end auto-generated )
   *
   * @webref pvector:method
   * @usage web_application
   * @brief Calculate the magnitude of the vector
   * @return magnitude (length) of the vector
   * @see PVectorD#magSq()
   */
  public double mag() {
    return Math.sqrt(x*x + y*y + z*z);
  }


  /**
   * ( begin auto-generated from PVectorD_mag.xml )
   *
   * Calculates the squared magnitude of the vector and returns the result
   * as a double (this is simply the equation <em>(x*x + y*y + z*z)</em>.)
   * Faster if the real length is not required in the
   * case of comparing vectors, etc.
   *
   * ( end auto-generated )
   *
   * @webref pvector:method
   * @usage web_application
   * @brief Calculate the magnitude of the vector, squared
   * @return squared magnitude of the vector
   * @see PVectorD#mag()
   */
  public double magSq() {
    return (x*x + y*y + z*z);
  }


  /**
   * ( begin auto-generated from PVectorD_add.xml )
   *
   * Adds x, y, and z components to a vector, adds one vector to another, or
   * adds two independent vectors together. The version of the method that
   * adds two vectors together is a static method and returns a PVectorD, the
   * others have no return value -- they act directly on the vector. See the
   * examples for more context.
   *
   * ( end auto-generated )
   *
   * @webref pvector:method
   * @usage web_application
   * @param v the vector to be added
   * @brief Adds x, y, and z components to a vector, one vector to another, or two independent vectors
   */
  public PVectorD add(PVectorD v) {
    x += v.x;
    y += v.y;
    z += v.z;
    return this;
  }


  /**
   * @param x x component of the vector
   * @param y y component of the vector
   */
  public PVectorD add(double x, double y) {
    this.x += x;
    this.y += y;
    return this;
  }


  /**
   * @param z z component of the vector
   */
  public PVectorD add(double x, double y, double z) {
    this.x += x;
    this.y += y;
    this.z += z;
    return this;
  }


  /**
   * Add two vectors
   * @param v1 a vector
   * @param v2 another vector
   */
  static public PVectorD add(PVectorD v1, PVectorD v2) {
    return add(v1, v2, null);
  }


  /**
   * Add two vectors into a target vector
   * @param target the target vector (if null, a new vector will be created)
   */
  static public PVectorD add(PVectorD v1, PVectorD v2, PVectorD target) {
    if (target == null) {
      target = new PVectorD(v1.x + v2.x,v1.y + v2.y, v1.z + v2.z);
    } else {
      target.set(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
    }
    return target;
  }


  /**
   * ( begin auto-generated from PVectorD_sub.xml )
   *
   * Subtracts x, y, and z components from a vector, subtracts one vector
   * from another, or subtracts two independent vectors. The version of the
   * method that subtracts two vectors is a static method and returns a
   * PVectorD, the others have no return value -- they act directly on the
   * vector. See the examples for more context.
   *
   * ( end auto-generated )
   *
   * @webref pvector:method
   * @usage web_application
   * @param v any variable of type PVectorD
   * @brief Subtract x, y, and z components from a vector, one vector from another, or two independent vectors
   */
  public PVectorD sub(PVectorD v) {
    x -= v.x;
    y -= v.y;
    z -= v.z;
    return this;
  }


  /**
   * @param x the x component of the vector
   * @param y the y component of the vector
   */
  public PVectorD sub(double x, double y) {
    this.x -= x;
    this.y -= y;
    return this;
  }


  /**
   * @param z the z component of the vector
   */
  public PVectorD sub(double x, double y, double z) {
    this.x -= x;
    this.y -= y;
    this.z -= z;
    return this;
  }


  /**
   * Subtract one vector from another
   * @param v1 the x, y, and z components of a PVectorD object
   * @param v2 the x, y, and z components of a PVectorD object
   */
  static public PVectorD sub(PVectorD v1, PVectorD v2) {
    return sub(v1, v2, null);
  }


  /**
   * Subtract one vector from another and store in another vector
   * @param target PVectorD in which to store the result
   */
  static public PVectorD sub(PVectorD v1, PVectorD v2, PVectorD target) {
    if (target == null) {
      target = new PVectorD(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
    } else {
      target.set(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
    }
    return target;
  }


  /**
   * ( begin auto-generated from PVectorD_mult.xml )
   *
   * Multiplies a vector by a scalar or multiplies one vector by another.
   *
   * ( end auto-generated )
   *
   * @webref pvector:method
   * @usage web_application
   * @brief Multiply a vector by a scalar
   * @param n the number to multiply with the vector
   */
  public PVectorD mult(double n) {
    x *= n;
    y *= n;
    z *= n;
    return this;
  }


  /**
   * @param v the vector to multiply by the scalar
   */
  static public PVectorD mult(PVectorD v, double n) {
    return mult(v, n, null);
  }


  /**
   * Multiply a vector by a scalar, and write the result into a target PVectorD.
   * @param target PVectorD in which to store the result
   */
  static public PVectorD mult(PVectorD v, double n, PVectorD target) {
    if (target == null) {
      target = new PVectorD(v.x*n, v.y*n, v.z*n);
    } else {
      target.set(v.x*n, v.y*n, v.z*n);
    }
    return target;
  }


  /**
   * ( begin auto-generated from PVectorD_div.xml )
   *
   * Divides a vector by a scalar or divides one vector by another.
   *
   * ( end auto-generated )
   *
   * @webref pvector:method
   * @usage web_application
   * @brief Divide a vector by a scalar
   * @param n the number by which to divide the vector
   */
  public PVectorD div(double n) {
    x /= n;
    y /= n;
    z /= n;
    return this;
  }


  /**
   * Divide a vector by a scalar and return the result in a new vector.
   * @param v the vector to divide by the scalar
   * @return a new vector that is v1 / n
   */
  static public PVectorD div(PVectorD v, double n) {
    return div(v, n, null);
  }


  /**
   * Divide a vector by a scalar and store the result in another vector.
   * @param target PVectorD in which to store the result
   */
  static public PVectorD div(PVectorD v, double n, PVectorD target) {
    if (target == null) {
      target = new PVectorD(v.x/n, v.y/n, v.z/n);
    } else {
      target.set(v.x/n, v.y/n, v.z/n);
    }
    return target;
  }


  /**
   * ( begin auto-generated from PVectorD_dist.xml )
   *
   * Calculates the Euclidean distance between two points (considering a
   * point as a vector object).
   *
   * ( end auto-generated )
   *
   * @webref pvector:method
   * @usage web_application
   * @param v the x, y, and z coordinates of a PVectorD
   * @brief Calculate the distance between two points
   */
  public double dist(PVectorD v) {
    double dx = x - v.x;
    double dy = y - v.y;
    double dz = z - v.z;
    return Math.sqrt(dx*dx + dy*dy + dz*dz);
  }


  /**
   * @param v1 any variable of type PVectorD
   * @param v2 any variable of type PVectorD
   * @return the Euclidean distance between v1 and v2
   */
  static public double dist(PVectorD v1, PVectorD v2) {
    double dx = v1.x - v2.x;
    double dy = v1.y - v2.y;
    double dz = v1.z - v2.z;
    return Math.sqrt(dx*dx + dy*dy + dz*dz);
  }
  static public double dist(double x1, double y1, double z1, double x2, double y2, double z2) {
	  double dx = x1 - x2;
	  double dy = y1 - y2;
	  double dz = z1 - z2;
	  return Math.sqrt(dx*dx + dy*dy + dz*dz);
  }
  static public double dist(double x1, double y1, double x2, double y2) {
	  double dx = x1 - x2;
	  double dy = y1 - y2;
	  return Math.sqrt(dx*dx + dy*dy);
  }


  /**
   * ( begin auto-generated from PVectorD_dot.xml )
   *
   * Calculates the dot product of two vectors.
   *
   * ( end auto-generated )
   *
   * @webref pvector:method
   * @usage web_application
   * @param v any variable of type PVectorD
   * @return the dot product
   * @brief Calculate the dot product of two vectors
   */
  public double dot(PVectorD v) {
    return x*v.x + y*v.y + z*v.z;
  }


  /**
   * @param x x component of the vector
   * @param y y component of the vector
   * @param z z component of the vector
   */
  public double dot(double x, double y, double z) {
    return this.x*x + this.y*y + this.z*z;
  }


  /**
   * @param v1 any variable of type PVectorD
   * @param v2 any variable of type PVectorD
   */
  static public double dot(PVectorD v1, PVectorD v2) {
    return v1.x*v2.x + v1.y*v2.y + v1.z*v2.z;
  }


  /**
   * ( begin auto-generated from PVectorD_cross.xml )
   *
   * Calculates and returns a vector composed of the cross product between
   * two vectors.
   *
   * ( end auto-generated )
   *
   * @webref pvector:method
   * @param v the vector to calculate the cross product
   * @brief Calculate and return the cross product
   */
  public PVectorD cross(PVectorD v) {
    return cross(v, null);
  }


  /**
   * @param v any variable of type PVectorD
   * @param target PVectorD to store the result
   */
  public PVectorD cross(PVectorD v, PVectorD target) {
    double crossX = y * v.z - v.y * z;
    double crossY = z * v.x - v.z * x;
    double crossZ = x * v.y - v.x * y;

    if (target == null) {
      target = new PVectorD(crossX, crossY, crossZ);
    } else {
      target.set(crossX, crossY, crossZ);
    }
    return target;
  }


  /**
   * @param v1 any variable of type PVectorD
   * @param v2 any variable of type PVectorD
   * @param target PVectorD to store the result
   */
  static public PVectorD cross(PVectorD v1, PVectorD v2, PVectorD target) {
    double crossX = v1.y * v2.z - v2.y * v1.z;
    double crossY = v1.z * v2.x - v2.z * v1.x;
    double crossZ = v1.x * v2.y - v2.x * v1.y;

    if (target == null) {
      target = new PVectorD(crossX, crossY, crossZ);
    } else {
      target.set(crossX, crossY, crossZ);
    }
    return target;
  }


  /**
   * ( begin auto-generated from PVectorD_normalize.xml )
   *
   * Normalize the vector to length 1 (make it a unit vector).
   *
   * ( end auto-generated )
   *
   * @webref pvector:method
   * @usage web_application
   * @brief Normalize the vector to a length of 1
   */
  public PVectorD normalize() {
    double m = mag();
    if (m != 0 && m != 1) {
      div(m);
    }
    return this;
  }


  /**
   * @param target Set to null to create a new vector
   * @return a new vector (if target was null), or target
   */
  public PVectorD normalize(PVectorD target) {
    if (target == null) {
      target = new PVectorD();
    }
    double m = mag();
    if (m > 0) {
      target.set(x/m, y/m, z/m);
    } else {
      target.set(x, y, z);
    }
    return target;
  }


  /**
   * ( begin auto-generated from PVectorD_limit.xml )
   *
   * Limit the magnitude of this vector to the value used for the <b>max</b> parameter.
   *
   * ( end auto-generated )
   *
   * @webref pvector:method
   * @usage web_application
   * @param max the maximum magnitude for the vector
   * @brief Limit the magnitude of the vector
   */
  public PVectorD limit(double max) {
    if (magSq() > max*max) {
      normalize();
      mult(max);
    }
    return this;
  }


  /**
   * ( begin auto-generated from PVectorD_setMag.xml )
   *
   * Set the magnitude of this vector to the value used for the <b>len</b> parameter.
   *
   * ( end auto-generated )
   *
   * @webref pvector:method
   * @usage web_application
   * @param len the new length for this vector
   * @brief Set the magnitude of the vector
   */
  public PVectorD setMag(double len) {
    normalize();
    mult(len);
    return this;
  }


  /**
   * Sets the magnitude of this vector, storing the result in another vector.
   * @param target Set to null to create a new vector
   * @param len the new length for the new vector
   * @return a new vector (if target was null), or target
   */
  public PVectorD setMag(PVectorD target, double len) {
    target = normalize(target);
    target.mult(len);
    return target;
  }


  /**
   * ( begin auto-generated from PVectorD_setMag.xml )
   *
   * Calculate the angle of rotation for this vector (only 2D vectors)
   *
   * ( end auto-generated )
   *
   * @webref pvector:method
   * @usage web_application
   * @return the angle of rotation
   * @brief Calculate the angle of rotation for this vector
   */
  public double heading() {
    double angle = Math.atan2(y, x);
    return angle;
  }


  @Deprecated
  public double heading2D() {
    return heading();
  }


  /**
   * ( begin auto-generated from PVectorD_rotate.xml )
   *
   * Rotate the vector by an angle (only 2D vectors), magnitude remains the same
   *
   * ( end auto-generated )
   *
   * @webref pvector:method
   * @usage web_application
   * @brief Rotate the vector by an angle (2D only)
   * @param theta the angle of rotation
   */
  public PVectorD rotate(float theta) {
    double temp = x;
    // Might need to check for rounding errors like with angleBetween function?
    x = x*PApplet.cos(theta) - y*PApplet.sin(theta);
    y = temp*PApplet.sin(theta) + y*PApplet.cos(theta);
    return this;
  }


  /**
   * ( begin auto-generated from PVectorD_rotate.xml )
   *
   * Linear interpolate the vector to another vector
   *
   * ( end auto-generated )
   *
   * @webref pvector:method
   * @usage web_application
   * @brief Linear interpolate the vector to another vector
   * @param v the vector to lerp to
   * @param amt  The amount of interpolation; some value between 0.0 (old vector) and 1.0 (new vector). 0.1 is very near the old vector; 0.5 is halfway in between.
   * @see PApplet#lerp(double, double, double)
   */

  /**
   * ( begin auto-generated from PVectorD_angleBetween.xml )
   *
   * Calculates and returns the angle (in radians) between two vectors.
   *
   * ( end auto-generated )
   *
   * @webref pvector:method
   * @usage web_application
   * @param v1 the x, y, and z components of a PVectorD
   * @param v2 the x, y, and z components of a PVectorD
   * @brief Calculate and return the angle between two vectors
   */
  static public double angleBetween(PVectorD v1, PVectorD v2) {

    // We get NaN if we pass in a zero vector which can cause problems
    // Zero seems like a reasonable angle between a (0,0,0) vector and something else
    if (v1.x == 0 && v1.y == 0 && v1.z == 0 ) return 0.0f;
    if (v2.x == 0 && v2.y == 0 && v2.z == 0 ) return 0.0f;

    double dot = v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    double v1mag = Math.sqrt(v1.x * v1.x + v1.y * v1.y + v1.z * v1.z);
    double v2mag = Math.sqrt(v2.x * v2.x + v2.y * v2.y + v2.z * v2.z);
    // This should be a number between -1 and 1, since it's "normalized"
    double amt = dot / (v1mag * v2mag);
    // But if it's not due to rounding error, then we need to fix it
    // http://code.google.com/p/processing/issues/detail?id=340
    // Otherwise if outside the range, acos() will return NaN
    // http://www.cppreference.com/wiki/c/math/acos
    if (amt <= -1) {
      return PConstants.PI;
    } else if (amt >= 1) {
      // http://code.google.com/p/processing/issues/detail?id=435
      return 0;
    }
    return Math.acos(amt);
  }


  @Override
  public String toString() {
    return "[ " + x + ", " + y + ", " + z + " ]";
  }


  /**
   * ( begin auto-generated from PVectorD_array.xml )
   *
   * Return a representation of this vector as a double array. This is only
   * for temporary use. If used in any other fashion, the contents should be
   * copied by using the <b>PVectorD.get()</b> method to copy into your own array.
   *
   * ( end auto-generated )
   *
   * @webref pvector:method
   * @usage: web_application
   * @brief Return a representation of the vector as a double array
   */
  public double[] array() {
    if (array == null) {
      array = new double[3];
    }
    array[0] = x;
    array[1] = y;
    array[2] = z;
    return array;
  }


  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof PVectorD)) {
      return false;
    }
    final PVectorD p = (PVectorD) obj;
    return x == p.x && y == p.y && z == p.z;
  }

}