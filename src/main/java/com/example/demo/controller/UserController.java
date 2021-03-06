package com.example.demo.controller;

import com.example.demo.model.Tweet;
import com.example.demo.model.User;
import com.example.demo.service.TweetService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TweetService tweetService;

    @GetMapping(value = "/users/{username}")
    public String getUser(@PathVariable(value="username") String username, Model model) {
        User loggedInUser = userService.getLoggedInUser();
        User user = userService.findByUsername(username);

        List<Tweet> tweets = tweetService.findAllByUser(user);
        List<User> following = loggedInUser.getFollowing();

        boolean isFollowing = false;
        for (User followedUser : following) {
            if (followedUser.getUsername().equals(username)) {
                isFollowing = true;
            }
        }
        boolean isSelfPage = loggedInUser.getUsername().equals(username);

        model.addAttribute("isSelfPage", isSelfPage);
        model.addAttribute("following", isFollowing);
        model.addAttribute("tweetList", tweets);
        model.addAttribute("user", user);
        return "user";
    }

    /*
    create a page that shows a list of all users. Let's add a controller method
    in UserController to serve up this page
     */
    @GetMapping(value = "/users")
    public String getUsers(Model model) {
        List<User> users = userService.findAll();
        User loggedInUser = userService.getLoggedInUser();

        List<User> usersFollowing = loggedInUser.getFollowing();
        SetFollowingStatus(users, usersFollowing, model);

        model.addAttribute("users", users);
        SetTweetCounts(users, model);

        return "users";
    }

    /*
    special functionality to this page to show how many times each user has
    tweeted. This method will take in a list of users and the Model, and update
    the Model to include tweet counts. To keep our controller method focused,
    we're choosing to break out this piece of functionality.
     */
    private void SetTweetCounts(List<User> users, Model model) {
        HashMap<String,Integer> tweetCounts = new HashMap<>();
        /*
        iterate through each user, getting a list of their tweets and adding the
        size of that list to the HashMap
         */
        for (User user : users) {
            List<Tweet> tweets = tweetService.findAllByUser(user);
            tweetCounts.put(user.getUsername(), tweets.size());
        }
        model.addAttribute("tweetCounts", tweetCounts);
    }

    /*
    this has something to do with slide 42 on Day 2 of twitter
     */
    private void SetFollowingStatus(List<User> users, List<User> usersFollowing, Model model) {
        HashMap<String, Boolean> followingStatus = new HashMap<>();
        String username = userService.getLoggedInUser().getUsername();
        for (User user : users) {
            if (usersFollowing.contains(user)) {
                followingStatus.put(user.getUsername(), true);
            } else if (!user.getUsername().equals(username)) {
                followingStatus.put(user.getUsername(), false);
            }
        }
        model.addAttribute("followingStatus", followingStatus);
    }

}//end UserController class