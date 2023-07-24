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
import org.springframework.web.bind.annotation.PathVariable;
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
    String profileError = "";

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
            if (!tags.matches("[a-zA-Z]+(?:[ ,][a-zA-Z]+)*$")) {
                uploadError = "Tags should be a one or more english words written through a space or a coma";
                return "redirect:/upload";
            }
            try {
                byte[] imageData = image.getBytes();
//                byte[] stretchedImageData = ImageManipulation.stretchImage(imageData, 500, 500);
                postService.save(new Posts(text, imageData, tags, getLogin(request),
                userService.findByLogin(getLogin(request)).getUsername(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy"))));
            } catch (IOException e) {
                uploadError = "Error while uploading image";
                return "redirect:/upload";
            }
            return "redirect:/posts";
    }

    @GetMapping("/posts")
    public String posts(Model model, HttpServletRequest request) {
        if (getLogin(request).equals("")) {
            return "redirect:/login";
        }
        List<Posts> posts = postService.findAll();
        Collections.sort(posts);
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
                loginCookie.setMaxAge(60 * 60);
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
        model.addAttribute("profileError", profileError);
        model.addAttribute("posts", postService.findPostsByLogin(user.getLogin()));
        return "my_profile";
    }
    @PostMapping("/my_profile/save")
    public String saveProfile(@RequestParam String username,
                              @RequestParam String description,
                              HttpServletRequest request){
        Users user = userService.findByLogin(getLogin(request));
        user.setUsername(username);
        user.setDescription(description);
        try {
            userService.save(user);
            profileError = "Profile saved";
            postService.findPostsByLogin(user.getLogin()).forEach(post -> {
                post.setAuthor_username(username);
                postService.save(post);
            });
        } catch (Exception e){
            profileError = "Description must be less than 300 characters long," +
                    " and username must be less than 50 characters long";
        }
        return "redirect:/my_profile";
    }
    @GetMapping("/profile/{login}")
    public String profile(@PathVariable String login, Model model, HttpServletRequest request){
        if (getLogin(request).equals("")) {
            return "redirect:/login";
        }
        if(getLogin(request).equals(login)){
            return "redirect:/my_profile";
        }
        Users user = userService.findByLogin(login);
        model.addAttribute("user", user);
        model.addAttribute("posts", postService.findPostsByLogin(user.getLogin()));
        return "profile";
    }
    @GetMapping("posts/{id}")
    public String post(@PathVariable int id, Model model, HttpServletRequest request){
        if (getLogin(request).equals("")) {
            return "redirect:/login";
        }
        Posts post = postService.findById(id);
        model.addAttribute("post", post);
        model.addAttribute("currentUser",userService.findByLogin(getLogin(request)));
        return "post";
    }
    @PostMapping("/logout")
    public String logout(HttpServletResponse response){
        Cookie loginCookie = new Cookie("login", "");
        loginCookie.setMaxAge(0);
        loginCookie.setPath("/");
        response.addCookie(loginCookie);
        loginError = "successfully logged out";
        return "redirect:/login";
    }
    @GetMapping("posts/{id}/edit")
    public String editPost(@PathVariable int id, Model model, HttpServletRequest request){
        if (getLogin(request).equals("")) {
            return "redirect:/login";
        }
        model.addAttribute("post", postService.findById(id));
        return "edit_post";
    }
    @PostMapping("posts/{id}/edit/save")
    public String saveEditedPost(@PathVariable int id,
                                 @RequestParam String text,

                                 @RequestParam(required = false) MultipartFile image,
                                 HttpServletRequest request){
        if (getLogin(request).equals("")) {
            return "redirect:/login";
        }
        Posts post = postService.findById(id);
        if(!image.isEmpty()){
            try {
                byte[] imageData = image.getBytes();
                post.setImage(imageData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        post.setText(text + " (edited)");;
        postService.save(post);
        return "redirect:/posts/"+id;
    }
    @PostMapping("posts/{id}/delete")
    public String deletePost(@PathVariable int id, HttpServletRequest request){
        if (getLogin(request).equals("")) {
            return "redirect:/login";
        }
        postService.deleteById(id);
        return "redirect:/my_profile";
    }
}
