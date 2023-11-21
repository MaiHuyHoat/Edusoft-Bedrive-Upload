package com.bedrive.app.bedrivefile.Service;

import com.bedrive.app.bedrivefile.Model.UserEntity;
import com.bedrive.app.bedrivefile.Respository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserService implements IUserService  {
    @Autowired
    private UserRepository userRepository;
    public ArrayList<UserEntity> getAllUser(){
        return (ArrayList<UserEntity>) this.userRepository.findAll();
    }
}
