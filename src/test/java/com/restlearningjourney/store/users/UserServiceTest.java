package com.restlearningjourney.store.users;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void givenValidSort_whenGetAllUser_thenReturnSortedUsers() {
        // given: repository returns some users and "email" is a valid sort key
        User u1 = new User();
        User u2 = new User();
        u1.setEmail("email2");
        u1.setId(1L);
        u2.setEmail("email1");
        u2.setId(2L);
        //set to make them distinct
        when(userRepository.findAll(Sort.by("email").ascending())).thenReturn(List.of(u1, u2));

        UserDto d1 = mock(UserDto.class);
        UserDto d2 = mock(UserDto.class);
        when(userMapper.toDto(u1)).thenReturn(d1);
        when(userMapper.toDto(u2)).thenReturn(d2);

        // when
        Iterable<UserDto> result = userService.getAllUser("email");

        // then
        List<UserDto> list = (List<UserDto>) result;
        assertNotNull(list);
        assertEquals(2, list.size());

        verify(userRepository).findAll(Sort.by("email").ascending());
        verify(userMapper).toDto(u1);
        verify(userMapper).toDto(u2);
    }

    @Test
    void givenInvalidSort_whenGetAllUser_thenFallbackToNameSort() {
        // given: invalid sort value should fallback to "name"
        User u = new User();
        when(userRepository.findAll(Sort.by("name").ascending())).thenReturn(List.of(u));

        UserDto dto = mock(UserDto.class);
        when(userMapper.toDto(u)).thenReturn(dto);

        // when
        Iterable<UserDto> result = userService.getAllUser("unknown");

        // then
        List<UserDto> list = (List<UserDto>) result;
        assertNotNull(list);
        assertEquals(1, list.size());
        assertSame(dto, list.get(0));

        verify(userRepository).findAll(Sort.by("name").ascending());
        verify(userMapper).toDto(u);
    }

    @Test
    void givenExistingUserId_whenGetUser_thenReturnDto() {
        // given: user exists
        Long id = 10L;
        User user = new User();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserDto dto = mock(UserDto.class);
        when(userMapper.toDto(user)).thenReturn(dto);

        // when
        UserDto result = userService.getUser(id);

        // then
        assertNotNull(result);
        assertSame(dto, result);

        verify(userRepository).findById(id);
        verify(userMapper).toDto(user);
    }

    @Test
    void givenNonExistingUserId_whenGetUser_thenThrowUserNotFoundException() {
        // given: repository empty
        Long id = 99L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class, () -> userService.getUser(id));

        verify(userRepository).findById(id);
        verifyNoInteractions(userMapper);
    }

    @Test
    void givenNewUserRequest_whenRegisterUser_thenSaveAndReturnDto() {
        // given: email not taken, mapper converts request to entity, password encoded
        RegisterUserRequest req = new RegisterUserRequest();
        req.setEmail("a@b.c");
        req.setPassword("plain");

        when(userRepository.existsByEmail(req.getEmail())).thenReturn(false);

        User userEntity = new User();
        userEntity.setPassword("plain"); // initial password before encoding
        when(userMapper.toEntity(req)).thenReturn(userEntity);

        when(passwordEncoder.encode("plain")).thenReturn("encoded-pass");

        UserDto dto = mock(UserDto.class);
        when(userMapper.toDto(userEntity)).thenReturn(dto);

        // when
        UserDto result = userService.registerUser(req);

        // then
        assertNotNull(result);
        assertSame(dto, result);
        assertEquals("encoded-pass", userEntity.getPassword());
        assertEquals(Role.USER, userEntity.getRole());

        verify(userRepository).existsByEmail(req.getEmail());
        verify(userMapper).toEntity(req);
        verify(passwordEncoder).encode("plain");
        verify(userRepository).save(userEntity);
        verify(userMapper).toDto(userEntity);
    }

    @Test
    void givenDuplicateEmail_whenRegisterUser_thenThrowDuplicateUserException() {
        // given: email already exists
        RegisterUserRequest req = new RegisterUserRequest();
        req.setEmail("exists@domain");
        when(userRepository.existsByEmail(req.getEmail())).thenReturn(true);

        // when & then
        assertThrows(DuplicateUserException.class, () -> userService.registerUser(req));

        verify(userRepository).existsByEmail(req.getEmail());
        verifyNoInteractions(userMapper, passwordEncoder);
    }

    @Test
    void givenExistingUserAndRequest_whenUpdateUser_thenUpdateAndReturnDto() {
        // given: user in DB
        Long id = 5L;
        UpdateUserRequest req = new UpdateUserRequest();
        User userDb = new User();
        when(userRepository.findById(id)).thenReturn(Optional.of(userDb));

        doNothing().when(userMapper).updateUser(req, userDb);

        UserDto dto = mock(UserDto.class);
        when(userMapper.toDto(userDb)).thenReturn(dto);

        // when
        UserDto result = userService.updateUser(id, req);

        // then
        assertNotNull(result);
        assertSame(dto, result);

        verify(userRepository).findById(id);
        verify(userMapper).updateUser(req, userDb);
        verify(userRepository).save(userDb);
        verify(userMapper).toDto(userDb);
    }

    @Test
    void givenMissingUser_whenUpdateUser_thenThrowUserNotFoundException() {
        // given
        Long id = 77L;
        UpdateUserRequest req = new UpdateUserRequest();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(id, req));

        verify(userRepository).findById(id);
        verifyNoInteractions(userMapper);
    }

    // ---------- deleteUser ----------

    @Test
    void givenExistingUserId_whenDeleteUser_thenDeleteCalled() {
        // given
        Long id = 8L;
        User user = new User();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        // when
        userService.deleteUser(id);

        // then
        verify(userRepository).findById(id);
        verify(userRepository).delete(user);
    }

    @Test
    void givenNonExistingUserId_whenDeleteUser_thenThrowUserNotFoundException() {
        // given
        Long id = 99L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(id));

        verify(userRepository).findById(id);
        verify(userRepository, never()).delete(any());
    }

    // ---------- changePassword ----------

    @Test
    void givenCorrectOldPassword_whenChangePassword_thenSaveNewPassword() {
        // given: user exists and old password matches exactly (service compares plain text)
        Long id = 3L;
        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setOldPassword("old");
        req.setNewPassword("new");

        User user = new User();
        user.setPassword("old"); // stored password equal to old password in request
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        // when
        userService.changePassword(id, req);

        // then
        assertEquals("new", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void givenWrongOldPassword_whenChangePassword_thenThrowAccessDeniedException() {
        // given
        Long id = 4L;
        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setOldPassword("notMatching");
        req.setNewPassword("new");

        User user = new User();
        user.setPassword("actualOld");
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        // when & then
        assertThrows(AccessDeniedException.class, () -> userService.changePassword(id, req));

        verify(userRepository).findById(id);
        verify(userRepository, never()).save(any());
    }

    @Test
    void givenMissingUser_whenChangePassword_thenThrowUserNotFoundException() {
        // given
        Long id = 100L;
        ChangePasswordRequest req = new ChangePasswordRequest();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class, () -> userService.changePassword(id, req));

        verify(userRepository).findById(id);
        verifyNoInteractions(userMapper, passwordEncoder);
    }
}