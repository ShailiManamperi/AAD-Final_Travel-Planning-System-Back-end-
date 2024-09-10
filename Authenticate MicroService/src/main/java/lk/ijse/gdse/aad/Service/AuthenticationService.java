package lk.ijse.gdse.aad.Service;

import lk.ijse.gdse.aad.Dto.AdminDTO;
import lk.ijse.gdse.aad.exception.*;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthenticationService extends UserDetailsService {
    AdminDTO searchUser(String email);

    int saveAdmin(AdminDTO adminDTO) throws SaveFailException;
}
