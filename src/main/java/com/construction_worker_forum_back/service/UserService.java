package com.construction_worker_forum_back.service;

import com.construction_worker_forum_back.model.dto.UserDto;
import com.construction_worker_forum_back.model.dto.UserRequestDto;
import com.construction_worker_forum_back.model.dto.simple.BioSimpleDto;
import com.construction_worker_forum_back.model.entity.User;
import com.construction_worker_forum_back.model.security.UserDetailsImpl;
import com.construction_worker_forum_back.config.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsernameIgnoreCase(username);

        return user.map(UserDetailsImpl::new)
                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));
    }

    public List<UserDto> getAllUsers() {
        return userRepository
                .findAll()
                .stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .toList();
    }

    @Cacheable(value = "userCache", key = "{#id}", cacheManager = "cacheManager1Hour")
    public Optional<UserDto> findById(Long id) {
        return userRepository.findById(id)
                .map(user -> modelMapper.map(user, UserDto.class));
    }

    public Optional<UserDto> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(user -> modelMapper.map(user, UserDto.class));
    }

    @Transactional
    public Optional<UserDto> register(UserRequestDto userRequestDto) {
        User user = modelMapper.map(userRequestDto, User.class);
        if (userRepository.existsByUsernameIgnoreCase(user.getUsername())) {
            return Optional.empty();
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return Optional.of(modelMapper.map(userRepository.save(user), UserDto.class));
    }

    @Transactional
    public UserDto changeBio(String username, BioSimpleDto newBio) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        user.setBio(newBio.getNewBio());
        return modelMapper.map(userRepository.save(user), UserDto.class);
    }

    @Transactional
    public byte[] changeAvatar(String username, MultipartFile multipartFile) throws IOException {
        User user = userRepository.findByUsername(username).get();
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        user.setAvatar("/user-avatar/" + user.getId() + "/" + fileName);

        User savedUser = userRepository.save(user);

        String uploadDir = "user-avatar/" + savedUser.getId();

        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        try (var fis = new FileInputStream("user-avatar/" + savedUser.getId() + "/cropped-image.jpeg")) {
            return fis.readAllBytes();
        }
    }

    @Transactional
    @CachePut(value = "userCache", key = "{#id}", cacheManager = "cacheManager1Hour")
    public UserDto updateUser(Long id, UserRequestDto userRequestDto) {
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        modelMapper.map(userRequestDto, user);
        user.setUpdatedAt(Date.from(Instant.now()));

        return modelMapper.map(user, UserDto.class);
    }

    @Transactional
    @CacheEvict(value = "userCache", key = "{#id}", cacheManager = "cacheManager1Hour")
    public boolean deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) return false;
        return userRepository.deleteByUsernameIgnoreCase(user.get().getUsername()) == 1;
    }
}
