/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang.RandomStringUtils;

/**
 *
 * @author rajeshk
 */
public class StringUtil {
    
    private static final char[] CHARACTERS = {'a', 'b', 'c', 'd', 'e', 'f', 'g','h', 'i', 'j', 'k', 'l',
                                              'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                                              'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L','M',
                                              'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                                              '1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
                                              '!', '@', '#','$', '*'};
    
    private static final int LENGTH = 10;
    
    public static String generateRandomPassword() {
        String password = RandomStringUtils.random(LENGTH, CHARACTERS);
        
        return password;
    }
    
    public static String generateRandomString(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }
    
    public static String generateRandomNumbers(int length) {
        return RandomStringUtils.randomNumeric(length);
    }
    
    public static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        
        md.update(input.getBytes());
        
        byte[] digest = md.digest();
        
        String md5 = new BigInteger(digest).toString(16);
        
        while (md5.length() < 32) {
            md5 = "0" + md5;
        }
        
        return md5;
    }
    
    public static boolean isBlank(String input) {
        return input == null || input.equals("");
    }
    
    public static String buildGoogleSignINHtmlPage()
    {
      return "<%@page contentType=\"text/html\" pageEncoding=\"UTF-8\"%>\n" +
"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"\n" +
"    \"http://www.w3.org/TR/html4/loose.dtd\">\n" +
"\n" +
"<html>\n" +
"    <head>\n" +
"        <script>\n" +
"            var host = window.location.host + \"/\";\n" +
"            if (host.includes(\"localhost\")) {\n" +
"                host += \"BathwaterKids/\";\n" +
"            }\n" +
"            document.write(\"<base href='\" + window.location.protocol + '//' + host + \"' >\")\n" +
"            \n" +
"        </script>\n" +
"        <meta charset=\"utf-8\">\n" +
"        <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
"        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
"        <meta name=\"description\" content=\"\">\n" +
"        <meta name=\"author\" content=\"\">\n" +
"        <link rel=\"icon\" href=\"img/fevicon32.ico\">\n" +
"\n" +
"        <title>Bathwater</title>\n" +
"\n" +
"        <link href=\"css/style.css\" rel=\"stylesheet\">\n" +
"        <!-- Bootstrap core CSS -->\n" +
"        <link href=\"css/bootstrap.css\" rel=\"stylesheet\">\n" +
"        <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->\n" +
"        <link href=\"css/ie10-viewport-bug-workaround.css\" rel=\"stylesheet\">\n" +
"\n" +
"        <!-- Custom styles for this template -->\n" +
"        <link href=\"css/dashboard.css\" rel=\"stylesheet\">\n" +
"\n" +
"        <!-- styles for bhoechie-tab -->\n" +
"        <link href=\"css/bhoechie-tab.css\" rel=\"stylesheet\">\n" +
"\n" +
"        <!-- Just for debugging purposes. Don't actually copy these 2 lines! -->\n" +
"        <!--[if lt IE 9]><script src=\"../../assets/js/ie8-responsive-file-warning.js\"></script><![endif]-->\n" +
"        <script src=\"js/ie-emulation-modes-warning.js\"></script>\n" +
"        <meta name=\"google-signin-client_id\" content=\"532068338146-cgtbu6ipvaj6clem5kf021u8maj8jm02.apps.googleusercontent.com\">\n" +
"        <script\n" +
"            src=\"https://code.jquery.com/jquery-1.12.4.min.js\"\n" +
"            integrity=\"sha256-ZosEbRLbNQzLpnKIkEdrPv7lOy9C27hHQ+Xp8a4MxAQ=\"\n" +
"        crossorigin=\"anonymous\"></script>\n" +
"        <script src=\\\"https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js\\\"></script>\n" +
"        <script src = \"https://ajax.googleapis.com/ajax/libs/angularjs/1.3.3/angular.min.js\"></script>\n" +
"\n" +
"    </head>\n" +
"    <body>\n" +
"    <nav class=\"navbar navbar-inverse navbar-fixed-top\">\n" +
"        <div class=\"container-fluid\">\n" +
"            <div class=\"navbar-header\">\n" +
"                <button type=\"button\" class=\"navbar-toggle collapsed\" data-toggle=\"collapse\" data-target=\"#navbar\" aria-expanded=\"false\" aria-controls=\"navbar\">\n" +
"                    <span class=\"sr-only\">Toggle navigation</span>\n" +
"                    <span class=\"icon-bar\"></span>\n" +
"                    <span class=\"icon-bar\"></span>\n" +
"                    <span class=\"icon-bar\"></span>\n" +
"                </button>\n" +
"                <a class=\"navbar-brand\" href=\"/\"><img src=\"img/logo.png\" alt=\"Logo\"></a>\n" +
"            </div>\n" +
"        </div>\n" +
"    </nav>\n" +
"\n" +
"    <script>\n" +
"            function onSignIn(googleUser) {\n" +
"                var profile = googleUser.getBasicProfile();\n" +
"                var host = window.location.host + \"/\";\n" +
"                if (host.includes(\"localhost\")) {\n" +
"                    host += \"BathwaterKids/\";\n" +
"                }\n" +
"                \n" +
"                 var url = window.location.protocol+\"//\"+host+\"/rest/admin/glogintemp\";           \n" +
"                $.ajax({\n" +
"                    type: 'POST',\n" +
"                    url: url,\n" +
"                    headers: {\n" +
"                        'Authorization': \"Basic YWRtaW46YWRtaW4=\"\n" +
"                    },\n" +
"                    data: JSON.stringify({\n" +
"                        email: profile.getEmail(),\n" +
"                        name: profile.getName(),\n" +
"                        id: profile.getId()\n" +
"                    }),\n" +
"                    contentType: 'application/json',\n" +
"                    dataType: 'json',\n" +
"                    success: function (data) {\n" +
"                        if (data.message === 'success') {\n" +
"                            var host = window.location.host + \"/\";\n" +
"                            if (host.includes(\"localhost\")) {\n" +
"                                host += \"BathwaterKids/\";\n" +
"                            }\n" +
"                            window.location=window.location.protocol+\"//\"+host+\"/rest/admin/gloginsuccess?email=\"+profile.getEmail()+\"&id=\"+profile.getId();\n" +
"                        }\n" +
"                    }\n" +
"                });\n" +
"                \n" +
"            }\n" +
"            \n" +
"\n" +
"    </script>\n" +
"\n" +
"\n" +
"    <div class=\"container-fluid\">\n" +
"        <div class=\"row\">\n" +
"\n" +
"        </div>\n" +
"\n" +
"        <div class=\"main\">\n" +
"            <div class=\"nav-table\" id=\"gSignIn\">\n" +
"                <div class=\"g-signin2\" data-onsuccess=\"onSignIn\" align=\"center\"></div>\n" +
"                <script src=\"https://apis.google.com/js/platform.js\" async defer></script>\n" +
"            </div>\n" +
"\n" +
"\n" +
"        </div>\n" +
"    </div>\n" +
"</body>\n" +
"</html>\n" +
"";  
    }
    
    public static String buildResetPasswordHtmlPage(String email, String token) {
        return "<!DOCTYPE html>\n" +
"<html>\n" +
"    <head>\n" +
"        <script>\n" +
"            var host = window.location.host + \"/\";\n" +
"            if (host.includes(\"localhost\")) {\n" +
"                host += \"BathwaterKids/\";\n" +
"            }\n" +
"            document.write(\"<base href='\"+ window.location.protocol +'//' + host + \"' >\")\n" +
"        </script>\n" +
"        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
"        <meta charset=\"utf-8\">\n" +
"        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
"        <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css\">\n" +
"        <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js\"></script>\n" +
"        <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js\"></script>\n" +
"        <script src = \"https://ajax.googleapis.com/ajax/libs/angularjs/1.3.3/angular.min.js\"></script>\n" +
"        <title>BathwaterKids Reset Password</title>\n" +
"        <link rel=\"stylesheet\" href=\"reset/resources/css/style.css\">\n" +
"        <script>\n" +
"            $(function () {\n" +
"                $(\"#header\").load(\"reset/resources/static/header.html\");\n" +
"            });\n" +
"        </script>\n" +
"    </head>\n" +
"    <body>\n" +
"\n" +
"        <div class=\"page-header\" id=\"header\">\n" +
"\n" +
"        </div>\n" +
"        <div class=\"container-fluid text-center\">\n" +
"            <div class=\"row content\">\n" +
"                <div class=\"col-sm-2 sidenav sidenav-left\">\n" +
"\n" +
"                </div>\n" +
"\n" +
"                <div class=\"col-sm-8 text-left\"> \n" +
"                    <h1>Reset Password</h1>\n" +
"                    <hr>\n" +
"                    <p> " + email + ", please reset your password: </p> <br>\n" +
"                    <div id=\"resetPasswordForm\" class=\"text-center\">\n" +
"                        <form action=\"\" id=\"resetForm\" class=\"form-horizontal\">\n" +
"                            \n" +
"                            <div class=\"row\">\n" +
"                                <div class=\"form-inline form-group\">\n" +
"                                    <div class=\"col-sm-4\">\n" +
"                                        <label for=\"newPassword\" >New Password:</label>\n" +
"                                    </div>\n" +
"                                    <div class=\"col-sm-4\">\n" +
"                                        <input type=\"password\" class=\"form-control\" id=\"newPassword\" name=\"newPassword\">\n" +
"                                    </div>\n" +
"                                </div>\n" +
"                            </div>\n" +
"                            <div class=\"row\">\n" +
"                                <div class=\"form-inline form-group\">\n" +
"                                    <div class=\"col-sm-4\">\n" +
"                                        <label for=\"confirmPassword\" >Confirm Password:</label>\n" +
"                                    </div>\n" +
"                                    <div class=\"col-sm-4\">\n" +
"                                        <input type=\"password\" class=\"form-control\" id=\"confirmPassword\" name=\"confirmPassword\">\n" +
"                                    </div>\n" +
"                                </div>\n" +
"                            </div>\n" +
"                            <div class=\"row\">\n" +
"                                <input type=\"hidden\" id=\"emailAddress\" name=\"emailAddress\" value=\""+ email +"\">\n" +
"                                <input type=\"hidden\" id=\"token\" name=\"token\" value=\""+ token +"\">\n" +
"                                <button type=\"submit\" class=\"btn btn-primary\">Reset Password</button>\n" +
"                            </div>\n" +
"                        </form>\n" +
"                        <hr>\n" +
"                        <div class=\"alert\" id=\"error\" style=\"display: none\">\n" +
"\n" +
"                        </div>\n" +
"\n" +
"                    </div>\n" +
"                </div>\n" +
"                <div class=\"col-sm-2 sidenav sidenav-right\">\n" +
"\n" +
"                </div>\n" +
"\n" +
"            </div>\n" +
"        </div>\n" +
"\n" +
"        <script>\n" +
"            $(document).ready(function () {\n" +
"                $(\"form#resetForm\").submit(function (event) {\n" +
"                    event.preventDefault();\n" +
"                    var data = {};\n" +
"\n" +
"                    $.each(this.elements, function (i, v) {\n" +
"                        var input = $(v);\n" +
"                        data[input.attr(\"name\")] = input.val();\n" +
"                        delete data[\"undefined\"];\n" +
"                    });\n" +
"\n" +
"                    var dataString = JSON.stringify(data);\n" +
"                    var urlString = 'rest/updatePassword';\n" +
"                    $.ajax({\n" +
"                        type: 'POST',\n" +
"                        header: {\n" +
"                            'Content-Type': 'application/json',\n" +
"                            'Authorization' : 'Basic YWRtaW46YWRtaW4='\n" +
"                        },\n" +
"                        contentType: 'application/json',\n" +
"                        dataType: 'json',\n" +
"                        url: urlString,\n" +
"                        data: dataString,\n" +
"                        success: function (data) {\n" +
"                            if (data && data.message === 'success') {\n" +
"                                $('#error').html('<p>Password successfully updated!</p>');\n" +
"                                $('#error').addClass('alert-success');\n" +
"\n" +
"                            } else {\n" +
"                                $('#error').html('<p>Password update failed!</p>');\n" +
"                                $('#error').addClass('alert-danger');\n" +
"                            }\n" +
"                            $('#error').css('display', 'block');\n" +
"                        }\n" +
"                    });\n" +
"                });\n" +
"            });\n" +
"        </script>\n" +
"    </body>\n" +
"</html>";
    }
    
    public static String buildInvalidTokenErrorPage() {
        return "<html><head><script>            var host = window.location.host + \"/\";            if (host.includes(\"localhost\")) {                host += \"BathwaterKids/\";           }           document.write(\"<base href='\"+ window.location.protocol +'//' + host + \"' >\")       </script>       <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">        <meta charset=\"utf-8\">        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">        <link rel=\"stylesheet\" href=\"http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css\">        <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js\"></script>        <script src=\"http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js\"></script>        <script src = \"https://ajax.googleapis.com/ajax/libs/angularjs/1.3.3/angular.min.js\"></script>        <title>Bathwater Reset Password</title>        <link rel=\"stylesheet\" href=\"reset/resources/css/style.css\">        <script>            $(function () {                $(\"#header\").load(\"reset/resources/static/header.html\");            });        </script>    </head>    <body>        <div class=\"page-header\" id=\"header\">        </div>        <div class=\"container-fluid text-center\">            <div class=\"row content\">                <div class=\"col-sm-2 sidenav sidenav-left\">                </div>                <div class=\"col-sm-8 text-left\">                    <div class=\"alert alert-danger\" id=\"error\">                       <p>Invalid Token!</p>                    </div>                </div>            </div>            <div class=\"col-sm-2 sidenav sidenav-right\">            </div>        </div>    </body></html>";
    }
}
