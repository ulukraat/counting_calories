package calories.counting_calories.controller;

import calories.counting_calories.model.Nutrition;
import calories.counting_calories.model.User;
import calories.counting_calories.repository.UserRepository;
import calories.counting_calories.service.NutritionService;
import calories.counting_calories.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class UserController {
    private final UserService userService;
    private final NutritionService nutritionService;
    private final UserRepository userRepository;

    public UserController(UserService userService, NutritionService nutritionService, UserRepository userRepository) {
        this.userService = userService;
        this.nutritionService = nutritionService;
        this.userRepository = userRepository;
    }

    @GetMapping("/welcome")
    public String showWelcome() {
        return "welcome"; // Показываем форму
    }
    @PostMapping("/welcome")
    public String createUser(@ModelAttribute User user, Model model) {
        User savedUser = userService.createUser(user);

        if (savedUser == null) {
            model.addAttribute("errorMessage", "Аккаунт уже занят!");
            return "welcome"; // та же форма, но с сообщением об ошибке
        }

        System.out.println("savedUser ID: " + savedUser.getId()); // <= ДОБАВЬ ЭТО
        return "redirect:/result?userId=" + savedUser.getId();
    }
    @PostMapping("/login")
    public String login(@ModelAttribute User user, Model model) {
        Optional<User> foundUser = userRepository.findByUsername(user.getUsername());
        if (foundUser.isPresent()) {
            return "redirect:/result?userId=" + foundUser.get().getId();
        }
        model.addAttribute("errorMessage","Такого аккаунта нет");
        return "login";
    }
    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

}
