package com.service.MainService;

import com.mapper.MainMapper.MainMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MainService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MainMapper mainMapper;


}
