package br.com.raphael.proposal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class App {
    public static void main(String[] args) throws IOException {

        List<List<String>> proposalsEvents = readProposalFile();
        HashMap<UUID, ProposalBufferDto> proposalDtoMap = mapToDtos(proposalsEvents);


        Map<UUID, Map<String, Object>> response = new LinkedHashMap<>();
        // icp
        for (Map.Entry<UUID, ProposalBufferDto> entry : proposalDtoMap.entrySet()) {
            ProposalBufferDto buffer = entry.getValue();
            try {
                // icp
                if (buffer.proponent.size() < 2) {
                    throw new IllegalArgumentException(String.format("The proposal with id = %s has less than 2 proponents",
                            entry.getValue()));
                }

                boolean hasOnePrincipal = false;
                // icp
                for (ProponentDto proponent : buffer.proponent) {
                    // icp
                    if (proponent.isPrincipal()) {
                        // icp
                        if (hasOnePrincipal) {
                            throw new IllegalArgumentException(String.format("The proposal with id = %s has more than one" +
                                    " principal", entry.getValue()));
                        }

                        hasOnePrincipal = true;

                        // icp
                        if (proponent.age() < 18) {
                            throw new IllegalArgumentException(String.format("The proponent with id = %s has " +
                                    "less than 18 years for the proposal with id = %s", proponent.age(), entry.getValue()));
                        }

                        BigDecimal installmentAmount = buffer.proposal.requestedAmount().divide(
                                new BigDecimal(buffer.proposal.installmentTerm()),
                                RoundingMode.CEILING);

                        BigDecimal monthlySalary = proponent.salary().divide(BigDecimal.valueOf(12), RoundingMode.CEILING);
                        BigDecimal goalAmount = installmentAmount.multiply(BigDecimal.TWO);
                        // icp
                        if (monthlySalary.compareTo(goalAmount) < 0) {
                            throw new IllegalArgumentException(String.format("The proponent with id = %s salary [%s]" +
                                            " is less than two times the installment value per month [%s] for the proposal with id = %s",
                                    proponent.id(),
                                    monthlySalary,
                                    installmentAmount,
                                    entry.getKey()
                            ));
                        }
                    }
                }

                // icp
                BigDecimal warrantyTotal = buffer.warranty.stream()
                        .map(WarrantyDto::value)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                boolean isWarrantiesValueEnough = warrantyTotal.compareTo(buffer.proposal.requestedAmount().multiply(BigDecimal.TWO)) > 0;
                // icp
                if (!isWarrantiesValueEnough) {
                    throw new IllegalArgumentException(String.format("The warranties sum [%s] are not enough for the" +
                                    " proposal requested amount [%s]",
                            warrantyTotal.toString(),
                            buffer.proposal.requestedAmount()));
                }

            } catch (IllegalArgumentException e) {
                // icp
                response.putIfAbsent(entry.getKey(), Map.of("valid", false, "reason", e.getMessage()));
                continue;
            }

            response.putIfAbsent(entry.getKey(), Map.of("valid", true));
        }

        System.out.println(response.toString());
    }

    private static HashMap<UUID, ProposalBufferDto> mapToDtos(List<List<String>> proposalsEvents) {
        HashMap<UUID, ProposalBufferDto> proposalMap = new HashMap<>();
        // icp
        for (List<String> proposal : proposalsEvents) {
            UUID proposalId = UUID.fromString(proposal.get(4));

            ProposalBufferDto proposalBufferDto = proposalMap.getOrDefault(proposalId, new ProposalBufferDto());

            // icp
            if (proposal.contains("proposal")) {
                proposalBufferDto.proposal = new ProposalDto(
                        proposalId,
                        new BigDecimal(proposal.get(5)),
                        Integer.valueOf(proposal.get(6))
                );
            }

            // icp
            if (proposal.contains("proponent")) {
                proposalBufferDto.proponent.add(new ProponentDto(
                        UUID.fromString(proposal.get(5)),
                        proposalId,
                        proposal.get(6),
                        Integer.valueOf(proposal.get(7)),
                        new BigDecimal(proposal.get(8)),
                        Boolean.valueOf(proposal.get(9))
                ));
            }

            // icp
            if (proposal.contains("warranty")) {
                proposalBufferDto.warranty.add(new WarrantyDto(
                        UUID.fromString(proposal.get(5)),
                        proposalId,
                        new BigDecimal(proposal.get(6)),
                        proposal.get(7)
                ));
            }

            proposalMap.putIfAbsent(proposalId, proposalBufferDto);
        }

        return proposalMap;
    }

    static class ProposalBufferDto {
        ProposalDto proposal;
        List<ProponentDto> proponent = new ArrayList<>();
        List<WarrantyDto> warranty = new ArrayList<>();

        @Override
        public String toString() {
            return "ProposalBufferDto{" +
                    "proposal=" + proposal +
                    ", proponent=" + proponent.toString() +
                    ", warranty=" + warranty.toString() +
                    '}';
        }
    }

    private static List<List<String>> readProposalFile() throws IOException {
        File file = new File("proposals.txt");

        List<List<String>> proposalsEvents = new ArrayList<>();
        // icp
        try (FileReader fileReader = new FileReader(file)) {
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();

            // icp
            while (line != null) {
                proposalsEvents.add(Arrays.asList(line.split(",")));
                line = bufferedReader.readLine();
            }
        }

        return proposalsEvents;
    }
}
