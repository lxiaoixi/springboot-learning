package com.example.mybatis.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.mybatis.entity.UserEntity;
import com.example.mybatis.param.UserParam;
import com.example.mybatis.result.Page;
import com.example.mybatis.service.UserService;
import com.example.mybatis.util.ExcelUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    //@Value获取application.properties配置中的属性
    @Value("${com.example.mybatis.name}")
    private String myname;

    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public String saveUser(UserEntity userEntity){
        String data = userService.saveUser(userEntity);
        System.out.print(data);
        return data;
    }
    //参数校验
    @PostMapping("/saveUser")
    public void saveUser(@Valid UserEntity userEntity, BindingResult result) {
        System.out.println("user:"+userEntity);
        if(result.hasErrors()) {
            List<ObjectError> list = result.getAllErrors();
            for (ObjectError error : list) {
                System.out.println(error.getCode() + "-" + error.getDefaultMessage());
            }
        }
    }

    @PostMapping("/delete")
    public String deleteUser(Long id){
        return userService.deleteUserById(id);
    }

    @PostMapping("/update")
    public String updateUser(UserEntity userEntity){
        System.out.println(userEntity.getUserName());
        return userService.updateUserById(userEntity);
    }

    @GetMapping("/search/{id}")
    public UserEntity findUserById(@PathVariable Long id){
        UserEntity userEntity = userService.findUserById(id);
        System.out.print(userEntity.getUserSex());
        System.out.print(userEntity.getUserName());
        return userEntity;
    }

    @GetMapping("/search/users")
    public List<UserEntity> findAllUsers(){
        System.out.print(myname);
        return userService.getUserList();
    }

    @GetMapping("/search/pages/users")
    public Page<UserEntity> searchUserByPage(UserParam userParam){
        List<UserEntity> users =  userService.userListByPage(userParam);
        int sum = userService.countUsers(userParam);
        //构造返回结果类
        Page page = new Page(userParam.getCurrentPage(),userParam.getPageSize(),sum,users);
        return page;
    }

    @GetMapping("/page/help/search")
    public PageInfo<UserEntity> getAll(UserParam userParam) {
        List<UserEntity> userList = userService.findUserByPage(userParam);
        //分页的包装类PageInfo,用PageInfo对结果进行包装
        return new PageInfo(userList);
    }

    @GetMapping(value = "/excel/report")
    public String getDetailReport(HttpServletResponse response) {
        Map<String, Object> item = new HashMap<>();
        item.put("dateStr", "2018-05-25");
        item.put("testingItemCost", 10000);
        item.put("orderPlaceCount", 100);
        item.put("rate", 10);
        item.put("name", "订单1");
        item.put("createTime", 1527234924000l);
        Map<String, Object> item1 = new HashMap<>();
        item1.put("dateStr", "2018-05-26");
        item1.put("testingItemCost", 12000);
        item1.put("orderPlaceCount", 90);
        item1.put("rate", 20);
        item1.put("name", "订单2");
        item1.put("createTime", 1527234924000l);
        List<Object> list1 = new ArrayList<>();

        list1.add(item);
        list1.add(item1);

        Map<String, Object> sum = new HashMap<>();
        sum.put("testingItemCost", 22000);
        sum.put("orderPlaceCount", 190);
        Map<String, Object> map = new HashMap<>();
        map.put("list", list1);
        map.put("sum", sum);
        String str = JSON.toJSONString(map);
        JSONObject jsonObject = JSONObject.parseObject(str);
        try {
            String filename = "ceshi2.xlsx";
            ExcelUtil.exportExcel(jsonObject, "reportsBusinessYearDetail.xlsx", response, filename);
            return "success";
        } catch (Exception e) {
            return "failed";
        }
    }

    @GetMapping(value = "/excel/merge")
    public String getIncomeDetailReport(HttpServletResponse response) {

        try {
            //获取classpath 文件的输入流
            InputStream fileInputStream =
                    UserController.class.getClassLoader().getResourceAsStream("config/mergeCell.json");

            byte[] bytes = new byte[0];
            bytes = new byte[fileInputStream.available()];
            fileInputStream.read(bytes);
            String str = new String(bytes);
            JSONObject jsonObject = JSON.parseObject(str);

            String filename = "ceshi.xlsx";
            ExcelUtil.mergeExcel(jsonObject, "incomeDetail.xlsx", response, filename);
            return "success";

        } catch (Exception e) {
            return "failed";
        }
    }


}
