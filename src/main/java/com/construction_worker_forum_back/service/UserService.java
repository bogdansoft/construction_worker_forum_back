package com.construction_worker_forum_back.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.construction_worker_forum_back.model.dto.UserDto;
import com.construction_worker_forum_back.model.dto.UserRequestDto;
import com.construction_worker_forum_back.model.dto.simple.BioSimpleDto;
import com.construction_worker_forum_back.model.entity.User;
import com.construction_worker_forum_back.model.security.UserDetailsImpl;
import com.construction_worker_forum_back.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${application.bucket.name}")
    private String bucketName;

    private final AmazonS3Client s3Client;

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
    public UserDto changeAvatar(String username, MultipartFile multipartFile) throws IOException {
        User savedUser = null;
        File file = null;
        String fileName = null;
        try {
            User user = userRepository.findByUsername(username).get();
            log.trace("Teraz jestem w serwisie");
            fileName = user.getId()+"_"+ UUID.randomUUID();
            file = FileUploadUtil.convertMultiPartFileToFile(multipartFile);
            log.trace("Wkladam obiekt");
            s3Client.putObject(new PutObjectRequest(bucketName, fileName, file));
            log.trace("Obiket wlozony");
            user.setAvatar(fileName);
            log.trace("Pobieram adres URL {}", s3Client.getResourceUrl(bucketName, fileName));
            savedUser = userRepository.save(user);
            file.delete();
        } catch (Exception e) {
            file.delete();
            log.trace("Jeblo to jeblo na chuj drazyc temat");
        }
        UserDto userDto = modelMapper.map(savedUser, UserDto.class);
        userDto.setAvatarBytes(FileUploadUtil.getAvatarBytes(s3Client, bucketName, fileName));
        log.trace("Nasze bajty >"+ Arrays.toString(userDto.getAvatarBytes()));
        return userDto;
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
