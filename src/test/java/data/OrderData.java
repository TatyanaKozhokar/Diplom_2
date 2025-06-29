package data;

import lombok.Data;

@Data
public class OrderData {
    public OrderData(String[] ingredients) {
        this.ingredients = ingredients;
    }

    private String[] ingredients;


}
