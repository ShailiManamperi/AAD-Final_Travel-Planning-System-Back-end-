package lk.ijse.gdse.aad.Service.impl;



import lk.ijse.gdse.aad.Dto.AdminDTO;
import lk.ijse.gdse.aad.Entity.Admin;
import lk.ijse.gdse.aad.Repo.AuthenticateRepo;
import lk.ijse.gdse.aad.Service.AuthenticationService;
import org.json.JSONArray;
import org.json.JSONException;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticateRepo adminRepository;
    private final ModelMapper modelMapper;

    public AuthenticationServiceImpl(AuthenticateRepo adminRepository, ModelMapper modelMapper) {
        this.adminRepository = adminRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByEmail(email);
        List<String> roles = new ArrayList<>();
        roles.add("USER");
        UserDetails userDetails =
                org.springframework.security.core.userdetails.User.builder()
                        .username(admin.getEmail())
                        .password(admin.getPassword())
                        .roles(roles.toArray(new String[0]))
                        .build();
        return userDetails;
    }

    @Override
    public AdminDTO searchUser(String email) {
        Admin byEmail = adminRepository.findByEmail(email);
        System.out.println(byEmail);
        AdminDTO map = modelMapper.map(byEmail, AdminDTO.class);
//        return map;
//        ArrayList<String> strings = jsonStringToArray(byEmail.getType());
//        map.setType(strings);
        System.out.println(map);
        return map;
    }

    ArrayList<String> jsonStringToArray(String jsonString) throws JSONException {

        ArrayList<String> stringArray = new ArrayList<String>();

        JSONArray jsonArray = new JSONArray(jsonString);

        for (int i = 0; i < jsonArray.length(); i++) {
            stringArray.add(jsonArray.getString(i));
        }

        return stringArray;
    }

}