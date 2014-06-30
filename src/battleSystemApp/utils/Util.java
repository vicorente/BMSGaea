/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package battleSystemApp.utils;

import gov.nasa.worldwind.util.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.logging.Logger;
import java.util.regex.*;

/**
 * @author tag
 * @version $Id: Util.java 1171 2013-02-11 21:45:02Z dcollins $
 */
public class Util
{
    public static final String DECIMAL_SYMBOL = Character.toString(new DecimalFormatSymbols().getDecimalSeparator());

    public static Logger getLogger()
    {
        return Logger.getLogger("gov.nasa.worldwind");
    }

    public static Frame findParentFrame(Component c)
    {
        while (c != null)
        {
            if (c instanceof Frame)
                return (Frame) c;

            c = c.getParent();
        }

        return null;
    }

    public static File ensureFileSuffix(File file, String suffixWithoutPeriod)
    {
        String suffix = WWIO.getSuffix(file.getPath());
        if (suffix == null || !suffix.equalsIgnoreCase(suffixWithoutPeriod))
            return new File(file.getPath() + (suffix == null ? "." : "") + suffixWithoutPeriod);
        else
            return file;
    }

    public static void centerDialogInContainer(JDialog dialog, Container frame)
    {
        Dimension prefSize = dialog.getPreferredSize();
        java.awt.Point parentLocation = frame.getLocationOnScreen();
        Dimension parentSize = frame.getSize();//Toolkit.getDefaultToolkit().getScreenSize();
        int x = parentLocation.x + (parentSize.width - prefSize.width) / 2;
        int y = parentLocation.y + (parentSize.height - prefSize.height) / 2;
        dialog.setLocation(x, y);
    }

    public static void positionDialogInContainer(JDialog dialog, Container frame, int horizontal, int vertical)
    {
        Dimension prefSize = dialog.getPreferredSize();
        java.awt.Point parentLocation = frame.getLocationOnScreen();
        Dimension parentSize = frame.getSize();

        // default to center
        int x = parentLocation.x + (parentSize.width - prefSize.width) / 2;
        int y = parentLocation.y + (parentSize.height - prefSize.height) / 2;

        switch (horizontal)
        {
            case SwingConstants.WEST:
                x = parentLocation.x;
                break;
            case SwingConstants.EAST:
                x = parentLocation.x + parentSize.width - prefSize.width;
                break;
        }

        switch (vertical)
        {
            case SwingConstants.NORTH:
                y = parentLocation.y;
                break;
            case SwingConstants.SOUTH:
                y = parentLocation.y + parentSize.height - prefSize.height;
                break;
        }

        dialog.setLocation(x, y);
    }

    public static String[] splitLines(String linesString)
    {
        if (WWUtil.isEmpty(linesString))
            return null;

        String[] lines = linesString.trim().split("\n");

        return lines.length > 0 ? lines : null;
    }

    public static String[] splitWords(String wordsString)
    {
        return splitWords(wordsString, ",");
    }

    public static String[] splitWords(String wordsString, String separators)
    {
        if (WWUtil.isEmpty(wordsString))
            return null;

        String[] words = wordsString.trim().split(separators);

        return words.length > 0 ? words : null;
    }

    public static String makeMultiLineToolTip(String original)
    {
        StringBuilder sb = new StringBuilder();

        if (!original.trim().toLowerCase().startsWith("<html>"))
            sb.append("<html>");

//        Pattern p = Pattern.compile("(.{0,80}\\b\\s*)|(.{80}\\B)");
        Pattern p = Pattern.compile("(.{0,80}\\b\\s*)");
        Matcher m = p.matcher(original);
        if (m.find())
            sb.append(original.substring(m.start(), m.end()).trim());
        while (m.find())
        {
            sb.append("<br>");
            sb.append(original.substring(m.start(), m.end()).trim());
        }

        if (!original.trim().toLowerCase().endsWith("</html>"))
            sb.append("</html>");

        return sb.toString();
    }
    
    public static final SimpleDateFormat DATE_FORMAT_GEOMESSAGE =
            new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
    static {
        DATE_FORMAT_GEOMESSAGE.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /**
     * A DateFormat object for military date/time in Zulu time.
     */
    public static final SimpleDateFormat DATE_FORMAT_MILITARY_ZULU =
            new SimpleDateFormat("ddHHmmss'Z 'MMM' 'yy");
    static {
        DATE_FORMAT_MILITARY_ZULU.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
    
    /**
     * A DateFormat object for military date/time in local time.
     */
    public static final SimpleDateFormat DATE_FORMAT_MILITARY_LOCAL =
            new SimpleDateFormat("ddHHmmss'J 'MMM' 'yy");
    static {
        //Check system time zone and adjust local format accordingly
        new Timer(1000 / 24, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                //Force re-read of OS time zone
                System.getProperties().remove("user.timezone");
                TimeZone.setDefault(null);
                
                //Adjust local format
                TimeZone tz = TimeZone.getDefault();
                DATE_FORMAT_MILITARY_LOCAL.setTimeZone(tz);
                DATE_FORMAT_MILITARY_LOCAL.applyPattern("ddHHmmss'" + getMilitaryTimeZoneCharacter(tz) + " 'MMM' 'yy");
            }
        }).start();
    }
    private static final int MILLISECONDS_IN_HOUR = 60 * 60 * 1000;
    private static char getMilitaryTimeZoneCharacter(TimeZone tz) {
        int offset = tz.getOffset(System.currentTimeMillis());
        //If it's not a whole number of hours, just return 'J'
        int offsetHours = offset / MILLISECONDS_IN_HOUR;
        if (0 != offset % MILLISECONDS_IN_HOUR || 12 < offsetHours || -12 > offsetHours) {
            return 'J';
        } else {
            if (0 == offsetHours) {
                return 'Z';
            } else if (0 < offsetHours) {
                char c = (char) ('A' + offsetHours - 1);
                if ('J' <= c) {
                    c += 1;
                }
                return c;
            } else {
                return (char) ('N' - offsetHours - 1);
            }
        }
    }
}
