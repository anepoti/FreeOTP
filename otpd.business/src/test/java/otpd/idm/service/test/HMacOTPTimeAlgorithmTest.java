package otpd.idm.service.test;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.junit.Test;

import otpd.idm.domain.Subject;
import otpd.idm.domain.impl.SubjectImpl;
import otpd.idm.service.impl.HMacOTPService;

public class HMacOTPTimeAlgorithmTest extends HMacOTPAlgorithmTest {


	@Test
	public void validateHOTPUserTokenTest() throws Exception {
		
		createUserForTest("totp");

		HMacOTPService hMacOTPService = (HMacOTPService) factory
				.getBean("hMacOTPService");

		Subject subject = new SubjectImpl();

		subject.setCommonName("otpd-test");
		subject.setFirstName("firstName");
		subject.setLastName("lastname");
		subject.setEmail("email");

		boolean validated = hMacOTPService.validatePassword(subject, "921677");

		assertTrue(validated);

	}

/*	// @Test
	public void generateTokenAdvancedTest() {
		// Seed for HMAC-SHA1 - 20 bytes
		String seed = "3132333435363738393031323334353637383930";

		// Seed for HMAC-SHA256 - 32 bytes
		String seed32 = "3132333435363738393031323334353637383930"
				+ "313233343536373839303132";

		// Seed for HMAC-SHA512 - 64 bytes
		String seed64 = "3132333435363738393031323334353637383930"
				+ "3132333435363738393031323334353637383930"
				+ "3132333435363738393031323334353637383930" + "31323334";

		long T0 = 0;
		long X = 30;
		long testTime[] = { 59L };

		String steps = "0";
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));

		Long today = df.getCalendar().getTime().getTime();

		try {
			System.out.println("+---------------+-----------------------+"
					+ "------------------+--------+--------+");
			System.out.println("|  Time(sec)    |   Time (UTC format)   "
					+ "| Value of T(Hex)  |  TOTP  | Mode   |");
			System.out.println("+---------------+-----------------------+" +

			"------------------+--------+--------+");

			for (int i = 0; i < testTime.length; i++) {
				long T = (testTime[i] - T0) / X;

				steps = Long.toHexString(T).toUpperCase();

				while (steps.length() < 16)
					steps = "0" + steps;

				String fmtTime = String.format("%1$-11s", testTime[i]);
				String utcTime = df.format(new Date(testTime[i] * 1000));

				System.out.print("|  " + fmtTime + "  |  " + utcTime + "  | "
						+ steps + " |");

				System.out.println(hMacOTPTimeValidator.generateTOTP(seed,
						steps, 8, "HmacSHA1")
						+ "| SHA1   |");
				System.out.print("|  " + fmtTime + "  |  " + utcTime + "  | "
						+ steps + " |");
				System.out.println(hMacOTPTimeValidator.generateTOTP(seed32,
						steps, 8, "HmacSHA256")
						+ "| SHA256 |");
				System.out.print("|  " + fmtTime + "  |  " + utcTime + "  | "
						+ steps + " |");
				System.out.println(hMacOTPTimeValidator.generateTOTP(seed64,
						steps, 8, "HmacSHA512")
						+ "| SHA512 |");

				System.out.println("+---------------+-----------------------+"
						+ "------------------+--------+--------+");
			}
		} catch (final Exception e) {
			System.out.println("Error : " + e);
		}
	}*/

	private Long calendarCalculator(Calendar cal, int sec) {
		cal.add(Calendar.SECOND, sec);
		return new Long(cal.getTimeInMillis());
	}

}
