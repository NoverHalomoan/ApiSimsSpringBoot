package sims.com.simastech.SimsData;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "JasaService")
public class InformationService {
    @Id
    @JsonProperty
    private String service_code;
    @JsonProperty
    private String service_name;
    @JsonProperty
    private String service_icon;

    public BannerInformation getBannerInformation() {
        return bannerInformation;
    }

    public void setBannerInformation(BannerInformation bannerInformation) {
        this.bannerInformation = bannerInformation;
    }

    @JsonProperty
    private Double service_tariff;
    @JsonIgnore
    @OneToOne
    private BannerInformation bannerInformation;

    public String getService_code() {
        return service_code;
    }

    public void setService_code(String service_code) {
        this.service_code = service_code;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getService_icon() {
        return service_icon;
    }

    public void setService_icon(String service_icon) {
        this.service_icon = service_icon;
    }

    public Double getService_tariff() {
        return service_tariff;
    }

    public void setService_tarif(Double service_tariff) {
        this.service_tariff = service_tariff;
    }

}
