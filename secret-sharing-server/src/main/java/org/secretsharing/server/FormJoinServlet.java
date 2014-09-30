package org.secretsharing.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.secretsharing.SecretPart;
import org.secretsharing.Secrets;
import org.secretsharing.codec.Base32;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

public class FormJoinServlet extends HttpServlet {
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

			List<SecretPart> partsBytes = new ArrayList<SecretPart>();
			for(String s : parts.split("\n")) {
				s = s.trim();
				if(s.isEmpty())
					continue;
				try {
					partsBytes.add(new SecretPart(s));
				} catch(Exception e) {
					throw new RuntimeException("Corrupt key part \"" + s + "\"" + (
							e.getMessage() == null ? 
									": Improper encoding of secret parts" : 
									": " + e.getMessage()), e);
				}
			}

			byte[] secret = Secrets.join(partsBytes.toArray(new SecretPart[0]));

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
