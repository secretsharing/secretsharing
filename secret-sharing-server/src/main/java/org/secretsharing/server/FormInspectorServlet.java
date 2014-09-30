package org.secretsharing.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.secretsharing.SecretPart;

public class FormInspectorServlet extends HttpServlet {
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
			
			SecretPart part;
			try {
				part = new SecretPart(req.getParameter("part").trim());
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
