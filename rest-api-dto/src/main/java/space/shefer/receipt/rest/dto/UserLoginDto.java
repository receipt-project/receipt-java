package space.shefer.receipt.rest.dto;

import lombok.Data;

@Data
public class UserLoginDto {
  private String login;
  private String password;
}
