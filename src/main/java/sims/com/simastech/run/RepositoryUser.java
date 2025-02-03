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

import sims.com.simastech.SimsData.ImageUploading;
import sims.com.simastech.SimsData.Logins;
import sims.com.simastech.SimsData.Users;

@Repository
public class RepositoryUser {

    private final JdbcClient jdbcClient;
    Map<String, String> datas = new HashMap<>();
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

    // cari data berdasarkan id
    public Optional<Users> searchbyuniqueid(String id) {
        Optional<Users> datacari = jdbcClient
                .sql("SELECT id,first_name,last_name from users where email=:id")
                .param("id", id).query(Users.class).optional();
        return datacari;
    }

    public Optional<Users> searchbyemail(String email) {
        Optional<Users> datacari = jdbcClient
                .sql("SELECT id,first_name,last_name from users where email=:email")
                .param("email", email).query(Users.class).optional();
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
            response.setLoginid(datacari.get().getId());
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
        if (searchbyemail(users.getEmail()).isPresent()) {
            response.setStatus(102);
            response.setMessage("Silahkan Login menggunakan email " + users.getEmail());
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

    // Repo untuk profile
    public ApiResponse<Object> getprofile(Logins logins) {
        response = new ApiResponse<>(0, "", null);
        Optional<Users> datacari = jdbcClient
                .sql("SELECT id,saldo, email, first_name, last_name,password,profile_image from users where token=:token")
                .param("token", logins.getToken()).query(Users.class).optional();
        if (logins.getLogintime().plusMinutes(20).isAfter(LocalTime.now())) {
            datas = new HashMap<>();
            datas.put("email", datacari.get().getEmail());
            datas.put("first_name", datacari.get().getFirst_name());
            datas.put("last_name", datacari.get().getLast_name());
            datas.put("profile_image", datacari.get().getProfile_image());

            response.setStatus(0);
            response.setMessage("Sukses");
            response.setData(datas);
            datas = new HashMap<>();
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

    // Repo untuk updateprofile
    public ApiResponse<Object> updateprofile(Logins logins, Users users) {
        response = new ApiResponse<>(0, "", null);
        Optional<Users> datacari = jdbcClient
                .sql("SELECT id,saldo, email, first_name, last_name,password,profile_image from users where token=:token")
                .param("token", logins.getToken()).query(Users.class).optional();
        if (!logins.getLogintime().plusMinutes(20).isAfter(LocalTime.now())) {
            response.setStatus(108);
            response.setMessage("Token tidak tidak valid atau kadaluwarsa");
            response.setData(null);
            var updated = jdbcClient.sql("update users set token = ? where id = ?")
                    .params(List.of("", datacari.get().getId())).update();
            // langsung keluar
        }
        // set data
        Double saldo = datacari.get().getSaldo();
        String email = datacari.get().getEmail();
        String first_name = datacari.get().getFirst_name();
        String last_name = datacari.get().getLast_name();
        String password = datacari.get().getPassword();
        if (users.getFirst_name() != null && users.getFirst_name() != "") {
            first_name = users.getFirst_name();
        }
        if (users.getLast_name() != null && users.getLast_name() != "") {
            last_name = users.getLast_name();
        }
        if (users.getEmail() != null && users.getEmail() != "") {
            email = users.getEmail();
        }
        if (users.getPassword() != "" && users.getPassword() != null) {
            password = users.getPassword();
        }
        System.out.println(first_name);
        System.out.println(last_name);
        if (datacari.isPresent()) {
            var updated = jdbcClient.sql(
                    "Update users set saldo=?, email=?, first_name=?, last_name=?,password=? where id=?")
                    .params(List.of(saldo, email, first_name, last_name, password, datacari.get().getId())).update();
            System.out.println(updated);
            datas = new HashMap<>();
            datas.put("email", email);
            datas.put("first_name", first_name);
            datas.put("last_name", last_name);
            datas.put("profile_image", datacari.get().getProfile_image());

            response.setStatus(0);
            response.setMessage("Update Profile berhasil");
            response.setData(datas);
        }

        return response;
    }

    public Integer cekimageProfile(String id) {
        Optional<ImageUploading> imgprofile = jdbcClient.sql("Select filename from image_profile where id =:id")
                .param("id", id).query(ImageUploading.class).optional();
        if (imgprofile.isPresent()) {
            var updated = jdbcClient
                    .sql("update users set profile_image=? where id=?")
                    .params(List.of(imgprofile.get().getFilename(), id))
                    .update();
            return 1;
        } else
            return 0;
    }

    // repo buat image
    public ApiResponse<Object> updateimage(ImageUploading imageUploading) {
        System.out.println("Ini adalah ID" + imageUploading.getId());
        if (imageUploading.getFiletype() != null
                && !imageUploading.getFiletype().toLowerCase().matches(".*\\.(png|jpg|jpeg)$")) {
            response.setStatus(0);
            response.setMessage("Format Image tidak sesuai");
            response.setData(null);
            return response;
        }
        if (cekimageProfile(imageUploading.getId()) == 0) {
            var updated = jdbcClient
                    .sql("Insert into image_profile(id, file_data, filename, filetype) VALUES(?, ?, ?, ?)")
                    .params(List.of(imageUploading.getId(), imageUploading.getFileData(), imageUploading.getFilename(),
                            imageUploading.getFiletype()))
                    .update();
            updated = cekimageProfile(imageUploading.getId());
        } else {
            var updated = jdbcClient
                    .sql("update image_profile set id=?, file_data=?, filename=?, filetype=?")
                    .params(List.of(imageUploading.getId(), imageUploading.getFileData(), imageUploading.getFilename(),
                            imageUploading.getFiletype()))
                    .update();
            updated = cekimageProfile(imageUploading.getId());
        }
        Optional<Users> dataupdate = searchbyuniqueid(imageUploading.getId());
        datas = new HashMap<>();
        datas.put("email", dataupdate.get().getEmail());
        datas.put("first_name", dataupdate.get().getFirst_name());
        datas.put("last_name", dataupdate.get().getLast_name());
        datas.put("profile_image", dataupdate.get().getProfile_image());

        response.setStatus(0);
        response.setMessage("Update Profile Image berhasil");
        response.setData(datas);
        datas = new HashMap<>();
        System.gc();
        return response;
    }

}
