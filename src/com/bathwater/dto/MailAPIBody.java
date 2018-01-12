/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.dto;

import java.util.List;

/**
 *
 * @author rajeshk
 */
public class MailAPIBody {
    
    private String key = "I5RFYn1KtMxr2XI-0AFtjQ";
    
    private Message message;
    
    private String template_name;
    
    List<TemplateContent> template_content;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getTemplate_name() {
        return template_name;
    }

    public void setTemplate_name(String template_name) {
        this.template_name = template_name;
    }

    public List<TemplateContent> getTemplate_content() {
        return template_content;
    }

    public void setTemplate_content(List<TemplateContent> template_content) {
        this.template_content = template_content;
    }
    
    public static class Message {
        String html;
        
        String subject;
        
        String from_email;
        
        String from_name;
        
        List<String> tags;
        
        List<To> to;

        public String getHtml() {
            return html;
        }

        public void setHtml(String html) {
            this.html = html;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getFrom_email() {
            return from_email;
        }

        public void setFrom_email(String from_email) {
            this.from_email = from_email;
        }

        public String getFrom_name() {
            return from_name;
        }

        public void setFrom_name(String from_name) {
            this.from_name = from_name;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public List<To> getTo() {
            return to;
        }

        public void setTo(List<To> to) {
            this.to = to;
        }
        
        public static class To {
            String email;
            
            String name;
            
            String type;

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }
        }
    }
    
    public static class TemplateContent {
        
        private String name;
        
        private String content;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
        
    }
    
 }
