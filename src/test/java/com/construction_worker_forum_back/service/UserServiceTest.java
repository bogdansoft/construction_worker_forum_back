package com.construction_worker_forum_back.service;

import com.construction_worker_forum_back.model.dto.UserDto;
import com.construction_worker_forum_back.model.dto.UserRequestDto;
import com.construction_worker_forum_back.model.dto.simple.BioSimpleDto;
import com.construction_worker_forum_back.model.entity.User;
import com.construction_worker_forum_back.config.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ActiveProfiles("dev")
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;



    @Test
    void itShouldGetAllUsers() {
        //Given
        List<User> userList = new ArrayList<>(List.of(new User()));
        given(userRepository.findAll()).willReturn(userList);

        //When
        var expected = userService.getAllUsers();

        //Then
        assertEquals(expected.size(), userList.size());
        verify(userRepository, atLeastOnce()).findAll();
    }

    @Test
    void itShouldGetByIdUser() {
        //Given
        User user = new User();
        user.setId(1L);

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(modelMapper.map(user, UserDto.class)).willReturn(UserDto.builder().id(1L).build());

        //When
        var expected = userService.findById(user.getId());

        //Then
        assertTrue(expected.isPresent());
        verify(userRepository, atLeastOnce()).findById(anyLong());
    }

    @Test
    void itShouldGetByUsername() {
        //Given
        User user = new User();
        user.setUsername("rodolfo");

        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));
        given(modelMapper.map(user, UserDto.class)).willReturn(UserDto.builder().username("rodolfo").build());

        //When
        var expected = userService.findByUsername(user.getUsername());

        //Then
        assertTrue(expected.isPresent());
        verify(userRepository, atLeastOnce()).findByUsername(anyString());
    }

    @Test
    void itShouldRegisterUser() {
        //Given
        UserRequestDto userRequestDto =  UserRequestDto.builder().username("adam").build();

        UserDto userDto =  UserDto.builder().username("adam").id(1L).build();

        User user = User.builder().username("adam").id(1L).build();

        given(modelMapper.map(userRequestDto, User.class)).willReturn(user);
        given(modelMapper.map(user, UserDto.class)).willReturn(userDto);
        given(userRepository.save(user)).willReturn(user);
        given(userRepository.existsByUsernameIgnoreCase(user.getUsername())).willReturn(false);


        //When
        var expected = userService.register(userRequestDto);

        //Then
        assertNotNull(expected);
        assertEquals(expected.get().getUsername(), userDto.getUsername());

        verify(userRepository, atLeastOnce()).save(user);
        verify(modelMapper, atLeastOnce()).map(user, UserDto.class);
        verify(modelMapper, atLeastOnce()).map(userRequestDto, User.class);
    }


    @Test
    void itShouldChangeBio() {
        //Given
        User user = User.builder().username("adam").id(1L).bio("old bio").build();
        UserDto userDto =  UserDto.builder().username("adam").id(1L).bio("new bio").build();
        BioSimpleDto bioSimpleDto = BioSimpleDto.builder().newBio("new bio").build();

        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));
        given(modelMapper.map(user, UserDto.class)).willReturn(userDto);
        given(userRepository.save(user)).willReturn(user);

        //When
        var expected = userService.changeBio(user.getUsername(), bioSimpleDto);

        //Then
        assertNotNull(expected);
        assertEquals(expected.getBio(), bioSimpleDto.getNewBio());

        verify(userRepository, atLeastOnce()).save(user);
        verify(modelMapper, atLeastOnce()).map(user, UserDto.class);
    }

    @Test
    void itShouldDeleteUser() {
        //Given
        User user = User.builder().username("Darek").id(1L).build();
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(userRepository.deleteByUsernameIgnoreCase(user.getUsername())).willReturn(1);

        //When
        var expected = userService.deleteUser(user.getId());

        //Then
        assertTrue(expected);
        verify(userRepository, atLeastOnce()).findById(user.getId());
        verify(userRepository, atLeastOnce()).deleteByUsernameIgnoreCase(user.getUsername());
    }


}
