package com.podcasts.jpa.service;

import com.podcasts.jpa.mapper.MediaMapper;
import com.podcasts.jpa.mapper.PersonalColumnMapper;
import com.podcasts.jpa.pojo.Media;
import com.podcasts.jpa.pojo.PersonalColumn;
import com.podcasts.jpa.util.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class PersonalColumnService {
    @Autowired
    PersonalColumnMapper personalColumnMapper;
    @Autowired
    MediaService mediaService;
    @Autowired
    MediaMapper mediaMapper;
    @Autowired(required = false)
    Message message;

    @Autowired
    FileStorageService fileStorageService;

    public Message create(PersonalColumn personalColumn) {
        try {
            PersonalColumn personalColumn1 = personalColumnMapper.save(personalColumn);
            message = new Message(true, 200, "", personalColumn1);
        } catch (Exception e) {
            message = new Message(false, 400, "发生错误", "");
        }
        return message;
    }

    public Message delete(Long id) {
        try {
            personalColumnMapper.deleteById(id);
            message = new Message(true, 200, "删除成功", "");
        } catch (Exception e) {
            message = new Message(false, 400, "参数错误", "");
        }
        return message;
    }

    public Message update(PersonalColumn personalColumn) {
        try {
            personalColumnMapper.save(personalColumn);
            message = new Message(true, 200, "修改成功", "");
        } catch (Exception e) {
            message = new Message(false, 400, "参数错误", "");
        }
        return message;
    }

    public Message findAll() {
        try {
            List<PersonalColumn> list = personalColumnMapper.findAll();
            for (PersonalColumn item : list) {
                System.out.println(item);
                if (!item.getMediaIdArray().equals("")) {
                    List<Media> temp = new LinkedList<>();
                    for (String id : item.getMediaIdArray().split(",")) {
                        temp.add(fileStorageService.findById(Long.valueOf(id)));
                    }
                    item.setAllMedia(temp);
                }
            }
            message = new Message(true, 200, "查找成功", list);
        } catch (Exception e) {
            message = new Message(false, 400, "", "");
        }
        return message;
    }

    public Message findAllByUserId(Long userId) {
        try {
            List<PersonalColumn> list = personalColumnMapper.findAllByUserId(userId);
            message = new Message(true, 200, "", list);
        } catch (Exception e) {
            message = new Message(false, 400, "", "");
        }
        return message;
    }

//    public Message findById(Long id) {
//        try {
//            Map<String, Object> map = personalColumnMapper.getByPersonalColumnId(id);
//            String mediaIdArray = (String) map.get("media_id_array"); // 获取媒体数组
//            mediaIdArray = mediaIdArray.substring(1, mediaIdArray.length() - 1); // 转换成"1,2,3"
//            long[] mediaIdArrays = StrUtil.splitToLong(mediaIdArray, ","); // 把字符串转换为long型数组
//            Map<String, Object> map1 = new HashMap<>(map);
//            System.out.println(mediaIdArrays);
//            List<Map<String, Object>> list = mediaMapper.getMediaByArrayLong(mediaIdArrays);
//            map1.put("media_array", list);
//            // TODO 返回值有问题类型接参数
//            message = new Message(true, 200, "", map1);
//        } catch (Exception e) {
//            System.out.println(e);
//            message = new Message(false, 400, "", "");
//        }
//        return message;
//    }

    public Message findById(Long id) {
        PersonalColumn personcolumn = personalColumnMapper.getById(id);
        message = new Message(true, 200, "", personcolumn);
        return message;
    }

    public Message findDetailById(Long id) {
        PersonalColumn personcolumn = personalColumnMapper.getById(id);
        if (!personcolumn.getMediaIdArray().equals("")) {
            List<Media> temp = new LinkedList<>();
            for (String ids : personcolumn.getMediaIdArray().split(",")) {
                temp.add(fileStorageService.findById(Long.valueOf(ids)));
            }
            personcolumn.setAllMedia(temp);
        }
        message = new Message(true, 200, "", personcolumn);
        return message;
    }

    public Message findAllByClassifyId(Long classifyId) {
        try {
            List<PersonalColumn> list = personalColumnMapper.findAllByClassifyId(classifyId);
            message = new Message(true, 200, "", list);
        } catch (Exception e) {
            message = new Message(false, 400, "", "");
        }
        return message;
    }

    public Message findByNameOrMediaName(String name) {
        Message message = null;
        try {
            List<PersonalColumn> list = personalColumnMapper.findByName(name);
            for (PersonalColumn item : list) {
                System.out.println(item);
                if (!item.getMediaIdArray().equals("")) {
                    List<Media> temp = new LinkedList<>();
                    for (String id : item.getMediaIdArray().split(",")) {
                        temp.add(fileStorageService.findById(Long.valueOf(id)));
                    }
                    item.setAllMedia(temp);
                }
            }
            message = new Message(true, 200, "", list);
        } catch (Exception e) {
            message = new Message(false, 400, "", "");
        }
        return message;
    }
}
