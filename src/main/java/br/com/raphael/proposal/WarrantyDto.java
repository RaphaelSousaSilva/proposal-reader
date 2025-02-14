package br.com.raphael.proposal;

import java.math.BigDecimal;
import java.util.UUID;

public record WarrantyDto(
        UUID id,
        UUID proposalId,
        BigDecimal value,
        String stateUF
) {
}
