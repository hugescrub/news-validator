package net.hugescrub.newsservice.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthRequest {
    private String username;
    private String password;
}
