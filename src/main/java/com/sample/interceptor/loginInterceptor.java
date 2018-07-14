package com.sample.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import com.sample.javabean.User;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class loginInterceptor extends MethodFilterInterceptor {
    private static final String AJAX_TIME_OUT="ajaxSessionTimeOut";
    @Override
    protected String doIntercept(ActionInvocation actionInvocation) throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        String url = request.getServletPath();
        url = url.substring(url.indexOf("_")+1);
        Map<String,Object> session = ServletActionContext.getContext().getSession();
        if( url == "logout" || url.equals("logout" )){
            session.remove("currentUser");
            return actionInvocation.invoke();
        }
        String type = request.getHeader("x-requested-with");
        User currentUser = (User) session.get("currentUser");
        if(currentUser == null){
            if(null != type) {
                if(type.equalsIgnoreCase("XMLHttpRequest")) {
                    PrintWriter out=null;
                    try{
                        out = ServletActionContext.getResponse().getWriter();
                        out.print(AJAX_TIME_OUT);
                        out.flush();
                        return "sessionTimeOut";
                    }catch (IOException e) {
                        e.printStackTrace();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }finally{
                        out.close();
                    }
                }else return "sessionTimeOut";
            }else return "sessionTimeOut";
        }
        return actionInvocation.invoke();
    }
}
