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
