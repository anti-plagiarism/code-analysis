package com.vk.codeanalysis.plagiarismalg;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tuple<T, U> {
    private T first;
    private U second;

    public Tuple(T first, U second) {
        this.first = first;
        this.second = second;
    }
}
