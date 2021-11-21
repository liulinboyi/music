package com.podcasts.jpa.controller;

import com.podcasts.jpa.pojo.PersonalColumn;
import com.podcasts.jpa.service.PersonalColumnService;
import com.podcasts.jpa.util.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/personalColumn")
public class PersonalColumnController {
    @Autowired
    PersonalColumnService personalColumnService;
    @Autowired(required = false)
    Message message;

    /**
     * showdoc
     *
     * @param "title"        必选 String 专栏标题
     * @param "content"      必选 String 专栏内容
     * @param "mediaIdArray" 必选 String 专栏内音视频id字符串，以,号分隔
     * @param "userId"       必选 Long 用户Id
     * @param "username"     必选 String 用户名
     * @param "classifyId"   必选 Long 专栏分类
     * @return {"status":200,"data":{"id":"1","username":"12154545"}}
     * @catalog 个人专栏接口
     * @title 专栏创建
     * @description 访问权限：用户；
     * @method post
     * @url /personalColumn/create
     * @header token 可选 string 设备token
     * @return_param status int 状态码
     * @return_param data String 数据
     * @return_param success Boolean 是否成功
     * @return_param message String 消息
     * @remark null
     * @number null
     */
    @PostMapping("/create")
    void create(@RequestBody PersonalColumn personalColumn, HttpServletResponse resp) throws IOException {
        System.out.println(personalColumn.getMediaIdArray());
        personalColumn.MediaIdArrayToString();
        message = personalColumnService.create(personalColumn);
        message.returnJson(resp);

    }

    /**
     * showdoc
     *
     * @param "id" 必选 Long 专栏标题
     * @return {"status":200,"data":{"id":"1","username":"12154545"}}
     * @catalog 个人专栏接口
     * @title 专栏删除
     * @description 访问权限：用户；
     * @method get
     * @url /personalColumn/delete
     * @header token 可选 string 设备token
     * @return_param status int 状态码
     * @return_param data String 数据
     * @return_param success Boolean 是否成功
     * @return_param message String 消息
     * @remark null
     * @number null
     */
    @GetMapping("/delete")
    void delete(@RequestParam(name = "id") Long id, HttpServletResponse resp) throws IOException {
        message = personalColumnService.delete(id);
        message.returnJson(resp);
    }

    /**
     * showdoc
     *
     * @param "id"           必选 Long 专栏标题
     * @param "title"        必选 String 专栏标题
     * @param "content"      必选 String 专栏内容
     * @param "mediaIdArray" 必选 Array 专栏内音视频id数组
     * @param "userId"       必选 Long 用户Id
     * @param "username"     必选 String 用户名
     * @param "classifyId"   必选 Long 专栏分类
     * @return {"status":200,"data":{"id":"1","username":"12154545"}}
     * @catalog 个人专栏接口
     * @title 专栏修改
     * @description 访问权限：用户；
     * @method post
     * @url /personalColumn/update
     * @header token 可选 string 设备token
     * @return_param status int 状态码
     * @return_param data String 数据
     * @return_param success Boolean 是否成功
     * @return_param message String 消息
     * @remark null
     * @number null
     */
    @PostMapping("/update")
    void update(@RequestBody PersonalColumn personalColumn, HttpServletResponse resp) throws IOException {
        personalColumn.MediaIdArrayToString();
        message = personalColumnService.update(personalColumn);
        message.returnJson(resp);
    }

    /**
     * showdoc
     *
     * @param "null" 必选 null 不需要参数
     * @return {"status":200,"data":{"id":"1","username":"12154545"}}
     * @catalog 个人专栏接口
     * @title 全部专栏
     * @description 访问权限：用户；
     * @method get
     * @url /personalColumn/findAll
     * @header token 可选 string 设备token
     * @return_param status int 状态码
     * @return_param data String 数据
     * @return_param success Boolean 是否成功
     * @return_param message String 消息
     * @remark null
     * @number null
     */
    @GetMapping("/findAll")
    void findAll(HttpServletResponse resp) throws IOException {
        message = personalColumnService.findAll();
        message.returnJson(resp);
    }

    /**
     * showdoc
     *
     * @param "userId" 必选 Long 用户id
     * @return {"status":200,"data":{"id":"1","username":"12154545"}}
     * @catalog 个人专栏接口
     * @title 通过用户Id查找专栏
     * @description 访问权限：用户；
     * @method get
     * @url /personalColumn/findAllByUserId
     * @header token 可选 string 设备token
     * @return_param status int 状态码
     * @return_param data String 数据
     * @return_param success Boolean 是否成功
     * @return_param message String 消息
     * @remark null
     * @number null
     */
    @GetMapping("/findAllByUserId")
    void findAllByUserId(@RequestParam(value = "userId") Long userId, HttpServletResponse resp) throws IOException {
        message = personalColumnService.findAllByUserId(userId);
        message.returnJson(resp);
    }

    /**
     * showdoc
     *
     * @param "classifyId" 必选 Long 专栏id
     * @return {"status":200,"data":{"id":"1","username":"12154545"}}
     * @catalog 个人专栏接口
     * @title 通过分类Id查找专栏
     * @description 访问权限：用户；
     * @method get
     * @url /personalColumn/findAllByClassifyId
     * @header token 可选 string 设备token
     * @return_param status int 状态码
     * @return_param data String 数据
     * @return_param success Boolean 是否成功
     * @return_param message String 消息
     * @remark null
     * @number null
     */
    @GetMapping("/findAllByClassifyId")
    void findAllByClassifyId(@RequestParam(value = "classifyId") Long classifyId, HttpServletResponse resp) throws IOException {
        message = personalColumnService.findAllByClassifyId(classifyId);
        message.returnJson(resp);
    }

    /**
     * showdoc
     *
     * @param "id" 必选 Long 专栏id
     * @return {"status":200,"data":{"id":"1","username":"12154545"}}
     * @catalog 个人专栏接口
     * @title 通过专栏id查找专栏
     * @description 访问权限：用户；
     * @method get
     * @url /personalColumn/findById
     * @header token 可选 string 设备token
     * @return_param status int 状态码
     * @return_param data String 数据
     * @return_param success Boolean 是否成功
     * @return_param message String 消息
     * @remark null
     * @number null
     */
    @GetMapping("/findById")
    void findById(@RequestParam(name = "id") Long id, HttpServletResponse resp) throws IOException {
        message = personalColumnService.findById(id);
        message.returnJson(resp);
    }

    @GetMapping("/findDetailById")
    void findDetailById(@RequestParam(value = "id") Long id, HttpServletResponse resp) throws IOException {
        message = personalColumnService.findDetailById(id);
        message.returnJson(resp);
    }

    @GetMapping("/findByName")
    void findByName(@RequestParam(name = "name") String name, HttpServletResponse response) throws IOException {
        message = personalColumnService.findByNameOrMediaName(name);
        message.returnJson(response);
    }
}
