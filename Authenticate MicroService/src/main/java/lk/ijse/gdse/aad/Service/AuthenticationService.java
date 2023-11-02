package lk.ijse.gdse.aad.Service;

import lk.ijse.gdse.aad.Dto.AdminDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthenticationService extends UserDetailsService {
    AdminDTO searchUser(String email);
}
