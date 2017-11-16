package com.example.nanden.awesomeusbmanager;

import java.util.concurrent.locks.Lock;

/**
 * Created by nanden on 11/15/17.
 */

public class Locker {

    public String code;
    public String lockerNumber;

    public Locker(String code, String lockerNumber) {
        this.code = code;
        this.lockerNumber = lockerNumber;
    }
}
