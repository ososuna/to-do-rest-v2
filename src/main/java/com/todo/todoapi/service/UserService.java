package com.todo.todoapi.service;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.todo.todoapi.model.User;
import com.todo.todoapi.model.dto.UserDto;
import com.todo.todoapi.repository.UserRepository;

@Service
public class UserService {
  
  private UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public UserDto getUser(String username) throws ResponseStatusException {
    Optional<User> user = userRepository.findByUsername(username);
    if (user.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
    }
    var userDto = new UserDto();
    BeanUtils.copyProperties(user.get(), userDto);
    return userDto;
  }

}
