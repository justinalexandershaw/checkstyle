////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2021 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.puppycrawl.tools.checkstyle;

import java.util.Locale;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.SeverityLevel;

/**
 * Represents the default formatter for log message.
 * Default log message format is:
 * [SEVERITY LEVEL] CheckName: message. (filePath, line #)
 * When the module id of the message has been set, the format is:
 * [SEVERITY LEVEL] ModuleId: message. (filePath, line #)
 */
public class AuditEventDefaultFormatter implements AuditEventFormatter {

    /** Length of all separators. */
    private static final int LENGTH_OF_ALL_SEPARATORS = 15;

    /** Suffix of module names like XXXXCheck. */
    private static final String SUFFIX = "Check";

    @Override
    public String format(AuditEvent event) {
        final String fileName = getShortFileName(event.getFileName());
        final String message = event.getMessage();
        final SeverityLevel severityLevel = event.getSeverityLevel();
        final String severityLevelName;
        if (severityLevel == SeverityLevel.WARNING) {
            // We change the name of severity level intentionally
            // to shorten the length of the log message.
            severityLevelName = "WARN";
        }
        else {
            severityLevelName = severityLevel.getName().toUpperCase(Locale.US);
        }

        // Avoid StringBuffer.expandCapacity
        final int bufLen = calculateBufferLength(event, severityLevelName.length());
        final StringBuilder sb = new StringBuilder(bufLen);

        // [ERROR] NameOfTheCheck: This is the message to fix it. (FileName.java, line 1)
        sb.append('[').append(severityLevelName).append("] ");
        sb.append(event.getModuleId() == null ? getCheckShortName(event) : event.getModuleId());
        sb.append(": ").append(message);
        sb.append(" (").append(fileName).append(", line ").append(event.getLine()).append(')');
        return sb.toString();
    }

    /**
     * Returns the length of the buffer for StringBuilder.
     * bufferLength = fileNameLength + messageLength + lengthOfAllSeparators +
     * + severityNameLength + checkNameLength.
     *
     * @param event audit event.
     * @param severityLevelNameLength length of severity level name.
     * @return the length of the buffer for StringBuilder.
     */
    private static int calculateBufferLength(AuditEvent event, int severityLevelNameLength) {
        return LENGTH_OF_ALL_SEPARATORS + getShortFileName(event.getFileName()).length()
            + event.getMessage().length() + severityLevelNameLength
            + getCheckShortName(event).length();
    }

    /**
     * Returns check name without 'Check' suffix.
     *
     * @param event audit event.
     * @return check name without 'Check' suffix.
     */
    private static String getCheckShortName(AuditEvent event) {
        final String checkFullName = event.getSourceName();
        final String checkShortName;
        final int lastDotIndex = checkFullName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            if (checkFullName.endsWith(SUFFIX)) {
                checkShortName = checkFullName.substring(0, checkFullName.lastIndexOf(SUFFIX));
            }
            else {
                checkShortName = checkFullName;
            }
        }
        else {
            if (checkFullName.endsWith(SUFFIX)) {
                checkShortName = checkFullName.substring(lastDotIndex + 1,
                    checkFullName.lastIndexOf(SUFFIX));
            }
            else {
                checkShortName = checkFullName.substring(lastDotIndex + 1);
            }
        }
        return checkShortName;
    }

    /**
     * Returns the shortened version of the filename (without the folders).
     */
    private static String getShortFileName(String fileName) {
        final int lastSlashIndex = fileName.lastIndexOf('/');
        final int indexOfLastChar = fileName.length() - 1;
        if (lastSlashIndex == -1 && lastSlashIndex < indexOfLastChar) {
            return fileName;
        } else {
            return fileName.substring(lastSlashIndex + 1);
        }
    }

}
