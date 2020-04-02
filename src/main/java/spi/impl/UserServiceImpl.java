package spi.impl;

import spi.UserService;

public class UserServiceImpl implements UserService {

    @Override
    public void findUsers() {
        System.out.println("user service");
    }
}
