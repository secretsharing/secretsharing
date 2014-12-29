/*

Copyright 2014 The MITRE Corporation

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

package org.mitre.secretsharing.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mitre.secretsharing.Part;

public class FormInspectorServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static enum Field {
		version,
		length,
		required,
		modulus,
		x,
		y,
		checksum,
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if(req.getParameter("part") == null)
			return;
		try {
			Field f = Field.valueOf(req.getParameter("field"));
			
			Part part;
			try {
				part = new Part(req.getParameter("part").trim());
			} catch(Exception e) {
				String em = (e.getMessage() != null) ? ": " + e.getMessage() : "";
				throw new RuntimeException("Corrupt secret part" + em, e);
			}
			
			switch(f) {
			case version:
				resp.getWriter().println(part.getVersion());
				break;
			case length:
				resp.getWriter().println(part.getLength());
				break;
			case required:
				resp.getWriter().println(part.getRequiredParts());
				break;
			case modulus:
				resp.getWriter().println(part.getModulus());
				break;
			case x:
				resp.getWriter().println(part.getPoint().getX());
				break;
			case y:
				resp.getWriter().println(part.getPoint().getY());
				break;
			case checksum:
				resp.getWriter().println(String.format("0x%04x", part.getChecksum().getChecksum()));
				break;
			}
			
		} catch(Throwable t) {
			if(t.getMessage() != null)
				resp.getWriter().print(t.getMessage());
			else
				resp.getWriter().print("error");
		}
	}
}
