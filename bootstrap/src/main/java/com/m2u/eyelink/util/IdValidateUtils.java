package com.m2u.eyelink.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.m2u.eyelink.common.ELAgentConstants;

public final class IdValidateUtils {

    private static final int DEFAULT_MAX_LENGTH = ELAgentConstants.AGENT_NAME_MAX_LEN;

    //    private static final Pattern ID_PATTERN = Pattern.compile("[a-zA-Z0-9\\._\\-]{1,24}");
    private static final Pattern ID_PATTERN = Pattern.compile("[a-zA-Z0-9\\._\\-]+");

    private IdValidateUtils() {
    }

    public static boolean validateId(String id) {
        return validateId(id, DEFAULT_MAX_LENGTH);
    }

    public static boolean validateId(String id, int maxLength) {
        if (id == null) {
            throw new NullPointerException("id must not be null");
        }
        if (maxLength <= 0) {
            throw new IllegalArgumentException("negative maxLength:" + maxLength);
        }

        if (!checkPattern(id)) {
            return false;
        }
        if (!checkLength(id, maxLength)) {
            return false;
        }

        return true;
    }

    public static boolean checkPattern(String id) {
        final Matcher matcher = ID_PATTERN.matcher(id);
        return matcher.matches();
    }

    public static boolean checkLength(String id, int maxLength) {
        if (id == null) {
            throw new NullPointerException("id must not be null");
        }
        // try encode
        final int idLength = getLength(id);
        if (idLength <= 0) {
            return false;
        }
        return idLength <= maxLength;
    }

    public static int getLength(String id) {
        if (id == null) {
            return -1;
        }

        final byte[] idBytes = BytesUtils.toBytes(id);
        if (idBytes == null) {
            // encoding fail
            return -1;
        }
        return idBytes.length;
    }

}
