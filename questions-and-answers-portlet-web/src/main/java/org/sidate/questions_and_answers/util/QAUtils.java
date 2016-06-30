package org.sidate.questions_and_answers.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by bentley on 30.06.16.
 */
public class QAUtils {

    public static String getTimeDifferenceString(Date comparativeDate) {
        long diffSeconds = 0;
        diffSeconds = (new Date().getTime() - comparativeDate.getTime()) / 1000;

        if (diffSeconds > 864000) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd. MMM yyyy");
            return "am " + sdf.format(comparativeDate);

        } else if (diffSeconds >= 86400) {
            int value = (int) diffSeconds / 60 / 60 / 24;
            if(value == 1) {
                return "vor " + value + " Tag";
            } else {
                return "vor " + value + " Tagen";
            }
        } else if (diffSeconds >= 3600) {
            int value = (int) diffSeconds / 60 / 60;
            if(value == 1) {
                return "vor " + value + " Stunde";
            } else {
                return "vor " + value + " Stunden";
            }
        } else if (diffSeconds >= 60) {
            int value = (int) diffSeconds / 60;
            if(value == 1) {
                return "vor " + value + " Minute";
            } else {
                return "vor " + value + " Minuten";
            }
        } else {
            int value = (int) diffSeconds;
            if(value == 1) {
                return "vor " + value + " Sekunde";
            } else {
                return "vor " + value + " Sekunden";
            }
        }
    }
}
