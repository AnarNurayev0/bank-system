package bank.bank.controller.impl;

import java.util.List;
import jakarta.validation.Valid;
import bank.bank.entity.PayProvider;
import bank.bank.dto.DtoPayProviderIU;
import bank.bank.dto.DtoAdminLogin;
import lombok.RequiredArgsConstructor;
import bank.bank.service.IPayProviderService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/admin/provider")
@RequiredArgsConstructor
public class AdminPayProviderController {

    private final IPayProviderService payProviderService;

    @PostMapping("/login")
    public ResponseEntity<?> adminLogin(@Valid @RequestBody DtoAdminLogin dto) {
        if ("admin@admin".equals(dto.getUsername()) && "admin".equals(dto.getPassword())) {
            return ResponseEntity.ok("Login successful");
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }

    @PostMapping("/create")
    public PayProvider createProvider(@Valid @RequestBody DtoPayProviderIU dto) {
        return payProviderService.createProvider(dto);
    }

    @PutMapping("/update/{id}")
    public PayProvider updateProvider(@PathVariable Long id, @Valid @RequestBody DtoPayProviderIU dto) {
        return payProviderService.updateProvider(id, dto);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteProvider(@PathVariable Long id) {
        payProviderService.deleteProvider(id);
        return "Provider deleted successfully";
    }

    @PatchMapping("/toggle-active/{id}")
    public PayProvider toggleActive(@PathVariable Long id) {
        return payProviderService.toggleActive(id);
    }

    @GetMapping("/list")
    public List<PayProvider> getAllProviders() {
        return payProviderService.getAllProviders();
    }

    @GetMapping("/{id}")
    public PayProvider getProviderById(@PathVariable Long id) {
        return payProviderService.getProviderById(id);
    }
}
