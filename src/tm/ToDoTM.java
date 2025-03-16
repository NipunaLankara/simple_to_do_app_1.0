package tm;

public class ToDoTM {
    private String todoID;
    private String description;
    private String userId;

    public ToDoTM () {

    }

    public ToDoTM (String todoID,String description,String userId) {
        this.todoID = todoID;
        this.description = description;
        this.userId = userId;

    }

    public void setTodoID (String todoID) {
        this.todoID = todoID;

    }

    public void setDescription (String description) {
        this.description = description;

    }

    public void setUserId (String userId) {
        this.userId = userId;

    }

    public String getTodoID() {
        return todoID;
    }

    public String getDescription() {
        return description;

    }

    public String getUserId() {
        return userId;

    }

    public String toString () {
        return description;
    }
}
