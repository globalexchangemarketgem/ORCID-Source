/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.11.24 at 04:27:39 PM GMT 
//

package org.orcid.jaxb.model.notification.addactivities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

import org.orcid.jaxb.model.notification.Notification;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.orcid.org/ns/common}put-code" minOccurs="0"/>
 *         &lt;element ref="{http://www.orcid.org/ns/notification}notification-type"/>
 *         &lt;element ref="{http://www.orcid.org/ns/notification}authorization-url"/>
 *         &lt;element ref="{http://www.orcid.org/ns/notification}activities"/>
 *         &lt;element ref="{http://www.orcid.org/ns/common}created-date" minOccurs="0"/>
 *         &lt;element ref="{http://www.orcid.org/ns/common}sent-date" minOccurs="0"/>
 *         &lt;element ref="{http://www.orcid.org/ns/common}source" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "putCode", "notificationType", "authorizationUrl", "subject", "items", "createdDate", "sentDate", "readDate", "actionedDate", "archivedDate", "source" })
@XmlRootElement(name = "notification", namespace = "http://www.orcid.org/ns/notification")
public class NotificationAddActivities extends Notification {

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "authorization-url", namespace = "http://www.orcid.org/ns/notification", required = true)
    protected AuthorizationUrl authorizationUrl;
    @XmlElement(namespace = "http://www.orcid.org/ns/notification", required = true)
    protected Activities items;
    @XmlElement(name = "actioned-date", namespace = "http://www.orcid.org/ns/notification")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar actionedDate;
    @XmlElement(name = "notification-subject", namespace = "http://www.orcid.org/ns/notification")
    protected String subject;
    @XmlElement(name = "notification-intro", namespace = "http://www.orcid.org/ns/notification")
    protected String intro;

    /**
     * Gets the value of the authorizationUrl property.
     * 
     * @return possible object is {@link AuthorizationUrl }
     * 
     */
    public AuthorizationUrl getAuthorizationUrl() {
        return authorizationUrl;
    }

    /**
     * Sets the value of the authorizationUrl property.
     * 
     * @param value
     *            allowed object is {@link AuthorizationUrl }
     * 
     */
    public void setAuthorizationUrl(AuthorizationUrl value) {
        this.authorizationUrl = value;
    }

    /**
     * Gets the value of the activities property.
     * 
     * @return possible object is {@link Activities }
     * 
     */
    public Activities getItems() {
        return items;
    }

    /**
     * Sets the value of the activities property.
     * 
     * @param value
     *            allowed object is {@link Activities }
     * 
     */
    public void setItems(Activities value) {
        this.items = value;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public XMLGregorianCalendar getActionedDate() {
        return actionedDate;
    }

    public void setActionedDate(XMLGregorianCalendar actionedDate) {
        this.actionedDate = actionedDate;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }           
}
