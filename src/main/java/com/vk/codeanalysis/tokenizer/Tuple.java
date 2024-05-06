package com.vk.codeanalysis.tokenizer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Tuple<T, U> {
    private T first;
    private U second;
}
