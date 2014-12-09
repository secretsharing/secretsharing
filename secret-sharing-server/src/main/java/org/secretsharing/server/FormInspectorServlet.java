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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.secretsharing.Part;

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
