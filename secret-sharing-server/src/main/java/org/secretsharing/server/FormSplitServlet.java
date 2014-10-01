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
