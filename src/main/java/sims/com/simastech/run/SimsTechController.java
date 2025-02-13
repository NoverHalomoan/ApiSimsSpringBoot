package sims.com.simastech.run;

import java.io.IOException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import jakarta.servlet.http.HttpServletRequest;
import sims.com.simastech.SimsData.BannerInformation;
import sims.com.simastech.SimsData.ImageUploading;
import sims.com.simastech.SimsData.Logins;
import sims.com.simastech.SimsData.Users;
import sims.com.simastech.SimsData.rectransaksi;

@RestController
class SimsTechController {

    private final RepositoryUser repositoryUser;
    private final RepositoryInformation repositoryInformation;
    private final RepositoryTransaksi repositoryTransaksi;
    private Logins logins;

    // catatan boleh login jika waktu nya masih dibawah 3 menit

    public SimsTechController(RepositoryUser repositoryUser, Logins logins,
            RepositoryInformation repositoryInformation, RepositoryTransaksi repositoryTransaksi) {
        this.repositoryUser = repositoryUser;
        this.logins = logins;
        this.repositoryInformation = repositoryInformation;
        this.repositoryTransaksi = repositoryTransaksi;
    }

    // proses registrasi
    @Tag(name = "Modul Membership")
    @PostMapping("/registration")
    public ResponseEntity<ApiResponse<Object>> CreateRegist(
            @RequestBody @Schema(hidden = true) Users users) {
        ApiResponse<Object> response = repositoryUser.CreateRegist(users);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // proses login
    @Tag(name = "Modul Membership")
    @PostMapping("/login")
    public @Schema(hidden = true) ResponseEntity<ApiResponse<Object>> postLogin(
            @RequestBody @Schema(hidden = true) Logins login) {

        ApiResponse<Object> response = repositoryUser.loginapp(login);
        if (response.getToken() != "" || response.getToken() != "") {
            logins.setEmail(login.getEmail());
            logins.setPassword(login.getPassword());
            logins.setToken(response.getToken());
            logins.setLogintime(LocalTime.now());
            logins.setIdlogins(response.getLoginid());
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // proses profile
    @Tag(name = "Modul Membership")
    @GetMapping("/profile")
    public @Schema(hidden = true) ResponseEntity<ApiResponse<Object>> GetLogins() {
        ApiResponse<Object> response = repositoryUser.getprofile(logins);
        if (response.getStatus() == 108) {
            logins = new Logins();
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // proses update profile
    @Tag(name = "Modul Membership")
    @PutMapping("/profile/update")
    public @Schema(hidden = true) ResponseEntity<ApiResponse<Object>> UpdateProfile(
            @RequestBody @Schema(hidden = true) Users users) {
        ApiResponse<Object> response = repositoryUser.updateprofile(logins, users);
        if (response.getStatus() == 108) {
            logins = new Logins();
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // proses update profile
    @Tag(name = "Modul Membership")
    @PutMapping("/profile/image")
    public @Schema(hidden = true) ResponseEntity<ApiResponse<Object>> Updateimage(
            @RequestHeader Map<String, String> headers,
            @RequestParam("File") @Schema(hidden = true) MultipartFile files) {
        try {

            ImageUploading uploadimages = new ImageUploading(logins.getIdlogins(), files.getOriginalFilename(),
                    files.getContentType(), files.getBytes());
            ApiResponse<Object> response = repositoryUser.updateimage(uploadimages);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            ApiResponse<Object> response = new ApiResponse<>(108, "Token tidak tidak valid atau kadaluwarsa", null);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }

    }

    // Controller for Information Banner
    @Tag(name = "Modul Information")
    @GetMapping("/banner")
    public @Schema(hidden = true) ResponseEntity<ApiResponse<Object>> findallbanner(HttpServletRequest headers) {
        ApiResponse<Object> response = new ApiResponse<>(108, "Token tidak tidak valid atau kadaluwarsa", null);
        if (headers.getContentType() != "application/json") {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        response = repositoryInformation.findallbanner();
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    // Controller for Information Services
    @Tag(name = "Modul Information")
    @GetMapping("/services")
    public @Schema(hidden = true) ResponseEntity<ApiResponse<Object>> findallservices(HttpServletRequest headers) {
        ApiResponse<Object> response = new ApiResponse<>(108, "Token tidak tidak valid atau kadaluwarsa", null);
        if (headers.getContentType() != "application/json") {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        response = repositoryInformation.findallservices();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // Controller balance
    @Tag(name = "Modul Transaction")
    @GetMapping("/balance")
    public @Schema(hidden = true) ResponseEntity<ApiResponse<Object>> GetBalance(HttpServletRequest headers) {
        ApiResponse<Object> response = new ApiResponse<>(108, "Token tidak tidak valid atau kadaluwarsa", null);

        if (headers.getHeader("accept").compareTo("application/json") != 0) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        response = repositoryTransaksi.GetBalance(logins.getIdlogins(), logins.getToken());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // Controller balance
    @Tag(name = "Modul Transaction")
    @PostMapping("/topup")
    public @Schema(hidden = true) ResponseEntity<ApiResponse<Object>> topup(HttpServletRequest headers,
            @RequestBody @Schema(hidden = true) rectransaksi rectransaksii) {
        ApiResponse<Object> response = new ApiResponse<>(108, "Token tidak tidak valid atau kadaluwarsa", null);

        response = repositoryTransaksi.InsertDataTopup(logins.getToken(),
                rectransaksii.top_up_amount(), logins.getIdlogins(), "topup");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // Controller transaction
    @Tag(name = "Modul Transaction")
    @PostMapping("/transaction")
    public @Schema(hidden = true) ResponseEntity<ApiResponse<Object>> transcation(
            @RequestBody @Schema(hidden = true) rectransaksi rectransaksii) {
        ApiResponse<Object> response = new ApiResponse<>(108, "Token tidak tidak valid atau kadaluwarsa", null);

        response = repositoryTransaksi.Transaksi(rectransaksii, logins);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // Controller all transaction
    @Tag(name = "Modul Transaction")
    @GetMapping("/transaction/history")
    public @Schema(hidden = true) ResponseEntity<ApiResponse<Object>> alltranscation(@RequestParam Integer offset,
            @RequestParam Integer limit) {
        ApiResponse<Object> response = new ApiResponse<>(108, "Token tidak tidak valid atau kadaluwarsa", null);
        response = repositoryTransaksi.allTransaksi(offset, limit, logins);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
