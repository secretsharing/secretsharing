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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mitre.secretsharing.Part;
import org.mitre.secretsharing.Secrets;

import com.fasterxml.jackson.core.Base64Variants;

public class FormJoinServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			String parts = req.getParameter("parts");
			if(parts == null)
				throw new RuntimeException("No secret parts provided");
			
			boolean base64 = false;
			if(req.getParameter("base64") != null)
				base64 = Boolean.parseBoolean(req.getParameter("base64"));

			List<Part> partsBytes = new ArrayList<Part>();
			for(String s : parts.split("\n")) {
				s = s.trim();
				if(s.isEmpty())
					continue;
				try {
					partsBytes.add(new Part(s));
				} catch(Exception e) {
					throw new RuntimeException("Corrupt key part \"" + s + "\"" + (
							e.getMessage() == null ? 
									": Improper encoding of secret parts" : 
									": " + e.getMessage()), e);
				}
			}

			byte[] secret = Secrets.join(partsBytes.toArray(new Part[0]));

			if(base64)
				resp.getWriter().print(Base64Variants.MIME.encode(secret));
			else
				resp.getWriter().print(new String(secret, "UTF-8"));
		} catch(Throwable t) {
			if(t.getMessage() != null)
				resp.getWriter().print("error: " + t.getMessage());
			else
				resp.getWriter().print("error");
		}
	}
}
