package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.persistence.jpa.entities.CustomEmailEntity;
import org.orcid.persistence.jpa.entities.EmailType;

public class CustomEmailForm implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();    
    private Text subject;
    private Text content;
    private Text emailType;
    
    public CustomEmailEntity toCustomEmailEntity() {
        CustomEmailEntity entity = new CustomEmailEntity();
        if(this.content != null)
            entity.setContent(this.content.getValue());
        if(this.subject != null)
            entity.setSubject(this.subject.getValue());
        if(this.emailType != null)
            entity.setEmailType(EmailType.valueOf(this.emailType.getValue()));
        return entity;
    }
    
    public static CustomEmailForm valueOf(CustomEmailEntity entity) {
        CustomEmailForm result = new CustomEmailForm();
        result.setContent(Text.valueOf(entity.getContent()));
        result.setSubject(Text.valueOf(entity.getSubject()));
        result.setEmailType(Text.valueOf(entity.getEmailType().name()));
        return result;
    }
    
    public List<String> getErrors() {
        return errors;
    }
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
    public Text getSubject() {
        return subject;
    }
    public void setSubject(Text subject) {
        this.subject = subject;
    }
    public Text getContent() {
        return content;
    }
    public void setContent(Text content) {
        this.content = content;
    }
    public Text getEmailType() {
        return emailType;
    }
    public void setEmailType(Text emailType) {
        this.emailType = emailType;
    }            
}
