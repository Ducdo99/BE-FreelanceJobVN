package group5.freelancejob.controllers;

import group5.freelancejob.services.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/account")
public class AccountController {
    private final AccountService _accService;

    public AccountController(AccountService accService) {
        _accService = accService;
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getPersonalInfo(@PathVariable Long id){
        var account = _accService.getAccountDetailFromId(id);
        var principal = _accService.getUserPrinciple(account);

        Map<String,String> token = new HashMap<>();
        token.put("role",principal.getRoleName());
        token.put("userId",principal.getUserId().toString());
        token.put("accountId",principal.getAccountId().toString());
        token.put("fullName",principal.getFullName());
        token.put("phone",principal.getPhone());
        token.put("email",principal.getEmail());
        token.put("avatar",principal.getAvatar());
        token.put("shortDesc",principal.getShortDesc());
        token.put("desc",principal.getDesc());

        return ResponseEntity.ok(token);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAll(){
        var resp = _accService.getAllAccountAndInfo();
        return ResponseEntity.ok().body(resp);
    }
}
