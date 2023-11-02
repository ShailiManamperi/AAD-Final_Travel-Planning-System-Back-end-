package lk.ijse.gdse.aad.service.impl;


import jakarta.transaction.Transactional;
import lk.ijse.gdse.aad.dto.UserDTO;
import lk.ijse.gdse.aad.entity.User;
import lk.ijse.gdse.aad.exception.CreateFailException;
import lk.ijse.gdse.aad.exception.DeleteFailException;
import lk.ijse.gdse.aad.exception.UpdateFailException;
import lk.ijse.gdse.aad.exception.UserNotFoundException;
import lk.ijse.gdse.aad.repo.UserRepo;
import lk.ijse.gdse.aad.service.UserService;
import org.json.JSONArray;
import org.json.JSONException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.function.Consumer;

@Service
public class UserServiceImpl implements UserService {
    @Value("${admin.data}")
    private String adminDataEndPoint;
    private final UserRepo userRepo;

    private final ModelMapper modelMapper;
    public UserServiceImpl (UserRepo userRepo,ModelMapper mapper){
        this.userRepo = userRepo;
        this.modelMapper = mapper;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email);
        System.out.println(user);
        List<String> roles = new ArrayList<>();
        roles.add("user");
        UserDetails userDetails =
                org.springframework.security.core.userdetails.User.builder()
                        .username(user.getEmail())
                        .password(user.getPassword())
                        .roles(roles.toArray(new String[1]))
                        .build();
        return userDetails;
    }

    @Override
    public UserDTO searchUserByEmail(String email) throws UserNotFoundException {
        try {
            User byEmail = userRepo.findByEmail(email);
            System.out.println(byEmail);
            UserDTO map = modelMapper.map(byEmail, UserDTO.class);
            if (byEmail.getBirthday() != null)
            map.setBirthday(byEmail.getBirthday().toLocalDate());
            importImages(byEmail,map);
            return map;
        }catch (Exception e){
            e.printStackTrace();
            throw new UserNotFoundException("User Not Found");
        }
    }

    @Override
    @Transactional
    public void updateUser(UserDTO email) throws UpdateFailException {
        try {
            User user = modelMapper.map(email, User.class);
            User byEmail = userRepo.findByEmail(email.getEmail());
            user.setBirthday(Date.valueOf(email.getBirthday()));
            deleteImages(email,byEmail);
            exportImages(email,user);
            userRepo.updateUserInfoByEmail(user.getUsername(),user.getPassword(), user.getUsernic(), user.getContact(),
                    user.getEmail(), user.getBirthday(), user.getGender(), user.getRemarks(), user.getNicFrontImg(),
                    user.getNicRearImg(),user.getProfilePic());

        }catch (Exception e){
            throw new UpdateFailException("Operation Failed",e);
        }
    }

    @Override
    @Transactional
    public int addUsers(UserDTO userDTO) throws CreateFailException {
        try {
            User user = modelMapper.map(userDTO, User.class);
            System.out.println(user);
            User save = userRepo.save(user);
            exportImages(userDTO,user);
            userRepo.updateImages(save.getProfilePic(),save.getNicFrontImg(),save.getNicRearImg(),save.getEmail());
            return save.getUserId();
        }catch (Exception e){
            throw new CreateFailException("Operation Failed",e);
        }
    }

    private void deleteImages(UserDTO userDTO, User byEmail) {
        if (userDTO.getProfilePicByte()!=null){
            boolean delete = new File(byEmail.getProfilePic()).delete();
        }
        if (userDTO.getNicFrontByte()!=null){
            boolean delete = new File(byEmail.getNicFrontImg()).delete();
        }
        if (userDTO.getNicRearByte()!=null){
            boolean delete = new File(byEmail.getNicRearImg()).delete();
        }
    }

    @Override
    public void deleteUser(String email) throws DeleteFailException {
        try {
            userRepo.deleteByEmail(email);
        }catch (Exception e){
            throw new DeleteFailException("Operation Failed",e);
        }
    }

    @Override
    public List<UserDTO> getAll(String email) throws UserNotFoundException {
        return null;
    }

//    public void exportImages(UserDTO userDTO,User user) throws IOException {
//        String dt = LocalDate.now().toString().replace("-", "_") + "__"
//                + LocalTime.now().toString().replace(":", "_");
//
//        if (userDTO.getProfilePicByte() != null){
//            InputStream is = new ByteArrayInputStream(userDTO.getProfilePicByte());
//            System.out.println("is " + is);
//            BufferedImage bi = ImageIO.read(is);
//            System.out.println("bi "+bi);
//            File outputfile = new File("images/user/pro_pic/"+dt+ ".jpg");
//            System.out.println(outputfile);
//            ImageIO.write(bi, "jpg", outputfile);
//            user.setProfilePic(outputfile.getAbsolutePath());
//        }
//
//        if (userDTO.getNicFrontByte() != null){
//            InputStream is1 = new ByteArrayInputStream(userDTO.getNicFrontByte());
//            BufferedImage bi1 = ImageIO.read(is1);
//            File outputfile1 = new File("images/user/nic_front/"+dt+ ".jpg");
//            ImageIO.write(bi1, "jpg", outputfile1);
//            user.setNicFrontImg(outputfile1.getAbsolutePath());
//        }
//
//        if (userDTO.getNicRearByte() != null){
//            InputStream is2 = new ByteArrayInputStream(userDTO.getNicRearByte());
//            BufferedImage bi2 = ImageIO.read(is2);
//            File outputfile2 = new File("images/user/nic_rear/"+dt+ ".jpg");
//            ImageIO.write(bi2, "jpg", outputfile2);
//            user.setNicRearImg(outputfile2.getAbsolutePath());
//        }
//
//    }


    public void exportImages(UserDTO userDTO, User user) {
        String dt = LocalDate.now().toString().replace("-", "_") + "__"
                + LocalTime.now().toString().replace(":", "_");

        try {
            if (userDTO.getProfilePicByte() != null) {
                exportImage(userDTO.getProfilePicByte(), "images/user/pro_pic/", dt, user::setProfilePic);
            }

            if (userDTO.getNicFrontByte() != null) {
                exportImage(userDTO.getNicFrontByte(), "images/user/nic_front/", dt, user::setNicFrontImg);
            }

            if (userDTO.getNicRearByte() != null) {
                exportImage(userDTO.getNicRearByte(), "images/user/nic_rear/", dt, user::setNicRearImg);
            }
        } catch (IOException e) {
            // Handle or log the exception
            e.printStackTrace();
            // You might want to throw a custom exception or handle this error more gracefully
        }
    }

    private void exportImage(byte[] imageBytes, String directory, String timestamp, Consumer<String> updateField) throws IOException {
        InputStream is = new ByteArrayInputStream(imageBytes);
        BufferedImage bi = ImageIO.read(is);

        File outputfile = new File(directory + timestamp + ".jpg");
        System.out.println("File path before writing: " + outputfile.getAbsolutePath());
        if (ImageIO.write(bi, "jpg", outputfile)) {
            System.out.println("File path after writing: " + outputfile.getAbsolutePath());
            updateField.accept(outputfile.getAbsolutePath());
        } else {
            // Handle the case where the image couldn't be written to the file
            // You might want to throw an exception or handle this error as appropriate
        }
    }

    public void importImages(User user,UserDTO userDTO) throws IOException {
        BufferedImage read = ImageIO.read(new File(user.getProfilePic()));
        System.out.println("im "+read);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.out.println("baos "+baos);
        ImageIO.write(read, "jpg", baos);
        byte[] bytes = baos.toByteArray();
        System.out.println(bytes);
        userDTO.setProfilePic(Base64.getEncoder().encodeToString(bytes));

        read = ImageIO.read(new File(user.getNicFrontImg()));
        baos = new ByteArrayOutputStream();
        ImageIO.write(read, "jpg", baos);
        bytes = baos.toByteArray();
        userDTO.setNicFront(Base64.getEncoder().encodeToString(bytes));
        userDTO.setNicFrontByte(bytes);

        read = ImageIO.read(new File(user.getNicRearImg()));
        baos = new ByteArrayOutputStream();
        ImageIO.write(read, "jpg", baos);
        bytes = baos.toByteArray();
        userDTO.setNicRear(Base64.getEncoder().encodeToString(bytes));
        userDTO.setNicRearByte(bytes);
    }

}