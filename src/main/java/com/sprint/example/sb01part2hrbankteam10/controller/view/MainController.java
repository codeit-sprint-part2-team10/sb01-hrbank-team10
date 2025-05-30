package com.sprint.example.sb01part2hrbankteam10.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("/")
public class MainController {

  @GetMapping
  public String index() {
    return "forward:/index.html";
  }

  @GetMapping("/dashboard")
  public String dashboard() {
    return "forward:/index.html";
  }

}
