package lk.ijse.gdse.aad.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDTO {
    private int id;
    private String username;
    private String password;
    private String email;
    private String type;
}