package de.tud.feedback.index;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.util.Date;

@Document(indexName = "feedback")
public class HistoricalState {

    @Id
    private String id;

    @Field(store = true, type = FieldType.String)
    private String context;

    @Field(store = true, type = FieldType.String)
    private String item;

    @Field(store = true, type = FieldType.String, index = FieldIndex.not_analyzed)
    private String state;

    @Field(type = FieldType.Date, format = DateFormat.basic_date_time)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd'T'HHmmss.SSSZ", timezone = "CET")
    private Date time;

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Date getTime() {
        return time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
