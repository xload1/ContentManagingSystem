package com.project.cms;

import com.project.cms.entities.Posts;
import com.project.cms.entities.Users;
import com.project.cms.other.ImageManipulation;
import com.project.cms.repsAndServiesies.PostService;
import com.project.cms.repsAndServiesies.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Controller
public class MainController {
    PostService postService;
    UserService userService;
    String registerError = "";
    String loginError = "";
    String uploadError = "";

    @Autowired
    public MainController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String redirect() {
        return "redirect:/posts";
    }

    @GetMapping("/upload")
    public String uploadImage(HttpServletRequest request, Model model) {
        if (getLogin(request).equals("")) {
            return "redirect:/login";
        }
        model.addAttribute("uploadError", uploadError);
        return "uploadImage";
    }

    @PostMapping("/uploadImage")
    public String uploadImageAction(@RequestParam String text, @RequestParam MultipartFile image,
                                    @RequestParam String tags, HttpServletRequest request) {
        if (!image.isEmpty()) {
            if (!tags.matches("[a-zA-Z]+(?:[ ,][a-zA-Z]+)*$")) {
                uploadError = "Tags should be a one english word written through a space or a coma";
                return "redirect:/upload";
            }
            try {
                byte[] imageData = image.getBytes();
                byte[] stretchedImageData = ImageManipulation.stretchImage(imageData, 500, 500);
                postService.save(new Posts(text, imageData, tags, getLogin(request),
                userService.findByLogin(getLogin(request)).getUsername(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy"))));
            } catch (IOException e) {
                uploadError = "Error while uploading image";
                return "redirect:/upload";
            }
            return "redirect:/posts";
        }
        return "redirect:/upload";
    }

    @GetMapping("/posts")
    public String posts(Model model, HttpServletRequest request) {
        if (getLogin(request).equals("")) {
            return "redirect:/login";
        }
        List<Posts> posts = postService.findAll();
        Collections.reverse(posts);
        model.addAttribute("posts", posts);
        return "posts";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("loginError", loginError);
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registerError", registerError);
        return "register";
    }

    public String getLogin(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("login")) {
                    return cookie.getValue();
                }
            }
        }
        return "";
    }

    @PostMapping("/login/check")
    public String loginCheck(@RequestParam String login, @RequestParam String password, HttpServletResponse response) {
        Users user = userService.findByLogin(login);
        if (user != null) {
            if (user.getPassword().equals(password)) {
                loginError = "successfully logged in";
                Cookie loginCookie = new Cookie("login", login);
                loginCookie.setMaxAge(30 * 60);
                loginCookie.setPath("/");
                response.addCookie(loginCookie);
                return "redirect:/login";
            } else {
                loginError = "Wrong password";
            }
        } else {
            loginError = "User does not exist";
        }
        return "redirect:/login";
    }

    @PostMapping("/register/check")
    public String registerCheck(@RequestParam String login, @RequestParam String password, HttpServletResponse response) {

        if (userService.findByLogin(login) == null) {
            if (password.length() >= 8) {
                if (login.matches("[a-z0-9]+")) {
                    if (login.length() < 20) {
                        if (password.length() < 25) {
                            userService.save(new Users(login, password));
                            registerError = "successfully registered";
                            Cookie loginCookie = new Cookie("login", login);
                            loginCookie.setMaxAge(30 * 60);
                            loginCookie.setPath("/");
                            response.addCookie(loginCookie);
                            return "redirect:/register";
                        } else {
                            registerError = "Password must be less than 25 characters long";
                        }
                    } else {
                        registerError = "Username must be less than 20 characters long";
                    }
                } else {
                    registerError = "Username must contain only lower letters and numbers";
                }
            } else {
                registerError = "Password must be at least 8 characters long";
            }
        } else {
            registerError = "User already exists";
        }
        return "redirect:/register";
    }

    @GetMapping("/my_profile")
    public String myProfile(Model model, HttpServletRequest request) {
        if (getLogin(request).equals("")) {
            return "redirect:/login";
        }
        Users user = userService.findByLogin(getLogin(request));
        model.addAttribute("user", user);
        return "my_profile";
    }
}
