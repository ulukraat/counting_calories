package calories.counting_calories.service;

import calories.counting_calories.model.User;
import calories.counting_calories.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
    public User createUser(User user) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (!existingUser.isPresent()) {
            user.setPassword(passwordEncoder.encode(user.getPassword())); // хеширование
            return userRepository.save(user);
        }
        return null;
    }
    public User updateUser(Long userId, double weight) {
        Optional<User> existingUser = userRepository.findById(userId);
        if(existingUser.isPresent()) {
            User user = existingUser.get();
            user.setWeight(weight);
            return userRepository.save(user);
        }   else{
            throw new RuntimeException("User not found");
        }
    }
    public String hashPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }

}
