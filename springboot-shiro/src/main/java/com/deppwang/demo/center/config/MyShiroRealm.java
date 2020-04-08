package com.deppwang.demo.center.config;

import com.deppwang.demo.center.service.UserService;
import com.deppwang.demo.core.model.SysRole;
import com.deppwang.demo.core.model.SysPermission;
import com.deppwang.demo.core.model.UserInfo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.SecureRandom;

/**
 * 描述：
 *
 * @author WangXQ
 * @date 2019/1/17 10:07
 */
public class MyShiroRealm extends AuthorizingRealm {
    @Autowired
    UserService userService;

    /**
     * 登陆时调用，用于检验用户的账号密码是否正确
     *
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("登陆验证--->MyShiroRealm.doGetAuthenticationInfo()");
        // 获取登录的用户名
        String username = (String) authenticationToken.getPrincipal();
        // 根据用户名在数据库中查找此用户
        // 实际项目中，这里可以根据实际情况做缓存，如果不做，Shiro 自己也是有时间间隔机制，2分钟内不会重复执行该方法?
        UserInfo userInfo = userService.findByUsername(username);
        if (userInfo == null) {
            return null;
        }
        //根据salt来验证token中的密码是否跟从数据库查找的密码匹配，匹配则登录成功。getName()设置当前Realm的唯一名称，可自定义
        return new SimpleAuthenticationInfo(
                userInfo,
                userInfo.getPassword(),
                ByteSource.Util.bytes(userInfo.getCredentialsSalt()),//盐
                getName());
    }

    /**
     * 当访问需要权限的 URL 时调用，用于验证当前用户是否有权限
     *
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("权限配置-->MyShiroRealm.doGetAuthorizationInfo()");
        UserInfo userInfo = (UserInfo) principalCollection.getPrimaryPrincipal();
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        // 获取当前用户的角色与权限，让 simpleAuthorizationInfo 去验证
        for (SysRole sysRole : userInfo.getRoleList()) {
            simpleAuthorizationInfo.addRole(sysRole.getRole());
            for (SysPermission sysPermission : sysRole.getPermissions()) {
                simpleAuthorizationInfo.addStringPermission(sysPermission.getPermission());
            }
        }
//        // 添加用户权限
//        simpleAuthorizationInfo.addStringPermission("user");
        return simpleAuthorizationInfo;
    }

    public static void main(String[] args) {
        // newPassword(密文密码)：d3c59d25033dbf980d29554025c23a75
        String newPassword = new SimpleHash("MD5",//散列算法:这里使用MD5算法
                "123456",//明文密码
                ByteSource.Util.bytes("admin8d78869f470951332959580424d4bf4f"),//salt：用户名 + salt
                2//散列的次数，相当于MD5(MD5(**))
        ).toHex();

        // 生成一个32位数的salt
        byte[] saltByte = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(saltByte);
        String salt = Hex.encodeToString((saltByte));

        System.out.println(newPassword);
        System.out.println(salt);
    }
}
