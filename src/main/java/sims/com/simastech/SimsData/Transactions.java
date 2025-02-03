package sims.com.simastech.SimsData;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnTransformer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

@Entity
public class Transactions {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonIgnore
    private String idtransaksi;
    @JsonProperty
    private LocalDateTime transactiontime;
    @JsonProperty
    private Double nilaivalue;
    @JsonProperty
    private String invoice_number;
    @JsonProperty
    private String description;

    public String getTipetransaksi() {
        return tipetransaksi;
    }

    public void setTipetransaksi(String tipetransaksi) {
        this.tipetransaksi = tipetransaksi;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    @ColumnTransformer(write = "UPPER(?)")
    private String tipetransaksi;
    @JsonIgnore
    @Lob // Menandakan kolom ini bisa menyimpan teks panjang
    @Column(columnDefinition = "TEXT")
    private String token;
    @ManyToOne // Setiap transaksi terkait dengan satu User
    private Users user;

    public InformationService getInformationService() {
        return informationService;
    }

    public void setInformationService(InformationService informationService) {
        this.informationService = informationService;
    }

    @OneToOne
    private InformationService informationService;

    public String getIdtransaksi() {
        return idtransaksi;
    }

    public void setIdtransaksi(String idtransaksi) {
        this.idtransaksi = idtransaksi;
    }

    public LocalDateTime getTransactiontime() {
        return transactiontime;
    }

    public void setTransactiontime(LocalDateTime transactiontime) {
        this.transactiontime = transactiontime;
    }

    public Double getNilaivalue() {
        return nilaivalue;
    }

    public void setNilaivalue(Double nilaivalue) {
        this.nilaivalue = nilaivalue;
    }

    public String getInvoice_number() {
        return invoice_number;
    }

    public void setInvoice_number(String invoice_number) {
        this.invoice_number = invoice_number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
