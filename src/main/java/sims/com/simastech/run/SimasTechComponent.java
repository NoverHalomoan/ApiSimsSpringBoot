package sims.com.simastech.run;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import aj.org.objectweb.asm.TypeReference;
import sims.com.simastech.SimsData.BannerInformation;
import sims.com.simastech.SimsData.BannerInformationList;
import sims.com.simastech.SimsData.InformationService;
import sims.com.simastech.SimsData.InformationServiceList;

@Component
public class SimasTechComponent implements CommandLineRunner {

    // fungsi untuk mencatat log disistem
    private final RepositoryInformation infserv;
    private static final Logger log = LoggerFactory.getLogger(SimasTechComponent.class);

    public SimasTechComponent(RepositoryInformation infserv) {
        this.infserv = infserv;
    }

    ObjectMapper obm = new ObjectMapper();

    @Override
    public void run(String... args) throws Exception {
        try {
            JsonNode arraynodes = obm.readTree(TypeReference.class.getResourceAsStream("/data/JasaService.json"));
            InformationServiceList informationServiceList = obm.treeToValue(arraynodes, InformationServiceList.class);
            for (InformationService is : informationServiceList.DataService()) {
                /// infserv.savejasainformation(is);
            }
            arraynodes = obm.readTree(TypeReference.class.getResourceAsStream("/data/banner.json"));
            BannerInformationList bannerlist = obm.treeToValue(arraynodes, BannerInformationList.class);
            for (BannerInformation is : bannerlist.DataBenner()) {
                // infserv.savebannerinformation(is);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read JSON data", e);
        }

    }

}
