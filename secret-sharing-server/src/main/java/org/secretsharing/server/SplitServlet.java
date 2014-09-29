package org.secretsharing.server;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.secretsharing.BytesSecrets;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

public class SplitServlet extends HttpServlet {
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
		public List<byte[]> parts;
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);

		try {
			Request jreq = mapper.readValue(req.getInputStream(), Request.class);

			byte[] secret;
			
			if(jreq.base64 != null && jreq.base64)
				secret = Base64Variants.MIME_NO_LINEFEEDS.decode(jreq.secret);
			else
				secret = jreq.secret.getBytes("UTF-8");
			
			if(jreq.secret == null || jreq.totalParts == null || jreq.requiredParts == null)
				throw new IllegalArgumentException();

			byte[][] parts = BytesSecrets.split(secret, jreq.totalParts, jreq.requiredParts, rnd);

			Response jresp = new Response();
			jresp.parts = Arrays.asList(parts);
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
