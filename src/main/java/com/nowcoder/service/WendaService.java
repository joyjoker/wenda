package com.nowcoder.service;

import org.springframework.stereotype.Service;


@Service
public class WendaService {
    public String getMessage(int userId) {
        return "Hello message:" + String.valueOf(userId);
    }
}