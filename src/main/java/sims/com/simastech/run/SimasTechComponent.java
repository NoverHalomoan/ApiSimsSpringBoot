package sims.com.simastech.run;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import aj.org.objectweb.asm.TypeReference;
import sims.com.simastech.SimsData.TranscationTypes;
import sims.com.simastech.SimsData.TypesofTransaction;

@Component
public class SimasTechComponent implements CommandLineRunner {

    // fungsi untuk mencatat log disistem
    private static final Logger log = LoggerFactory.getLogger(SimasTechComponent.class);
    private final RepositoryTipes repositoryTipes;
    ObjectMapper obm = new ObjectMapper();

    public SimasTechComponent(RepositoryTipes repositoryTipes) {
        this.repositoryTipes = repositoryTipes;
    }

    @Override
    public void run(String... args) throws Exception {
        // Metode ini jalan lagsung dan langsung alokasi data di data-transaksitype.json
        try (InputStream inputstream = TypeReference.class.getResourceAsStream("/data/transaksitype.json")) {
            TypesofTransaction DataTypeTransaction = obm.readValue(inputstream, TypesofTransaction.class);

            // Insert Data Awal sebagai Tipe Transaksi
            for (TranscationTypes tpdt : DataTypeTransaction.TipeTransaction()) {
                repositoryTipes.create(tpdt);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read JSON data", e);
        }

    }

}
