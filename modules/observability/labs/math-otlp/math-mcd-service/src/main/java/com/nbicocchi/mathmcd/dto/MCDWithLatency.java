package com.nbicocchi.mathmcd.dto;

import java.util.List;

public record MCDWithLatency(
        Long a,
        Long b,
        List<Long> aDivisors,
        List<Long> bDivisors,
        Long mcd,
        Long latency
) {}
