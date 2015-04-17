
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class Message implements JSONAware {

	private String userName;
	private int id;
	private String message;
	private boolean deleted = false;
	private boolean changed = false;
	private String requst;
	
	public Message() {
		userName = "none";
		message = "";
		id = -1;		
	}
	public Message(String message,String userName) {
		this.userName = userName;
		this.message = message;
		id = -1;		
	}
	public Message(int id, String userName, String message) {
		this.id = id;
		this.userName = userName;
		this.message = message;
	}
	public void setRequst(String requst) {
		this.requst = requst;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public void setNameUser(String nameUser) {
		this.userName = nameUser;
	}
	public void setID(int id) {
		this.id = id;
	}
	public void setDelete(boolean deleted) {
		this.deleted = deleted;
	}
	public void setChange(boolean changed) {
		this.changed = changed;
	}
	public void setText(String text) {
		this.message = text;
	}
	public String getRequst() {
		return requst;
	}
	public String getNameUser() {
		return userName;
	}
	public int getID() {
		return id;
	}
	public String getText() {
		return message;
	}
	public boolean isDelete() {
		return deleted;
	}
	public boolean getChange() {
		return changed;
	}
	
	public void deleteMessage() {
		if(deleted != true) {
			this.message = "message has deleted.";
			this.setDelete(true);
		}
	}
	public static Message parseMessage(JSONObject obj) {
		Message info = new Message();
		
		info.setNameUser((String)obj.get("user"));
		info.setMessage((String)obj.get("message"));
		if(obj.get("id") != null) {
		info.setID(Integer.parseInt(obj.get("id").toString()));
		}
		return info;
	}	

	@Override
	public String toJSONString() {
		JSONObject obj = new JSONObject();
		obj.put("user", userName);
		obj.put("message", message);
		obj.put("id", id);
		obj.put("requst", requst);
		return obj.toString();
	}
	@Override
	public String toString() {
		return userName + " : " + message;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Message other = (Message) obj;
        if (id != other.id)
            return false;
        
        return true;
	}
	@Override
    public int hashCode() {
        return 1*id+2*message.hashCode()+3*userName.hashCode();
    }
}