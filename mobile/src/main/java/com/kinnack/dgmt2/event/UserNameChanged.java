package com.kinnack.dgmt2.event;

import com.kinnack.dgmt2.option.Option;

public class UserNameChanged {
    private Option<String> mUsername;
    public UserNameChanged(Option<String> username) {
       mUsername = username;
    }

    public Option<String> getUsername() { return mUsername; }
}
