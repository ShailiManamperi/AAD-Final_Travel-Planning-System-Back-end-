package lk.ijse.gdse.aad.api;

import lk.ijse.gdse.aad.dto.UserDTO;
import lk.ijse.gdse.aad.dto.sec.ErrorRes;
import lk.ijse.gdse.aad.dto.sec.LoginReq;
import lk.ijse.gdse.aad.dto.sec.LoginRes;
import lk.ijse.gdse.aad.exception.UserNotFoundException;
import lk.ijse.gdse.aad.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/user")
public class UserApi {
    @Autowired
    UserService userService;

    @ResponseBody
    @GetMapping()
    public ResponseEntity login(@RequestBody LoginReq loginReq)  {
        try {
            UserDTO user = userService.searchUserByEmail(loginReq.getEmail());
            String email= user.getEmail();
            LoginRes loginRes = new LoginRes(email);
            return ResponseEntity.ok(loginRes);

        }catch (UserNotFoundException e){
            ErrorRes errorResponse = new ErrorRes(HttpStatus.BAD_REQUEST,"Invalid username or password");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }catch (Exception e){
            ErrorRes errorResponse = new ErrorRes(HttpStatus.BAD_REQUEST, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

}
