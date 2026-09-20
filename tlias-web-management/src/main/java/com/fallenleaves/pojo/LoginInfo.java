package com.fallenleaves.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginInfo {
    Integer id;
    String username;
    String password;
    String token;


}
