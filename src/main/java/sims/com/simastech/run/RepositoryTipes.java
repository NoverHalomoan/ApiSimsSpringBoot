package sims.com.simastech.run;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import sims.com.simastech.SimsData.TranscationTypes;

@Repository
public class RepositoryTipes {
    private final JdbcClient jdbcClient;

    public RepositoryTipes(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    // find data by ID
    public Optional<TranscationTypes> findById(String id) {
        return jdbcClient.sql("SELECT idoftypetrans, nameoftypetrans FROM tipe_transactions where idoftypetrans=:id")
                .param("id", id).query(TranscationTypes.class).optional();
    }

    // Insert Data
    public void create(TranscationTypes transcationTypes) {
        var updated = jdbcClient.sql("INSERT INTO tipe_transactions(idoftypetrans, nameoftypetrans) VALUES(?, ?)")
                .params(List.of(transcationTypes.getIdoftypetrans().toString(),
                        transcationTypes.getNameoftypetrans().toString()))
                .update();

        Assert.state(updated == 1, "Gagal saat create data " + transcationTypes.getNameoftypetrans());
    }
}
