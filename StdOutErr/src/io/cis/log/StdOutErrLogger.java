package io.cis.log;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;

/*
 * Re-wire the standard out/err logs to logger
 * 
 * Thanks to slf4j, this logger can then be writing to the root log, a dedicated log
 * and to a rotating log file.
 * 
 * Implementation note: enum's implementation of singleton
 */
public enum StdOutErrLogger {
	INSTANCE;

	StdOutErrLogger() {
		final Logger logger = LoggerFactory.getLogger(StdOutErrLogger.class);

		System.setOut(new PrintStream(System.out) {
            public void print(final String string) {
                logger.info(string);
            }
            public void println(final String string) {
                logger.info(string);
            }
        });

        System.setErr(new PrintStream(System.err) {
            public void print(final String string) {
                logger.error(string);
            }
            public void println(final String string) {
                logger.error(string);
            }
        });
	}

}
