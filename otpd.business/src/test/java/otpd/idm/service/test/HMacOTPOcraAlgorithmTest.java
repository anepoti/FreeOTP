package otpd.idm.service.test;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import otpd.algorithm.HMacOTPOcraValidator;

public class HMacOTPOcraAlgorithmTest extends HMacOTPAlgorithmTest {

	
	@Test
	public void validateUserTokenTest() throws Exception {
	
		HMacOTPOcraValidator hMacOTPOcraValidator = new HMacOTPOcraValidator();

		String ocra = "";
		String seed = "";
		String ocraSuite = "";
		String counter = "";
		String password = "";
		String sessionInformation = "";
		String question = "";
		String qHex = "";
		String timeStamp = "";

		String PASS1234 = "7110eda4d09e062aa5e4a390b0a572ac0d2c0220";
		String SEED = "3132333435363738393031323334353637383930";
		String SEED32 = "31323334353637383930313233343536373839"
				+ "30313233343536373839303132";
		String SEED64 = "31323334353637383930313233343536373839"
				+ "3031323334353637383930313233343536373839"
				+ "3031323334353637383930313233343536373839" + "3031323334";
		int STOP = 5;

		Date myDate = Calendar.getInstance().getTime();
		BigInteger b = new BigInteger("0");
		String sDate = "Mar 25 2008, 12:06:30 GMT";

		try {
			
			DateFormat df = new SimpleDateFormat("MMM dd yyyy, HH:mm:ss zzz");
			myDate = df.parse(sDate);
			
			b = new BigInteger("0" + myDate.getTime());
			b = b.divide(new BigInteger("60000"));

			System.out.println("Time of \"" + sDate + "\" is in");
			System.out.println("milli sec: " + myDate.getTime());
			System.out.println("minutes: " + b.toString());
			System.out.println("minutes (HEX encoded): "
					+ b.toString(16).toUpperCase());
			System.out.println("Time of \"" + sDate
					+ "\" is the same as this localized");
			System.out.println("time, \"" + new Date(myDate.getTime()) + "\"");

			System.out.println();
			System.out.println("Standard 20Byte key: "
					+ "3132333435363738393031323334353637383930");
			System.out.println("Standard 32Byte key: "
					+ "3132333435363738393031323334353637383930");
			System.out.println("            " + "313233343536373839303132");
			System.out.println("Standard 64Byte key: 313233343536373839"
					+ "3031323334353637383930");
			System.out.println("                     313233343536373839"
					+ "3031323334353637383930");
			System.out.println("                     313233343536373839"
					+ "3031323334353637383930");
			System.out.println("                     31323334");

			System.out.println();
			System.out.println("Plain challenge response");
			System.out.println("========================");
			System.out.println();

			ocraSuite = "OCRA-1:HOTP-SHA1-6:QN08";
			System.out.println(ocraSuite);
			System.out.println("=======================");
			seed = SEED;
			counter = "";
			question = "";

			password = "";
			sessionInformation = "";
			timeStamp = "";
			
			for (int i = 0; i < 10; i++) {
				question = "" + i + i + i + i + i + i + i + i;
				qHex = new String((new BigInteger(question, 10)).toString(16))
						.toUpperCase();
				ocra = hMacOTPOcraValidator.generateOCRA(ocraSuite, seed,
						counter, qHex, password, sessionInformation, timeStamp);
				System.out.println("Key: Standard 20Byte  Q: " + question
						+ "  OCRA: " + ocra);
			}
			System.out.println();

			/*
			ocraSuite = "OCRA-1:HOTP-SHA256-8:C-QN08-PSHA1";
			System.out.println(ocraSuite);
			System.out.println("=================================");
			seed = SEED32;
			counter = "";
			question = "12345678";
			password = PASS1234;
			sessionInformation = "";
			timeStamp = "";
			for (int i = 0; i < 10; i++) {
				counter = "" + i;
				qHex = new String((new BigInteger(question, 10)).toString(16))
						.toUpperCase();
				ocra = hMacOTPOcraValidator.generateOCRA(ocraSuite, seed,
						counter, qHex, password, sessionInformation, timeStamp);
				System.out.println("Key: Standard 32Byte  C: " + counter
						+ "  Q: " + question + "  PIN(1234): ");
				System.out.println(password + "  OCRA: " + ocra);
			}
			System.out.println();

			ocraSuite = "OCRA-1:HOTP-SHA256-8:QN08-PSHA1";
			System.out.println(ocraSuite);
			System.out.println("===============================");
			seed = SEED32;
			counter = "";
			question = "";
			password = PASS1234;
			sessionInformation = "";
			timeStamp = "";
			for (int i = 0; i < STOP; i++) {
				question = "" + i + i + i + i + i + i + i + i;

				qHex = new String((new BigInteger(question, 10)).toString(16))
						.toUpperCase();
				ocra = hMacOTPOcraValidator.generateOCRA(ocraSuite, seed,
						counter, qHex, password, sessionInformation, timeStamp);
				System.out.println("Key: Standard 32Byte  Q: " + question
						+ "  PIN(1234): ");
				System.out.println(password + "  OCRA: " + ocra);
			}
			System.out.println();

			ocraSuite = "OCRA-1:HOTP-SHA512-8:C-QN08";
			System.out.println(ocraSuite);
			System.out.println("===========================");
			seed = SEED64;
			counter = "";
			question = "";
			password = "";
			sessionInformation = "";
			timeStamp = "";
			for (int i = 0; i < 10; i++) {
				question = "" + i + i + i + i + i + i + i + i;
				qHex = new String((new BigInteger(question, 10)).toString(16))
						.toUpperCase();
				counter = "0000" + i;
				ocra = hMacOTPOcraValidator.generateOCRA(ocraSuite, seed,
						counter, qHex, password, sessionInformation, timeStamp);
				System.out.println("Key: Standard 64Byte  C: " + counter
						+ "  Q: " + question + "  OCRA: " + ocra);
			}
			System.out.println();

			ocraSuite = "OCRA-1:HOTP-SHA512-8:QN08-T1M";
			System.out.println(ocraSuite);
			System.out.println("=============================");
			seed = SEED64;
			counter = "";
			question = "";
			password = "";
			sessionInformation = "";
			timeStamp = b.toString(16);
			for (int i = 0; i < STOP; i++) {
				question = "" + i + i + i + i + i + i + i + i;
				counter = "";
				qHex = new String((new BigInteger(question, 10)).toString(16))
						.toUpperCase();
				ocra = hMacOTPOcraValidator.generateOCRA(ocraSuite, seed,
						counter, qHex, password, sessionInformation, timeStamp);

				System.out
						.println("Key: Standard 64Byte  Q: " + question
								+ "  T: " + timeStamp.toUpperCase()
								+ "  OCRA: " + ocra);
			}
			System.out.println();

			System.out.println();
			System.out.println("Mutual Challenge Response");
			System.out.println("=========================");
			System.out.println();

			ocraSuite = "OCRA-1:HOTP-SHA256-8:QA08";
			System.out.println("OCRASuite (server computation) = " + ocraSuite);
			System.out.println("OCRASuite (client computation) = " + ocraSuite);
			System.out.println("==============================="
					+ "===========================");
			seed = SEED32;
			counter = "";
			question = "";
			password = "";
			sessionInformation = "";
			timeStamp = "";
			for (int i = 0; i < STOP; i++) {
				question = "CLI2222" + i + "SRV1111" + i;
				qHex = asHex(question.getBytes());
				ocra = hMacOTPOcraValidator.generateOCRA(ocraSuite, seed,
						counter, qHex, password, sessionInformation, timeStamp);
				System.out.println("(server)Key: Standard 32Byte  Q: "
						+ question + "  OCRA: " + ocra);
				question = "SRV1111" + i + "CLI2222" + i;
				qHex = asHex(question.getBytes());
				ocra = hMacOTPOcraValidator.generateOCRA(ocraSuite, seed,
						counter, qHex, password, sessionInformation, timeStamp);
				System.out.println("(client)Key: Standard 32Byte  Q: "
						+ question + "  OCRA: " + ocra);
			}
			System.out.println();

			String ocraSuite1 = "OCRA-1:HOTP-SHA512-8:QA08";
			String ocraSuite2 = "OCRA-1:HOTP-SHA512-8:QA08-PSHA1";
			System.out.println("OCRASuite (server computation) = "

			+ ocraSuite1);
			System.out
					.println("OCRASuite (client computation) = " + ocraSuite2);
			System.out.println("==============================="
					+ "=================================");
			ocraSuite = "";
			seed = SEED64;
			counter = "";
			question = "";
			password = "";
			sessionInformation = "";
			timeStamp = "";
			for (int i = 0; i < STOP; i++) {
				ocraSuite = ocraSuite1;
				question = "CLI2222" + i + "SRV1111" + i;
				qHex = asHex(question.getBytes());
				password = "";
				ocra = hMacOTPOcraValidator.generateOCRA(ocraSuite, seed,
						counter, qHex, password, sessionInformation, timeStamp);
				System.out.println("(server)Key: Standard 64Byte  Q: "
						+ question + "  OCRA: " + ocra);
				ocraSuite = ocraSuite2;
				question = "SRV1111" + i + "CLI2222" + i;
				qHex = asHex(question.getBytes());
				password = PASS1234;
				ocra = hMacOTPOcraValidator.generateOCRA(ocraSuite, seed,
						counter, qHex, password, sessionInformation, timeStamp);
				System.out.println("(client)Key: Standard 64Byte  Q: "
						+ question);
				System.out.println("P: " + password.toUpperCase() + "  OCRA: "
						+ ocra);
			}
			System.out.println();

			System.out.println();
			System.out.println("Plain Signature");
			System.out.println("===============");
			System.out.println();
			ocraSuite = "OCRA-1:HOTP-SHA256-8:QA08";
			System.out.println(ocraSuite);
			System.out.println("=========================");
			seed = SEED32;
			counter = "";
			question = "";
			password = "";
			sessionInformation = "";

			timeStamp = "";
			for (int i = 0; i < STOP; i++) {
				question = "SIG1" + i + "000";
				qHex = asHex(question.getBytes());
				ocra = hMacOTPOcraValidator.generateOCRA(ocraSuite, seed,
						counter, qHex, password, sessionInformation, timeStamp);
				System.out
						.println("Key: Standard 32Byte  Q(Signature challenge): "
								+ question);
				System.out.println("   OCRA: " + ocra);
			}
			System.out.println();

			ocraSuite = "OCRA-1:HOTP-SHA512-8:QA10-T1M";
			System.out.println(ocraSuite);
			System.out.println("=============================");
			seed = SEED64;
			counter = "";
			question = "";
			password = "";
			sessionInformation = "";
			timeStamp = b.toString(16);
			for (int i = 0; i < STOP; i++) {
				question = "SIG1" + i + "00000";
				qHex = asHex(question.getBytes());
				ocra = hMacOTPOcraValidator.generateOCRA(ocraSuite, seed,
						counter, qHex, password, sessionInformation, timeStamp);
				System.out
						.println("Key: Standard 64Byte  Q(Signature challenge): "
								+ question);
				System.out.println("   T: " + timeStamp.toUpperCase()
						+ "  OCRA: " + ocra);
			}
			*/

		} catch (Exception e) {
			System.out.println("Error : " + e);
		}
	}

	private String asHex(byte buf[]) {
		StringBuffer strbuf = new StringBuffer(buf.length * 2);
		int i;

		for (i = 0; i < buf.length; i++) {
			if (((int) buf[i] & 0xff) < 0x10)
				strbuf.append("0");
			strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
		}
		return strbuf.toString();
	}

}
