/*
 * Copyright 2011-2018 joptimizer.com
 *
 * This work is licensed under the Creative Commons Attribution-NoDerivatives 4.0 
 * International License. To view a copy of this license, visit 
 *
 *        http://creativecommons.org/licenses/by-nd/4.0/ 
 *
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */
package com.joptimizer.functions;

import cern.colt.matrix.DoubleFactory1D;
import cern.colt.matrix.DoubleMatrix1D;


/**
 * f(x) = q.x + r
 * 
 * @author <a href="mailto:info@joptimizer.com">JOptimizer Team</a>
 */
public class LinearMultivariateRealFunction extends	QuadraticMultivariateRealFunction implements ConvexMultivariateRealFunction {

	public LinearMultivariateRealFunction(double[] qVector, double r) {
		this(DoubleFactory1D.dense.make(qVector), r);
	}
	
	public LinearMultivariateRealFunction(DoubleMatrix1D qVector, double r) {
		super(null, qVector, r);
	}
}
