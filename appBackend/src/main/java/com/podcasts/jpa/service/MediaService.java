package com.podcasts.jpa.service;

import com.podcasts.jpa.mapper.MediaMapper;
import com.podcasts.jpa.util.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MediaService {
    @Autowired
    MediaMapper mediaMapper;
    @Autowired(required = false)
    Message message;

    public Message getMediaByArray(List<String> lists) {
        List<Map<String,Object>> list = mediaMapper.getMediaByArrayString(lists);
        message = new Message(true, 200, "", list);
        return message;
    }
}
