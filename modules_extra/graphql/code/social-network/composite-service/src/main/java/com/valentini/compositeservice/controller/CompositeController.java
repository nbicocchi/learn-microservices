package com.valentini.compositeservice.controller;


import com.valentini.compositeservice.model.User;
import com.valentini.compositeservice.service.FileStorageService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@NoArgsConstructor
@Controller
public class CompositeController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Value("${application.api.key}")
    private String api_key;
    @Autowired
    private FileStorageService fileStorageService;

    @Value("${default-avatar-path}")
    private String defaultAvatarPath;

    @Value("${default-post-image-path}")
    private String defaultPostImagePath;

    private User retrieveLoggedUser() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-API-Key", api_key);

        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails loggedUser = (UserDetails) authentication.getPrincipal();
        log.info("credentials" , authentication.getCredentials());
        // Build the GraphQL query
        String query = "{ getUserByUsername(username: \"" + loggedUser.getUsername() + "\") { id username email password avatarPath} }";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity("http://user:7001/graphql", request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();

            // Check for errors in the response
            if (responseBody.containsKey("errors")) {
                throw new RuntimeException("Error retrieving data");
            }

            Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
            if (data == null) {
                throw new RuntimeException("Error retrieving data");
            }
            Map<String, Object> userMap = (Map<String, Object>) data.get("getUserByUsername");
            User user = new User();
            user.setId(Long.valueOf(userMap.get("id").toString()));
            user.setUsername((String) userMap.get("username"));
            user.setEmail((String) userMap.get("email"));
            user.setPassword((String) userMap.get("password"));
            user.setAvatarPath((String) userMap.get("avatarPath"));

            return user;
        } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new BadCredentialsException("Invalid API Key");
        } else {
            throw new RuntimeException("Error retrieving data");
        }
    }

    private void updateLoggedUser(User user) {
        try{
            // Get the currently authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            log.info("credentials" , authentication.getCredentials());
            Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                    org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .build(),
                    user.getPassword()
                    );

            // Set new authentication in the security context
            SecurityContextHolder.getContext().setAuthentication(newAuthentication);
        } catch (Exception e) {
            throw new RuntimeException("Error updating logged user: {}", e);
        }

    }

    @GetMapping("/")
    public String showDashboard(Model model) throws Exception {
        return homePage(model);  // Renders the Thymeleaf template for the home page
    }

    @GetMapping("/home")
    public String homePage(Model model) throws Exception {
        User user = retrieveLoggedUser();


        // Add the username to the model to display it in the view
        model.addAttribute("user", user );

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-API-Key", api_key);

        // Build the GraphQL query
        String query = "{ getPosts { id description user { id username avatarPath } comments { id user { id username avatarPath } content } likesCount imagePath } }";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity("http://post:7002/graphql", request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();

            // Check for errors in the response
            if (responseBody.containsKey("errors")) {
                throw new Exception("Error retrieving data");
            }

            Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
            if (data == null) {
                throw new Exception("Error retrieving data");
            }
            List<Map<String,Object>> posts = (List<Map<String, Object>>) data.get("getPosts");

            if (posts != null) {
                for (Map<String,Object> post : posts) {
                    // Build the GraphQL query
                    query = "{ isPostLikedByUser(postId: \"" + post.get("id") + "\", username: \"" + user.getUsername()+ "\") }";
                    requestBody = new HashMap<>();
                    requestBody.put("query", query);
                    request = new HttpEntity<>(requestBody, headers);
                    response = restTemplate.postForEntity("http://interaction:7003/graphql", request, Map.class);
                    if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                        responseBody = response.getBody();

                        // Check for errors in the response
                        if (responseBody.containsKey("errors")) {
                            throw new Exception("Error retrieving data");
                        }

                        data = (Map<String, Object>) responseBody.get("data");
                        if (data == null) {
                            throw new Exception("Error retrieving data");
                        }
                        post.put("isLiked", data.get("isPostLikedByUser"));
                    } else {
                        throw new Exception("Error retrieving data");
                    }
                }
                model.addAttribute("posts", posts);
            }
        } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new BadCredentialsException("Invalid API Key");
        } else {
            throw new Exception("Error retrieving data");
        }
            // Return the view name (e.g., home.html)
        return "home";
    }

    @GetMapping("/createPost")
    public String showCreatePostForm(Model model) {
        return "createPost";  // Renders the Thymeleaf template for the create post form
    }

    //NEW POST
    @PostMapping("/newPost")
    public String newPost(@RequestParam String description, @RequestParam("postPicture") MultipartFile postPictureFile, Model model) throws Exception {

        try {
            String imagePath = defaultPostImagePath;

            if (!postPictureFile.isEmpty()) {
                // Validate file type
                String contentType = postPictureFile.getContentType();
                if (contentType != null && contentType.startsWith("image/")) {
                    try {
                        // Upload the file to MinIO
                        imagePath = fileStorageService.uploadFile("post", postPictureFile);

                    } catch (Exception e) {
                        model.addAttribute("postCreationError", "Error uploading post image.");
                        return "createPost";
                    }
                } else {
                    model.addAttribute("postCreationError", "Invalid file type. Please upload an image.");
                    return "createPost";
                }
            }
            log.info("Avatar path: {}", imagePath);

            User retrievedUser = retrieveLoggedUser();
            // Build the GraphQL query to create post
            String mutation = "mutation { createPost( description: \"" + description + "\", userId: \"" + retrievedUser.getId() + "\" imagePath: \"" + imagePath + "\" ){ id description user { id username } imagePath } }";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-API-Key", api_key);
            Map<String, Object> requestBody = new HashMap<>();

            requestBody.put("query", mutation);
            log.info("Request body: {}", requestBody);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity("http://post:7002/graphql", request, Map.class);
            log.info("Response: {}", response);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null && response.getBody().containsKey("data")) {
                model.addAttribute("postCreationSuccess", "Post created successfully");
                return homePage(model);
            }else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                fileStorageService.deleteFile(imagePath);
                throw new BadCredentialsException("Invalid API Key");
            } else {
                fileStorageService.deleteFile(imagePath);
                model.addAttribute("postCreationError", "Error during post creation");
                return showCreatePostForm(model);
            }
        } catch (BadCredentialsException e) {
            throw e;
        } catch (Exception e) {
            model.addAttribute("postCreationError", "Error during post creation");
            return showCreatePostForm(model);
        }

    }

    @PostMapping("/posts/addComment")
    public ResponseEntity<Map<String, Object>> addComment(@RequestParam String content, @RequestParam Long postId, Model model) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-API-Key", api_key);

        // Get the currently authenticated user
        User retrievedUser = retrieveLoggedUser();

        // Build the GraphQL query
        String query = "mutation { addComment(postId: \"" + postId + "\", userId: \"" + retrievedUser.getId() + "\", content: \"" + content + "\"){ id content user { id username avatarPath } } }";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity("http://interaction:7003/graphql", request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();

            // Check for errors in the response
            if (responseBody.containsKey("errors")) {
                throw new RuntimeException("Error adding comment");
            }

            Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
            if (data == null) {
                throw new RuntimeException("Error adding comment");
            }

            return ResponseEntity.ok(responseBody);

        } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new BadCredentialsException("Invalid API Key");
        } else {
            throw new RuntimeException("Error adding comment");
        }

    }

    @PostMapping("/posts/like")
    public ResponseEntity<Void> likePost(@RequestParam String postId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-API-Key", api_key);

        // Get the currently authenticated user
       User retrievedUser = retrieveLoggedUser();

        // Build the GraphQL query
        String query = "mutation { likePost(postId: \"" + postId + "\", userId: \"" + retrievedUser.getId() + "\"){ id postId userId } }";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity("http://interaction:7003/graphql", request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();

            // Check for errors in the response
            if (responseBody.containsKey("errors")) {
                throw new RuntimeException("Error liking post");
            }

            Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
            if (data == null) {
                throw new RuntimeException("Error liking post");
            }

            return ResponseEntity.ok().build();

            //return homePage(model);

        } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new BadCredentialsException("Invalid API Key");
        } else {
            throw new RuntimeException("Error liking post");
        }
    }

    @PostMapping("/posts/unlike")
    public ResponseEntity<Void> unlikePost(@RequestParam String postId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-API-Key", api_key);

        // Get the currently authenticated user
        User retrievedUser = retrieveLoggedUser();

        // Build the GraphQL query
        String query = "mutation { unlikePost(postId: \"" + postId + "\", userId: \"" + retrievedUser.getId() + "\") }";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity("http://interaction:7003/graphql", request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();

            // Check for errors in the response
            if (responseBody.containsKey("errors")) {
                throw new RuntimeException("Error unliking post");
            }

            Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
            if (data == null) {
                throw new RuntimeException("Error unliking post");
            }

            return ResponseEntity.ok().build();

            //return homePage(model);

        } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new BadCredentialsException("Invalid API Key");
        } else {
            throw new RuntimeException("Error unliking post");
        }
    }

    @PostMapping("/posts/delete")
    public ResponseEntity<Void> deletePost(@RequestParam String postId, @RequestParam String imagePath) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-API-Key", api_key);

        // Build the GraphQL query
        String query = "mutation { deletePost(id: \"" + postId + "\") }";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity("http://post:7002/graphql", request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();

            // Check for errors in the response
            if (responseBody.containsKey("errors")) {
                throw new RuntimeException("Error deleting post");
            }
            if (!imagePath.equals(defaultPostImagePath)) {
                fileStorageService.deleteFile(imagePath);
            }

            return ResponseEntity.ok().build();

        } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new BadCredentialsException("Invalid API Key");
        } else {
            throw new RuntimeException("Error deleting post");
        }
    }

    @PostMapping("/posts/deleteComment")
    public ResponseEntity<Void> deleteComment(@RequestParam String commentId) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-API-Key", api_key);

        // Build the GraphQL query
        String query = "mutation { deleteComment(id: \"" + commentId + "\") }";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity("http://interaction:7003/graphql", request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();

            // Check for errors in the response
            if (responseBody.containsKey("errors")) {
                throw new RuntimeException("Error deleting comment");
            }

            return ResponseEntity.ok().build();

        } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new BadCredentialsException("Invalid API Key");
        } else {
            throw new RuntimeException("Error deleting comment");
        }
    }


    //LOGIN and REGISTRATION

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";  // Renders the Thymeleaf template for the login form
    }

    @GetMapping("/registration")
    public String showRegistrationForm() {
        return "registration";  // Renders the Thymeleaf template for the registration form
    }

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String email, @RequestParam String password, @RequestParam("avatar") MultipartFile avatarFile, Model model) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-API-Key", api_key);
        Map<String, Object> requestBody = new HashMap<>();

        String avatarPath = defaultAvatarPath;

        if (!avatarFile.isEmpty()) {
            // Validate file type
            String contentType = avatarFile.getContentType();
            if (contentType != null && contentType.startsWith("image/")) {
                try {
                    // Upload the file to MinIO
                    avatarPath = fileStorageService.uploadFile("avatar", avatarFile);

                } catch (Exception e) {
                    model.addAttribute("error", "Error uploading avatar image.");
                    return "registration";
                }
            } else {
                model.addAttribute("error", "Invalid file type. Please upload an image.");
                return "registration";
            }
        }
        log.info("Avatar path: {}", avatarPath);

        String query = "mutation CreateUser($username: String!, $email: String!, $password: String!, $avatarPath: String!) { createUser(username: $username, email: $email, password: $password, avatarPath: $avatarPath){ id username email password avatarPath } }";

        Map<String, Object> variables = new HashMap<>();
        variables.put("username", username);
        variables.put("email", email);
        variables.put("password", passwordEncoder.encode(password));
        variables.put("avatarPath", avatarPath);

        requestBody.put("query", query);
        requestBody.put("variables", variables);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity("http://user:7001/graphql", request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();
            if (responseBody.containsKey("errors")) {
                if (!avatarPath.equals(defaultAvatarPath)) {
                    fileStorageService.deleteFile(avatarPath);
                }
                List<Map<String, Object>> errors = (List<Map<String, Object>>) responseBody.get("errors");
                log.info("Errors: {}", errors);
                for (Map<String, Object> error : errors) {
                    String message = (String) error.get("message");
                    String errorType =((Map<String, String>) error.get("extensions")).get("classification");

                    // Check for the specific error message
                    if ("Username is already taken".equals(message) && errorType.equals("FORBIDDEN")) {
                        // Handle the "AlreadyExist" error here
                        model.addAttribute("error", "Username is already taken");
                        return "registration";
                    } else {
                        model.addAttribute("error", "Unexpected error occurred");
                        return "registration";
                    }
                }
            } else {
                Map<String, Object> userData = (Map<String, Object>) ((Map<String, Object>) responseBody.get("data")).get("createUser");
                User createdUser = new User();
                createdUser.setId(Long.valueOf(userData.get("id").toString()));
                createdUser.setUsername((String) userData.get("username"));
                createdUser.setEmail((String) userData.get("email"));
                createdUser.setPassword((String) userData.get("password"));
                createdUser.setAvatarPath((String) userData.get("avatarPath"));
                log.info("Created user: {}", createdUser);
                model.addAttribute("user", createdUser);
                return "userCreated";
            }
        } else if(response.getStatusCode() == HttpStatus.UNAUTHORIZED){
            if (!avatarPath.equals(defaultAvatarPath)) {
                fileStorageService.deleteFile(avatarPath);
            }
            throw new BadCredentialsException("Invalid API Key");
        } else {
            if (!avatarPath.equals(defaultAvatarPath)) {
                fileStorageService.deleteFile(avatarPath);
            }
            throw new RuntimeException("Unexpected error: " + response.getStatusCode());
        }

        return "userCreated";
    }

    @GetMapping("/user/{username}")
    public String showUserProfile(@PathVariable String username, Model model) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-API-Key", api_key);

        User user = retrieveLoggedUser();

        // Build the GraphQL query
        String query = "{ getUserByUsername(username: \"" + username + "\") { id username email avatarPath } }";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity("http://user:7001/graphql", request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();

            // Check for errors in the response
            if (responseBody.containsKey("errors")) {
                throw new RuntimeException("Error retrieving data");
            }

            Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
            if (data == null) {
                throw new RuntimeException("Error retrieving data");
            }
            if (data.get("getUserByUsername") == null) {
                throw new RuntimeException("Error retrieving data");
            }
            Map<String, Object> userData = (Map<String, Object>) data.get("getUserByUsername");
            User selectedUser = new User();
            selectedUser.setId(Long.valueOf(userData.get("id").toString()));
            selectedUser.setUsername((String) userData.get("username"));
            selectedUser.setEmail((String) userData.get("email"));
            selectedUser.setAvatarPath((String) userData.get("avatarPath"));

            model.addAttribute("currentUser", user);
            model.addAttribute("selectedUser", selectedUser);

            // Build the GraphQL query
            query = "{ getPostsByUserId(userId: \"" + selectedUser.getId() + "\") { id description user { id username avatarPath } comments { id user { id username avatarPath } content } likesCount imagePath } }";

            requestBody = new HashMap<>();
            requestBody.put("query", query);
            request = new HttpEntity<>(requestBody, headers);
            response = restTemplate.postForEntity("http://post:7002/graphql", request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                responseBody = response.getBody();

                // Check for errors in the response
                if (responseBody.containsKey("errors")) {
                    throw new RuntimeException("Error retrieving data");
                }

                data = (Map<String, Object>) responseBody.get("data");
                if (data == null) {
                    throw new RuntimeException("Error retrieving data");
                }
                List<Map<String,Object>> posts = (List<Map<String, Object>>) data.get("getPostsByUserId");

                if (posts != null) {
                    for (Map<String,Object> post : posts) {
                        // Build the GraphQL query
                        query = "{ isPostLikedByUser(postId: \"" + post.get("id") + "\", username: \"" + user.getUsername()+ "\") }";
                        requestBody = new HashMap<>();
                        requestBody.put("query", query);
                        request = new HttpEntity<>(requestBody, headers);
                        response = restTemplate.postForEntity("http://interaction:7003/graphql", request, Map.class);
                        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                            responseBody = response.getBody();

                            // Check for errors in the response
                            if (responseBody.containsKey("errors")) {
                                throw new RuntimeException("Error retrieving data");
                            }

                            data = (Map<String, Object>) responseBody.get("data");
                            if (data == null) {
                                throw new RuntimeException("Error retrieving data");
                            }
                            post.put("isLiked", data.get("isPostLikedByUser"));
                        } else {
                            throw new RuntimeException("Error retrieving data");
                        }
                    }
                    model.addAttribute("posts", posts);
                }
            } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new BadCredentialsException("Invalid API Key");
            } else {
                throw new RuntimeException("Error retrieving data");
            }

            return "userProfile";

        } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new BadCredentialsException("Invalid API Key");
        } else {
            throw new RuntimeException("Error retrieving data");
        }
    }

    @GetMapping("/editProfile")
    public String showUserProfile(Model model){
        model.addAttribute("user", retrieveLoggedUser());
        return "updateUser";
        // Renders the Thymeleaf template for the user profile form
    }

    @PostMapping("/userUpdate")
    public String updateUserProfile(
            @ModelAttribute("user") User user,
            BindingResult bindingResult,
            @RequestParam("avatarFile") MultipartFile avatarFile,
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) throws Exception {

        if (bindingResult.hasErrors()) {
            return "updateUser"; // Return to form if validation errors occur
        }

        User currentUser = retrieveLoggedUser();
        model.addAttribute("user", currentUser);
        // Fetch the logged-in user

        // Update basic user information
        currentUser.setUsername(user.getUsername());
        currentUser.setEmail(user.getEmail());

        // Handle password change
        if (!currentPassword.isEmpty() && !newPassword.isEmpty() && !confirmPassword.isEmpty()) {
            // Validate current password
            if (!passwordEncoder.matches(currentPassword, currentUser.getPassword())) {
                model.addAttribute("passwordError", "Current password is incorrect.");
                return "updateUser";
            }
            // Check if new passwords match
            if (!newPassword.equals(confirmPassword)) {
                model.addAttribute("passwordError", "New passwords do not match.");
                return "updateUser";
            }

            // Update password
            currentUser.setPassword(passwordEncoder.encode(newPassword));
        }

        // Handle profile picture upload
        if (!avatarFile.isEmpty()) {
            try {
                String avatarUrl = fileStorageService.uploadFile("avatar", avatarFile);
                currentUser.setAvatarPath(avatarUrl);
            } catch (Exception e) {
                model.addAttribute("profilePictureError", "Failed to upload profile picture.");
                return "updateUser";
            }
        }

        // Update user profile
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-API-Key", api_key);

        // Build the GraphQL query
        String query = "mutation UpdateUser($id: ID!, $username: String!, $email: String!, $password: String!, $avatarPath: String!) { updateUser(id: $id, username: $username, email: $email, password: $password, avatarPath: $avatarPath){ id username email password avatarPath } }";

        Map<String, Object> variables = new HashMap<>();
        variables.put("id", currentUser.getId());
        variables.put("username", currentUser.getUsername());
        variables.put("email", currentUser.getEmail());
        variables.put("password", currentUser.getPassword());
        variables.put("avatarPath", currentUser.getAvatarPath());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);
        requestBody.put("variables", variables);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity("http://user:7001/graphql", request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();
            if (responseBody.containsKey("errors")) {
                if(!avatarFile.isEmpty()) {
                    if (!currentUser.getAvatarPath().equals(defaultAvatarPath)) {
                        fileStorageService.deleteFile(currentUser.getAvatarPath());
                    }
                }


                List<Map<String, Object>> errors = (List<Map<String, Object>>) responseBody.get("errors");
                log.info("Errors: {}", errors);
                for (Map<String, Object> error : errors) {
                    String message = (String) error.get("message");
                    String errorType =((Map<String, String>) error.get("extensions")).get("classification");

                    // Check for the specific error message
                    if ("Username is already taken".equals(message) && errorType.equals("FORBIDDEN")) {
                        // Handle the "AlreadyExist" error here
                        model.addAttribute("errorMessage", "Username is already taken");
                        return "updateUser";
                    } else {
                        model.addAttribute("errorMessage", "Unexpected error occurred");
                        return "updateUser";
                    }
                }
            }
            updateLoggedUser(currentUser);
            model.addAttribute("successMessage", "Profile updated successfully.");
            model.addAttribute("user", ((Map<String, Object>) responseBody.get("data")).get("updateUser"));
            return "userUpdated";

        } else if(response.getStatusCode() == HttpStatus.UNAUTHORIZED){
            if(!avatarFile.isEmpty()) {
                if (!currentUser.getAvatarPath().equals(defaultAvatarPath)) {
                    fileStorageService.deleteFile(currentUser.getAvatarPath());
                }
            }
            throw new BadCredentialsException("Invalid API Key");
        } else {
            if(!avatarFile.isEmpty()) {
                if (!currentUser.getAvatarPath().equals(defaultAvatarPath)) {
                    fileStorageService.deleteFile(currentUser.getAvatarPath());
                }
            }
            throw new RuntimeException("Unexpected error: " + response.getStatusCode());
        }
    }



    @GetMapping("avatar/{avatarFile}")
    public ResponseEntity<?> getAvatar(@PathVariable String avatarFile) {
        try {
            InputStream inputStream = fileStorageService.getFile("avatar/" + avatarFile);
            String contentType = fileStorageService.getContentType("avatar/" + avatarFile); // Adjust as needed

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(new InputStreamResource(inputStream));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("post/{postImage}")
    public ResponseEntity<?> getPostImage(@PathVariable String postImage) {
        try {
            InputStream inputStream = fileStorageService.getFile("post/" + postImage);
            String contentType = fileStorageService.getContentType("post/" + postImage); // Adjust as needed

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(new InputStreamResource(inputStream));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}

