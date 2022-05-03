package io.eeaters.refine.example.entity;

import lombok.Data;

@Data
public class Result <T>{
    private String message;
    private T t;
    private Integer code;
}
