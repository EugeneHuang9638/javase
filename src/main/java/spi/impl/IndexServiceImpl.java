package spi.impl;

import spi.IndexService;
import spi.UserService;

public class IndexServiceImpl implements IndexService {

    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void index() {
        System.out.println("index service");
        // userService.findUsers();
    }
}
