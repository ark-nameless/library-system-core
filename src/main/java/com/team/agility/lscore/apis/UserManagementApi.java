package com.team.agility.lscore.apis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.team.agility.lscore.constants.Constants;
import com.team.agility.lscore.constants.Endpoints;
import com.team.agility.lscore.dtos.CreateNewUserDTO;
import com.team.agility.lscore.dtos.UpdateUserDTO;
import com.team.agility.lscore.entities.User;
import com.team.agility.lscore.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;


@CrossOrigin(origins={"*"})
@RestController
@RequiredArgsConstructor
@RequestMapping(Endpoints.USERS_V1)
@SecurityRequirement(name = Constants.SECURITY_REQUIREMENT)
@Tag(name = "Users API", description = "User management API")
public class UserManagementApi {

    private final UserService userService;

    @Operation(summary = "Create new User",
        description = "This endpoint is used to create a new user login information")
    @PostMapping
    public ResponseEntity<User> registerNewUser(
        @RequestBody @Valid CreateNewUserDTO payload
    ) {
        User user = User.builder()
            .firstname(payload.getFirstname())
            .lastname(payload.getLastname())
            .email(payload.getEmail())
            .username(payload.getUsername())
            .password(payload.getPassword())
            .build();
            
        return ResponseEntity.ok().body(userService.save(user));
    }

    @Operation(summary = "Update user",
        description = "This endpoint is used to update an existing user using username")
    @PutMapping("/{username}")
    public ResponseEntity<User> updateUserDetails(
        @PathVariable @NotBlank @Size(min = 3, max = 255) String username,
        @RequestBody @Valid UpdateUserDTO updateUser
    ) {
        return ResponseEntity.ok().body(userService.update(username, updateUser));
    }
    

    @Operation(summary = "Delete User",
        description = "This endpoint is used to delete an already existing user that is store in the system using the user's email")
    @DeleteMapping("/{username}/delete")
    public ResponseEntity<?> deleteUser(
        @PathVariable @NotBlank @Size(min = 3, max = 255) String username
    ) {
        userService.remove(username);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Fetch All Users",
        description = "This endpoint is used to get all users stored in the system")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok().body(userService.findAll());
    }

    @Operation(summary = "Fetch User Using Username",
        description = "This endpoint is used to fetch a user that is store in the database using email")
    @GetMapping("/{username}")
    public ResponseEntity<User> getUserByUsername(
        @PathVariable @NotBlank @Size(min = 3, max = 255) String username
    ) {
        return ResponseEntity.ok().body(userService.findByUsername(username));
    }


    @Operation(summary = "Add Role to a User",
        description = "This endpoint is used to add an existing role to the specificed user email")
    @PutMapping("/{username}/role/{roleName}")
    public ResponseEntity<User> addNewRoleToUser(
        @PathVariable @NotBlank @Size(min = 3, max = 255) String username,
        @PathVariable @NotBlank @Size(min = 3, max = 255) String roleName
    ) {
        User user = userService.addRole(username, roleName);
        return ResponseEntity.ok().body(user);
    }

    @Operation(summary = "Remove Role to a User",
        description = "This endpoint is used to remove an existing role in the specified user email")
    @DeleteMapping("/{username}/role/{roleName}")
    public ResponseEntity<?> removeRoleInUser(
        @PathVariable @NotBlank @Size(min = 3, max = 255) String username,
        @PathVariable @NotBlank @Size(min = 3, max = 255) String roleName
    ) {
        userService.removeRole(username, roleName);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Register Using Excel",
        description = "This endpoint is used to add bulk of users using excel file")
    @RequestMapping(
            path = "/register-excel", 
            method = RequestMethod.POST, 
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<User>> registerUsersUsingExcel(
        @RequestParam("file") MultipartFile file
    ) throws IOException {
        List<User> errorRegisterUsers = new ArrayList<>();
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());

        Sheet sheet = workbook.getSheetAt(0);
        
        errorRegisterUsers = userService.saveWithExcel(sheet);

        workbook.close();
        return ResponseEntity.ok().body(errorRegisterUsers);
    }

    @Operation(summary = "Lock User",
        description = "This endpoint is used to lock user")
    @PutMapping("/{username}/lock")
    public ResponseEntity<User> lockUser(
        @PathVariable @NotBlank @Size(min = 3, max = 255) String username
    ) {
        userService.lockUser(username);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Unlock User",
        description = "This endpoint is used to unlock user")
    @PutMapping("/{username}/unlock")
    public ResponseEntity<User> unlockUser(
        @PathVariable @NotBlank @Size(min = 3, max = 255) String username
    ) {
        userService.unlockUser(username);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Disable User",
        description = "This endpoint is used to deactivate user")
    @PutMapping("/{username}/disable")
    public ResponseEntity<User> disableUser(
        @PathVariable @NotBlank @Size(min = 3, max = 255) String username
    ) {
        userService.disableUser(username);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Enable User",
        description = "This endpoint is used to enable user")
    @PutMapping("/{username}/enable")
    public ResponseEntity<User> activateUser(
        @PathVariable @NotBlank @Size(min = 3, max = 255) String username
    ) {
        userService.enableUser(username);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Reset User's Password", 
        description = "This endpoint is used to reset a password of a user using the username of the user")
    @PutMapping("/{username}/password-reset")
    public ResponseEntity<?> resetUserPasswordWithUsername(
        @PathVariable @NotBlank @Size(min=3, max = 255) String username
    ) {
        userService.resetPasswordWithUsername(username);
        return ResponseEntity.ok().build();
    }

}
