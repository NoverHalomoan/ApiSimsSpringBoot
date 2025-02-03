package sims.com.simastech.run;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import io.swagger.v3.oas.annotations.media.Schema;
import sims.com.simastech.SimsData.InformationService;
import sims.com.simastech.SimsData.Logins;
import sims.com.simastech.SimsData.Transactions;
import sims.com.simastech.SimsData.Users;
import sims.com.simastech.SimsData.rectransaksi;

@Repository
@Schema(hidden = true)
public class RepositoryTransaksi {
    private final JdbcClient jdbcClient;
    Map<String, Object> datas = new HashMap<>();
    private static final GenerateToken geneteratedtoken = new GenerateToken();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .withZone(ZoneOffset.UTC);
    ApiResponse<Object> response = new ApiResponse<>(0, "", null);

    public RepositoryTransaksi(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    // get servicetrans
    private Optional<InformationService> getServicetrans(String service_code) {
        Optional<InformationService> infoServices = jdbcClient.sql(
                "SELECT service_tariff, service_code, service_icon, service_name FROM jasa_service where service_code=:service_code")
                .param("service_code", service_code).query(InformationService.class).optional();
        return infoServices;
    }

    // get histori transaksi
    private List<Transactions> findalltransaksi(String id, Integer limit) {
        List<Transactions> infoServices = jdbcClient.sql(
                "SELECT * FROM transactions where user_id=? order by transactiontime asc limit ?")
                .params(List.of(id, limit))
                .query(Transactions.class).list();
        return infoServices;
    }

    private Integer jumlahtransaksi(String tipetransaksi) {
        int jumlahtransaksi = Integer
                .parseInt(jdbcClient.sql("select count(1) from transactions where tipetransaksi=:tipetransaksi")
                        .param("tipetransaksi", tipetransaksi).query().singleValue().toString());
        return jumlahtransaksi + 1;

    }

    // getsaldo
    private Double getsaldoakhir(String token) {
        Double saldoakhir = jdbcClient.sql("Select saldo from users where token=:token").param("token", token)
                .query(Users.class)
                .optional().get().getSaldo();
        return saldoakhir;
    }

    // insert transaksi
    private void transaksiinsert(Double saldo, String Tipetransaksi, String token, String id, String invoice_number,
            String description) {
        var updated = jdbcClient
                .sql("Insert into transactions(idtransaksi,nilaivalue, transactiontime,tipetransaksi,token,user_id,invoice_number,description) values(?,?,?,?,?,?,?,?)")
                .params(List.of(LocalDateTime.now() + id, saldo, LocalDateTime.now(), Tipetransaksi, token, id,
                        invoice_number, description))
                .update();

    }

    // update saldo akhir tipe disini ada dua pengeluaran atau pemasukan
    private String updatesaldo(String token, Double nilaisaldo, String tipes) {
        Double Sisasaldo = 0.0;
        if (tipes == "1") {
            Sisasaldo = getsaldoakhir(token) + nilaisaldo;
        } else {
            Sisasaldo = getsaldoakhir(token) - nilaisaldo;
        }

        if (Sisasaldo < 0) {
            return "0";
        }
        var updated = jdbcClient.sql("update users set saldo=? where token=?").params(List.of(Sisasaldo, token))
                .update();
        return "1";
    }

    // get balance
    public ApiResponse<Object> GetBalance(String id, String token) {
        Double balnces = getsaldoakhir(token);
        response.setStatus(0);
        response.setMessage("Get Balance Berhasil");
        response.setData(Map.of("balance", balnces));
        return response;
    }

    // insert data top up
    public ApiResponse<Object> InsertDataTopup(String token, Double saldo, String id, String Tipetransaksi) {
        try {
            if (saldo < 0) {
                response.setStatus(102);
                response.setMessage("Paramter amount hanya boleh angka dan tidak boleh lebih kecil dari 0");
                response.setData(null);
                return response;

            }

            if (updatesaldo(token, saldo, "1") == "1") {
                String invoice_code = geneteratedtoken
                        .generatedInvoice(jumlahtransaksi(Tipetransaksi));
                transaksiinsert(saldo, Tipetransaksi, token, id, invoice_code, "Top Up Money");
                response.setStatus(0);
                response.setMessage("Top Up Balance berhasil");
                response.setData(Map.of("balance", saldo));
            }
            return response;
        } catch (Exception e) {
            response.setStatus(108);
            response.setMessage("Token tidak tidak valid atau kadaluwarsa");
            response.setData(null);
            return response;
        }

    }

    // transaksi
    public ApiResponse<Object> Transaksi(rectransaksi rectransaksii, Logins logins) {
        Optional<InformationService> infserver = getServicetrans(rectransaksii.service_code());
        if (!infserver.isPresent()) {
            response.setStatus(102);
            response.setMessage("Service ataus Layanan tidak ditemukan");
            response.setData(null);
            return response;
        }
        try {
            String hasiltransaksi = updatesaldo(logins.getToken(), infserver.get().getService_tariff(), "2");

            if (hasiltransaksi == "1") {
                datas = new HashMap<>();
                String invoice_code = geneteratedtoken
                        .generatedInvoice(jumlahtransaksi(infserver.get().getService_code().toString()));

                transaksiinsert(infserver.get().getService_tariff(), infserver.get().getService_code(),
                        logins.getToken(), logins.getIdlogins(), invoice_code,
                        "Pembayaran " + infserver.get().getService_name());
                datas.put("invoice_number", invoice_code);
                datas.put("service_code", infserver.get().getService_code());
                datas.put("service_name", infserver.get().getService_name());
                datas.put("transcation_type", infserver.get().getService_icon());
                datas.put("total_amount", infserver.get().getService_tariff());

                datas.put("created_on", formatter.format(Instant.now()));

                response.setStatus(0);
                response.setMessage("Transaksi berhasil");
                response.setData(datas);
            } else {
                response.setStatus(108);
                response.setMessage(hasiltransaksi);
                response.setData(null);
            }

        } catch (Exception e) {
            response.setStatus(108);
            response.setMessage("Token tidak tidak valid atau kadaluwarsa");
            response.setData(null);
            return response;
        }
        return response;

    }

    // history transaksi all
    public ApiResponse<Object> allTransaksi(Integer offset, Integer limit, Logins logins) {
        if (logins.getToken() == "" || logins.getToken() == null) {
            response.setStatus(108);
            response.setMessage("Token tidak tidak valid atau kadaluwarsa");
            response.setData(null);
            return response;
        }
        List<Transactions> datatransaction = findalltransaksi(logins.getIdlogins(), limit);
        response.setStatus(0);
        response.setMessage("Get History Berhasil");
        datas = new HashMap<>();
        datas.put("offset", offset);
        datas.put("lmit", limit);
        List<Map<String, Object>> allrecords = new ArrayList<>();
        for (Transactions dt : datatransaction) {
            Map<String, Object> tempData = new HashMap<>();
            tempData.put("invoice_number", dt.getInvoice_number());
            tempData.put("transaction_type", dt.getTipetransaksi());
            tempData.put("description", dt.getDescription());
            tempData.put("total_amount", dt.getNilaivalue());

            tempData.put("created_on", formatter.format(dt.getTransactiontime()));
            allrecords.add(tempData);
        }
        datas.put("records", allrecords);
        response.setData(datas);
        return response;
    }

}
