package com.nbicocchi.math.dto;

import java.util.List;

public record DivisorsWithLatency(Long n, List<Long> divisors, Long latency) {};
