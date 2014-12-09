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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.secretsharing.Part;
import org.secretsharing.Secrets;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

public class SplitServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Random rnd = new SecureRandom();
	
	public static class Request {
		public String secret;
		public Integer totalParts;
		public Integer requiredParts;
		public Boolean base64;
	}
	
	@JsonInclude(Include.NON_NULL)
	public static class Response {
		public String status;
		public List<String> parts;
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);

		resp.setContentType("application/json");
		
		try {
			Request jreq = mapper.readValue(req.getParameter("q"), Request.class);

			byte[] secret;
			
			if(jreq.base64 != null && jreq.base64)
				secret = Base64Variants.MIME_NO_LINEFEEDS.decode(jreq.secret);
			else
				secret = jreq.secret.getBytes("UTF-8");
			
			if(jreq.secret == null || jreq.totalParts == null || jreq.requiredParts == null)
				throw new IllegalArgumentException();

			Part[] parts = Secrets.split(secret, jreq.totalParts, jreq.requiredParts, rnd);

			Response jresp = new Response();
			jresp.parts = new ArrayList<String>();
			for(Part part : parts)
				jresp.parts.add(part.toString());
			jresp.status = "ok";

			mapper.writeValue(resp.getOutputStream(), jresp);
		} catch(Throwable t) {
			t.printStackTrace();
			
			Response jresp = new Response();
			jresp.status = "error";
			
			mapper.writeValue(resp.getOutputStream(), jresp);
		}
	}
}
