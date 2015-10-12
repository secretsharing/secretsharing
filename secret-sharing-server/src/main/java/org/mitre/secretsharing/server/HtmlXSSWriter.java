package org.mitre.secretsharing.server;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.lang.StringEscapeUtils;

public class HtmlXSSWriter extends FilterWriter {

	public HtmlXSSWriter(Writer out) {
		super(out);
	}

	@Override
	public void write(int c) throws IOException {
		StringEscapeUtils.escapeHtml(out, Character.toString((char) c));
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		StringEscapeUtils.escapeHtml(out, new String(cbuf, off, len));
	}

	@Override
	public void write(String str, int off, int len) throws IOException {
		StringEscapeUtils.escapeHtml(out, str.substring(off, off + len));
	}

}
