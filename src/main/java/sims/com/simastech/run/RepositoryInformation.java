package sims.com.simastech.run;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;

import sims.com.simastech.SimsData.BannerInformation;
import sims.com.simastech.SimsData.InformationService;

@Repository
public class RepositoryInformation {

    private final JdbcClient jdbcClient;
    ApiResponse<Object> response = new ApiResponse<>(0, "", null);

    public RepositoryInformation(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void savejasainformation(InformationService informationService) {
        var update = jdbcClient.sql(
                "Insert into jasa_service(service_code, service_icon, service_name, service_tariff) values(?,?,?,?)")
                .params(List.of(informationService.getService_code(), informationService.getService_code(),
                        informationService.getService_name(), informationService.getService_tariff()))
                .update();
    }

    public void savebannerinformation(BannerInformation informationbanner) {
        var update = jdbcClient.sql(
                "Insert into banner_information(banner_image, banner_name, description) values(?,?,?)")
                .params(List.of(informationbanner.getBanner_image(), informationbanner.getBanner_name(),
                        informationbanner.getDescription()))
                .update();
    }

    // for search all information banner
    public ApiResponse<Object> findallbanner() {
        response = new ApiResponse<>(0, "", null);
        List<BannerInformation> bannerdata = jdbcClient.sql("Select * from banner_information")
                .query(BannerInformation.class).list();
        response.setStatus(0);
        response.setMessage("Sukses");
        response.setData(bannerdata);
        return response;
    }

    public ApiResponse<Object> findallservices() {
        response = new ApiResponse<>(0, "", null);
        List<InformationService> servicedata = jdbcClient.sql("Select * from jasa_service")
                .query(InformationService.class).list();
        response.setStatus(0);
        response.setMessage("Sukses");
        response.setData(servicedata);
        return response;
    }

}
