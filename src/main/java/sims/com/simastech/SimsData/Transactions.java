package sims.com.simastech.SimsData;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
public class Transactions {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idtransaksi;
    private String typetransaksi;
    private LocalDateTime transactiontime;
    private Double nilaivalue;

    public String getTypetransaksi() {
        return typetransaksi;
    }

    public void setTypetransaksi(String typetransaksi) {
        this.typetransaksi = typetransaksi;
    }

    @ManyToOne // Setiap transaksi terkait dengan satu User
    private Users user;

    @OneToOne
    private TranscationTypes transcationTypes;

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

}
