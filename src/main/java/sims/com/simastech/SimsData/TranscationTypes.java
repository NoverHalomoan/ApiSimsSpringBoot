package sims.com.simastech.SimsData;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tipe_transactions") // Nama Table Transaksi
public class TranscationTypes {
    @Id
    @Column(unique = true, nullable = false)
    private String idoftypetrans;

    @Column(unique = true, nullable = false)
    private String nameoftypetrans;

    public TranscationTypes() {
    }

    public TranscationTypes(String idTransaksi, String namaTransaksi) {
        this.idoftypetrans = idTransaksi;
        this.nameoftypetrans = namaTransaksi;
    }

    public String getIdoftypetrans() {
        return idoftypetrans;
    }

    public void setIdoftypetrans(String idoftypetrans) {
        this.idoftypetrans = idoftypetrans;
    }

    public String getNameoftypetrans() {
        return nameoftypetrans;
    }

    public void setNameoftypetrans(String nameoftypetrans) {
        this.nameoftypetrans = nameoftypetrans;
    }

}
