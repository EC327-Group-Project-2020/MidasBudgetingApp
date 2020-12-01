package com.example.personalfinanceplanner;

import org.junit.Test;

import static org.junit.Assert.*;

public class LogInActivityTest {
    @Test
    public void emptyUsername(){
        Boolean result = LogInActivity.emptyFields("","jolo");
        assertTrue(result);
    }

    @Test
    public void emptyPassword(){
        Boolean result = LogInActivity.emptyFields("kilo","");
        assertTrue(result);
    }

    @Test
    public void allUserFieldsOk(){
        Boolean result = LogInActivity.emptyFields("kilo","gram");
        assertFalse(result);
    }
}
