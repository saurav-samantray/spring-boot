package com.nlp.restservice.pojo.response;

public class Entity
{
	public Entity() {
		this.text = null;
		this.type = null;
	}
	public Entity(String text,String type) {
		this.text = text;
		this.type = type;
	}
    private String text;

    private String type;

    public String getText ()
    {
        return text;
    }

    public void setText (String text)
    {
        this.text = text;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    @Override
    public String toString()
    {
        return "Entity [text = "+text+", type = "+type+"]";
    }
}
