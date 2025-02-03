package sims.com.simastech.SimsData;

import com.fasterxml.jackson.annotation.JsonProperty;

public record rectransaksi(
        @JsonProperty Double top_up_amount,
        @JsonProperty String service_code) {

}
