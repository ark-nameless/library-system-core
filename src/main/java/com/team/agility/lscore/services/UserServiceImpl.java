package com.team.agility.lscore.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.team.agility.lscore.constants.Constants;
import com.team.agility.lscore.dtos.ChangeUserPasswordDTO;
import com.team.agility.lscore.dtos.UpdateUserDTO;
import com.team.agility.lscore.entities.Role;
import com.team.agility.lscore.entities.User;
import com.team.agility.lscore.enums.AuditAction;
import com.team.agility.lscore.exceptions.AuthorizationException;
import com.team.agility.lscore.exceptions.DuplicateRecordException;
import com.team.agility.lscore.exceptions.InvalidCredentialsException;
import com.team.agility.lscore.exceptions.ResourceNotFoundException;
import com.team.agility.lscore.repos.RoleRepository;
import com.team.agility.lscore.repos.UserRepository;
import com.team.agility.lscore.utilities.EmailUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;


@Log4j2
@Transactional
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;

    private final RoleRepository roleRepo;

    private final PasswordEncoder passwordEncoder;

    private final AuditLogService auditLogService;


    @Override
    public User save(User user) {
        if (existsByEmail(user.getEmail())) {
            throw new DuplicateRecordException("Email already in use");
        }
        if (existsByUsername(user.getUsername())) {
            throw new DuplicateRecordException("Username already in use");
        }
        log.trace("Saving user: {}", user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        auditLogService.log(AuditAction.INSERT, user);
        return userRepo.save(user);
    }

    @Override
    public List<User> saveWithExcel(Sheet sheet) {
        List<User> errorRegisterUsers = new ArrayList<>();
        List<User> registeredUser = new ArrayList<>();
        DataFormatter dataFormatter = new DataFormatter();

        Iterator<Row> rowIterator = sheet.rowIterator();
        rowIterator.next();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            String firstname = dataFormatter.formatCellValue(row.getCell(0));
            String lastname = dataFormatter.formatCellValue(row.getCell(1));
            String username = dataFormatter.formatCellValue(row.getCell(2));
            String email = dataFormatter.formatCellValue(row.getCell(3));
            String password = dataFormatter.formatCellValue(row.getCell(4));

            User user = User.builder()
                .firstname(firstname)
                .lastname(lastname)
                .username(username)
                .email(email)
                .password(password)
                .build();

            try {
                save(user);
                registeredUser.add(user);
            } catch (Exception e) {
                errorRegisterUsers.add(user);
            }
        }

        auditLogService.log(AuditAction.INSERT, registeredUser);

        return errorRegisterUsers;
    }

    @Override
    public void remove(String username) {
        User user = findByUsername(username);
        log.trace("Deleting user: {}", user);
        auditLogService.log(AuditAction.INSERT, user);
        userRepo.delete(user);
    }

    @Override
    public List<User> findAll() {
        log.trace("Fetching all users");
        return userRepo.findAll();
    }

    @Override
    public User findByEmail(String email) {
        log.trace("Fetching user with email: {}", email);
        return userRepo.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public User findByUsername(String username) {
        log.trace("Fetching user with username: {}", username);
        return userRepo.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public boolean existsByEmail(String email) {
        log.trace("Checking user with email: {}", email);
        return userRepo.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        log.trace("Checking user with username: {}", username);
        return userRepo.existsByUsername(username);
    }

    @Override 
    public User update(String username, UpdateUserDTO data) {
        User user = findByUsername(username);

        if (existsByEmail(data.getEmail()) && !user.getEmail().equals(data.getEmail())) {
            throw new DuplicateRecordException("Email already in use");
        }
        if (existsByUsername(data.getUsername()) && !user.getUsername().equals(data.getUsername())) {
            throw new DuplicateRecordException("Username already in use");
        }

        log.trace("Update user: {}", user);
        
        if (!data.getFirstname().isBlank()) {
            user.setFirstname(data.getFirstname());
        }
        if (!data.getLastname().isBlank()) {
            user.setLastname(data.getLastname());
        }
        if (!data.getUsername().isBlank()) {
            user.setUsername(data.getUsername());
        }
        if (!data.getEmail().isBlank()) {
            user.setEmail(data.getEmail());
        }
        if (!data.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(data.getPassword()));
        }

        auditLogService.log(AuditAction.UPDATE, user);
        return user;
    }

    @Override
    public void changePassword(String username, ChangeUserPasswordDTO payload) {
        User user = findByUsername(username);

        boolean passwordMatched = passwordEncoder.matches(payload.getCurrentPassword(), user.getPassword());
        if (!passwordMatched) {
            throw new InvalidCredentialsException("Password does not match");
        }
        user.setPassword(passwordEncoder.encode(payload.getNewPassword()));
        auditLogService.log(username, AuditAction.UPDATE, user);
    }
    
    @Override 
    public void resetPasswordWithUsername(String username) {
        User user = findByUsername(username);

        String newPassword = EmailUtil.generateRandomPassword();
        Map<String, String> recipients = new HashMap<>();
        recipients.put(user.getEmail(), newPassword);

        EmailUtil.sendPasswordResetEmail(recipients);
        user.setPassword(passwordEncoder.encode(newPassword));
        auditLogService.log(AuditAction.UPDATE, user);
    }

    @Override
    public User addRole(String username, String roleName) throws AuthorizationException {
        User user = findByUsername(username);
        Role role = roleRepo.findByName(roleName).orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        
        boolean hasAccessToPrivilege = role.getPermissions().stream().anyMatch(r -> hasAccessToPrivilege(r.getName()));
        if (!hasAccessToPrivilege) {
            throw new AuthorizationException("Unauthorized to add role");
        }
        log.trace("Adding role: {} to user: {}", role, user);
        user.getRoles().add(role);
        auditLogService.log(AuditAction.UPDATE, user);
        return user;
    }

    @Override
    public User removeRole(String username, String roleName) throws AuthorizationException {
        User user = findByUsername(username);
        Role role = roleRepo.findByName(roleName).orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        
        boolean hasAccessToPrivilege = role.getPermissions().stream().anyMatch(r -> hasAccessToPrivilege(r.getName()));
        if (!hasAccessToPrivilege) {
            throw new AuthorizationException("Unauthorized to remove role");
        }
        log.trace("Removing role: [{}] to user: {}", role, user);
        user.getRoles().remove(role);
        auditLogService.log(AuditAction.UPDATE, user);
        return user;
    }

    @Override
    public void lockUser(String username){
        User user = findByUsername(username);

        log.trace("Locking user: {}", username);
        user.setLocked(true);
        user.setInvalidAttempts(0);
    }

    @Override
    public void unlockUser(String username){
        User user = findByUsername(username);

        log.trace("Unlock user: {}", username);
        user.setLocked(false);
        user.setInvalidAttempts(0);
        auditLogService.log(AuditAction.UPDATE, user);
    }

    @Override
    public void disableUser(String username){
        User user = findByUsername(username);

        log.trace("Disable user: {}", username);
        user.setActive(false);
        user.setInvalidAttempts(0);
        auditLogService.log(AuditAction.UPDATE, user);
    }

    @Override
    public void enableUser(String username){
        User user = findByUsername(username);

        log.trace("Enable user: {}", username);
        user.setActive(true);
        user.setInvalidAttempts(0);
        auditLogService.log(AuditAction.UPDATE, user);
    }

    @Override
    public void incrementInvalidAttempt(String username){
        User user = findByUsername(username);

        log.trace("Adding invalid authentication attempts to user: {}", username);
        user.setInvalidAttempts(user.getInvalidAttempts() + 1);
        if (user.getInvalidAttempts() >= Constants.MAX_INVALID_ATTEMPT) {
            lockUser(username);
        }
    }

    @Override
    public void incrementInvalidAttemptUsingEmail(String email){
        User user = findByEmail(email);

        log.trace("Adding invalid authentication attempts to user: {}", email);
        user.setInvalidAttempts(user.getInvalidAttempts() + 1);
        if (user.getInvalidAttempts() >= Constants.MAX_INVALID_ATTEMPT) {
            lockUser(user.getUsername());
        }
    }


    @Override
    public void resetInvalidAttempt(String username) {
        User user = findByUsername(username);

        log.trace("Resetting invalid password attempt");
        user.setInvalidAttempts(0);
    }
    
    @SuppressWarnings("unchecked")
    private boolean hasAccessToPrivilege(String privilege) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (null == authentication) {
            return false;
        }
        Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) authentication.getAuthorities();
        return authorities.stream().anyMatch( p -> p.getAuthority().equals(privilege));
    }

    
}
