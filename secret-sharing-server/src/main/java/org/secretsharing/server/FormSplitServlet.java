/*

Copyright (c) 2014, The MITRE Corporation
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that 
the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
	in the documentation and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived 
	from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR 
BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 */

package org.secretsharing.server;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.secretsharing.Part;
import org.secretsharing.Secrets;
import com.fasterxml.jackson.core.Base64Variants;

public class FormSplitServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Random rnd = new SecureRandom();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			String secret = req.getParameter("secret");
			if(secret == null)
				throw new RuntimeException("No secret parameter");
			int totalParts;
			try {
				totalParts = Integer.parseInt(req.getParameter("total_parts"));
				if(totalParts < 1)
					throw new RuntimeException();
			} catch(Exception e) {
				throw new RuntimeException("Total parts not an integer at least 1.");
			}
			int requiredParts;
			try {
				requiredParts = Integer.parseInt(req.getParameter("required_parts"));
				if(requiredParts < 1 || requiredParts > totalParts)
					throw new RuntimeException();
			} catch(Exception e) {
				throw new RuntimeException("Required parts not an integer at least 1 and not more than total parts.");
			}
			boolean base64 = false;
			if(req.getParameter("base64") != null)
				base64 = Boolean.parseBoolean(req.getParameter("base64"));

			byte[] secretBytes;

			if(base64) {
				try {
					secretBytes = Base64Variants.MIME.decode(secret);
				} catch(Exception e) {
					throw new RuntimeException("Improper encoding of base64 secret");
				}
			} else
				secretBytes = secret.getBytes("UTF-8");

			Part[] parts = Secrets.split(secretBytes, totalParts, requiredParts, rnd);

			for(Part part : parts) {
				resp.getWriter().println(part);
			}
		} catch(Throwable t) {
			if(t.getMessage() != null)
				resp.getWriter().print(t.getMessage());
			else
				resp.getWriter().print("error");
		}
	}
}
