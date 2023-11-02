package lk.ijse.gdse.aad.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;
    private String username;
    private String password;
    @Column(unique = true)
    private String usernic;
    private String contact;
    @Column(unique = true)
    private String email;
    private Date birthday;
    private String nicFrontImg;
    private String nicRearImg;
    private String profilePic;
    private String gender;
    private String type;
    private String remarks;
}
