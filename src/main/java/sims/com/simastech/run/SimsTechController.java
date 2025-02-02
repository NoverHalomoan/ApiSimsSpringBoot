package sims.com.simastech.run;

import java.sql.Time;
import java.time.LocalTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import sims.com.simastech.SimsData.Logins;
import sims.com.simastech.SimsData.Users;

@RestController
class SimsTechController {

    private final RepositoryUser repositoryUser;
    private Logins logins;

    // catatan boleh login jika waktu nya masih dibawah 3 menit

    public SimsTechController(RepositoryUser repositoryUser, Logins logins) {
        this.repositoryUser = repositoryUser;
        this.logins = logins;
    }

    @GetMapping("")
    public String home(Model model) {
        model.addAttribute("message", "Hello, Spring Boot MVC!");
        return "index";
    }

    // proses registrasi
    @PostMapping("/registration")
    public ResponseEntity<ApiResponse<Object>> CreateRegist(@RequestBody Users users) {
        ApiResponse<Object> response = repositoryUser.CreateRegist(users);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // proses login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> postLogin(@RequestBody Logins login) {

        ApiResponse<Object> response = repositoryUser.loginapp(login);
        if (!response.getToken().isEmpty()) {
            logins.setEmail(login.getEmail());
            logins.setPassword(login.getPassword());
            logins.setToken(response.getToken());
            logins.setLogintime(LocalTime.now());
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // proses profile
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<Object>> GetLogins() {
        ApiResponse<Object> response = repositoryUser.getprofile(logins);
        if (response.getStatus() == 108) {
            logins = new Logins();
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // proses update profile
    @PutMapping("/profile/update")
    public ResponseEntity<ApiResponse<Object>> UpdateProfile() {
        ApiResponse<Object> response = new ApiResponse<>(0, "", null);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
