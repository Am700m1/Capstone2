package org.example.flightsbookingmanagementsystem.Service;


import lombok.RequiredArgsConstructor;
import org.example.flightsbookingmanagementsystem.Api.ApiException;
import org.example.flightsbookingmanagementsystem.Api.EmailApi;
import org.example.flightsbookingmanagementsystem.Api.WhatsAppApi;
import org.example.flightsbookingmanagementsystem.Model.User;
import org.example.flightsbookingmanagementsystem.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EmailApi emailApi;
    private final WhatsAppApi whatsAppApi;


    public List<User> getUsers(){
        List<User> users = userRepository.findAll();

        if(users.isEmpty()){
            throw new ApiException("No users exist yet!");
        }

        return users;
    }


    public void addUser(User user){
        user.setLogin(false);
        userRepository.save(user);
    }


    public void updateUser(Integer id, User user){
        User oldUser = userRepository.findUserById(id);

        if(oldUser == null){
            throw new ApiException("User was not found!");
        }

        oldUser.setUsername(user.getUsername());
        oldUser.setEmail(user.getEmail());
        oldUser.setPassword(user.getPassword());
        oldUser.setLogin(false);

        userRepository.save(oldUser);
    }


    public void deleteUser(Integer id){
        User user = userRepository.findUserById(id);

        if(user == null){
            throw new ApiException("User was not found!");
        }

        userRepository.delete(user);
    }


    public void login(String username, String password){
        User user = userRepository.findUserByUsernameAndPassword(username, password);

        if(user == null){
            throw new ApiException("Username/Email or password is incorrect!");
        }

        user.setLogin(true);
        userRepository.save(user);
    }


    public void register(User user){
        user.setLogin(false);
        userRepository.save(user);
        emailApi.sendEmail(user.getEmail(), "Welcome to the family: " + user.getUsername(), "Welcome to the family, you are registered in our flight management system.");
    }


    public void logout(String username, String password){
        User user = userRepository.findUserByUsernameAndPassword(username, password);
        if(user == null){
            throw new ApiException("User not found!");
        }
        user.setLogin(false);
        userRepository.save(user);
    }
}
