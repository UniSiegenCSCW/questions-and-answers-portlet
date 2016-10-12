package org.sidate.qanda.model.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by leon on 10/12/16.
 */
public class TimeDiffFormatter {
	public static final long MINUTE = 60;
	public static final long HOUR = 60 * MINUTE;
	public static final long DAY = 24 * HOUR;

	public static String format(Date date) {
		long diffSeconds = (new Date().getTime() - date.getTime()) / 1000;

		if (diffSeconds > (10 * DAY)) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd. MMM yyyy", Locale.GERMAN);
			return "am " + sdf.format(date);

		} else if (diffSeconds >= DAY) {
			int value = (int) (diffSeconds / DAY);
			if (value == 1) {
				return "vor einem Tag";
			} else {
				return "vor " + value + " Tagen";
			}
		} else if (diffSeconds >= HOUR) {
			int value = (int) (diffSeconds / HOUR);
			if (value == 1) {
				return "vor einer Stunde";
			} else {
				return "vor " + value + " Stunden";
			}
		} else if (diffSeconds >= MINUTE) {
			int value = (int) (diffSeconds / MINUTE);
			if (value == 1) {
				return "vor einer Minute";
			} else {
				return "vor " + value + " Minuten";
			}
		} else {
			int value = (int) diffSeconds;
			if (value == 1) {
				return "vor einer Sekunde";
			} else {
				return "vor " + value + " Sekunden";
			}
		}
	}
}
