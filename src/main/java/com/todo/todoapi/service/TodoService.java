package com.todo.todoapi.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.todo.todoapi.exception.NotFoundException;
import com.todo.todoapi.exception.TodoException;
import com.todo.todoapi.model.ToDo;
import com.todo.todoapi.model.dto.NewToDoDto;
import com.todo.todoapi.model.dto.TodoDto;
import com.todo.todoapi.model.dto.UpdateToDoDto;
import com.todo.todoapi.model.enums.Status;
import com.todo.todoapi.repository.ToDoRepository;
import com.todo.todoapi.repository.UserRepository;
import com.todo.todoapi.util.ToDoUtil;

@Service
public class TodoService {
  
  private ToDoRepository toDoRepository;
  private ToDoUtil toDoUtil;
  private UserRepository userRepository;

  public TodoService(
    ToDoRepository toDoRepository,
    ToDoUtil toDoUtil,
    UserRepository userRepository)
  {
    this.toDoRepository = toDoRepository;
    this.toDoUtil = toDoUtil;
    this.userRepository = userRepository;
  }

  public List<TodoDto> getToDos() throws TodoException {
    return toDoRepository.findAllByActiveTrue()
    .stream()
    .map(toDoUtil::convertToDotoDto)
    .collect(Collectors.toList());
  }

  public List<TodoDto> getToDosByUser(String userId) throws TodoException {
    return toDoRepository.findAllByActiveTrueAndUserIdIsAndStatusIs(userId, Status.PENDING.toString().toUpperCase())
    .stream()
    .map(toDoUtil::convertToDotoDto)
    .collect(Collectors.toList());
  }

  public ToDo createTodo(String userId, NewToDoDto todo) throws TodoException {
    var user = userRepository.findById(userId).orElseThrow(
      () -> new NotFoundException("Usuario no encontrado")
    );
    var newTodo = new ToDo();
    BeanUtils.copyProperties(todo, newTodo);
    newTodo.setUser(user);
    newTodo.setDate(LocalDate.now());
    newTodo.setActive(true);
    newTodo.setStatus(Status.PENDING);
    return toDoRepository.save(newTodo);
  }

  public ToDo updateTodo(String id, UpdateToDoDto updateToDoDto) throws TodoException {
    var toDo = toDoRepository.findById(id).orElseThrow(
      () -> new NotFoundException("To do no encontrado")
    );
    BeanUtils.copyProperties(updateToDoDto, toDo);
    return toDoRepository.save(toDo);
  }

}
