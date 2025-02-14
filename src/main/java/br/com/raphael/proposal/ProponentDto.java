package br.com.raphael.proposal;

import java.math.BigDecimal;
import java.util.UUID;

public record ProponentDto(
        UUID id,
        UUID proposalId,
        String name,
        Integer age,
        BigDecimal salary,
        Boolean isPrincipal
) {
}
