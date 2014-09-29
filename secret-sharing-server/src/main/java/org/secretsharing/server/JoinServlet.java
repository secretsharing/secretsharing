package org.secretsharing.server;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.secretsharing.BytesSecrets;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

public class JoinServlet extends HttpServlet {
	public static class Request {
		public List<byte[]> parts;
		public Boolean base64;
	}
	
	@JsonInclude(Include.NON_NULL)
	public static class Response {
		public String status;
		public String secret;
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);

		try {
			Request jreq = mapper.readValue(req.getParameter("q"), Request.class);

			if(jreq.parts == null)
				throw new IllegalArgumentException();
			
			byte[] secret = BytesSecrets.join(jreq.parts.toArray(new byte[0][]));

			Response jresp = new Response();
			jresp.status = "ok";

			if(jreq.base64 != null && jreq.base64)
				jresp.secret = Base64Variants.MIME_NO_LINEFEEDS.encode(secret);
			else
				jresp.secret = new String(secret, "UTF-8");

			mapper.writeValue(resp.getOutputStream(), jresp);
		} catch(Throwable t) {
			t.printStackTrace();
			
			Response jresp = new Response();
			jresp.status = "error";
			
			mapper.writeValue(resp.getOutputStream(), jresp);
		}
	}
}
