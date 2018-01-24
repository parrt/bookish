/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package us.parr.bookish.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class StreamVacuum implements Runnable {
	private StringBuilder buf = new StringBuilder();
	private BufferedReader in;
	private Thread sucker;
	public StreamVacuum(InputStream in) {
		this.in = new BufferedReader( new InputStreamReader(in, StandardCharsets.UTF_8) );
	}
	public void start() {
		sucker = new Thread(this);
		sucker.start();
	}
	@Override
	public void run() {
		try {
			append(in, buf);
		}
		catch (IOException ioe) {
			System.err.println("can't read output from process");
		}
	}

	/** wait for the thread to finish */
	public void join() throws InterruptedException {
		sucker.join();
	}

	@Override
	public String toString() {
		return buf.toString();
	}

	public static void append(BufferedReader in, StringBuilder buf) throws IOException {
		String line = in.readLine();
		while (line!=null) {
			buf.append(line);
			// NOTE: This appends a newline at EOF
			// regardless of whether or not the
			// input actually ended with a
			// newline.
			//
			// We should revisit this and read a
			// block at a time rather than a line
			// at a time, and change all tests
			// which rely on this behavior to
			// remove the trailing newline at EOF.
			//
			// When we fix this, we can remove the
			// TestOutputReading class entirely.
			buf.append('\n');
			line = in.readLine();
		}
	}
}
