package org.example.model;

public class Lesson {
//    private Integer id;

    // time
    private Integer timetableId;
    private Integer slotId;

    //place
    private String classroom;

    // who
    private Integer teacherId;

    // what
    private Integer subjectId;

    // to whom
    private Integer course;
    private Integer group;
    private Integer subgroup;

    public Lesson(Integer timetableId, Integer slotId, String classroom, Integer teacherId, Integer subjectId, Integer course, Integer group, Integer subgroup) {
        this.timetableId = timetableId;
        this.slotId = slotId;
        this.classroom = classroom;
        this.teacherId = teacherId;
        this.subjectId = subjectId;
        this.course = course;
        this.group = group;
        this.subgroup = subgroup;
    }

    public Integer getTimetableId() {
        return timetableId;
    }

    public void setTimetableId(Integer timetableId) {
        this.timetableId = timetableId;
    }

    public Integer getSlotId() {
        return slotId;
    }

    public void setSlotId(Integer slotId) {
        this.slotId = slotId;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public Integer getCourse() {
        return course;
    }

    public void setCourse(Integer course) {
        this.course = course;
    }

    public Integer getGroup() {
        return group;
    }

    public void setGroup(Integer group) {
        this.group = group;
    }

    public Integer getSubgroup() {
        return subgroup;
    }

    public void setSubgroup(Integer subgroup) {
        this.subgroup = subgroup;
    }
}
