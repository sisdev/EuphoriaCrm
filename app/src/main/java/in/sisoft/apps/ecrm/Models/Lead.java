package in.sisoft.apps.ecrm.Models;

public class Lead {

    private String id ;
    private String qry_source ;
    private String name ;//3
    private String address ;
    private String street ;
    private String sector ;
    private String market ;
    private String phone ;//5
    private String city ;
    private String qry_type ;//1
    private String district ;
    private String email ;//6
    private String qry_details ;//4
    private String qry_status;//2
    private String company_name;
    private String req_dtm;


    public Lead(String id, String qry_source, String name, String address, String street, String sector, String market, String phone, String city, String qry_type, String district, String email, String qry_details, String qry_status, String company_name, String req_dtm) {
        this.id = id;
        this.qry_source = qry_source;
        this.name = name;
        this.address = address;
        this.street = street;
        this.sector = sector;
        this.market = market;
        this.phone = phone;
        this.city = city;
        this.qry_type = qry_type;
        this.district = district;
        this.email = email;
        this.qry_details = qry_details;
        this.qry_status = qry_status;
        this.company_name = company_name;
        this.req_dtm = req_dtm;
    }

    public String getQry_type() {
        return qry_type;
    }

    public void setQry_type(String qry_type) {
        this.qry_type = qry_type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQry_source() {
        return qry_source;
    }

    public void setQry_source(String qry_source) {
        this.qry_source = qry_source;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getQry_details() {
        return qry_details;
    }

    public void setQry_details(String qry_details) {
        this.qry_details = qry_details;
    }

    public String getQry_status() {
        return qry_status;
    }

    public void setQry_status(String qry_status) {
        this.qry_status = qry_status;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getReq_dtm() {
        return req_dtm;
    }

    public void setReq_dtm(String req_dtm) {
        this.req_dtm = req_dtm;
    }
}
