package com.fooddude.cookbook.service;

import com.fooddude.cookbook.exception.UserNotFoundException;
import com.fooddude.cookbook.model.Recipe;
import com.fooddude.cookbook.model.User;
import com.fooddude.cookbook.repository.UserRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceImplTest {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void getUser() throws UserNotFoundException {
        User user = new User();
        user.setUsername("testUser");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("password");
        user.setEmail("user@test.com");
        user.setSavedRecipeIds(new ArrayList<>());
        user = userRepository.save(user);

        String msg = "Testing getUser method";
        String expected = user.getUsername();
        String actual = userService.getUser("testUser", "password").getUsername();
        assertEquals(expected, actual, msg);
    }

    @Test
    void getUserFail() throws UserNotFoundException {
        String msg = "Testing getUser method - Test Case: username was valid was found but password was not valid";

        User user = new User();
        user.setUsername("testUser");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("password");
        user.setEmail("user@test.com");
        user.setSavedRecipeIds(new ArrayList<>());
        user = userRepository.save(user);

        assertNull(userService.getUser("testUser", "wrong_password"));
    }

    @Test
    void userNotFound() throws UserNotFoundException {
        String msg = "Testing getUser method - Test Case: Username not found";
        assertThrows(UserNotFoundException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                userService.getUser("user_not_found", "password");
            }
        });
    }

    @Test
    @Transactional
    void createUser() {
        String msg = "Testing createUser method";

        List<Integer> recipes = new ArrayList<>();
        recipes.add(1);

        User user = new User();
        user.setUsername("testUser");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("password");
        user.setEmail("user@test.com");
        user.setSavedRecipeIds(recipes);

        List<Integer> expectedRecipes = user.getSavedRecipeIds();

        user = userService.createUser(user);
        Integer userId = user.getId();

        Optional<User> resultOp = userRepository.findById(userId);

        assertTrue(resultOp.isPresent(), "User Creation Failed");
        User actual = resultOp.get();
        List<Integer> actualRecipes = actual.getSavedRecipeIds();

        assertEquals(user.getUsername(), actual.getUsername(), "Username does not match");
        assertEquals(user.getFirstName(), actual.getFirstName(), "User firstName does not match");
        assertEquals(user.getLastName(), actual.getLastName(), "User lastName does not match");
        assertEquals(user.getPassword(), actual.getPassword(), "User password does not match");
        assertEquals(user.getEmail(), actual.getEmail(), "User email does not match");
        assertEquals(expectedRecipes.toString(), actualRecipes.toString(), "User recipes do not match");

    }

    @Test
    @Transactional
    void updateUser() throws NoSuchFieldException, IllegalAccessException {

        List<Integer> recipes = new ArrayList<>();
        recipes.add(1);

        User user = new User();
        user.setUsername("testUser");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("password");
        user.setEmail("user@test.com");
        user.setSavedRecipeIds(recipes);
        user = userRepository.save(user);
        Integer userId = user.getId();

        User orig = new User();
        final Field field = orig.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(orig, userId);
        orig.setFirstName(user.getFirstName());
        orig.setLastName(user.getLastName());
        orig.setUsername(user.getUsername());
        orig.setPassword(user.getPassword());
        orig.setEmail(user.getEmail());
        orig.setSavedRecipeIds(user.getSavedRecipeIds());

        assertNotEquals(user, orig, "Deep copy failed");

        orig.setPassword("strong_password");
        userService.updateUser(orig);

        User expected = orig;
        List<Integer> expectedRecipes = expected.getSavedRecipeIds();

        Optional<User> actualOp = userRepository.findById(userId);
        assertTrue(actualOp.isPresent());
        User actual = actualOp.get();
        List<Integer> actualRecipes = actual.getSavedRecipeIds();

        assertNotEquals(expected, actual, "Referencing expected and actual are the same object");
        assertEquals(expected.getUsername(), actual.getUsername(), "Username does not match");
        assertEquals(expected.getFirstName(), actual.getFirstName(), "User firstName does not match");
        assertEquals(expected.getLastName(), actual.getLastName(), "User lastName does not match");
        assertEquals(expected.getPassword(), actual.getPassword(), "User password does not match");
        assertEquals(expected.getEmail(), actual.getEmail(), "User email does not match");
        assertEquals(expectedRecipes.toString(), actualRecipes.toString(), "User recipes do not match");

    }
}