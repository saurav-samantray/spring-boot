package com.nlp.restservice.pojo.response;

import java.util.List;

public class Response
{
    private List<Entity> entities;

    public List<Entity> getEntities() {
		return entities;
	}

	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}

	@Override
    public String toString()
    {
        return "Response [entities = "+entities+"]";
    }
}
