package com.example.android.notestracker.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;
@Entity(tableName = "task")
public class TaskEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String text;
    private Date updatedAt;
    private String imageUri;

    @Ignore
    public TaskEntry(String title , String text , Date updatedAt)
    {
        this.title = title;
        this.text = text;
        this.updatedAt= updatedAt;
    }

    @Ignore
    public TaskEntry(String title , String text ,Date updatedAt, String imageUri)
    {
        this.updatedAt = updatedAt;
        this.imageUri = imageUri;
        this.title = title;
        this.text = text;
    }

    public TaskEntry(int id ,String title , String text , String imageUri ,Date updatedAt)
    {
        this.id = id;
        this.imageUri = imageUri;
        this.title = title;
        this.text = text;
        this.updatedAt= updatedAt;
    }
    public int getId()
    {
        return id;
    }
    public void setID(int id)
    {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getText()
    {
        return text;
    }
    public void setText(String Text)
    {
        this.text = Text;
    }
    public Date getUpdatedAt()
    {
        return updatedAt;
    }
    public void setUpdatedAt(Date update)
    {
        this.updatedAt = update;
    }
    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

}

