package com.aurum.casesintegrator.util;

import java.time.LocalDate;
import java.time.ZoneId;

public class DateUtil {

    public static long getCurrentDateInstantZero() {
        return LocalDate.now().atTime(0, 0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
