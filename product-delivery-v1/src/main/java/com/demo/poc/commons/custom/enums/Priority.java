package com.demo.poc.commons.custom.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Priority {

    ZERO("0"),
    FIRST("1");

    private final String code;
}
