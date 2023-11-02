package lk.ijse.gdse.aad.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class UserDTO {
    private int userId;
    private String username;
    private String password;
    private String usernic;
    private String contact;
    private String email;
    private LocalDate birthday;
    private String nicFront;
    private String nicRear;
    private String roles;
    private String gender;
    private String remarks;
    private String profilePic;
    private String type;
    private byte []profilePicByte;
    private byte[] nicFrontByte;
    private byte[] nicRearByte;


}
