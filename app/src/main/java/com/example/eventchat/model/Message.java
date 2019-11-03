package com.example.eventchat.model;

public class Message {
    private int id;
    private int studentId;
    private double latitude;
    private double lastName;
    private String studentMessage;

    public Message(int id, int studentId, double latitude, double lastName, String studentMessage)
    {
        this.id = id;
        this.studentId = studentId;
        this.latitude = latitude;
        this.lastName = lastName;
        this.studentMessage = studentMessage;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getStudentId()
    {
        return studentId;
    }

    public void setStudentId(int studentId)
    {
        this.studentId = studentId;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    public double getLastName()
    {
        return lastName;
    }

    public void setLastName(double lastName)
    {
        this.lastName = lastName;
    }

    public String getStudentMessage()
    {
        return studentMessage;
    }

    public void setStudentMessage(String studentMessage)
    {
        this.studentMessage = studentMessage;
    }
}
