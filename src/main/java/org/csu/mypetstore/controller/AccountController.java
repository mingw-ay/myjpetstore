package org.csu.mypetstore.controller;

import org.csu.mypetstore.domain.Account;
import org.csu.mypetstore.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/account") //要进入此功能模块,首先要求url开头为/account
public class AccountController {

    //自动注入accountService
    @Autowired
    AccountService accountService;

    //此乃页面跳转方法,点击链接为/account/viewLogin的方法则返回account下的login.html
    @GetMapping("/viewLogin")
    public String viewLogin(){
        return "account/login";
    }

    //页面跳转方法,get_url为/account/viewRegister则返回注册界面
    @GetMapping("/viewRegister")
    public String viewRegister(){
        return "account/register";
    }

    //页面跳转方法,客户端通过get方法传输/viewAccount,返回账户信息界面
    @GetMapping("viewAccount")
    String viewAccount(){
        return "account/accountInformation";
    }

    //页面跳转方法,点击sign out则跳转到viewMain界面,并且将session中的account转为空
    @GetMapping("/signOut")
    public String signOut(HttpSession httpSession){
        httpSession.setAttribute("account", null);  //这里直接将account对象设为null,提示说value account is always null
        return "catalog/main";  //返回主界面
    }

    //此方法对应logon的post方法
    @PostMapping("/login")
    public String login(Account account, Model model, HttpSession httpSession) {
        //倘若用户名为空
        if (account.getUsername().equals(""))
        {
            String msg = "Please enter your username";
            model.addAttribute("msg",msg);
            return "account/login"; //返回到login页面并且提示输入用户名
        }
        else if (account.getPassword().equals(""))
        {
            String msg = "Please enter your password";
            model.addAttribute("msg",msg);
            return "account/login"; //原理同上
        }
        else
        {
            //查询数据库,是否存在该账户
            account = accountService.getAccountByUsernameAndPassword(account);
            if (account == null)    //若是为空,则返回提示信息用户名或密码不存在
            {
                String msg = "Invalid Username or Password";
                model.addAttribute("msg",msg);
                return "account/login";
            }
            else
                httpSession.setAttribute("account",account);    //没关系,这样就能很好的完成任务,没必要用注解的方式,来让ioc给他注入,矫枉过正了可能
                return "catalog/main";
        }
    }

    //注册新用户对应的功能模块
    @PostMapping("/register")
    public String register(Account account){
        //添加新用户,三个数据库插入信息
        accountService.insertAccount(account);
        return "account/login"; //返回登录页面
    }

    //客户对post方法请求updateAccount
    @PostMapping("updateAccount")
    public String updateAccount(Account account, String newPassword, HttpSession httpSession){
        //要更新Account相关的三个数据库的账户信息
        //判断是否更新了密码,若是更新了,则要将穿过来的account对象的password更新
        if(!newPassword.equals("")){
            account.setPassword(newPassword);
        }
        //然后进行account session化并且返回页面
        accountService.updateAccount(account);
        httpSession.setAttribute("account",account);
        return "account/accountInformation";
    }


}
