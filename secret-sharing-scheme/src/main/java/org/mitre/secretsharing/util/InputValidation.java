/*

Copyright 2016 The MITRE Corporation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

This project contains content developed by The MITRE Corporation. If this 
code is used in a deployment or embedded within another project, it is 
requested that you send an email to opensource@mitre.org in order to let 
us know where this software is being used.

 */

package org.mitre.secretsharing.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple class wrapping a {@link List} of {@link IllegalArgumentException}s,
 * used to perform input validation on method arguments.
 * @author Robin Kirkman
 *
 */
public class InputValidation {
	/**
	 * Create and return a new {@link InputValidation}
	 * @return A new {@link InputValidation}
	 */
	public static InputValidation begin() {
		return new InputValidation();
	}
	
	/**
	 * List of {@link IllegalArgumentException}, created lazily
	 */
	protected List<IllegalArgumentException> failures = null;

	/**
	 * Create a new {@link InputValidation}
	 */
	public InputValidation() {
	}

	/**
	 * Check whether {@code failureCase} is {@code true}, and if so, add {@code problem}
	 * (as a the message in a new {@link IllegalArgumentException}) to the
	 * failures list. 
	 * @param failureCase The result of a parameter test
	 * @param problem The problem detected by the test, if {@code true}
	 * @return This object, for method chaining
	 */
	public InputValidation when(boolean failureCase, String problem) {
		if(failureCase) {
			if(failures == null)
				failures = new ArrayList<IllegalArgumentException>();
			for(IllegalArgumentException f : failures) {
				if(problem.equals(f.getMessage()))
					return this;
			}
			failures.add(new IllegalArgumentException(problem));
		}
		return this;
	}
	
	/**
	 * If {@link #failed()}, then {@code throw} the return value of {@link #getFailure()}
	 * @throws InputValidationException If there was an input validation problem
	 */
	public InputValidation validate() {
		if(failed())
			throw getFailure();
		return this;
	}
	
	/**
	 * Returns whether there are any input failures
	 * @return {@code true} if there are failures
	 */
	public boolean failed() {
		return getFailures().size() > 0;
	}
	
	/**
	 * Returns an {@link IllegalArgumentException} suitable for throwing
	 * to indicate input validation problems.  If there have been no
	 * input validation problems, this method throws {@link IllegalStateException}
	 * instead of returning.  If there has been only one, it returns the
	 * {@link IllegalArgumentException} created at that time.  Otherwise,
	 * summarizes all failures and returns a new {@link IllegalArgumentException}
	 * with the summary as the message.
	 * @return An {@link IllegalArgumentException} summarizing the input validation failure
	 * @throws IllegalStateException If there were no input validation failures
	 */
	public InputValidationException getFailure() {
		if(!failed())
			throw new IllegalStateException("validation has not failed");
		return new InputValidationException(failures);
	}
	
	/**
	 * Returns the list of input validation failures, or {@link Collections#emptyList()}
	 * if there have been no failures.
	 * @return The input validation failures
	 */
	public List<IllegalArgumentException> getFailures() {
		if(failures == null)
			return Collections.emptyList();
		return failures;
	}
}
