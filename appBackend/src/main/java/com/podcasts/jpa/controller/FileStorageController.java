package com.podcasts.jpa.controller;

import com.podcasts.jpa.pojo.Media;
import com.podcasts.jpa.pojo.User;
import com.podcasts.jpa.service.FileStorageService;
import com.podcasts.jpa.service.UserService;
import com.podcasts.jpa.util.Message;
import com.podcasts.jpa.util.NonStaticResourceHttpRequestHandler;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@AllArgsConstructor
@RequestMapping("/file")
public class FileStorageController {
    private final NonStaticResourceHttpRequestHandler nonStaticResourceHttpRequestHandler;
    @Autowired
    FileStorageService fileStorageService;
    @Autowired
    UserService userService;

    /**
     * showdoc
     *
     * @param "id" 必选 Long 媒体文件id
     * @return {"status":200,"data":{"id":"1","username":"12154545"}}
     * @catalog 文件接口
     * @title 查看媒体文件详情
     * @description 访问权限：用户，管理员；
     * @method get
     * @url /file/findMediaById
     * @header token 可选 string 设备token
     * @return_param status int 状态码
     * @return_param data String 数据
     * @return_param success Boolean 是否成功
     * @return_param message String 消息
     * @remark null
     * @number null
     */
    @GetMapping("/findMediaById")
    public void findMediaById(HttpServletResponse resp, @RequestParam(name = "id") Long id) throws IOException {
        Message message;
        message = fileStorageService.findMediaById(id);
        message.returnJson(resp);
    }

    /**
     * showdoc
     *
     * @param "id" 必选 Long 用户id
     * @return {"data":[{"id":6,"title":"hello","classify":"hi1","path":"FileStorage/podcasts/video_20210802_165149.mp4","type":"video/mp4","time":1633690586665,"size":40582600,"userId":1,"username":"podcasts","fileName":"video_20210802_165149.mp4"}],"success":true,"message":"","status":200}
     * @catalog 文件接口
     * @title 用户全部文件
     * @description 访问权限：用户，管理员；
     * @method get
     * @url /file/findAllMediaByUserId
     * @header token 可选 string 设备token
     * @return_param status int 状态码
     * @return_param data String 数据
     * @return_param success Boolean 是否成功
     * @return_param message String 消息
     * @remark null
     * @number null
     */
    @GetMapping("/findAllMediaByUserId")
    public void findAllByUserId(@RequestParam(name = "id") Long id, HttpServletResponse resp) throws IOException {
        Message message;
        message = fileStorageService.findAllByUserId(id);
        message.returnJson(resp);
    }

    /**
     * showdoc
     *
     * @param "id"         必选 Long   媒体文件的id
     * @param "title"      必选 String 媒体文件的标题
     * @param "classifyId" 必选 Long 媒体文件所属的分类
     * @return {"status":200,"data":{"id":"1","username":"12154545"}}
     * @catalog 文件接口
     * @title 媒体文件信息修改
     * @description 访问权限：用户，管理员；
     * @method post
     * @url /file/updateMedia
     * @header token 可选 string 设备token
     * @return_param status int 状态码
     * @return_param data String 数据
     * @return_param success Boolean 是否成功
     * @return_param message String 消息
     * @remark null
     * @number null
     */
    @PostMapping("/updateMedia")
    public void updateMedia(@RequestBody Media media, HttpServletResponse resp) throws IOException {
        Message message;
//        System.out.println(media);
        message = fileStorageService.updateMedia(media);
        message.returnJson(resp);
    }

    /**
     * showdoc
     *
     * @param "file" 必选 char 选定一个文件
     * @return {"status":200,"data":{"id":"1","username":"12154545"}}
     * @catalog 文件接口
     * @title 媒体文件上传
     * @description 访问权限：用户或管理员；
     * @method post
     * @url /file/upload
     * @return_param status int 状态码
     * @return_param data String 数据
     * @remark null
     * @number null
     */
    @PostMapping("/upload")
    public void upload(@RequestParam("file") MultipartFile file, HttpServletResponse resp) throws IOException {
        // 上传媒体文件 音频或视频
        Message message;
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        User user = (User) principal;

        String username = "";
// 获取安全上下文对象，就是那个保存在 ThreadLocal 里面的安全上下文对象
// 总是不为null(如果不存在，则创建一个authentication属性为null的empty安全上下文对象)
        SecurityContext securityContext = SecurityContextHolder.getContext();
// 获取当前认证了的 principal(当事人),或者 request token (令牌)
// 如果没有认证，会是 null,该例子是认证之后的情况
        Authentication authentication = securityContext.getAuthentication();
// 获取当事人信息对象，返回结果是Object类型，但实际上可以是应用程序自定义的带有更多应用相关信息的某个类型。
// 很多情况下，该对象是Spring Security核心接口UserDetails的一个实现类，
// 可以把UserDetails想像成数据库中保存的一个用户信息到SecurityContextHolder中Spring Security需要的用户信息格式的一个适配器。
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        System.out.println(username);

        User user = userService.getUser(username, null);
        try {
            message = fileStorageService.save(file, user, "file", null);
        } catch (Exception e) {
            message = new Message(false, 400, "文件上传失败", "");
        }
        message.returnJson(resp);
    }

    /**
     * showdoc
     *
     * @param "img"  必选 char 选定一个文件
     * @param "type" 必选 String 媒体图片(media)或用户头像(avatar)或专栏图片(personalColumn)使用英文作为类型识别
     * @param "id"   必选 Number 操作对象的Id，可以是媒体id，专栏id
     * @return {"status":200,"data":{"id":"1","username":"12154545"}}
     * @catalog 文件接口
     * @title 图片文件上传
     * @description 访问权限：用户或管理员；
     * @method post
     * @url /file/uploadImg
     * @return_param status int 状态码
     * @return_param data String 数据
     * @remark null
     * @number null
     */
    @PostMapping("/uploadImg")
    public void uploadImg(@RequestParam(name = "id", required = false) Long mediaId, @RequestParam(name = "type", required = true) String type, @RequestParam(value = "img", required = true) MultipartFile img, HttpServletResponse resp, Authentication authentication) throws IOException {
        Message message;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = (User) principal;
        // 上传图片
        try {
            message = fileStorageService.save(img, user, type, mediaId);
        } catch (Exception e) {
            message = new Message(false, 400, "图片上传失败", "");
        }
        message.returnJson(resp);

    }

    /**
     * showdoc
     *
     * @param "fileName" 必选 String 文件名称
     * @param "path"     必选 String 文件路径
     * @return {"status":200,"data":{"id":"1","username":"12154545"}}
     * @catalog 文件接口
     * @title 文件下载
     * @description 访问权限：用户；
     * @method get
     * @url /file/download
     * @return_param status int 状态码
     * @return_param data String 数据
     * @return_param success Boolean 是否成功
     * @return_param message String 消息
     * @remark null
     * @number null
     */
    @GetMapping("/download")
    public void download(@RequestParam(name = "fileName") String fileName, @RequestParam(name = "path") String path, HttpServletResponse resp) throws IOException {
        Message message = fileStorageService.download(fileName, path, resp);
        message.returnJson(resp);
    }

    /**
     * showdoc
     *
     * @param "id" 必选 Long 文件id
     * @return {"status":200,"data":{"id":"1","username":"12154545"}}
     * @catalog 文件接口
     * @title 文件删除
     * @description 访问权限：用户；
     * @method get
     * @url /file/deleteById
     * @return_param status int 状态码
     * @return_param data String 数据
     * @return_param success Boolean 是否成功
     * @return_param message String 消息
     * @remark null
     * @number null
     */
    @GetMapping("/deleteById")
    public void deleteById(@RequestParam(name = "id") Long id, HttpServletResponse resp) throws IOException {
        Message message = fileStorageService.deleteById(id);
        message.returnJson(resp);
    }

    /**
     * showdoc
     *
     * @param 'path' 必选 String 媒体地址
     * @return {}
     * @catalog 音频接口
     * @title 播放
     * @description 播放权限：任意；播放视频或音频
     * @method get
     * @url /file/video/player
     * @return_param status int 状态码
     * @return_param data String 数据
     * @return_param success Boolean 是否成功
     * @return_param message String 消息
     * @remark
     * @number
     */
    @GetMapping("/video/player")
    public void videoPreview(@RequestParam(name = "path") String path, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Path filePath = Paths.get(path);
        Path fPath = Path.of(URLDecoder.decode(String.valueOf(filePath), StandardCharsets.UTF_8));
        if (Files.exists(fPath)) {
            String mimeType = Files.probeContentType(fPath);
            if (!StringUtils.isEmpty(mimeType)) {
                response.setContentType(mimeType);
            }
            request.setAttribute(NonStaticResourceHttpRequestHandler.ATTR_FILE, fPath);
            nonStaticResourceHttpRequestHandler.handleRequest(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        }
    }
}
