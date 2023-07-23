package com.project.cms.repsAndServiesies;

import com.project.cms.entities.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    UserRepository userRepository;
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public void save(Users user){
        userRepository.save(user);
    }
    public Users findByLogin(String login){
        return userRepository.findById(login).orElse(null);
    }

}
