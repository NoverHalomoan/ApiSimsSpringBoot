package sims.com.simastech.run;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import sims.com.simastech.SimsData.Logins;
import sims.com.simastech.SimsData.Users;

@Repository
public class RepositoryUser {

    private final JdbcClient jdbcClient;
    private static final GenerateToken geneteratedtoken = new GenerateToken();
    ApiResponse<Object> response = new ApiResponse<>(0, "", null);

    public RepositoryUser(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Optional<Users> searchbyName(String first_name, String last_name) {
        Optional<Users> datacari = jdbcClient
                .sql("SELECT id,first_name,last_name from users where upper(first_name)=:first_name and upper(last_name)=:last_name")
                .param("first_name", first_name.toUpperCase())
                .param("last_name", last_name.toUpperCase()).query(Users.class).optional();
        return datacari;
    }

    public String GeneratedIdRegist(String first_name, String last_name) {
        Optional<Users> finalsearch = searchbyName(first_name, last_name);
        String datatoken = geneteratedtoken.geneteratedtoken(10);
        String hasil = "";
        if (finalsearch.isPresent()) {
            hasil = finalsearch.get().getId();
        } else {
            hasil = datatoken + first_name + last_name;
        }
        return hasil;
    }

    // Repo untuk login
    public ApiResponse<Object> loginapp(Logins logins) {

        response = new ApiResponse<>(0, "", null);
        if (logins.getPassword() == null) {
            response.setStatus(102);
            response.setMessage("Paramter password kosong");
            return response;
        }
        if (logins.getEmail() == null || !logins.getEmail().contains("@")) {
            response.setStatus(102);
            response.setMessage("Paramter email tidak sesuai format");
            return response;
        }
        Optional<Users> datacari = jdbcClient
                .sql("SELECT id,saldo, email, first_name, last_name,password from users where email=:email and password=:password")
                .param("email", logins.getEmail())
                .param("password", logins.getPassword()).query(Users.class).optional();
        if (datacari.isPresent()) {
            String tokenlogin = geneteratedtoken.geneteratedtoken(371);
            var updated = jdbcClient.sql("update users set token = ? where id = ?")
                    .params(List.of(tokenlogin, datacari.get().getId())).update();
            response.setStatus(0);
            response.setMessage("Login Sukses");
            response.setToken(tokenlogin);
            response.setData(Map.of("token", tokenlogin));
        } else {
            response.setStatus(102);
            response.setMessage("Username atau password salah");
            response.setData(null);
        }
        return response;
    }

    // Repo untuk buat registrasi
    public ApiResponse<Object> CreateRegist(Users users) {
        if (users.getFirst_name() == null) {
            response.setStatus(102);
            response.setMessage("Paramter first name tidak sesuai format");
            return response;
        }
        if (users.getLast_name() == null) {
            response.setStatus(102);
            response.setMessage("Paramter last name tidak sesuai format");
            return response;
        }
        if (users.getPassword() == null) {
            response.setStatus(102);
            response.setMessage("Paramter password tidak sesuai format");
            return response;
        }
        if (users.getEmail() == null || !users.getEmail().contains("@")) {
            response.setStatus(102);
            response.setMessage("Paramter email tidak sesuai format");
            return response;
        }
        var updated = jdbcClient
                .sql("INSERT INTO users(saldo, email, first_name, id, last_name,password,profile_image)VALUES(?, ?, ?, ?, ?,?,?)")
                .params(List.of(0, users.getEmail(), users.getFirst_name().toString(),
                        GeneratedIdRegist(users.getFirst_name(), users.getLast_name()),
                        users.getLast_name().toString(), users.getPassword().toString(),
                        "https://yoururlapi.com/profile.jpeg"))
                .update();

        if (updated == 1) {
            response.setStatus(0);
            response.setMessage("Registrasi berhasil silahkan login");

        } else {
            response.setStatus(102);
            response.setMessage("Registrasi Gagal");
        }

        return response;

    }

    // Repo untuk update profile
    public ApiResponse<Object> getprofile(Logins logins) {
        response = new ApiResponse<>(0, "", null);
        Optional<Users> datacari = jdbcClient
                .sql("SELECT id,saldo, email, first_name, last_name,password,profile_image from users where token=:token")
                .param("token", logins.getToken()).query(Users.class).optional();
        if (logins.getLogintime().plusMinutes(10).isAfter(logins.getLogintime())) {
            Map<String, String> datas = new HashMap<>();
            datas.put("email", datacari.get().getEmail());
            datas.put("first_name", datacari.get().getFirst_name());
            datas.put("last_name", datacari.get().getLast_name());
            // datas.put("profile_image", datacari.get().getProfile_image());

            response.setStatus(0);
            response.setMessage("Sukses");
            response.setData(datas);
            System.gc();
        } else {
            response.setStatus(108);
            response.setMessage("Token tidak tidak valid atau kadaluwarsa");
            response.setData(null);
            var updated = jdbcClient.sql("update users set token = ? where id = ?")
                    .params(List.of("", datacari.get().getId())).update();
        }
        return response;
    }

}
