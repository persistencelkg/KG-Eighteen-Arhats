package org.lkg.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.lkg.utils.matcher.AntPathMatcher;

@Getter
@AllArgsConstructor
public enum AntEnum {
    DOT(new AntPathMatcher(StringEnum.DOT)),
    STAR(new AntPathMatcher(StringEnum.STAR)),
    DEFAULT(new AntPathMatcher());
    private final AntPathMatcher antPathMatcher;
}