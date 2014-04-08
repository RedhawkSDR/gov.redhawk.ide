/*******************************************************************************
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package gov.redhawk.mfile.parser.tests;

import gov.redhawk.mfile.parser.MFileParser;
import gov.redhawk.mfile.parser.ParseException;
import gov.redhawk.mfile.parser.model.MFile;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 */
public class MFileParserTest {

	@Test
	public void testParseComments() throws IOException, ParseException {
		MFile file = parseFile("comments.m");
		Assert.assertNotNull(file);
		Assert.assertNotNull(file.getFunction());
	}

	@Test
	public void testParseFunctionDef01() throws IOException, ParseException {
		MFile file = parseFile("functionDef01.m");
		Assert.assertNotNull(file);
		Assert.assertNotNull(file.getFunction());
	}

	@Test
	public void testParseFunctionDef02() throws IOException, ParseException {
		MFile file = parseFile("functionDef02.m");
		Assert.assertNotNull(file);
		Assert.assertNotNull(file.getFunction());
	}

	@Test
	public void testParseFunctionDef03() throws IOException, ParseException {
		MFile file = parseFile("functionDef03.m");
		Assert.assertNotNull(file);
		Assert.assertNotNull(file.getFunction());
	}

	@Test
	public void testParseFunctionDef04() throws IOException, ParseException {
		MFile file = parseFile("functionDef04.m");
		Assert.assertNotNull(file);
		Assert.assertNotNull(file.getFunction());
	}

	@Test
	public void testParseAmData() throws IOException, ParseException {
		MFile file = parseFile("amData.m");
		Assert.assertNotNull(file);
		Assert.assertNotNull(file.getFunction());
		Assert.assertEquals("amData", file.getFunction().getName());
		Assert.assertEquals(1, file.getFunction().getOutputs().size());
		Assert.assertEquals("rawData", file.getFunction().getOutputs().get(0));
		Assert.assertEquals(5, file.getFunction().getInputs().size());
		Assert.assertEquals("fs", file.getFunction().getInputs().get(0));
		Assert.assertEquals("fc", file.getFunction().getInputs().get(1));
		Assert.assertEquals("bw", file.getFunction().getInputs().get(2));
		Assert.assertEquals("T", file.getFunction().getInputs().get(3));
		Assert.assertEquals("sampleType", file.getFunction().getInputs().get(4));
	}

	@Test
	public void testParseAutofam() throws IOException, ParseException {
		MFile file = parseFile("autofam.m");
		Assert.assertNotNull(file);
		Assert.assertNotNull(file.getFunction());
		Assert.assertEquals("autofam", file.getFunction().getName());
		Assert.assertEquals(3, file.getFunction().getOutputs().size());
		Assert.assertEquals("Sx", file.getFunction().getOutputs().get(0));
		Assert.assertEquals("alphao", file.getFunction().getOutputs().get(1));
		Assert.assertEquals("fo", file.getFunction().getOutputs().get(2));
		Assert.assertEquals(4, file.getFunction().getInputs().size());
		Assert.assertEquals("x", file.getFunction().getInputs().get(0));
		Assert.assertEquals("fs", file.getFunction().getInputs().get(1));
		Assert.assertEquals("df", file.getFunction().getInputs().get(2));
		Assert.assertEquals("dalpha", file.getFunction().getInputs().get(3));
	}

	@Test
	public void testParseBpskData() throws IOException, ParseException {
		MFile file = parseFile("bpskData.m");
		Assert.assertNotNull(file);
		Assert.assertNotNull(file.getFunction());
		Assert.assertEquals("bpskData", file.getFunction().getName());
		Assert.assertEquals(5, file.getFunction().getInputs().size());
		Assert.assertEquals("fs", file.getFunction().getInputs().get(0));
		Assert.assertEquals("fc", file.getFunction().getInputs().get(1));
		Assert.assertEquals("rb", file.getFunction().getInputs().get(2));
		Assert.assertEquals("T", file.getFunction().getInputs().get(3));
		Assert.assertEquals("sampleType", file.getFunction().getInputs().get(4));
	}

	@Test
	public void testParseCumulant() throws IOException, ParseException {
		MFile file = parseFile("Cumulant.m");
		Assert.assertNotNull(file);
		Assert.assertNotNull(file.getFunction());
		Assert.assertEquals("Cumulant", file.getFunction().getName());
		Assert.assertEquals(5, file.getFunction().getOutputs().size());
		Assert.assertEquals("C20", file.getFunction().getOutputs().get(0));
		Assert.assertEquals("C21", file.getFunction().getOutputs().get(1));
		Assert.assertEquals("C40", file.getFunction().getOutputs().get(2));
		Assert.assertEquals("C41", file.getFunction().getOutputs().get(3));
		Assert.assertEquals("C42", file.getFunction().getOutputs().get(4));
		Assert.assertEquals(1, file.getFunction().getInputs().size());
		Assert.assertEquals("y", file.getFunction().getInputs().get(0));
	}

	@Test
	public void testParseCycloClassifier() throws IOException, ParseException {
		MFile file = parseFile("CycloClassifier.m");
		Assert.assertNotNull(file);
		Assert.assertNotNull(file.getFunction());
		Assert.assertEquals("CycloClassifier", file.getFunction().getName());
		Assert.assertEquals(1, file.getFunction().getOutputs().size());
		Assert.assertEquals("M", file.getFunction().getOutputs().get(0));
		Assert.assertEquals(1, file.getFunction().getInputs().size());
		Assert.assertEquals("scaledPoints", file.getFunction().getInputs().get(0));
	}

	@Test
	public void testParseDetectWithRTL_Cyclo() throws IOException, ParseException {
		MFile file = parseFile("detectWithRTL_Cyclo.m");
		Assert.assertNotNull(file);
		Assert.assertNotNull(file.getFunction());
		Assert.assertEquals("detectWithRTL_Cyclo", file.getFunction().getName());
		Assert.assertEquals(2, file.getFunction().getOutputs().size());
		Assert.assertEquals("Sxa", file.getFunction().getOutputs().get(0));
		Assert.assertEquals("Ia", file.getFunction().getOutputs().get(1));
		Assert.assertEquals(3, file.getFunction().getInputs().size());
		Assert.assertEquals("Ft", file.getFunction().getInputs().get(0));
		Assert.assertEquals("ds", file.getFunction().getInputs().get(1));
		Assert.assertEquals("BW", file.getFunction().getInputs().get(2));
	}

	@Test
	public void testParseDetectWithRTL() throws IOException, ParseException {
		MFile file = parseFile("detectWithRTL.m");
		Assert.assertNotNull(file);
		Assert.assertNotNull(file.getFunction());
		Assert.assertEquals("detectWithRTL", file.getFunction().getName());
		Assert.assertEquals(0, file.getFunction().getOutputs().size());
		Assert.assertEquals(3, file.getFunction().getInputs().size());
		Assert.assertEquals("Ft", file.getFunction().getInputs().get(0));
		Assert.assertEquals("ds", file.getFunction().getInputs().get(1));
		Assert.assertEquals("BW", file.getFunction().getInputs().get(2));
	}

	@Test
	public void testParseFftAvg() throws IOException, ParseException {
		MFile file = parseFile("fftAvg.m");
		Assert.assertNotNull(file);
		Assert.assertNotNull(file.getFunction());
		Assert.assertEquals("fftAvg", file.getFunction().getName());
		Assert.assertEquals(1, file.getFunction().getOutputs().size());
		Assert.assertEquals("output", file.getFunction().getOutputs().get(0));
		Assert.assertEquals(2, file.getFunction().getInputs().size());
		Assert.assertEquals("data", file.getFunction().getInputs().get(0));
		Assert.assertEquals("N", file.getFunction().getInputs().get(1));
	}

	@Test
	public void testParseFmData() throws IOException, ParseException {
		MFile file = parseFile("fmData.m");
		Assert.assertNotNull(file);
		Assert.assertNotNull(file.getFunction());
		Assert.assertEquals("fmData", file.getFunction().getName());
		Assert.assertEquals(1, file.getFunction().getOutputs().size());
		Assert.assertEquals("rawData", file.getFunction().getOutputs().get(0));
		Assert.assertEquals(5, file.getFunction().getInputs().size());
		Assert.assertEquals("fs", file.getFunction().getInputs().get(0));
		Assert.assertEquals("fc", file.getFunction().getInputs().get(1));
		Assert.assertEquals("bw", file.getFunction().getInputs().get(2));
		Assert.assertEquals("T", file.getFunction().getInputs().get(3));
		Assert.assertEquals("sampleType", file.getFunction().getInputs().get(4));
	}

	@Test
	public void testParseFreqShift() throws IOException, ParseException {
		MFile file = parseFile("freqShift.m");
		Assert.assertNotNull(file);
		Assert.assertNotNull(file.getFunction());
		Assert.assertEquals("freqShift", file.getFunction().getName());
		Assert.assertEquals(1, file.getFunction().getOutputs().size());
		Assert.assertEquals("output", file.getFunction().getOutputs().get(0));
		Assert.assertEquals(3, file.getFunction().getInputs().size());
		Assert.assertEquals("input", file.getFunction().getInputs().get(0));
		Assert.assertEquals("fo", file.getFunction().getInputs().get(1));
		Assert.assertEquals("Fs", file.getFunction().getInputs().get(2));
	}

	@Test
	public void testParseGenerateTemplates() throws IOException, ParseException {
		MFile file = parseFile("generateTemplates.m");
		Assert.assertNotNull(file);
		Assert.assertNotNull(file.getFunction());
		Assert.assertEquals("generateTemplates", file.getFunction().getName());
		Assert.assertEquals(1, file.getFunction().getOutputs().size());
		Assert.assertEquals("templates", file.getFunction().getOutputs().get(0));
		Assert.assertEquals(2, file.getFunction().getInputs().size());
		Assert.assertEquals("BlockSize", file.getFunction().getInputs().get(0));
		Assert.assertEquals("maxAvg", file.getFunction().getInputs().get(1));
	}

	@Test
	public void testParseManipulateCollect() throws IOException, ParseException {
		MFile file = parseFile("manipulateCollect.m");
		Assert.assertNotNull(file);
		Assert.assertNotNull(file.getFunction());
		Assert.assertEquals("manipulateCollect", file.getFunction().getName());
		Assert.assertEquals(0, file.getFunction().getOutputs().size());
		Assert.assertEquals(1, file.getFunction().getInputs().size());
		Assert.assertEquals("N", file.getFunction().getInputs().get(0));
	}

	@Test
	public void testParseMySxao() throws IOException, ParseException {
		MFile file = parseFile("mySxa.m");
		Assert.assertNotNull(file);
		Assert.assertNotNull(file.getFunction());
		Assert.assertEquals("mySxa", file.getFunction().getName());
		Assert.assertEquals(2, file.getFunction().getOutputs().size());
		Assert.assertEquals("Sxa", file.getFunction().getOutputs().get(0));
		Assert.assertEquals("Ia", file.getFunction().getOutputs().get(1));
		Assert.assertEquals(6, file.getFunction().getInputs().size());
		Assert.assertEquals("rawData", file.getFunction().getInputs().get(0));
		Assert.assertEquals("sampleType", file.getFunction().getInputs().get(1));
		Assert.assertEquals("Fc", file.getFunction().getInputs().get(2));
		Assert.assertEquals("Fs", file.getFunction().getInputs().get(3));
		Assert.assertEquals("BlockSize", file.getFunction().getInputs().get(4));
		Assert.assertEquals("maxAvg", file.getFunction().getInputs().get(5));
	}

	@Test
	public void testParseQamData() throws IOException, ParseException {
		MFile file = parseFile("qamData.m");
		Assert.assertNotNull(file);
		Assert.assertNotNull(file.getFunction());
		Assert.assertEquals("qamData", file.getFunction().getName());
		Assert.assertEquals(1, file.getFunction().getOutputs().size());
		Assert.assertEquals("rawData", file.getFunction().getOutputs().get(0));
		Assert.assertEquals(6, file.getFunction().getInputs().size());
		Assert.assertEquals("fs", file.getFunction().getInputs().get(0));
		Assert.assertEquals("fc", file.getFunction().getInputs().get(1));
		Assert.assertEquals("rb", file.getFunction().getInputs().get(2));
		Assert.assertEquals("m", file.getFunction().getInputs().get(3));
		Assert.assertEquals("T", file.getFunction().getInputs().get(4));
		Assert.assertEquals("sampleType", file.getFunction().getInputs().get(5));
	}

	@Test
	public void testParseSsbMod() throws IOException, ParseException {
		MFile file = parseFile("ssbmod.m");
		Assert.assertNotNull(file);
		Assert.assertNotNull(file.getFunction());
		Assert.assertEquals("ssbmod", file.getFunction().getName());
		Assert.assertEquals(1, file.getFunction().getOutputs().size());
		Assert.assertEquals("y", file.getFunction().getOutputs().get(0));
		Assert.assertEquals(4, file.getFunction().getInputs().size());
		Assert.assertEquals("x", file.getFunction().getInputs().get(0));
		Assert.assertEquals("Fc", file.getFunction().getInputs().get(1));
		Assert.assertEquals("Fs", file.getFunction().getInputs().get(2));
		Assert.assertEquals("varargin", file.getFunction().getInputs().get(3));
	}

	@Test
	public void testParseTestCumulant() throws IOException, ParseException {
		MFile file = parseFile("testCumulant.m");
		Assert.assertNotNull(file);
		Assert.assertNotNull(file.getFunction());
		Assert.assertEquals("testCumulant", file.getFunction().getName());
		Assert.assertEquals(3, file.getFunction().getOutputs().size());
		Assert.assertEquals("M", file.getFunction().getOutputs().get(0));
		Assert.assertEquals("C40", file.getFunction().getOutputs().get(1));
		Assert.assertEquals("C42", file.getFunction().getOutputs().get(2));
		Assert.assertEquals(4, file.getFunction().getInputs().size());
		Assert.assertEquals("input", file.getFunction().getInputs().get(0));
		Assert.assertEquals("SNR", file.getFunction().getInputs().get(1));
		Assert.assertEquals("Fc", file.getFunction().getInputs().get(2));
		Assert.assertEquals("Fs", file.getFunction().getInputs().get(3));
	}

	@Test
	public void testParseTestCumulantMethod() throws IOException, ParseException {
		MFile file = parseFile("testCumulantMethod.m");
		Assert.assertNotNull(file);
		Assert.assertNotNull(file.getFunction());
		Assert.assertEquals("testCumulantMethod", file.getFunction().getName());
		Assert.assertEquals(7, file.getFunction().getOutputs().size());
		Assert.assertEquals("AMOut", file.getFunction().getOutputs().get(0));
		Assert.assertEquals("SSBOut", file.getFunction().getOutputs().get(1));
		Assert.assertEquals("FMOut", file.getFunction().getOutputs().get(2));
		Assert.assertEquals("BPSKOut", file.getFunction().getOutputs().get(3));
		Assert.assertEquals("QAMOut", file.getFunction().getOutputs().get(4));
		Assert.assertEquals("QAM16Out", file.getFunction().getOutputs().get(5));
		Assert.assertEquals("QAM64Out", file.getFunction().getOutputs().get(6));
		Assert.assertEquals(0, file.getFunction().getInputs().size());
	}

	@Test
	public void testParseTestCyclo() throws IOException, ParseException {
		MFile file = parseFile("testCyclo.m");
		Assert.assertNotNull(file);
		Assert.assertNotNull(file.getFunction());
		Assert.assertEquals("testCyclo", file.getFunction().getName());
		Assert.assertEquals(1, file.getFunction().getOutputs().size());
		Assert.assertEquals("M", file.getFunction().getOutputs().get(0));
		Assert.assertEquals(8, file.getFunction().getInputs().size());
		Assert.assertEquals("input", file.getFunction().getInputs().get(0));
		Assert.assertEquals("templates", file.getFunction().getInputs().get(1));
		Assert.assertEquals("SNR", file.getFunction().getInputs().get(2));
		Assert.assertEquals("Fc", file.getFunction().getInputs().get(3));
		Assert.assertEquals("Fs", file.getFunction().getInputs().get(4));
		Assert.assertEquals("sampleType", file.getFunction().getInputs().get(5));
		Assert.assertEquals("BlockSize", file.getFunction().getInputs().get(6));
		Assert.assertEquals("maxAvg", file.getFunction().getInputs().get(7));
	}

	@Test
	public void testParseTestCycloMethod() throws IOException, ParseException {
		MFile file = parseFile("testCycloMethod.m");
		Assert.assertNotNull(file);
		Assert.assertNotNull(file.getFunction());
		Assert.assertEquals("testCycloMethod", file.getFunction().getName());
		Assert.assertEquals(7, file.getFunction().getOutputs().size());
		Assert.assertEquals("AMOut", file.getFunction().getOutputs().get(0));
		Assert.assertEquals("SSBOut", file.getFunction().getOutputs().get(1));
		Assert.assertEquals("FMOut", file.getFunction().getOutputs().get(2));
		Assert.assertEquals("BPSKOut", file.getFunction().getOutputs().get(3));
		Assert.assertEquals("QAMOut", file.getFunction().getOutputs().get(4));
		Assert.assertEquals("QAM16Out", file.getFunction().getOutputs().get(5));
		Assert.assertEquals("QAM64Out", file.getFunction().getOutputs().get(6));
		Assert.assertEquals(0, file.getFunction().getInputs().size());
	}

	@Test
	public void testParseZeroShift() throws IOException, ParseException {
		MFile file = parseFile("zeroShift.m");
		Assert.assertNotNull(file);
		Assert.assertNotNull(file.getFunction());
		Assert.assertEquals("zeroShift", file.getFunction().getName());
		Assert.assertEquals(1, file.getFunction().getOutputs().size());
		Assert.assertEquals("y", file.getFunction().getOutputs().get(0));
		Assert.assertEquals(2, file.getFunction().getInputs().size());
		Assert.assertEquals("x", file.getFunction().getInputs().get(0));
		Assert.assertEquals("n", file.getFunction().getInputs().get(1));
	}

	@Test
	public void testExample01() throws IOException, ParseException {
		MFile file = parseFile("example01.m");
		Assert.assertNotNull(file);
		Assert.assertNotNull(file.getFunction());
		Assert.assertEquals("octaveTestComponent", file.getFunction().getName());
		Assert.assertEquals(5, file.getFunction().getOutputs().size());
		Assert.assertEquals("myOutput1", file.getFunction().getOutputs().get(0));
		Assert.assertEquals("seqProp", file.getFunction().getOutputs().get(1));
		Assert.assertEquals("myOutput2", file.getFunction().getOutputs().get(2));
		Assert.assertEquals("stringProp", file.getFunction().getOutputs().get(3));
		Assert.assertEquals("doubleProp", file.getFunction().getOutputs().get(4));

		Assert.assertEquals(5, file.getFunction().getInputs().size());
		Assert.assertEquals("seqProp", file.getFunction().getInputs().get(0));
		Assert.assertEquals("myInput1", file.getFunction().getInputs().get(1));
		Assert.assertEquals("stringProp", file.getFunction().getInputs().get(2));
		Assert.assertEquals("myInput2", file.getFunction().getInputs().get(3));
		Assert.assertEquals("doubleProp", file.getFunction().getInputs().get(4));
	}

	@Test
	public void testExample02() throws IOException, ParseException {
		MFile file = parseFile("example02.m");
		Assert.assertNotNull(file);
		Assert.assertNotNull(file.getFunction());
		Assert.assertEquals("octaveTestComponent", file.getFunction().getName());
		Assert.assertEquals(5, file.getFunction().getOutputs().size());
		Assert.assertEquals("myOutput1", file.getFunction().getOutputs().get(0));
		Assert.assertEquals("seqProp", file.getFunction().getOutputs().get(1));
		Assert.assertEquals("myOutput2", file.getFunction().getOutputs().get(2));
		Assert.assertEquals("stringProp", file.getFunction().getOutputs().get(3));
		Assert.assertEquals("doubleProp", file.getFunction().getOutputs().get(4));

		Assert.assertEquals(5, file.getFunction().getInputs().size());
		Assert.assertEquals("seqProp", file.getFunction().getInputs().get(0));
		Assert.assertEquals("myInput1", file.getFunction().getInputs().get(1));
		Assert.assertEquals("stringProp", file.getFunction().getInputs().get(2));
		Assert.assertEquals("myInput2", file.getFunction().getInputs().get(3));
		Assert.assertEquals("doubleProp", file.getFunction().getInputs().get(4));
	}

	@Test
	public void testExample03() throws IOException, ParseException {
		MFile file = parseFile("example03.m");
		Assert.assertNotNull(file);
		Assert.assertNotNull(file.getFunction());
		Assert.assertEquals("octaveTestComponent", file.getFunction().getName());
		Assert.assertEquals(0, file.getFunction().getOutputs().size());
		Assert.assertEquals(0, file.getFunction().getInputs().size());
	}

	@Test
	public void testExample04() throws IOException, ParseException {
		MFile file = parseFile("example04.m");
		Assert.assertNotNull(file);
		Assert.assertNotNull(file.getFunction());
		Assert.assertEquals("octaveTestComponent", file.getFunction().getName());
		Assert.assertEquals(0, file.getFunction().getOutputs().size());
		Assert.assertEquals(0, file.getFunction().getInputs().size());
	}

	public MFile parseFile(String name) throws IOException, ParseException {
		InputStream fileStream = null;
		try {
			fileStream = MFileTestActivator.openTestFile(name);
			return MFileParser.parse(fileStream, null);
		} finally {
			try {
				fileStream.close();
			} catch (IOException e) {
				// PASS
			}
		}

	}

}
