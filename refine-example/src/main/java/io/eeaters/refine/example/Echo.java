package io.eeaters.refine.example;


import io.eeaters.refine.example.entity.Result;

import java.util.Map;

public interface Echo {

    String echo(String message);

    default String defaultEcho(String message, Result<Map<String, String>> message2) {
        return "abc";
    }

    Result<String> echo(Result<String> message1, String message2);


}
