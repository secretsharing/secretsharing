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

import java.util.Collection;

/**
 * Subclass of {@link IllegalArgumentException} to indicate input validation problems
 * @author Robin Kirkman
 *
 */
public class InputValidationException extends IllegalArgumentException {
	private static final long serialVersionUID = 0;
	
	public static String messages(Collection<? extends IllegalArgumentException> c) {
		if(c.size() == 0)
			return null;
		String m = "";
		for(IllegalArgumentException e : c)
			m += ", " + e.getMessage();
		return m.substring(2);
	}
	
	/**
	 * Create a new {@link InputValidationException}
	 * @param causes (possibly empty) collection of {@link IllegalArgumentException} that
	 * caused this {@link InputValidationException}
	 */
	public InputValidationException(Collection<? extends IllegalArgumentException> causes) {
		super(messages(causes), causes.size() == 1 ? causes.iterator().next() : null);
	}

}
