package de.thro.inf.prg3.a07.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Peter Kurfer on 11/19/17.
 */

public class Meal {
    private int id;
    private String name;
    private String category;
    private List<String> notes;

    public Meal() {
        notes = new LinkedList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getNotes() {
        return notes;
    }

    public void setNotes(List<String> notes) {
        this.notes = notes;
    }

	public boolean isVegetarian(){
		return (toString().toLowerCase().contains("vegetarisch") || toString().toLowerCase().contains("vegan"));
	}
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Meal)) return false;

        Meal meal = (Meal) o;

        return new EqualsBuilder()
                .append(getId(), meal.getId())
                .append(getName(), meal.getName())
                .append(getCategory(), meal.getCategory())
                .append(getNotes(), meal.getNotes())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(getName())
                .append(getCategory())
                .append(getNotes())
                .toHashCode();
    }

    @Override
    public String toString() {
        StringBuilder notesBuilder = new StringBuilder();
        for(String s : notes){
            notesBuilder.append(String.format("%s, ", s));
        }
        if(notesBuilder.length() > 0) {
            notesBuilder.setLength(notesBuilder.length() - 2);
        }else {
            notesBuilder.append("No notes");
        }
        return String.format("%s\n%s\n%s", name, category, notesBuilder.toString());
    }
}
