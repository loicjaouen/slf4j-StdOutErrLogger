package io.cis.log.test;


import io.cis.log.StdOutErrLogger;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Test stdout and stderr redirecton using slf4j / log4j 
 * 
 * @author - cis.io - loic jaouen 
 *
 */
public class StdOutErrLoggerTest {
	// three log file names
	static String rootLogFileName = "logs/logging.log";
	static String extraLogFileName = "logs/extra.log";
	static String stdLogFileName = "logs/stdouterr.log";

	String myExceptionText = "my exception";

	// would fit here normally, but just to test, it is in the second test
	// don't statically include; it bothers log4j
	//	StdOutErrLogger stdLog = StdOutErrLogger.INSTANCE;

	// own logger : logs to logging.log by config
	Logger log = LoggerFactory.getLogger(StdOutErrLoggerTest.class);
	// named logger : logs to extra.log by config
	Logger extralog = LoggerFactory.getLogger("namedlogger");

	boolean expectedStdLog = false;

	/*
	 * delete existing log files
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// delete log files
		String[] logFiles = { rootLogFileName, extraLogFileName, stdLogFileName};
		for (String fileName : logFiles) {
			File file = new File(fileName);
			file.delete();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/*
	 * check that the log files exist and are not empty
	 */
	void testFiles() {
		File rootLog = new File(rootLogFileName);
		Assert.assertTrue("expected root log file to be there", rootLog.exists());
		Assert.assertTrue("expected root log file to not be empty", rootLog.length()>0);
		File extraLog = new File(extraLogFileName);
		Assert.assertTrue("expected named log file to be there", extraLog.exists());
		Assert.assertTrue("expected named log file to not be empty", extraLog.length()>0);
		File stdLog = new File(stdLogFileName);
		Assert.assertTrue("expected std log file to be there", stdLog.exists());

		// read default logger for std out / err logs
		// 
		boolean hasStd = false;
		try {
			FileReader log = new FileReader(rootLog);
			BufferedReader br = new BufferedReader(log);
			try {
				String line = null;
				while ((line = br.readLine()) != null) {
					hasStd = hasStd || line.contains(myExceptionText);
				}
			} catch (IOException e) {
				Assert.fail("failed reading"+ rootLogFileName);
			}
		} catch (FileNotFoundException e) {
			Assert.fail(rootLogFileName +" is not readable");
		}

		if (expectedStdLog) {
			Assert.assertTrue("expected std out/err log file to not be empty", stdLog.length()>0);
			Assert.assertTrue("expected to find std statements in the log", hasStd);
		} else {
			Assert.assertTrue("expected std out/err log file to be empty", stdLog.length()==0);			
			Assert.assertFalse("expected not to find std statements in the log", hasStd);
		}
	}

	@Test
	public void testWithoutInstance() {
		// logs to this class' logger
		log.info("anything that glows is not gold");
		// log to the named logger
		extralog.info("... isn't gold");
		// log to the std out
		System.out.println("to std.out");
		// log to std err
		new Exception(myExceptionText).printStackTrace();

		// test if files have been written to
		testFiles();
	}

	@Test
	public void testInstance() {
		expectedStdLog = true;
		StdOutErrLogger logger = StdOutErrLogger.INSTANCE;
		testWithoutInstance();
	}

}
