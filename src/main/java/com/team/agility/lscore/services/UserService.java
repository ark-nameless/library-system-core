package com.team.agility.lscore.services;

import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;

import com.team.agility.lscore.dtos.ChangeUserPasswordDTO;
import com.team.agility.lscore.dtos.UpdateUserDTO;
import com.team.agility.lscore.entities.User;
import com.team.agility.lscore.exceptions.AuthorizationException;

public interface UserService {
    User save(User user);
    void remove(String username);
    List<User> findAll();
    User findByEmail(String email);
    User findByUsername(String username);
    List<User> saveWithExcel(Sheet sheet);

    boolean existsByEmail(String email);
    boolean existsByUsername(String email);
    
    User update(String username, UpdateUserDTO data);
    void changePassword(String username, ChangeUserPasswordDTO payload);
    void resetPasswordWithUsername(String username);


    User addRole(String username, String roleName) throws AuthorizationException;
    User removeRole(String username, String roleName) throws AuthorizationException;

    void lockUser(String username);
    void unlockUser(String username);
    void disableUser(String username);
    void enableUser(String username);

    void incrementInvalidAttempt(String username);
    void incrementInvalidAttemptUsingEmail(String email);
    void resetInvalidAttempt(String username);
}
