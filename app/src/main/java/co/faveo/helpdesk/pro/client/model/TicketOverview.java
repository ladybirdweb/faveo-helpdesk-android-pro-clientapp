package co.faveo.helpdesk.pro.client.model;

/**
 * Created by Sayar
 * Model class for ticket overview.
 */
public class TicketOverview {

    public int ticketID;
    public String ticketNumber;
    public String clientName;
    public String ticketSubject;
    public String ticketTime;
    public String ticketStatus;
    public String placeholder;
    public String agentName;
    public String priorityColor;
    public String agentPicture;
    public String priorityName;
    public String sourcename;
    public String departmentName;

    public TicketOverview(int ticketID, String ticketNumber, String clientName, String ticketSubject, String ticketTime, String ticketStatus, String placeholder, String agentName, String priorityColor, String agentPicture, String priorityName, String sourcename, String departmentName) {

        this.ticketID = ticketID;
        this.ticketNumber = ticketNumber;
        this.clientName = clientName;
        this.ticketSubject = ticketSubject;
        this.ticketTime = ticketTime;
        this.ticketStatus = ticketStatus;
        this.placeholder = placeholder;
        this.agentName = agentName;
        this.priorityColor = priorityColor;
        this.agentPicture = agentPicture;
        this.priorityName = priorityName;
        this.sourcename = sourcename;
        this.departmentName = departmentName;
    }

    public String getPriorityName() {
        return priorityName;
    }

    public void setPriorityName(String priorityName) {
        this.priorityName = priorityName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getPriorityColor() {
        return priorityColor;
    }

    public void setPriorityColor(String priorityColor) {
        this.priorityColor = priorityColor;
    }

    public String getAgentPicture() {
        return agentPicture;
    }

    public void setAgentPicture(String agentPicture) {
        this.agentPicture = agentPicture;
    }

    public int getTicketID() {
        return ticketID;
    }

    public void setTicketID(int ticketID) {
        this.ticketID = ticketID;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getTicketSubject() {
        return ticketSubject;
    }

    public void setTicketSubject(String ticketSubject) {
        this.ticketSubject = ticketSubject;
    }

    public String getTicketTime() {
        return ticketTime;
    }

    public void setTicketTime(String ticketTime) {
        this.ticketTime = ticketTime;
    }

    public String getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }
}
