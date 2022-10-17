package com.construction_worker_forum_back.service;

import com.construction_worker_forum_back.model.DTOs.UserRequest;
import com.construction_worker_forum_back.model.entity.AccountStatus;
import com.construction_worker_forum_back.model.entity.Role;
import com.construction_worker_forum_back.model.entity.User;
import com.construction_worker_forum_back.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ModelMapper modelMapper;

    @Transactional
    public Optional<User> register(User user) {
        if (userRepository.existsByUsernameIgnoreCase(user.getUsername())) {
            return Optional.empty();
        }
        user.setAccountStatus(AccountStatus.CREATED);
        user.setUserRoles(Role.USER);

        return Optional.of(userRepository.save(user));
    }

    public Optional<User> getUser(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public Boolean deleteUser(Long id) {
        User user = userRepository.findById(id).get();
        return userRepository.deleteByUsernameIgnoreCase(user.getUsername()) == 1;
    }

    @Transactional
    public User updateUser(Long id, UserRequest userRequest) {
        if(!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        User user = userRepository.findById(id).get();
        user.setUsername(userRequest.getUsername());
        user.setPassword(userRequest.getPassword());
        user.setEmail(userRequest.getEmail());
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());

        return userRepository.save(user);
    }
}
