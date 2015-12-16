package org.mitre.secretsharing.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mitre.secretsharing.Part;
import org.mitre.secretsharing.Secrets;
import org.mitre.secretsharing.codec.PartFormats;

import com.fasterxml.jackson.core.Base64Variants;

public class JoinLinesServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			BufferedReader lines = new BufferedReader(new InputStreamReader(req.getInputStream(), Charset.forName("UTF-8")));
			List<Part> parts = new ArrayList<>();
			for(String line = lines.readLine(); line != null; line = lines.readLine()) {
				if(line.trim().isEmpty())
					continue;
				parts.add(PartFormats.parse(line));
			}
			Part[] p = parts.toArray(new Part[0]);
			byte[] secret = p[0].join(Arrays.copyOfRange(p, 1, p.length));
			Writer writer = new OutputStreamWriter(resp.getOutputStream(), Charset.forName("UTF-8"));
			writer.write(Base64Variants.MIME.encode(secret));
			writer.flush();
		} catch(Exception e) {
		}
		resp.getOutputStream().close();
	}
}
