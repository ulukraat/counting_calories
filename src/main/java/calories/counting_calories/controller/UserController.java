package calories.counting_calories.controller;

import calories.counting_calories.model.Nutrition;
import calories.counting_calories.model.User;
import calories.counting_calories.service.NutritionService;
import calories.counting_calories.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {
    private final UserService userService;
    private final NutritionService nutritionService;

    public UserController(UserService userService, NutritionService nutritionService) {
        this.userService = userService;
        this.nutritionService = nutritionService;
    }

    @GetMapping("/welcome")
    public String showWelcome() {
        return "welcome"; // Показываем форму
    }
    @PostMapping("/welcome")
    public String createUser(@ModelAttribute User user) {
        User savedUser = userService.createUser(user);
        System.out.println("savedUser ID: " + savedUser.getId()); // <= ДОБАВЬ ЭТО
        return "redirect:/result?userId=" + savedUser.getId();
    }


}
