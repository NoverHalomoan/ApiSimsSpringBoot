package sims.com.simastech.SimsData;

import java.util.List;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

public record TypesofTransaction(List<TranscationTypes> TipeTransaction) {

}
