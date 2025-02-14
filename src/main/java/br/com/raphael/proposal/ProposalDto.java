package br.com.raphael.proposal;

import java.math.BigDecimal;
import java.util.UUID;

public record ProposalDto(
        UUID id,
        BigDecimal requestedAmount,
        /**
        * time in months that the requested amount will be paid
        */
        Integer installmentTerm
) {
}
