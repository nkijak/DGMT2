package com.kinnack.dgmt2.model;

import java.util.Date;

/**
 * Created by prodrive555 on 4/19/15.
 */
public interface PingCalculator {
    long nextPingInMillis(Date previous);
}
