package calories.counting_calories.service;

import calories.counting_calories.model.User;
import calories.counting_calories.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public User createUser(User user) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (!existingUser.isPresent()) {
            return userRepository.save(user);
        }
        System.out.println("Этот пользователь уже есть ты че ебень ");
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


}
