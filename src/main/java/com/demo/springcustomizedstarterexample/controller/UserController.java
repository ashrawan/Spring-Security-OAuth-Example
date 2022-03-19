package com.demo.springcustomizedstarterexample.controller;

import com.demo.springcustomizedstarterexample.security.AppSecurityUtils;
import com.demo.springcustomizedstarterexample.services.common.GenericResponseDTO;
import com.demo.springcustomizedstarterexample.services.webapp.user.UserService;
import com.demo.springcustomizedstarterexample.services.webapp.user.dto.UpdatePasswordRequestDTO;
import com.demo.springcustomizedstarterexample.services.webapp.user.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("users")
//@PreAuthorize("isAuthenticated()")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getAllUser(Pageable pageable) {
        log.info("User API: get all user");
        List<UserDTO> userDTOList = userService.getAllUsers(pageable);
        return new ResponseEntity<>(userDTOList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        log.info("User API: get user by id: ", id);
        UserDTO userDTO = userService.getUserById(id);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    // TODO Remove this Method: Registration Functionality Provided from public endpoint in AuthenticationController
//    @PostMapping
//    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {
//        log.info("User API: create user");
//        UserDTO returnedUserDTO = userService.createUser(userDTO);
//        return new ResponseEntity<>(returnedUserDTO, HttpStatus.OK);
//    }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody UserDTO userDTO) {
        log.info("User API: update user");
        UserDTO returnedUserDTO = userService.updateUser(userDTO);
        return new ResponseEntity<>(returnedUserDTO, HttpStatus.OK);
    }

    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordRequestDTO updatePasswordRequest) {
        log.info("User API: processing password update for userId: ");
        GenericResponseDTO<Boolean> genericResponse = userService.updatePassword(updatePasswordRequest);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<?> retrieveAuthenticatedUser() {
        Optional<Long> currentUserId = AppSecurityUtils.getCurrentUserId();
        log.info("User API: retrieve authenticated user details for userId: ", currentUserId.get());
        UserDTO genericResponse = userService.getUserById(currentUserId.get());
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @GetMapping("/email-exists")
    public ResponseEntity<?> exists(@RequestParam("email") String email) {
        GenericResponseDTO<Boolean> genericResponseDTO = userService.userEmailExists(email);
        return new ResponseEntity<>(genericResponseDTO, HttpStatus.OK);
    }

}
