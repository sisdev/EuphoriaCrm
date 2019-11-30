package in.sisoft.apps.ecrm.Models;

public class Task {

    private String id;
    private String LeadQryId ;
    private String Name ;
    private String PhoneNo ;
    private String QryDetails ;
    private String AssignedUser ;
    private String TaskDateTime;
    private String TaskType ;
    private String Narration ;
    private String Status;
    private String CreatedBy ;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Task(){
    }

    public Task(String id,String leadQryId, String name, String phoneNo, String qryDetails, String
            assignedUser, String taskDateTime, String taskType, String narration, String
            status, String createdBy) {

        LeadQryId = leadQryId;
        Name = name;
        PhoneNo = phoneNo;
        QryDetails = qryDetails;
        AssignedUser = assignedUser;
        TaskDateTime = taskDateTime;
        TaskType = taskType;
        Narration = narration;
        Status = status;
        CreatedBy = createdBy;
        this.id = id;
    }

    public String getLeadQryId() {
        return LeadQryId;
    }

    public void setLeadQryId(String leadQryId) {
        LeadQryId = leadQryId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhoneNo() {
        return PhoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        PhoneNo = phoneNo;
    }

    public String getQryDetails() {
        return QryDetails;
    }

    public void setQryDetails(String qryDetails) {
        QryDetails = qryDetails;
    }

    public String getAssignedUser() {
        return AssignedUser;
    }

    public void setAssignedUser(String assignedUser) {
        AssignedUser = assignedUser;
    }

    public String getTaskDateTime() {
        return TaskDateTime;
    }

    public void setTaskDateTime(String taskDateTime) {
        TaskDateTime = taskDateTime;
    }

    public String getTaskType() {
        return TaskType;
    }

    public void setTaskType(String taskType) {
        TaskType = taskType;
    }

    public String getNarration() {
        return Narration;
    }

    public void setNarration(String narration) {
        Narration = narration;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }
}
