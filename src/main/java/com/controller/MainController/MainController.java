package com.controller.MainController;

import com.service.MainService.MainService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(name = "메인", value = "/main")
public class MainController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MainService mainService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String mainPage(
            HttpServletRequest request
            , HttpServletResponse response) {

        logger.info("test12345");

        return "";
    }
}
